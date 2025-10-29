package org.zephy.zrenderlib.modern

//#if MC>12100
import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.platform.DestFactor
import com.mojang.blaze3d.platform.SourceFactor
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.GpuTexture
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import net.minecraft.client.render.RenderLayer
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import org.joml.Quaternionf
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

//#if MC>=12106
//$$import com.mojang.blaze3d.textures.GpuTextureView
//#endif

object RenderUtils {
    // WorldRenderer
    private val NEWLINE_REGEX = """\n|\r\n?""".toRegex()
    private var firstVertex = true
    private var began = false

    @JvmStatic
    fun getFontRenderer() = Client.getMinecraft().textRenderer
    private val ucWorldRenderer = UGraphics.getFromTessellator()

    // GUIRenderer
    @JvmField var colorized: Long? = null

    @JvmField var vertexColor: Color? = null

    internal lateinit var matrixStack: UMatrixStack
    private val matrixStackStack = ArrayDeque<UMatrixStack>()
    internal var matrixPushCounter = 0

    @JvmStatic
    fun setMatrixStack(stack: UMatrixStack) = apply {
        matrixStack = stack
    }

//    private var matrixStackReflectionField: Field? = null
//    private var matrixStackReflectionInstance: Any? = null
//    init {
//        try {
//            var clazz = Class.forName("com.chattriggers.ctjs.api.render.RenderUtils")
//            var instance = clazz.getField("INSTANCE").get(null)
//            if (instance == null) {
//                clazz = Class.forName("com.chattriggers.ctjs.api.render.Renderer")
//                instance = clazz.getField("INSTANCE").get(null)
//            }
//
//            var field = clazz.getDeclaredField("matrixStack")
//            field.isAccessible = true
//
//            matrixStackReflectionField = field
//            matrixStackReflectionInstance = instance
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    fun getMatrixStack(): UMatrixStack? {
//        return try {
//            matrixStackReflectionField?.get(matrixStackReflectionInstance) as? UMatrixStack
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }

//    private var partialTicksReflectionField: Field? = null
//    private var partialTicksReflectionInstance: Any? = null
//    init {
//        try {
//            var clazz = Class.forName("com.chattriggers.ctjs.api.render.GUIRenderer")
//            var instance = clazz.getField("INSTANCE").get(null)
//            if (instance == null) {
//                clazz = Class.forName("com.chattriggers.ctjs.api.render.Renderer")
//                instance = clazz.getField("INSTANCE").get(null)
//            }
//
//            var field = clazz.getDeclaredField("partialTicks")
//            field.isAccessible = true
//
//            partialTicksReflectionField = field
//            partialTicksReflectionInstance = instance
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    fun getPartialTicks(): Float {
//        try {
//            return partialTicksReflectionField?.get(partialTicksReflectionInstance) as Float
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return 0f
//    }

    @JvmField val BLACK = RGBAColor(0, 0, 0, 255).getLong()

    @JvmField val DARK_BLUE = RGBAColor(0, 0, 190, 255).getLong()

    @JvmField val DARK_GREEN = RGBAColor(0, 190, 0, 255).getLong()

    @JvmField val DARK_AQUA = RGBAColor(0, 190, 190, 255).getLong()

    @JvmField val DARK_RED = RGBAColor(190, 0, 0, 255).getLong()

    @JvmField val DARK_PURPLE = RGBAColor(190, 0, 190, 255).getLong()

    @JvmField val GOLD = RGBAColor(217, 163, 52, 255).getLong()

    @JvmField val GRAY = RGBAColor(190, 190, 190, 255).getLong()

    @JvmField val DARK_GRAY = RGBAColor(63, 63, 63, 255).getLong()

    @JvmField val BLUE = RGBAColor(63, 63, 254, 255).getLong()

    @JvmField val GREEN = RGBAColor(63, 254, 63, 255).getLong()

    @JvmField val AQUA = RGBAColor(63, 254, 254, 255).getLong()

    @JvmField val RED = RGBAColor(254, 63, 63, 255).getLong()

    @JvmField val LIGHT_PURPLE = RGBAColor(254, 63, 254, 255).getLong()

    @JvmField val YELLOW = RGBAColor(254, 254, 63, 255).getLong()

    @JvmField val WHITE = RGBAColor(255, 255, 255, 255).getLong()

    @JvmStatic
    fun color(color: Int): Long = when (color) {
        0 -> BLACK
        1 -> DARK_BLUE
        2 -> DARK_GREEN
        3 -> DARK_AQUA
        4 -> DARK_RED
        5 -> DARK_PURPLE
        6 -> GOLD
        7 -> GRAY
        8 -> DARK_GRAY
        9 -> BLUE
        10 -> GREEN
        11 -> AQUA
        12 -> RED
        13 -> LIGHT_PURPLE
        14 -> YELLOW
        else -> WHITE
    }

    @JvmStatic
    fun _beginRenderLayer(renderLayer: RenderLayer = RenderLayers.QUADS()) = apply {
        ucWorldRenderer.beginRenderLayer(renderLayer)
    }
    @JvmStatic
    fun _endVertex() = apply {
        ucWorldRenderer.endVertex()
    }

    /**
     * Begin drawing with the world renderer
     *
     * @param renderLayer The [RenderLayer] to use
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun begin(renderLayer: RenderLayer = RenderLayers.QUADS()) = apply {
        pushMatrix().blendFunc(
            BlendFunction(
                SourceFactor.SRC_ALPHA,
                DestFactor.ONE_MINUS_SRC_ALPHA,
                SourceFactor.ONE,
                DestFactor.ZERO
            )
        )

        colorized = null
        _beginRenderLayer(renderLayer)

        firstVertex = true
        began = true
    }

    /**
     * Begin drawing with the world renderer
     *
     * @param drawMode The [DrawMode] to use
     * @param vertexFormat The [VertexFormat] to use
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun begin(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
    ) = apply {
        RenderLayers.getRenderLayer(drawMode, vertexFormat)?.let { renderLayer ->
            begin(renderLayer)
        }
    }

    /**
     * Begin drawing with the world renderer
     *
     * @param drawMode The [DrawMode] to use
     * @param vertexFormat The [VertexFormat] to use
     * @param snippet The [RenderSnippet] to use
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun begin(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ) = apply {
        begin(PipelineBuilder.begin(drawMode, vertexFormat, snippet).layer())
    }

    /**
     * Sets a new vertex in the world renderer.
     *
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun pos(x: Float, y: Float, z: Float) = apply {
        if (!began) {
            begin()
        }
        if (!firstVertex) {
            _endVertex()
        }
        val camera = Client.getMinecraft().gameRenderer.camera.pos
        ucWorldRenderer.pos(matrixStack, x.toDouble() - camera.x, y.toDouble() - camera.y, z.toDouble() - camera.z)
        vertexColor?.let {
            color(vertexColor!!)
        }

        firstVertex = false
    }

    /**
     * Sets a new vertex in the world renderer.
     *
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    @JvmOverloads
    fun cameraPos(x: Float, y: Float, z: Float = 0f) = apply {
        val camera = Client.getMinecraft().gameRenderer.camera.pos
        pos(x + camera.x.toFloat(), y + camera.y.toFloat(), z + camera.z.toFloat())
    }

    @JvmStatic
    fun worldPos(x: Float, y: Float, z: Float) = apply {
        if (!began) {
            begin()
        }
        if (!firstVertex) {
            _endVertex()
        }
        ucWorldRenderer.pos(matrixStack, x.toDouble(), y.toDouble(), z.toDouble())
        vertexColor?.let {
            color(vertexColor!!)
        }

        firstVertex = false
    }

    /**
     * Sets the texture location on the last defined vertex.
     *
     * @param u the u position in the texture
     * @param v the v position in the texture
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun tex(u: Float, v: Float) = apply {
        ucWorldRenderer.tex(u.toDouble(), v.toDouble())
    }

    /**
     * Sets the color for the last defined vertex.
     *
     * @param r the red value of the color, between 0 and 1
     * @param g the green value of the color, between 0 and 1
     * @param b the blue value of the color, between 0 and 1
     * @param a the alpha value of the color, between 0 and 1
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    @JvmOverloads
    fun color(r: Float, g: Float, b: Float, a: Float = 1f) = apply {
        ucWorldRenderer.color(r, g, b, a)
    }

    /**
     * Sets the color for the last defined vertex.
     *
     * @param r the red value of the color, between 0 and 255
     * @param g the green value of the color, between 0 and 255
     * @param b the blue value of the color, between 0 and 255
     * @param a the alpha value of the color, between 0 and 255
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    @JvmOverloads
    fun color(r: Int, g: Int, b: Int, a: Int = 255) = apply {
        color(r / 255f, g / 255f, b / 255f, a / 255f)
    }

    /**
     * Sets the color for the last defined vertex.
     *
     * @param color the color value, can use [getColor] to get this
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun color(color: Long) = apply {
        val awtColor = Color(color.toInt(), true)
        val r = awtColor.red
        val g = awtColor.green
        val b = awtColor.blue
        val a = awtColor.alpha
        color(r, g, b, a)
    }

    @JvmStatic
    fun color(color: Color) = apply {
        color(color.red, color.green, color.blue, color.alpha)
    }

    /**
     * Sets the normal of the vertex. This is mostly used with [VertexFormat.LINES]
     *
     * @param x the x position of the normal vector
     * @param y the y position of the normal vector
     * @param z the z position of the normal vector
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun normal(x: Float, y: Float, z: Float) = apply {
        ucWorldRenderer.norm(matrixStack, x, y, z)
    }

    /**
     * Sets the overlay location on the last defined vertex.
     *
     * @param u the u position in the overlay
     * @param v the v position in the overlay
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun overlay(u: Int, v: Int) = apply {
        ucWorldRenderer.overlay(u, v)
    }

    /**
     * Sets the light location on the last defined vertex.
     *
     * @param u the u position in the light
     * @param v the v position in the light
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun light(u: Int, v: Int) = apply {
        ucWorldRenderer.light(u, v)
    }

    /**
     * Sets the line width when rendering [DrawMode.LINES]
     *
     * @param width the width of the line
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun lineWidth(width: Float) = apply {
        RenderSystem.lineWidth(width)
    }

    @JvmStatic
    fun resetLineWidth() = apply {
        lineWidth(1f)
    }

    /**
     * Finalizes vertices and draws the world renderer.
     */
    @JvmStatic
    fun draw() = apply {
        if (!began) return this
        began = false

        ucWorldRenderer.endVertex()
        ucWorldRenderer.drawDirect()

        colorize_01(1f, 1f, 1f, 1f)
            .disableBlend()
            .popMatrix()
    }

    @JvmStatic
    fun enableCull() = apply {
        PipelineBuilder.enableCull()
    }

    @JvmStatic
    fun disableCull() = apply {
        PipelineBuilder.disableCull()
    }

    @JvmStatic
    fun enableLighting() = apply {
        UGraphics.enableLighting()
    }

    @JvmStatic
    fun disableLighting() = apply {
        UGraphics.disableLighting()
    }

    @JvmStatic
    fun enableDepth() = apply {
        PipelineBuilder.enableDepth()
    }

    @JvmStatic
    fun disableDepth() = apply {
        PipelineBuilder.disableDepth()
    }

    @JvmStatic
    fun depthFunc(function: DepthTestFunction) = apply {
        PipelineBuilder.setDepthTestFunction(function)
    }

    @JvmStatic
    fun enableBlend() = apply {
        PipelineBuilder.enableBlend()
    }

    @JvmStatic
    fun disableBlend() = apply {
        PipelineBuilder.disableBlend()
    }

    @JvmStatic
    fun blendFunc(function: BlendFunction) = apply {
        PipelineBuilder.setBlendFunction(function)
    }

    @JvmStatic
    fun getSourceFactorFromInt(value: Int): SourceFactor {
        return when (value) {
            0 -> SourceFactor.ZERO
            1 -> SourceFactor.ONE
            768 -> SourceFactor.SRC_COLOR
            769 -> SourceFactor.ONE_MINUS_SRC_COLOR
            774 -> SourceFactor.DST_COLOR
            775 -> SourceFactor.ONE_MINUS_DST_COLOR
            32769 -> SourceFactor.CONSTANT_COLOR
            32770 -> SourceFactor.ONE_MINUS_CONSTANT_COLOR
            770 -> SourceFactor.SRC_ALPHA
            771 -> SourceFactor.ONE_MINUS_SRC_ALPHA
            772 -> SourceFactor.DST_ALPHA
            773 -> SourceFactor.ONE_MINUS_DST_ALPHA
            32771 -> SourceFactor.CONSTANT_ALPHA
            32772 -> SourceFactor.ONE_MINUS_CONSTANT_ALPHA
            776 -> SourceFactor.SRC_ALPHA_SATURATE
            else -> throw IllegalArgumentException("Invalid source factor value: $value")
        }
    }

    @JvmStatic
    fun getDestFactorFromInt(value: Int): DestFactor {
        return when (value) {
            0 -> DestFactor.ZERO
            1 -> DestFactor.ONE
            768 -> DestFactor.SRC_COLOR
            769 -> DestFactor.ONE_MINUS_SRC_COLOR
            774 -> DestFactor.DST_COLOR
            775 -> DestFactor.ONE_MINUS_DST_COLOR
            32769 -> DestFactor.CONSTANT_COLOR
            32770 -> DestFactor.ONE_MINUS_CONSTANT_COLOR
            770 -> DestFactor.SRC_ALPHA
            771 -> DestFactor.ONE_MINUS_SRC_ALPHA
            772 -> DestFactor.DST_ALPHA
            773 -> DestFactor.ONE_MINUS_DST_ALPHA
            32771 -> DestFactor.CONSTANT_ALPHA
            32772 -> DestFactor.ONE_MINUS_CONSTANT_ALPHA
//            776 -> DestFactor.SRC_ALPHA_SATURATE
            else -> throw IllegalArgumentException("Invalid source factor value: $value")
        }
    }

    @JvmStatic
    fun tryBlendFuncSeparate(
        sourceFactor: Int,
        destFactor: Int,
        sourceFactorAlpha: Int,
        destFactorAlpha: Int,
    ) = apply {
        val srcFactor = getSourceFactorFromInt(sourceFactor)
        val dstFactor = getDestFactorFromInt(destFactor)
        val srcFactorAlpha = getSourceFactorFromInt(sourceFactorAlpha)
        val dstFactorAlpha = getDestFactorFromInt(destFactorAlpha)

        blendFunc(BlendFunction(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha))
    }

    @JvmStatic
    fun tryBlendFuncSeparate(
        sourceFactor: SourceFactor,
        destFactor: DestFactor,
        sourceFactorAlpha: SourceFactor,
        destFactorAlpha: DestFactor,
    ) = apply {
        blendFunc(BlendFunction(sourceFactor, destFactor, sourceFactorAlpha, destFactorAlpha))
    }

    @JvmStatic
    @JvmOverloads
    fun bindTexture(textureImage: Image, textureIndex: Int = 0) = apply {
        UGraphics.bindTexture(textureIndex, textureImage.getTexture()?.image?.imageId()?.toInt() ?: 0)
    }

    @JvmStatic
    fun deleteTexture(texture: Image) = apply {
        GL11.glDeleteTextures(texture.getTexture()?.image?.imageId()?.toInt() ?: 0)
    }

    @JvmStatic
    //#if MC>=12106
    //$$fun setShaderTexture(textureIndex: Int, texture: GpuTextureView?) = apply {
    //#else
    fun setShaderTexture(textureIndex: Int, texture: GpuTexture?) = apply {
        //#endif
        RenderSystem.setShaderTexture(textureIndex, texture)
    }

    @JvmStatic
    fun setShaderTexture(textureIndex: Int, textureImage: Image) = apply {
        val gpuTexture = textureImage.getTexture()
        gpuTexture?.let {
            //#if MC>=12106
            //$$RenderSystem.setShaderTexture(textureIndex, gpuTexture.glTextureView)
            //#else
            RenderSystem.setShaderTexture(textureIndex, gpuTexture.glTexture)
            //#endif
        }
    }

    @JvmStatic
    fun pushMatrix(stack: UMatrixStack = matrixStack) = apply {
        matrixPushCounter++
        matrixStackStack.addLast(stack)
        matrixStack = stack
        stack.push()
    }

    @JvmStatic
    fun popMatrix() = apply {
        matrixPushCounter--
        matrixStackStack.removeLast()
        matrixStack.pop()
    }

    @JvmStatic
    @JvmOverloads
    fun translate(x: Float, y: Float, z: Float = 0.0F) = apply {
        matrixStack.translate(x, y, z)
    }

    @JvmStatic
    @JvmOverloads
    fun scale(scaleX: Float, scaleY: Float = scaleX, scaleZ: Float = 1f) = apply {
        matrixStack.scale(scaleX, scaleY, scaleZ)
    }

    @JvmStatic
    @JvmOverloads
    fun rotate(angle: Float, x: Float = 0f, y: Float = 0f, z: Float = 1f) = apply {
        matrixStack.rotate(angle, x, y, z)
    }

    @JvmStatic
    fun multiply(quaternion: Quaternionf) = apply {
        matrixStack.multiply(quaternion)
    }

    @JvmStatic
    fun colorizeRGBA(color: Long) = apply {
        val (r, g, b, a) = RGBAColor.fromLongRGBA(color)
        colorize_255(r, g, b, a)
    }

    @JvmStatic
    fun colorizeARGB(color: Long) = apply {
        val (a, r, g, b) = ARGBColor.fromLongARGB(color)
        colorize_255(r, g, b, a)
    }

    @JvmStatic
    @JvmOverloads
    fun colorize_01(r: Float, g: Float, b: Float, a: Float = 1f) = apply {
        val red = (r * 255f).coerceIn(0f, 255f).toInt()
        val green = (g * 255f).coerceIn(0f, 255f).toInt()
        val blue = (b * 255f).coerceIn(0f, 255f).toInt()
        val alpha = (a * 255f).coerceIn(0f, 255f).toInt()
        colorized = RGBAColor(red, green, blue, alpha).getLong()
        vertexColor = Color(red, green, blue, alpha)
    }

    @JvmStatic
    fun colorize_255(r: Int, g: Int, b: Int, a: Int = 255) = apply {
        RenderUtils.colorize_01(
            r.coerceIn(0, 255) / 255f,
            g.coerceIn(0, 255) / 255f,
            b.coerceIn(0, 255) / 255f,
            a.coerceIn(0, 255) / 255f
        )
    }

    @JvmStatic
    fun resetColor() = apply {
        RenderUtils.colorize_01(1f, 1f, 1f, 1f)
    }

    @JvmStatic
    fun getStringWidth(text: String) = getFontRenderer().getWidth(addColor(text))

    @JvmStatic
    @JvmOverloads
    fun getRainbowColors(step: Float, speed: Float = 1f): IntArray {
        val red = ((sin(step / speed) + 0.75) * 170).toInt()
        val green = ((sin(step / speed + 2 * PI / 3) + 0.75) * 170).toInt()
        val blue = ((sin(step / speed + 4 * PI / 3) + 0.75) * 170).toInt()
        return intArrayOf(red, green, blue)
    }

    @JvmStatic
    @JvmOverloads
    fun getRainbow(step: Float, speed: Float = 1f): Long {
        val (r, g, b) = getRainbowColors(step, speed)
        return RGBAColor(r, g, b).getLong()
    }

    /**
     * Calculate the box render parameter by providing 2 coordinates
     * @param {number} x1 - X Coordinates of first position
     * @param {number} y1 - Y Coordinates of first position
     * @param {number} z1 - Z Coordinates of first position
     * @param {number} x2 - X Coordinates of second position
     * @param {number} y2 - Y Coordinates of second position
     * @param {number} z2 - Z Coordinates of second position
     *
     * @returns {Object} An object containing center coordinates and dimensions.
     * - `cx`: The x-coordinate of the center of the box.
     * - `cy`: The y-coordinate of the center of the box, taken as the lower of the two y-coordinates provided.
     * - `cz`: The z-coordinate of the center of the box.
     * - `wx`: The width of the box in the x-direction.
     * - `h`: The height of the box.
     * - `wz`: The width of the box in the z-direction.
     */
    @JvmStatic
    fun calculateCenter(
        x1: Float,
        y1: Float,
        z1: Float,
        x2: Float,
        y2: Float,
        z2: Float,
    ) : Any {
        val cx = (x1 + x2) / 2
        val cy =  if (y1 > y2) y2 else y1
        val cz = (z1 + z2) / 2

        val wx = abs(x2 - x1)
        val h = abs(y2 - y1)
        val wz = abs(z2 - z1)

        return object {
            val cx = cx
            val cy = cy
            val cz = cz
            val wx = wx
            val h = h
            val wz = wz
        }
    }

    /**
     * Replaces the easier to type '&' color codes with proper color codes in a string.
     *
     * @param message The string to add color codes to
     * @return the formatted message
     */
    @JvmStatic
    fun addColor(message: String?): String {
        return message.toString().replace("(?<!\\\\)&(?![^0-9a-fk-or]|$)".toRegex(), "\u00a7")
    }

    data class TextLines(val lines: List<OrderedText>, val width: Float, val height: Float)

    @JvmStatic
    fun splitText(text: Text, maxWidth: Int): TextLines {
        val renderer = getFontRenderer()
        val wrappedLines = renderer.wrapLines(text, maxWidth)

        return TextLines(
            wrappedLines,
            wrappedLines.maxOf { getFontRenderer().getWidth(it) }.toFloat(),
            (getFontRenderer().fontHeight * wrappedLines.size + (wrappedLines.size - 1)).toFloat(),
        )
    }

    fun blendColorsRGBA(color1: Long, color2: Long): RGBAColor {
        return blendColorsRGBA(
            RGBAColor.fromLongRGBA(color1),
            RGBAColor.fromLongRGBA(color2)
        )
    }
    fun blendColorsRGBA(color1: RenderColor, color2: RenderColor): RGBAColor {
        val (r1, g1, b1, a1) = color1.getIntComponentsRGBA()
        val (r2, g2, b2, a2) = color2.getIntComponentsRGBA()
        return RGBAColor(
            (r1 + r2) / 2,
            (g1 + g2) / 2,
            (b1 + b2) / 2,
            (a1 + a2) / 2
        )
    }

    abstract class RenderColor {
        abstract val r: Int
        abstract val g: Int
        abstract val b: Int
        abstract val a: Int

        operator fun component1() = r
        operator fun component2() = g
        operator fun component3() = b
        operator fun component4() = a

        abstract fun getLong(): Long
        fun Long.toColorInt(): Int = (this and 0xFFFFFFFFL).toInt()

        fun getIntRGBA(): Int = getLongRGBA().toColorInt()
        fun getIntComponentsRGBA(): IntArray = intArrayOf(r, g, b, a)
        fun getLongRGBA(): Long {
            return ((r.coerceIn(0, 255) shl 24) or
                    (g.coerceIn(0, 255) shl 16) or
                    (b.coerceIn(0, 255) shl 8) or
                    a.coerceIn(0, 255)).toLong() and 0xFFFFFFFFL
        }
        fun getRGBA(): FloatArray = floatArrayOf(
            r.coerceIn(0, 255) / 255f,
            g.coerceIn(0, 255) / 255f,
            b.coerceIn(0, 255) / 255f,
            a.coerceIn(0, 255) / 255f,
        )

        fun getIntARGB(): Int = getLongARGB().toColorInt()
        fun getIntComponentsARGB(): IntArray = intArrayOf(a, r, g, b)
        fun getLongARGB(): Long {
            return ((a.coerceIn(0, 255) shl 24) or
                    (r.coerceIn(0, 255) shl 16) or
                    (g.coerceIn(0, 255) shl 8) or
                    b.coerceIn(0, 255)).toLong() and 0xFFFFFFFFL
        }
        fun getARGB(): FloatArray = floatArrayOf(
            a.coerceIn(0, 255) / 255f,
            r.coerceIn(0, 255) / 255f,
            g.coerceIn(0, 255) / 255f,
            b.coerceIn(0, 255) / 255f,
        )

        fun getHex(): String {
            val hex = StringBuilder("")
            hex.append(r.coerceIn(0, 255).toString(16).padStart(2, '0'))
            hex.append(g.coerceIn(0, 255).toString(16).padStart(2, '0'))
            hex.append(b.coerceIn(0, 255).toString(16).padStart(2, '0'))
            return hex.toString().uppercase()
        }

        override fun toString(): String = "Color(r=$r, g=$g, b=$b, a=$a)"

        companion object {
            internal fun parseHex(hex: String): IntArray {
                val clean = hex.removePrefix("#")
                val len = if (clean.length == 8) 8 else 6
                val padded = clean.padStart(len, '0')
                val r = Integer.parseInt(padded.substring(0, 2), 16)
                val g = Integer.parseInt(padded.substring(2, 4), 16)
                val b = Integer.parseInt(padded.substring(4, 6), 16)
                val a = if (len == 8) Integer.parseInt(padded.substring(6, 8), 16) else 255
                return intArrayOf(r, g, b, a)
            }

            internal fun parseLongRGBA(color: Long): IntArray {
                val intColor = color.toInt()
                val r = (intColor shr 24) and 0xFF
                val g = (intColor shr 16) and 0xFF
                val b = (intColor shr 8) and 0xFF
                val a = intColor and 0xFF
                return intArrayOf(r, g, b, a)
            }
            internal fun parseLongARGB(color: Long): IntArray {
                val (r, g, b, a) = parseLongRGBA(color)
                return intArrayOf(a, r, g, b)
            }
        }
    }

    class ARGBColor(
        override val r: Int,
        override val g: Int,
        override val b: Int,
        override val a: Int = 255
    ) : RenderColor() {
        override fun getLong(): Long = getLongARGB()

        companion object {
            @JvmStatic
            fun fromHex(hex: String): ARGBColor {
                val (r, g, b, a) = parseHex(hex)
                return ARGBColor(r, g, b, a)
            }

            @JvmStatic
            fun fromLongRGBA(color: Long): ARGBColor {
                val (r, g, b, a) = parseLongRGBA(color)
                return ARGBColor(r, g, b, a)
            }
            @JvmStatic
            fun fromLongARGB(color: Long): ARGBColor {
                val (r, g, b, a) = parseLongARGB(color)
                return ARGBColor(r, g, b, a)
            }
        }
    }

    class RGBAColor(
        override val r: Int,
        override val g: Int,
        override val b: Int,
        override val a: Int = 255
    ) : RenderColor() {
        override fun getLong(): Long = getLongRGBA()

        companion object {
            @JvmStatic
            fun fromHex(hex: String): RGBAColor {
                val (r, g, b, a) = parseHex(hex)
                return RGBAColor(r, g, b, a)
            }

            @JvmStatic
            fun fromLongRGBA(color: Long): RGBAColor {
                val (r, g, b, a) = parseLongRGBA(color)
                return RGBAColor(r, g, b, a)
            }
            @JvmStatic
            fun fromLongARGB(color: Long): RGBAColor {
                val (r, g, b, a) = parseLongARGB(color)
                return RGBAColor(r, g, b, a)
            }
        }
    }
}
//#endif
