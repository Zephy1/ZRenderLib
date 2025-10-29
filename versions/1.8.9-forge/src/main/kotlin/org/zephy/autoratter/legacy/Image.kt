package org.zephy.zrenderlib.legacy

import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Image(var image: BufferedImage?) {
    private lateinit var texture: DynamicTexture
    private val textureWidth = image?.width ?: 0
    private val textureHeight = image?.height ?: 0
    private val aspectRatio = if (textureHeight != 0) textureHeight.toFloat() / textureWidth else 0f

    fun getTextureWidth(): Int = textureWidth
    fun getTextureHeight(): Int = textureHeight
    fun getTexture(): DynamicTexture {
        if (!::texture.isInitialized) {
            try {
                texture = DynamicTexture(image!!)
                image = null
            } catch (e: Exception) {
                println("Trying to bake texture in a non-rendering context.")
                throw e
            }
        }
        return texture
    }

    fun destroy() {
        texture.deleteGlTexture()
        image = null
    }

    @SubscribeEvent
    fun onRender(event: RenderGameOverlayEvent.Pre) {
        if (image == null) return
        texture = DynamicTexture(image!!)
        image = null
        MinecraftForge.EVENT_BUS.unregister(this)
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
        if (image != null) return@apply
        GUIRenderer.drawImage(this, xPosition, yPosition, drawWidth, drawHeight, zOffset, color)
    }

    companion object {
        /**
         * Create an image object from a java.io.File object. Throws an exception
         * if the file cannot be found.
         */
        @JvmStatic
        fun fromFile(file: File) = Image(ImageIO.read(file))

        /**
         * Create an image object from a file path. Throws an exception
         * if the file cannot be found.
         */
        @JvmStatic
        fun fromFile(file: String) = Image(ImageIO.read(File(file)))
    }
}
