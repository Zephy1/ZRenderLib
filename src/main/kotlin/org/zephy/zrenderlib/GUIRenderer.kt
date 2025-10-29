package org.zephy.zrenderlib

//#if MC>12100
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import java.awt.Color
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object GUIRenderer {
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
     * @param zOffset the Z-offset for the rectangle
     */
    @JvmStatic
    @JvmOverloads
    fun drawSquareRGBA(drawContext: DrawContext, xPosition: Float, yPosition: Float, size: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, zOffset: Float = 0f) {
        drawRect(drawContext, xPosition, yPosition, size, size, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), zOffset)
    }

    /**
     * Draws a square to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param size the size of the square
     * @param color the color as a [Long] value in RGBA format
     * @param zOffset the Z-offset for the rectangle
     */
    @JvmStatic
    @JvmOverloads
    fun drawSquare(drawContext: DrawContext, xPosition: Float, yPosition: Float, size: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, zOffset: Float = 0f) {
        drawRect(drawContext, xPosition, yPosition, size, size, color, zOffset)
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
     * @param zOffset the Z-offset for the rectangle
     */
    @JvmStatic
    @JvmOverloads
    fun drawRectRGBA(drawContext: DrawContext, xPosition: Float, yPosition: Float, width: Float = 1f, height: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, zOffset: Float = 0f) {
        drawRect(drawContext, xPosition, yPosition, width, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), zOffset)
    }

    /**
     * Draws a rectangle to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param color the color as a [Long] value in RGBA format
     * @param zOffset the Z-offset for the rectangle
     */
    @JvmStatic
    @JvmOverloads
    fun drawRect(
        drawContext: DrawContext,
        xPosition: Float,
        yPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        zOffset: Float = 0f,
    ) {
//        !! fix with drawcontext
        val x1 = xPosition
        val x2 = xPosition + width
        val y1 = yPosition
        val y2 = yPosition + height

        RenderUtils
            .guiStartDraw()

            .begin(RenderLayers.QUADS())
            .colorizeRGBA(color)
            .translate(0f, 0f, zOffset)
            .cameraPos(x1, y2, 0f)
            .cameraPos(x2, y2, 0f)
            .cameraPos(x2, y1, 0f)
            .cameraPos(x1, y1, 0f)
            .draw()

            .guiEndDraw()
    }

    @JvmStatic
    @JvmOverloads
    fun drawRoundedRectRGBA(drawContext: DrawContext, xPosition: Float, yPosition: Float, width: Float, height: Float, radius: Float = 4f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, flatCorners: List<RenderUtils.FlattenRoundedRectCorner> = emptyList(), segments: Int = 16, zOffset: Float = 0f) {
        drawRoundedRect(drawContext, xPosition, yPosition, width, height, radius, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), flatCorners, segments, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawRoundedRect(
        drawContext: DrawContext,
        xPosition: Float,
        yPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        radius: Float = 4f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        flatCorners: List<RenderUtils.FlattenRoundedRectCorner> = emptyList(),
        segments: Int = 16,
        zOffset: Float = 0f,
    ) {
//        !! fix with draw context
        val x1 = xPosition
        val y1 = yPosition
        val x2 = xPosition + width
        val y2 = yPosition + height

        val centerX = (x1 + x2) / 2f
        val centerY = (y1 + y2) / 2f
        val clampedRadius = radius.coerceAtMost(minOf(width, height) / 2f)
        val flatCornersSet = flatCorners.toSet()

        RenderUtils
            .guiStartDraw()

            .begin(RenderLayers.TRIANGLE_FAN())
            .colorizeRGBA(color)
            .translate(0f, 0f, zOffset)
            .cameraPos(centerX, centerY, 0f)

        RenderUtils.cameraPos(x2 - clampedRadius, y1, 0f)
        if (RenderUtils.FlattenRoundedRectCorner.TOP_RIGHT in flatCornersSet) {
            RenderUtils.cameraPos(x2, y1, 0f)
        } else {
            addCornerVertices(drawContext, x2 - clampedRadius, y1 + clampedRadius, clampedRadius, 270f, 360f, segments)
            RenderUtils.cameraPos(x2, y1 + clampedRadius, 0f)
        }

        // right edge
        RenderUtils.cameraPos(x2, y2 - clampedRadius, 0f)

        if (RenderUtils.FlattenRoundedRectCorner.BOTTOM_RIGHT in flatCornersSet) {
            RenderUtils.cameraPos(x2, y2, 0f)
        } else {
            addCornerVertices(drawContext, x2 - clampedRadius, y2 - clampedRadius, clampedRadius, 0f, 90f, segments)
            RenderUtils.cameraPos(x2 - clampedRadius, y2, 0f)
        }

        // bottom edge
        RenderUtils.cameraPos(x1 + clampedRadius, y2, 0f)

        if (RenderUtils.FlattenRoundedRectCorner.BOTTOM_LEFT in flatCornersSet) {
            RenderUtils.cameraPos(x1, y2, 0f)
        } else {
            addCornerVertices(drawContext, x1 + clampedRadius, y2 - clampedRadius, clampedRadius, 90f, 180f, segments)
            RenderUtils.cameraPos(x1, y2 - clampedRadius, 0f)
        }

        // left edge
        RenderUtils.cameraPos(x1, y1 + clampedRadius, 0f)

        if (RenderUtils.FlattenRoundedRectCorner.TOP_LEFT in flatCornersSet) {
            RenderUtils.cameraPos(x1, y1, 0f)
        } else {
            addCornerVertices(drawContext, x1 + clampedRadius, y1 + clampedRadius, clampedRadius, 180f, 270f, segments)
            RenderUtils.cameraPos(x1 + clampedRadius, y1, 0f)
        }

        // top edge
        RenderUtils
            .cameraPos(x2 - clampedRadius, y1, 0f)

            .draw()
            .guiEndDraw()
    }
    private fun addCornerVertices(
        drawContext: DrawContext,
        centerX: Float,
        centerY: Float,
        radius: Float,
        startAngle: Float,
        endAngle: Float,
        segments: Int,
    ) {
//        !! fix with draw context
        val angleStep = (endAngle - startAngle) / segments
        for (i in 1..segments) {
            val angle = Math.toRadians((startAngle + angleStep * i).toDouble())
            val x = centerX + (radius * cos(angle)).toFloat()
            val y = centerY + (radius * sin(angle)).toFloat()
            RenderUtils.cameraPos(x, y, 0f)
        }
    }

    /**
     * Draws a simple gradient rectangle with 2 colors
     *
     * @param x the X-coordinate
     * @param y the Y-coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param startColor the starting color as a [Color] object
     * @param endColor the ending color as a [Color] object
     * @param direction the direction of the gradient
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleGradient(drawContext: DrawContext, x: Float, y: Float, width: Float, height: Float, startColor: Color, endColor: Color, direction: RenderUtils.GradientDirection = RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT, zOffset: Float = 0f) {
        val startColorLong = RenderUtils.RGBAColor(startColor.red, startColor.green, startColor.blue, startColor.alpha).getLong()
        val endColorLong = RenderUtils.RGBAColor(endColor.red, endColor.green, endColor.blue, endColor.alpha).getLong()
        drawSimpleGradient(drawContext, x, y, width, height, startColorLong, endColorLong, direction, zOffset)
    }

    /**
     * Draws a simple gradient rectangle with 2 colors
     *
     * @param x the X-coordinate
     * @param y the Y-coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param startRed the starting red component (0-255)
     * @param startGreen the starting green component (0-255)
     * @param startBlue the starting blue component (0-255)
     * @param startAlpha the starting alpha component (0-255)
     * @param endRed the ending red component (0-255)
     * @param endGreen the ending green component (0-255)
     * @param endBlue the ending blue component (0-255)
     * @param endAlpha the ending alpha component (0-255)
     * @param direction the direction of the gradient
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleGradientRGBA(drawContext: DrawContext, x: Float, y: Float, width: Float, height: Float, startRed: Int = 255, startGreen: Int = 255, startBlue: Int = 255, startAlpha: Int = 255, endRed: Int = 0, endGreen: Int = 0, endBlue: Int = 0, endAlpha: Int = 255, direction: RenderUtils.GradientDirection = RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT, zOffset: Float = 0f) {
        val startColor = RenderUtils.RGBAColor(startRed, startGreen, startBlue, startAlpha).getLong()
        val endColor = RenderUtils.RGBAColor(endRed, endGreen, endBlue, endAlpha).getLong()
        drawSimpleGradient(drawContext, x, y, width, height, startColor, endColor, direction, zOffset)
    }

    /**
     * Draws a simple gradient rectangle with 2 colors
     *
     * @param x the X-coordinate
     * @param y the Y-coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param startColor the starting color as a [Long] value in RGBA format
     * @param endColor the ending color as a [Long] value in RGBA format
     * @param direction the direction of the gradient
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleGradient(drawContext: DrawContext, x: Float, y: Float, width: Float, height: Float, startColor: Long = RenderUtils.WHITE, endColor: Long = RenderUtils.BLACK, direction: RenderUtils.GradientDirection = RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT, zOffset: Float = 0f) {
        val gradientColors = RenderUtils.getGradientColors(direction, startColor, endColor)
        drawGradient(drawContext, x, y, width, height, gradientColors.topLeft, gradientColors.topRight, gradientColors.bottomLeft, gradientColors.bottomRight, direction, zOffset)
    }

    /**
     * Draws a simple gradient rectangle to the screen
     *
     * @param x the X-coordinate
     * @param y the Y-coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param topLeftColor the color in the top-left corner as a [Long] value in RGBA format
     * @param topRightColor the color in the top-right corner as a [Long] value in RGBA format
     * @param bottomLeftColor the color in the bottom-left corner as a [Long] value in RGBA format
     * @param bottomRightColor the color in the bottom-right corner as a [Long] value in RGBA format
     * @param direction the direction of the gradient
     */
    @JvmStatic
    @JvmOverloads
    fun drawGradient(
        drawContext: DrawContext,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        topLeftColor: Long = RenderUtils.WHITE,
        topRightColor: Long = RenderUtils.WHITE,
        bottomLeftColor: Long = RenderUtils.BLACK,
        bottomRightColor: Long = RenderUtils.BLACK,
        direction: RenderUtils.GradientDirection = RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT,
        zOffset: Float = 0f,
    ) {
//        !! fix with drawContext
        val x2 = x + width
        val y2 = y + height
        RenderUtils
            .guiStartDraw()

            .begin(RenderLayers.TRIANGLES())
            .colorizeRGBA(topLeftColor).cameraPos(x, y, 0f)
            .colorizeRGBA(topRightColor).cameraPos(x2, y, 0f)
            .colorizeRGBA(bottomLeftColor).cameraPos(x, y2, 0f)

            .colorizeRGBA(bottomLeftColor).cameraPos(x, y2, 0f)
            .colorizeRGBA(topRightColor).cameraPos(x2, y, 0f)
            .colorizeRGBA(bottomRightColor).cameraPos(x2, y2, 0f)

            .draw()
            .guiEndDraw()
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
    fun drawLineRGBA(drawContext: DrawContext, startX: Float, startY: Float, endX: Float, endY: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, lineThickness: Float = 1f, zOffset: Float = 0f) {
        drawLine(drawContext, startX, startY, endX, endY, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), lineThickness, zOffset)
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
        drawContext: DrawContext,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        lineThickness: Float = 1f,
        zOffset: Float = 0f
    ) {
//        !! fix with drawContext
        val theta = -atan2(endY - startY, endX - startX)
        val i = sin(theta) * (lineThickness / 2)
        val j = cos(theta) * (lineThickness / 2)

        RenderUtils
            .guiStartDraw()

            .begin(RenderLayers.QUADS_ESP())
            .colorizeRGBA(color)
            .cameraPos(startX + i, startY + j, 0f)
            .cameraPos(endX + i, endY + j, 0f)
            .cameraPos(endX - i, endY - j, 0f)
            .cameraPos(startX - i, startY - j, 0f)

            .draw()
            .guiEndDraw()
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
    fun drawSimpleCircleRGBA(drawContext: DrawContext, xPosition: Float, yPosition: Float, radius: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, edges: Int = 32, zOffset: Float = 0f) {
        drawCircle(drawContext, xPosition, yPosition, radius, radius, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), edges, 0f, 0f, 0f, zOffset)
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
    fun drawSimpleCircle(drawContext: DrawContext, xPosition: Float, yPosition: Float, radius: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, edges: Int = 32, zOffset: Float = 0f) {
        drawCircle(drawContext, xPosition, yPosition, radius, radius, color, edges, 0f, 0f, 0f, zOffset)
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
    fun drawCircleRGBA(drawContext: DrawContext, xPosition: Float, yPosition: Float, xScale: Float = 1f, yScale: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, edges: Int = 32, rotationDegrees: Float = 0f, xRotationOffset: Float = 0f, yRotationOffset: Float = 0f, zOffset: Float = 0f) {
        drawCircle(drawContext, xPosition, yPosition, xScale, yScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), edges, rotationDegrees, xRotationOffset, yRotationOffset, zOffset)
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
        drawContext: DrawContext,
        xPosition: Float,
        yPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        edges: Int = 32,
        rotationDegrees: Float = 0f,
        xRotationOffset: Float = 0f,
        yRotationOffset: Float = 0f,
        zOffset: Float = 0f,
    ) {
//        !! fix with drawContext
        val theta = 2 * PI / edges
        val cos = cos(theta).toFloat()
        val sin = sin(theta).toFloat()

        var xHolder: Float
        var circleX = 1f
        var circleY = 0f

        // rotation from circle's center
        RenderUtils
            .guiStartDraw()
            .translate(xPosition + xRotationOffset, yPosition + yRotationOffset, 0f)
            .rotate(rotationDegrees % 360, 0f, 0f, 1f)
            .translate(-xPosition + -xRotationOffset, -yPosition + -yRotationOffset, 0f)
            .begin(RenderLayers.TRIANGLE_STRIP_ESP())
            .colorizeRGBA(color)

        for (i in 0..edges) {
            RenderUtils
                .cameraPos(xPosition, yPosition, 0f)
                .cameraPos(circleX * xScale + xPosition, circleY * yScale + yPosition, 0f)
            xHolder = circleX
            circleX = cos * circleX - sin * circleY
            circleY = sin * xHolder + cos * circleY

            RenderUtils.cameraPos(circleX * xScale + xPosition, circleY * yScale + yPosition, 0f)
        }

        RenderUtils
            .draw()
            .guiEndDraw()
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
     * @param maxWidth the maximum width of the text before it wraps
     * @param zOffset the Z-offset for the text
     */
    @JvmStatic
    @JvmOverloads
    fun drawStringWithShadowRGBA(drawContext: DrawContext, text: String, xPosition: Float, yPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, textScale: Float = 1f, renderBackground: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawString(drawContext, text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, true, maxWidth, zOffset)
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
     * @param maxWidth the maximum width of the text before it wraps
     * @param zOffset the Z-offset for the text
     */
    @JvmStatic
    @JvmOverloads
    fun drawStringWithShadow(drawContext: DrawContext, text: String, xPosition: Float, yPosition: Float, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, textScale: Float = 1f, renderBackground: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawString(drawContext, text, xPosition, yPosition, color, textScale, renderBackground, true, maxWidth, zOffset)
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
     * @param maxWidth the maximum width of the text before it wraps
     * @param zOffset the Z-offset for the text
     */
    @JvmStatic
    @JvmOverloads
    fun drawStringRGBA(drawContext: DrawContext, text: String, xPosition: Float, yPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, textScale: Float = 1f, renderBackground: Boolean = false, textShadow: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawString(drawContext, text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, textShadow, maxWidth, zOffset)
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
     * @param maxWidth the maximum width of the text before it wraps
     * @param zOffset the Z-offset for the text
     */
    @JvmStatic
    @JvmOverloads
    fun drawString(drawContext: DrawContext, text: String, xPosition: Float, yPosition: Float, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, textScale: Float = 1f, renderBackground: Boolean = false, textShadow: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawString(drawContext, Text.of(text), xPosition, yPosition, color, textScale, renderBackground, textShadow, maxWidth, zOffset)
    }

    /**
     * Draws text with a shadow to the screen
     *
     * @param text the text as a [Text] object
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param textScale the text scale
     * @param renderBackground whether to draw a transparent background
     * @param maxWidth the maximum width of the text before it wraps
     * @param zOffset the Z-offset for the text
     */
    @JvmStatic
    @JvmOverloads
    fun drawStringWithShadowRGBA(drawContext: DrawContext, text: Text, xPosition: Float, yPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, textScale: Float = 1f, renderBackground: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawString(drawContext, text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, true, maxWidth, zOffset)
    }

    /**
     * Draws text with a shadow to the screen
     *
     * @param text the text as a [Text] object
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param color the color as a [Long] value in RGBA format
     * @param textScale the text scale
     * @param renderBackground whether to draw a transparent background
     * @param maxWidth the maximum width of the text before it wraps
     * @param zOffset the Z-offset for the text
     */
    @JvmStatic
    @JvmOverloads
    fun drawStringWithShadow(drawContext: DrawContext, text: Text, xPosition: Float, yPosition: Float, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, textScale: Float = 1f, renderBackground: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawString(drawContext, text, xPosition, yPosition, color, textScale, renderBackground, true, maxWidth, zOffset)
    }

    /**
     * Draws text to the screen
     *
     * @param text the text as a [Text] object
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param textScale the text scale
     * @param renderBackground whether to draw a transparent background
     * @param textShadow whether to draw a shadow behind the text
     * @param maxWidth the maximum width of the text before it wraps
     * @param zOffset the Z-offset for the text
     */
    @JvmStatic
    @JvmOverloads
    fun drawStringRGBA(drawContext: DrawContext, text: Text, xPosition: Float, yPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, textScale: Float = 1f, renderBackground: Boolean = false, textShadow: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawString(drawContext, text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, textShadow, maxWidth, zOffset)
    }

    /**
     * Draws text to the screen
     *
     * @param text the text as a [Text] object
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param color the color as a [Long] value in RGBA format
     * @param textScale the text scale
     * @param renderBackground whether to draw a transparent background
     * @param textShadow whether to draw a shadow behind the text
     * @param maxWidth the maximum width of the text before it wraps
     * @param zOffset the Z-offset for the text
     */
    @JvmStatic
    @JvmOverloads
    fun drawString(
        drawContext: DrawContext,
        text: Text,
        xPosition: Float,
        yPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        textShadow: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f,
    ) {
//        !! fix with drawContext
        val fontRenderer = RenderUtils.getTextRenderer()
        val vertexConsumers = Client.getMinecraft().bufferBuilders.entityVertexConsumers

        val backgroundColorInt = if (renderBackground) {
            Color(0, 0, 0, 150).rgb
        } else {
            0
        }

        RenderUtils
            .pushMatrix()
            .translate(xPosition, yPosition, zOffset)

        val positionMatrix = RenderUtils.matrixStack.peek().model
        positionMatrix.scale(textScale, textScale, 1f)

        // TextRender.tweakTransparency gets called in TextRender.draw resetting the alpha value to 255 if it's less than 4
        val (a, r, g, b) = RenderUtils.RGBAColor.fromLongRGBA(color).getIntComponentsARGB()
        if (a == 0) {
            RenderUtils.popMatrix()
            return
        }
        val safeAlpha = if (a in 1..3) 4 else a
        val safeColorARGB = RenderUtils.ARGBColor(r, g, b, safeAlpha).getIntARGB()

        var newY = 0f
        RenderUtils.splitText(text, maxWidth).lines.forEach { line ->
            fontRenderer.draw(
                line,
                0f,
                newY,
                safeColorARGB,
                textShadow,
                positionMatrix,
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                backgroundColorInt,
                0xF000F0,
            )
            newY += fontRenderer.fontHeight
        }
        vertexConsumers.draw()

        positionMatrix.scale(1f / textScale, 1f / textScale, 1f)
        RenderUtils
//            .resetColor()
//            .enableCull()
//            .disableBlend()
//            .enableDepth()
            .popMatrix()
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
    fun drawImageRGBA(drawContext: DrawContext, image: Image, xPosition: Float, yPosition: Float, width: Float? = null, height: Float? = null, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, zOffset: Float = 0f) {
        drawImage(drawContext, image, xPosition, yPosition, width, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), zOffset)
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
        drawContext: DrawContext,
        image: Image,
        xPosition: Float,
        yPosition: Float,
        width: Float? = null,
        height: Float? = null,
        color: Long = RenderUtils.WHITE,
        zOffset: Float = 0f
    ) {
//        !! fix with drawContext
        val texture = image.getTexture() ?: throw IllegalStateException("Image is null.")

        val identifier = image.getIdentifier()
        val (drawWidth, drawHeight) = image.getImageSize(width, height)

        RenderUtils
            .pushMatrix()
            //#if MC>=12106
            //$$.setShaderTexture(0, texture.glTextureView)
            //#else
            .setShaderTexture(0, texture.glTexture)
            //#endif
            .scale(1f, 1f, 50f)
            .begin(RenderLayers.TEXTURED_QUADS_ESP(textureIdentifier = identifier!!))
            .colorizeRGBA(color)
            .translate(0f, 0f, zOffset)
            .cameraPos(xPosition, yPosition + drawHeight, 0f).tex(0f, 1f)
            .cameraPos(xPosition + drawWidth, yPosition + drawHeight, 0f).tex(1f, 1f)
            .cameraPos(xPosition + drawWidth, yPosition, 0f).tex(1f, 0f)
            .cameraPos(xPosition, yPosition, 0f).tex(0f, 0f)
            .draw()

            .resetColor()
            .popMatrix()
    }
}
//#endif
