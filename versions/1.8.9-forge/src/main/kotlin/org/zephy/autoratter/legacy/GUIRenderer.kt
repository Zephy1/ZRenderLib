package org.zephy.zrenderlib.legacy

import gg.essential.universal.UMinecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.util.*
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object GUIRenderer {
    @JvmField
    val screen = ScreenWrapper()

    /**
     * Draws a square to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param size the size of the square
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param zOffset useless in legacy, included for modern parity
     */
    @JvmStatic
    @JvmOverloads
    fun drawSquareRGBA(
        xPosition: Float,
        yPosition: Float,
        size: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        zOffset: Float = 0f,
    ) {
        drawRect(xPosition, yPosition, size, size, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), zOffset)
    }

    /**
     * Draws a square to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param size the size of the square
     * @param color the color as a [Long] value in RGBA format
     * @param zOffset useless in legacy, included for modern parity
     */
    @JvmStatic
    @JvmOverloads
    fun drawSquare(
        xPosition: Float,
        yPosition: Float,
        size: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        zOffset: Float = 0f,
    ) {
        drawRect(xPosition, yPosition, size, size, color, zOffset)
    }

    /**
     * Draws a rectangle to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param zOffset useless in legacy, included for modern parity
     */
    @JvmStatic
    @JvmOverloads
    fun drawRectRGBA(
        xPosition: Float,
        yPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        zOffset: Float = 0f,
    ) {
        drawRect(xPosition, yPosition, width, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), zOffset)
    }

    /**
     * Draws a rectangle to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param color the color as a [Long] value in RGBA format
     * @param zOffset useless in legacy, included for modern parity
     */
    @JvmStatic
    @JvmOverloads
    fun drawRect(
        xPosition: Float,
        yPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        zOffset: Float = 0f,
    ) {
        val pos = mutableListOf(xPosition, yPosition, xPosition + width, yPosition + height)
        if (pos[0] > pos[2]) Collections.swap(pos, 0, 2)
        if (pos[1] > pos[3]) Collections.swap(pos, 1, 3)

        RenderUtils
            .pushMatrix()
            .resetColor()
            .disableCull()
            .enableBlend()
            .tryBlendFuncSeparate(770, 771, 1, 0)
            .depthMask(false)
            .disableTexture2D()
            .enableLineSmooth()

            .begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
            .colorizeRGBA(color)
            .pos(pos[0], pos[3], 0f)
            .pos(pos[2], pos[3], 0f)
            .pos(pos[2], pos[1], 0f)
            .pos(pos[0], pos[1], 0f)
            .draw()

            .resetColor()
            .enableCull()
            .disableBlend()
            .depthMask(true)
            .enableTexture2D()
            .disableLineSmooth()
            .resetLineWidth()
            .enableDepth()
            .popMatrix()
    }

    /**
     * Draws a line on the screen from point (startX, startY) to (endX, endY)
     *
     * @param startX the starting X-coordinate
     * @param startY the starting Y-coordinate
     * @param endX the ending X-coordinate
     * @param endY the ending Y-coordinate
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param lineThickness the thickness of the line
     */
    @JvmStatic
    @JvmOverloads
    fun drawLineRGBA(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        lineThickness: Float = 1f,
    ) {
        drawLine(startX, startY, endX, endY, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), lineThickness)
    }

    /**
     * Draws a line on the screen from point (startX, startY) to (endX, endY)
     *
     * @param startX the starting X-coordinate
     * @param startY the starting Y-coordinate
     * @param endX the ending X-coordinate
     * @param endY the ending Y-coordinate
     * @param color the color as a [Long] value in RGBA format
     * @param lineThickness the thickness of the line
     */
    @JvmStatic
    @JvmOverloads
    fun drawLine(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        lineThickness: Float = 1f,
    ) {
        val theta = -atan2(endY - startY, endX - startX)
        val i = sin(theta) * (lineThickness / 2)
        val j = cos(theta) * (lineThickness / 2)

        RenderUtils
            .pushMatrix()
            .resetColor()
            .disableCull()
            .enableBlend()
            .tryBlendFuncSeparate(770, 771, 1, 0)
            .depthMask(false)
            .disableTexture2D()
            .enableLineSmooth()

            .begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
            .colorizeRGBA(color)
            .pos(startX + i, startY + j, 0f)
            .pos(endX + i, endY + j, 0f)
            .pos(endX - i, endY - j, 0f)
            .pos(startX - i, startY - j, 0f)
            .draw()

            .resetColor()
            .enableCull()
            .disableBlend()
            .depthMask(true)
            .enableTexture2D()
            .disableLineSmooth()
            .resetLineWidth()
            .enableDepth()
            .popMatrix()
    }

    /**
     * Draws a circle to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param radius the radius of the circle
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param edges the number of edges
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleCircleRGBA(
        xPosition: Float,
        yPosition: Float,
        radius: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        edges: Int = 32,
    ) {
        drawCircle(xPosition, yPosition, radius, radius, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), edges, 0f, 0f, 0f)
    }

    /**
     * Draws a circle to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param radius the radius of the circle
     * @param color the color as a [Long] value in RGBA format
     * @param edges the number of edges
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleCircle(
        xPosition: Float,
        yPosition: Float,
        radius: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        edges: Int = 32,
    ) {
        drawCircle(xPosition, yPosition, radius, radius, color, edges, 0f, 0f, 0f)
    }

    /**
     * Draws a circle to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param xScale the X-radius of the circle
     * @param yScale the Y-radius of the circle
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param edges the number of edges
     * @param rotationDegrees number of degrees to rotate the circle on the Z-axis
     * @param xRotationOffset the X-offset for the rotation
     * @param yRotationOffset the Y-offset for the rotation
     */
    @JvmStatic
    @JvmOverloads
    fun drawCircleRGBA(
        xPosition: Float,
        yPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        edges: Int = 32,
        rotationDegrees: Float = 0f,
        xRotationOffset: Float = 0f,
        yRotationOffset: Float = 0f,
    ) {
        drawCircle(xPosition, yPosition, xScale, yScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), edges, rotationDegrees, xRotationOffset, yRotationOffset)
    }

    /**
     * Draws a circle to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param xScale the X-radius of the circle
     * @param yScale the Y-radius of the circle
     * @param color the color as a [Long] value in RGBA format
     * @param edges the number of edges
     * @param rotationDegrees number of degrees to rotate the circle on the Z-axis
     * @param xRotationOffset the X-offset for the rotation
     * @param yRotationOffset the Y-offset for the rotation
     */
    @JvmStatic
    @JvmOverloads
    fun drawCircle(
        xPosition: Float,
        yPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        edges: Int = 32,
        rotationDegrees: Float = 0f,
        xRotationOffset: Float = 0f,
        yRotationOffset: Float = 0f,
    ) {
        val theta = 2 * PI / edges
        val cos = cos(theta).toFloat()
        val sin = sin(theta).toFloat()

        var xHolder: Float
        var circleX = 1f
        var circleY = 0f

        // rotation from circle's center
        RenderUtils
            .pushMatrix()
            .resetColor()
            .disableCull()
            .enableBlend()
            .tryBlendFuncSeparate(770, 771, 1, 0)
            .depthMask(false)
            .disableTexture2D()
            .enableLineSmooth()
            .translate(xPosition + xRotationOffset, yPosition + yRotationOffset, 0f)
            .rotate(rotationDegrees % 360, 0f, 0f, 1f)
            .translate(-xPosition + -xRotationOffset, -yPosition + -yRotationOffset, 0f)
            .begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION)
            .colorizeRGBA(color)

        for (i in 0..edges) {
            RenderUtils
                .pos(xPosition, yPosition, 0f)
                .pos(circleX * xScale + xPosition, circleY * yScale + yPosition, 0f)
            xHolder = circleX
            circleX = cos * circleX - sin * circleY
            circleY = sin * xHolder + cos * circleY

            RenderUtils.pos(circleX * xScale + xPosition, circleY * yScale + yPosition, 0f)
        }

        RenderUtils
            .draw()
            .resetColor()
            .enableCull()
            .disableBlend()
            .depthMask(true)
            .enableTexture2D()
            .disableLineSmooth()
            .resetLineWidth()
            .enableDepth()
            .popMatrix()
    }

    /**
     * Draws text with a shadow to the screen
     *
     * @param text the text as a string
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param textScale the text scale
     * @param renderBackground whether to draw a transparent background
     * @param maxWidth useless in legacy, included for modern parity
     */
    @JvmStatic
    @JvmOverloads
    fun drawStringWithShadowRGBA(
        text: String,
        xPosition: Float,
        yPosition: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        maxWidth: Int = 512,
    ) {
        drawString(text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, true, maxWidth)
    }

    /**
     * Draws text with a shadow to the screen
     *
     * @param text the text as a string
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param color the color as a [Long] value in RGBA format
     * @param textScale the text scale
     * @param renderBackground whether to draw a transparent background
     * @param maxWidth useless in legacy, included for modern parity
     */
    @JvmStatic
    @JvmOverloads
    fun drawStringWithShadow(
        text: String,
        xPosition: Float,
        yPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        maxWidth: Int = 512,
    ) {
        drawString(text, xPosition, yPosition, color, textScale, renderBackground, true, maxWidth)
    }

    /**
     * Draws text to the screen
     *
     * @param text the text as a string
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param textScale the text scale
     * @param renderBackground whether to draw a transparent background
     * @param textShadow whether to draw a shadow behind the text
     * @param maxWidth useless in legacy, included for modern parity
     */
    @JvmStatic
    @JvmOverloads
    fun drawStringRGBA(
        text: String,
        xPosition: Float,
        yPosition: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        textShadow: Boolean = false,
        maxWidth: Int = 512,
    ) {
        drawString(text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, textShadow, maxWidth)
    }

    /**
     * Draws text to the screen
     *
     * @param text the text as a string
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param color the color as a [Long] value in RGBA format
     * @param textScale the text scale
     * @param renderBackground whether to draw a transparent background
     * @param textShadow whether to draw a shadow behind the text
     * @param maxWidth useless in legacy, included for modern parity
     */
    @JvmStatic
    @JvmOverloads
    fun drawString(
        text: String,
        xPosition: Float,
        yPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        textShadow: Boolean = false,
        maxWidth: Int = 512,
    ) {
        val fontRenderer = UMinecraft.getFontRenderer()
        var newY = yPosition

        RenderUtils.pushMatrix()
        RenderUtils.addColor(text).split("\n").forEach {
            fontRenderer.drawString(it, xPosition, newY, RenderUtils.RGBAColor.fromLongRGBA(color).getLongARGB().toInt(), textShadow)
            newY += fontRenderer.FONT_HEIGHT
        }
        RenderUtils.popMatrix()
    }

    /**
     * Draws an image to the screen
     *
     * @param image the image
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param width new image width
     * @param height new image height
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     */
    @JvmStatic
    @JvmOverloads
    fun drawImageRGBA(
        image: Image,
        xPosition: Float,
        yPosition: Float,
        width: Float? = null,
        height: Float? = null,
        zOffset: Float = 0f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
    ) {
        drawImage(image, xPosition, yPosition, width, height, zOffset, RenderUtils.RGBAColor(red, green, blue, alpha).getLong())
    }

    /**
     * Draws an image to the screen
     *
     * @param image the image
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param width new image width
     * @param height new image height
     */
    @JvmStatic
    @JvmOverloads
    fun drawImage(
        image: Image,
        xPosition: Float,
        yPosition: Float,
        width: Float? = null,
        height: Float? = null,
        zOffset: Float = 0f,
        color: Long = RenderUtils.WHITE,
    ) {
        val (drawWidth, drawHeight) = image.getImageSize(width, height)

        RenderUtils
            .pushMatrix()
            .resetColor()
            .disableCull()
            .enableBlend()
            .translate(0f, 0f, zOffset)
            .scale(1f, 1f, 50f)
            .bindTexture(image.getTexture().getGlTextureId())
            .enableTexture2D()

            .begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            .colorizeRGBA(color)
            .pos(xPosition, yPosition + drawHeight, 0f, false).tex(0f, 1f)
            .pos(xPosition + drawWidth, yPosition + drawHeight, 0f, false).tex(1f, 1f)
            .pos(xPosition + drawWidth, yPosition, 0f, false).tex(1f, 0f)
            .pos(xPosition, yPosition, 0f, false).tex(0f, 0f)
            .draw()

            .resetColor()
            .disableBlend()
            .disableTexture2D()
            .popMatrix()
    }

//    internal fun withMatrix(stack: MatrixStack?, partialTicks: Float = GUIRenderer.partialTicks, block: () -> Unit) {
//        GUIRenderer.partialTicks = partialTicks
//        RenderUtils.matrixPushCounter = 0
//
//        try {
//            if (stack != null) RenderUtils.pushMatrix(UMatrixStack(stack))
//            block()
//        } finally {
//            if (stack != null) RenderUtils.popMatrix()
//        }
//
//        if (RenderUtils.matrixPushCounter > 0) {
//            println("Warning: Render function missing a call to RenderUtils.popMatrix()")
//        } else if (RenderUtils.matrixPushCounter < 0) {
//            println("Warning: Render function has too many calls to RenderUtils.popMatrix()")
//        }
//    }

    class ScreenWrapper {
        fun getWidth(): Int = ScaledResolution(UMinecraft.getMinecraft()).scaledWidth
    
        fun getHeight(): Int = ScaledResolution(UMinecraft.getMinecraft()).scaledHeight
    
        fun getScale(): Double = ScaledResolution(UMinecraft.getMinecraft()).scaleFactor.toDouble()
    }
}