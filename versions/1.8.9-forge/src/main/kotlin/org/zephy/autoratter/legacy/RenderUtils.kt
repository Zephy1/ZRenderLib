package org.zephy.zrenderlib.legacy

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.WorldRenderer
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexFormat
import org.lwjgl.opengl.GL11
import gg.essential.universal.UMinecraft
import java.lang.reflect.Field
import javax.vecmath.Vector3d
import kotlin.math.abs

object RenderUtils {
    @JvmStatic
    val mc: Minecraft = UMinecraft.getMinecraft()

    @JvmStatic
    val renderManager: RenderManager = mc.renderManager

    @JvmStatic
    val tessellator: Tessellator = Tessellator.getInstance()

    @JvmStatic
    val worldRenderer: WorldRenderer? = tessellator.worldRenderer

    @JvmField
    var colorized: Long? = null

    @JvmStatic
    fun getFontRenderer() = UMinecraft.getFontRenderer()

    @JvmStatic
    fun getStringWidth(text: String) = getFontRenderer().getStringWidth(addColor(text))

    private var partialTicksReflectionField: Field? = null
    private var partialTicksReflectionInstance: Any? = null
    init {
        try {
            var clazz = Class.forName("com.chattriggers.ctjs.api.render.GUIRenderer")
            var instance = clazz.getField("INSTANCE").get(null)
            if (instance == null) {
                clazz = Class.forName("com.chattriggers.ctjs.api.render.Renderer")
                instance = clazz.getField("INSTANCE").get(null)
            }

            var field = clazz.getDeclaredField("partialTicks")
            field.isAccessible = true

            partialTicksReflectionField = field
            partialTicksReflectionInstance = instance
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getPartialTicks(): Float {
        try {
            return partialTicksReflectionField?.get(partialTicksReflectionInstance) as Float
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0f
    }

    private var getRenderXMethod: java.lang.reflect.Method? = null
    private var getRenderYMethod: java.lang.reflect.Method? = null
    private var getRenderZMethod: java.lang.reflect.Method? = null

    init {
        var xMethod: java.lang.reflect.Method? = null
        var yMethod: java.lang.reflect.Method? = null
        var zMethod: java.lang.reflect.Method? = null

        try {
            val clazz = Class.forName("com.chattriggers.ctjs.minecraft.wrappers.Player")
            xMethod = clazz.getMethod("getRenderX")
            yMethod = clazz.getMethod("getRenderY")
            zMethod = clazz.getMethod("getRenderZ")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        getRenderXMethod = xMethod
        getRenderYMethod = yMethod
        getRenderZMethod = zMethod
    }

    fun getRenderPos() : Vector3d {
        return try {
            Vector3d(
                getRenderXMethod?.invoke(null) as Double,
                getRenderYMethod?.invoke(null) as Double,
                getRenderZMethod?.invoke(null) as Double
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Vector3d(0.0, 0.0, 0.0)
        }
    }

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
    fun addColor(message: String?): String {
        return message.toString().replace("(?<!\\\\)&(?![^0-9a-fk-or]|$)".toRegex(), "\u00a7")
    }

    @JvmStatic
    fun getCameraPos(): Vector3d {
        return Vector3d(
            renderManager.viewerPosX,
            renderManager.viewerPosY,
            renderManager.viewerPosZ
        )
    }

    @JvmStatic
    fun pushMatrix() = apply {
        GlStateManager.pushMatrix()
    }

//    @JvmStatic
//    @JvmOverloads
//    fun getColor(red: Int, green: Int, blue: Int, alpha: Int = 255): Long {
//        return ((alpha.coerceIn(0, 255) shl 24) or
//                (red.coerceIn(0, 255) shl 16) or
//                (green.coerceIn(0, 255) shl 8) or
//                blue.coerceIn(0, 255)).toLong()
//    }
//
//    @JvmStatic
//    @JvmOverloads
//    fun getColor(red: Float, green: Float, blue: Float, alpha: Float = 255f): Long = getColor(
//        red.toInt(),
//        green.toInt(),
//        blue.toInt(),
//        alpha.toInt(),
//    )
//
//    @JvmStatic
//    @JvmOverloads
//    fun getColor0_1(r: Float, g: Float, b: Float, a: Float = 1f): Long {
//        val ri = (r.coerceIn(0f, 1f) * 255).toInt()
//        val gi = (g.coerceIn(0f, 1f) * 255).toInt()
//        val bi = (b.coerceIn(0f, 1f) * 255).toInt()
//        val ai = (a.coerceIn(0f, 1f) * 255).toInt()
//
//        val colorInt = ((ai and 0xFF) shl 24) or
//                ((ri and 0xFF) shl 16) or
//                ((gi and 0xFF) shl 8) or
//                (bi and 0xFF)
//
//        return colorInt.toLong() and 0xFFFFFFFFL
//    }
//
//    @JvmStatic
//    fun getColorRGBA(color: Long): FloatArray {
//        val intColor = color.toInt()
//        val r = ((intColor shr 24) and 0xFF).toFloat() / 255f
//        val g = ((intColor shr 16) and 0xFF).toFloat() / 255f
//        val b = ((intColor shr 8) and 0xFF).toFloat() / 255f
//        val a = (intColor and 0xFF).toFloat() / 255f
//
//        return floatArrayOf(
//            r.coerceIn(0f, 1f),
//            g.coerceIn(0f, 1f),
//            b.coerceIn(0f, 1f),
//            a.coerceIn(0f, 1f),
//        )
//    }
//
//    @JvmStatic
//    fun getARGBColorFromRGBAColor(color: Long): Long {
//        val (r, g, b, a) = getColorRGBA(color)
//        return getColor0_1(a, r, g, b)
//    }

    @JvmStatic
    fun popMatrix() = apply {
        GlStateManager.popMatrix()
    }

    @JvmStatic
    fun lineWidth(width: Float) = apply {
        GL11.glLineWidth(width)
    }

    @JvmStatic
    fun resetLineWidth() = apply {
        lineWidth(1f)
    }

    @JvmStatic
    fun enableCull() = apply {
        GlStateManager.enableCull()
    }

    @JvmStatic
    fun disableCull() = apply {
        GlStateManager.disableCull()
    }

    @JvmStatic
    fun enableBlend() = apply {
        GlStateManager.enableBlend()
    }

    @JvmStatic
    fun disableBlend() = apply {
        GlStateManager.disableBlend()
    }

    @JvmStatic
    fun enableDepth() = apply {
        GlStateManager.enableDepth()
    }

    @JvmStatic
    fun disableDepth() = apply {
        GlStateManager.disableDepth()
    }

    @JvmStatic
    fun enableLighting() = apply {
        GlStateManager.enableLighting()
    }

    @JvmStatic
    fun disableLighting() = apply {
        GlStateManager.disableLighting()
    }

    @JvmStatic
    fun blendFunc(srcFactor: Int, dstFactor: Int) = apply {
        GlStateManager.blendFunc(srcFactor, dstFactor)
    }

    @JvmStatic
    fun tryBlendFuncSeparate(
        srcRGB: Int,
        dstRGB: Int,
        srcAlpha: Int,
        dstAlpha: Int
    ) = apply {
        GlStateManager.tryBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha)
    }

    @JvmStatic
    fun bindTexture(textureId: Int) = apply {
        GlStateManager.bindTexture(textureId)
    }

    @JvmStatic
    fun depthMask(mask: Boolean) = apply {
        GlStateManager.depthMask(mask)
    }

    @JvmStatic
    fun enableTexture2D() = apply {
        GlStateManager.enableTexture2D()
    }

    @JvmStatic
    fun disableTexture2D() = apply {
        GlStateManager.disableTexture2D()
    }

    @JvmStatic
    fun enableLineSmooth() = apply {
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
    }

    @JvmStatic
    fun disableLineSmooth() = apply {
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }

    @JvmStatic
    fun translate(x: Float, y: Float, z: Float) = apply {
        translate(x.toDouble(), y.toDouble(), z.toDouble())
    }

    @JvmStatic
    fun translate(x: Double, y: Double, z: Double) = apply {
        GlStateManager.translate(x, y, z)
    }

    @JvmStatic
    fun rotate(x: Float, y: Float, z: Float, w: Float) = apply {
        GlStateManager.rotate(x, y, z, w)
    }

    @JvmStatic
    @JvmOverloads
    fun scale(x: Float, y: Float = x, z: Float = x) = apply {
        GlStateManager.scale(x, y, z)
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
        GlStateManager.color(r, g, b, a)

        val red = (r * 255f).coerceIn(0f, 255f).toInt()
        val green = (g * 255f).coerceIn(0f, 255f).toInt()
        val blue = (b * 255f).coerceIn(0f, 255f).toInt()
        val alpha = (a * 255f).coerceIn(0f, 255f).toInt()
        colorized = RGBAColor(red, green, blue, alpha).getLong()
    }

    @JvmStatic
    fun colorize_255(r: Int, g: Int, b: Int, a: Int = 255) = apply {
        colorize_01(
            r.coerceIn(0, 255) / 255f,
            g.coerceIn(0, 255) / 255f,
            b.coerceIn(0, 255) / 255f,
            a.coerceIn(0, 255) / 255f
        )
    }

    @JvmStatic
    fun resetColor() = apply {
        colorize_01(1f, 1f, 1f, 1f)
    }

    @JvmStatic
    @JvmOverloads
    fun begin(
        drawMode: Int = GL11.GL_QUADS,
        vertexFormat: VertexFormat = DefaultVertexFormats.POSITION,
    ) = apply {
        colorized = null
        worldRenderer?.let {
            it.begin(drawMode, vertexFormat)
        }
    }

    @JvmStatic
    fun pos(x: Double, y: Double, z: Double, endVertex: Boolean = true) = apply {
        val vertex = worldRenderer?.pos(x, y, z)
        if (endVertex) vertex?.endVertex()
    }

    @JvmStatic
    fun pos(x: Float, y: Float, z: Float, endVertex: Boolean = true) = apply {
        pos(x.toDouble(), y.toDouble(), z.toDouble(), endVertex)
    }

    @JvmStatic
    fun tex(x: Double, y: Double, endVertex: Boolean = true) = apply {
        val vertex = worldRenderer?.tex(x, y)
        if (endVertex) vertex?.endVertex()
    }

    @JvmStatic
    fun tex(x: Float, y: Float, endVertex: Boolean = true) = apply {
        tex(x.toDouble(), y.toDouble(), endVertex)
    }

    @JvmStatic
    fun draw() = apply {
        tessellator.draw()
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
        fun getLongRGBA(): Long {
            return ((r.coerceIn(0, 255) shl 24) or
                    (g.coerceIn(0, 255) shl 16) or
                    (b.coerceIn(0, 255) shl 8) or
                    a.coerceIn(0, 255)).toLong() and 0xFFFFFFFFL
        }
        fun getLongARGB(): Long{
            return ((a.coerceIn(0, 255) shl 24) or
                    (r.coerceIn(0, 255) shl 16) or
                    (g.coerceIn(0, 255) shl 8) or
                    b.coerceIn(0, 255)).toLong() and 0xFFFFFFFFL
        }

        fun getRGBA(): FloatArray = floatArrayOf(
            r.coerceIn(0, 255) / 255f,
            g.coerceIn(0, 255) / 255f,
            b.coerceIn(0, 255) / 255f,
            a.coerceIn(0, 255) / 255f
        )

        fun getHex(): String {
            val hex = StringBuilder("")
            hex.append(r.coerceIn(0, 255).toString(16).padStart(2, '0'))
            hex.append(g.coerceIn(0, 255).toString(16).padStart(2, '0'))
            hex.append(b.coerceIn(0, 255).toString(16).padStart(2, '0'))
            return hex.toString().uppercase()
        }

        fun getIntComponents(): IntArray = intArrayOf(r, g, b, a)

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
                val intColor = color.toInt()
                val a = (intColor shr 24) and 0xFF
                val r = (intColor shr 16) and 0xFF
                val g = (intColor shr 8) and 0xFF
                val b = intColor and 0xFF
                return intArrayOf(r, g, b, a)
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
