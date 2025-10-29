package org.zephy.zrenderlib.modern

//#if MC>12100
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.util.Identifier
import org.lwjgl.system.MemoryUtil
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.util.UUID
import javax.imageio.ImageIO

class Image(var image: BufferedImage?) {
    private var texture: Texture? = null
    private val textureWidth = image?.width ?: 0
    private val textureHeight = image?.height ?: 0
    private val aspectRatio = if (textureHeight != 0) textureHeight.toFloat() / textureWidth else 0f
    private var identifier: Identifier? = null

    init {
        Client.scheduleTask {
            setTexture(bufferedImageToNativeTexture(image!!))
        }
    }

    fun getTextureWidth(): Int = textureWidth
    fun getTextureHeight(): Int = textureHeight
    fun getIdentifier(): Identifier? = identifier
    fun getTexture(): NativeImageBackedTexture? = texture?.texture
    fun setTexture(tex: Texture?) {
        destroy()
        texture = tex
        if (texture == null) return

        identifier = Identifier.of("zrenderlib", texture!!.uniqueName)
        Client.getMinecraft().textureManager.registerTexture(identifier!!, texture!!.texture)
    }

    /**
     * Clears the image from GPU memory and removes its references CT side
     * that way it can be garbage collected if not referenced in js code.
     */
    fun destroy() {
        if (identifier != null) {
            Client.getMinecraft().textureManager.destroyTexture(identifier!!)
        }

        texture?.texture?.close()
        texture?.buffer?.let(MemoryUtil::memFree)
        texture = null
        identifier = null
        image = null
    }

    fun getImageSize(
        width: Float? = null,
        height: Float? = null,
    ): Pair<Float, Float> {
        return when {
            width == null && height == null -> textureWidth.toFloat() to textureHeight.toFloat()
            width == null -> height!! / aspectRatio to height
            height == null -> width to width * aspectRatio
            else -> width to height
        }
    }

    /**
     * Draws the image on screen
     * @return The Image object to allow for method chaining
     */
    @JvmOverloads
    fun drawRGBA(
        xPosition: Float,
        yPosition: Float,
        width: Float? = null,
        height: Float? = null,
        zOffset: Float = 0f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
    ) = apply {
        draw(xPosition, yPosition, width, height, zOffset, RenderUtils.RGBAColor(red, green, blue, alpha).getLong())
    }

    /**
     * Draws the image on screen
     * @return The Image object to allow for method chaining
     */
    @JvmOverloads
    fun draw(
        xPosition: Float,
        yPosition: Float,
        width: Float? = null,
        height: Float? = null,
        zOffset: Float = 0f,
        color: Long = RenderUtils.WHITE,
    ) = apply {
        val (drawWidth, drawHeight) = getImageSize(width, height)
        if (texture == null) return@apply
        GUIRenderer.drawImage(this, xPosition, yPosition, drawWidth, drawHeight, zOffset, color)
    }

    data class Texture(val texture: NativeImageBackedTexture, val buffer: ByteBuffer, val uniqueName: String)

    companion object {
        /**
         * Create an image object from a java.io.File object. Throws an exception
         * if the file cannot be found.
         */
        @JvmStatic
        fun fromFile(file: File): Image {
            val bufferedImage = ImageIO.read(file) ?: throw IllegalArgumentException("Could not read image file.")

            val newImage = Image(bufferedImage)
            return newImage
        }

        /**
         * Create an image object from a file path. Throws an exception
         * if the file cannot be found.
         */
        @JvmStatic
        fun fromFile(file: String) = fromFile(File(file))

        @JvmStatic
        fun bufferedImageToNativeTexture(image: BufferedImage): Texture {
            return ByteArrayOutputStream().use {
                ImageIO.write(image, "png", it)
                val buffer = MemoryUtil.memAlloc(it.size())
                buffer.put(it.toByteArray())
                buffer.rewind()

                val uniqueName = "image-${UUID.randomUUID()}"
                Texture(
                    NativeImageBackedTexture({ "zrenderlib:$uniqueName" }, NativeImage.read(buffer)),
                    buffer,
                    uniqueName
                )
            }
        }
    }
}
//#endif
