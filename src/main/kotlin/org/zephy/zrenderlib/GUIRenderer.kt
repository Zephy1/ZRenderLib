package org.zephy.zrenderlib

//#if MC>12100
import net.minecraft.client.font.TextRenderer
import net.minecraft.text.Text
import java.awt.Color
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
     * @param zOffset the Z-offset for the rectangle
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
     * @param zOffset the Z-offset for the rectangle
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
     * @param zOffset the Z-offset for the rectangle
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
     * @param zOffset the Z-offset for the rectangle
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
        val x1 = xPosition
        val x2 = xPosition + width
        val y1 = yPosition
        val y2 = yPosition + height

        RenderUtils
            .pushMatrix()
            .resetColor()
            .disableCull()
            .enableBlend()
            .tryBlendFuncSeparate(770, 771, 1, 0)

            .begin(_root_ide_package_.org.zephy.zrenderlib.RenderLayers.QUADS())
            .colorizeRGBA(color)
            .translate(0f, 0f, zOffset)
            .cameraPos(x1, y2, 0f)
            .cameraPos(x2, y2, 0f)
            .cameraPos(x2, y1, 0f)
            .cameraPos(x1, y1, 0f)
            .draw()

            .resetColor()
            .enableCull()
            .disableBlend()
            .resetLineWidth()
            .enableDepth()
            .popMatrix()
    }

    enum class FlattenRoundedRectCorner {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT;
    }

    @JvmStatic
    @JvmOverloads
    fun drawRoundedRectRGBA(
        xPosition: Float,
        yPosition: Float,
        width: Float,
        height: Float,
        radius: Float = 4f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        flatCorners: List<FlattenRoundedRectCorner> = emptyList(),
        zOffset: Float = 0f,
        segments: Int = 16,
    ) {
        drawRoundedRect(xPosition, yPosition, width, height, radius, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), flatCorners, zOffset, segments)
    }

    @JvmStatic
    @JvmOverloads
    fun drawRoundedRect(
        xPosition: Float,
        yPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        radius: Float = 4f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        flatCorners: List<FlattenRoundedRectCorner> = emptyList(),
        zOffset: Float = 0f,
        segments: Int = 16,
    ) {
        val x1 = xPosition
        val y1 = yPosition
        val x2 = xPosition + width
        val y2 = yPosition + height

        val centerX = (x1 + x2) / 2f
        val centerY = (y1 + y2) / 2f
        val clampedRadius = radius.coerceAtMost(minOf(width, height) / 2f)
        val flatCornersSet = flatCorners.toSet()

        RenderUtils
            .pushMatrix()
            .resetColor()
            .disableCull()
            .enableBlend()
            .tryBlendFuncSeparate(770, 771, 1, 0)
            .begin(_root_ide_package_.org.zephy.zrenderlib.RenderLayers.TRIANGLE_FAN())
            .colorizeRGBA(color)
            .translate(0f, 0f, zOffset)
            .cameraPos(centerX, centerY, 0f)

        RenderUtils.cameraPos(x2 - clampedRadius, y1, 0f)
        if (FlattenRoundedRectCorner.TOP_RIGHT in flatCornersSet) {
            RenderUtils.cameraPos(x2, y1, 0f)
        } else {
            addCornerVertices(x2 - clampedRadius, y1 + clampedRadius, clampedRadius, 270f, 360f, segments)
            RenderUtils.cameraPos(x2, y1 + clampedRadius, 0f)
        }

        // right edge
        RenderUtils.cameraPos(x2, y2 - clampedRadius, 0f)

        if (FlattenRoundedRectCorner.BOTTOM_RIGHT in flatCornersSet) {
            RenderUtils.cameraPos(x2, y2, 0f)
        } else {
            addCornerVertices(x2 - clampedRadius, y2 - clampedRadius, clampedRadius, 0f, 90f, segments)
            RenderUtils.cameraPos(x2 - clampedRadius, y2, 0f)
        }

        // bottom edge
        RenderUtils.cameraPos(x1 + clampedRadius, y2, 0f)

        if (FlattenRoundedRectCorner.BOTTOM_LEFT in flatCornersSet) {
            RenderUtils.cameraPos(x1, y2, 0f)
        } else {
            addCornerVertices(x1 + clampedRadius, y2 - clampedRadius, clampedRadius, 90f, 180f, segments)
            RenderUtils.cameraPos(x1, y2 - clampedRadius, 0f)
        }

        // left edge
        RenderUtils.cameraPos(x1, y1 + clampedRadius, 0f)

        if (FlattenRoundedRectCorner.TOP_LEFT in flatCornersSet) {
            RenderUtils.cameraPos(x1, y1, 0f)
        } else {
            addCornerVertices(x1 + clampedRadius, y1 + clampedRadius, clampedRadius, 180f, 270f, segments)
            RenderUtils.cameraPos(x1 + clampedRadius, y1, 0f)
        }

        // top edge
        RenderUtils.cameraPos(x2 - clampedRadius, y1, 0f)

        RenderUtils
            .draw()
            .resetColor()
            .enableCull()
            .disableBlend()
            .resetLineWidth()
            .enableDepth()
            .popMatrix()
    }
    private fun addCornerVertices(
        centerX: Float,
        centerY: Float,
        radius: Float,
        startAngle: Float,
        endAngle: Float,
        segments: Int
    ) {
        val angleStep = (endAngle - startAngle) / segments
        for (i in 1..segments) {
            val angle = Math.toRadians((startAngle + angleStep * i).toDouble())
            val x = centerX + (radius * cos(angle)).toFloat()
            val y = centerY + (radius * sin(angle)).toFloat()
            RenderUtils.cameraPos(x, y, 0f)
        }
    }

    enum class GradientDirection {
        TOP_TO_BOTTOM,
        BOTTOM_TO_TOP,
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        TOP_LEFT_TO_BOTTOM_RIGHT,
        TOP_RIGHT_TO_BOTTOM_LEFT,
        BOTTOM_LEFT_TO_TOP_RIGHT,
        BOTTOM_RIGHT_TO_TOP_LEFT;

        fun getGradientColors(startColor: Long, endColor: Long): GradientColors {
            val startRGBA = RenderUtils.RGBAColor.fromLongRGBA(startColor).getLong()
            val endRGBA = RenderUtils.RGBAColor.fromLongRGBA(endColor).getLong()
            val blendedRGBA = RenderUtils.blendColorsRGBA(startRGBA, endRGBA).getLong()
            return when (this) {
                TOP_TO_BOTTOM -> GradientColors(startRGBA, startRGBA, endRGBA, endRGBA)
                BOTTOM_TO_TOP -> GradientColors(endRGBA, endRGBA, startRGBA, startRGBA)
                LEFT_TO_RIGHT -> GradientColors(startRGBA, endRGBA, startRGBA, endRGBA)
                RIGHT_TO_LEFT -> GradientColors(endRGBA, startRGBA, endRGBA, startRGBA)
                TOP_LEFT_TO_BOTTOM_RIGHT -> GradientColors(
                    topLeft = startRGBA,
                    topRight = blendedRGBA,
                    bottomLeft = blendedRGBA,
                    bottomRight = endRGBA,
                )
                TOP_RIGHT_TO_BOTTOM_LEFT -> GradientColors(
                    topLeft = blendedRGBA,
                    topRight = startRGBA,
                    bottomLeft = endRGBA,
                    bottomRight = blendedRGBA,
                )
                BOTTOM_LEFT_TO_TOP_RIGHT -> GradientColors(
                    topLeft = blendedRGBA,
                    topRight = endRGBA,
                    bottomLeft = startRGBA,
                    bottomRight = blendedRGBA,
                )
                BOTTOM_RIGHT_TO_TOP_LEFT -> GradientColors(
                    topLeft = endRGBA,
                    topRight = blendedRGBA,
                    bottomLeft = blendedRGBA,
                    bottomRight = startRGBA,
                )
            }
        }
    }
    data class GradientColors(
        val topLeft: Long,
        val topRight: Long,
        val bottomLeft: Long,
        val bottomRight: Long,
    )

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
    fun drawSimpleGradient(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        startColor: Color,
        endColor: Color,
        direction: GradientDirection = GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT
    ) {
        val startColorLong = RenderUtils.RGBAColor(startColor.red, startColor.green, startColor.blue, startColor.alpha).getLong()
        val endColorLong = RenderUtils.RGBAColor(endColor.red, endColor.green, endColor.blue, endColor.alpha).getLong()
        drawSimpleGradient(x, y, width, height, startColorLong, endColorLong, direction)
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
    fun drawSimpleGradientRGBA(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        startRed: Int = 255,
        startGreen: Int = 255,
        startBlue: Int = 255,
        startAlpha: Int = 255,
        endRed: Int = 0,
        endGreen: Int = 0,
        endBlue: Int = 0,
        endAlpha: Int = 255,
        direction: GradientDirection = GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT
    ) {
        val startColor = RenderUtils.RGBAColor(startRed, startGreen, startBlue, startAlpha).getLong()
        val endColor = RenderUtils.RGBAColor(endRed, endGreen, endBlue, endAlpha).getLong()
        drawSimpleGradient(x, y, width, height, startColor, endColor, direction)
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
    fun drawSimpleGradient(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        startColor: Long = RenderUtils.WHITE,
        endColor: Long = RenderUtils.BLACK,
        direction: GradientDirection = GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT
    ) {
        val gradientColors = direction.getGradientColors(startColor, endColor)
        drawGradient(x, y, width, height, gradientColors.topLeft, gradientColors.topRight, gradientColors.bottomLeft, gradientColors.bottomRight, direction)
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
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        topLeftColor: Long = RenderUtils.WHITE,
        topRightColor: Long = RenderUtils.WHITE,
        bottomLeftColor: Long = RenderUtils.BLACK,
        bottomRightColor: Long = RenderUtils.BLACK,
        direction: GradientDirection = GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT
    ) {
        val x2 = x + width
        val y2 = y + height
        RenderUtils
            .pushMatrix()
            .resetColor()
            .disableCull()
            .enableBlend()
            .tryBlendFuncSeparate(770, 771, 1, 0)
//            .begin(CustomRenderLayers.QUADS())
////
//            .colorizeRGBA(topLeftColor)
//            .cameraPos(x, y, 0f)
//            .colorizeRGBA(topRightColor)
//            .cameraPos(x2, y, 0f)
//            .colorizeRGBA(bottomRightColor)
//            .cameraPos(x2, y2, 0f)
//            .colorizeRGBA(bottomLeftColor)
//            .cameraPos(x, y2, 0f)
            .begin(_root_ide_package_.org.zephy.zrenderlib.RenderLayers.TRIANGLES())

            .colorizeRGBA(topLeftColor).cameraPos(x, y, 0f)
            .colorizeRGBA(topRightColor).cameraPos(x2, y, 0f)
            .colorizeRGBA(bottomLeftColor).cameraPos(x, y2, 0f)

            .colorizeRGBA(bottomLeftColor).cameraPos(x, y2, 0f)
            .colorizeRGBA(topRightColor).cameraPos(x2, y, 0f)
            .colorizeRGBA(bottomRightColor).cameraPos(x2, y2, 0f)

            .draw()
            .resetColor()
            .enableCull()
            .disableBlend()
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

            .begin(_root_ide_package_.org.zephy.zrenderlib.RenderLayers.QUADS_ESP())
            .colorizeRGBA(color)
            .cameraPos(startX + i, startY + j, 0f)
            .cameraPos(endX + i, endY + j, 0f)
            .cameraPos(endX - i, endY - j, 0f)
            .cameraPos(startX - i, startY - j, 0f)
            .draw()

            .resetColor()
            .enableCull()
            .disableBlend()
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
            .resetColor()
            .enableCull()
            .disableBlend()
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
     * @param maxWidth the maximum width of the text before it wraps
     * @param zOffset the Z-offset for the text
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
        zOffset: Float = 0f,
    ) {
        drawString(text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, true, maxWidth, zOffset)
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
    fun drawStringWithShadow(
        text: String,
        xPosition: Float,
        yPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f,
    ) {
        drawString(text, xPosition, yPosition, color, textScale, renderBackground, true, maxWidth, zOffset)
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
        zOffset: Float = 0f,
    ) {
        drawString(text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, textShadow, maxWidth, zOffset)
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
    fun drawString(
        text: String,
        xPosition: Float,
        yPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        textShadow: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f,
    ) {
        drawString(Text.of(text), xPosition, yPosition, color, textScale, renderBackground, textShadow, maxWidth, zOffset)
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
    fun drawStringWithShadowRGBA(
        text: Text,
        xPosition: Float,
        yPosition: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f,
    ) {
        drawString(text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, true, maxWidth, zOffset)
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
    fun drawStringWithShadow(
        text: Text,
        xPosition: Float,
        yPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f,
    ) {
        drawString(text, xPosition, yPosition, color, textScale, renderBackground, true, maxWidth, zOffset)
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
    fun drawStringRGBA(
        text: Text,
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
        zOffset: Float = 0f,
    ) {
        drawString(text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, textShadow, maxWidth, zOffset)
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
        val fontRenderer = RenderUtils.getTextRenderer()
        val vertexConsumers = Client.getMinecraft().bufferBuilders.entityVertexConsumers

//        val guiScale = mc.window.scaleFactor.toFloat() / 2f
//        val adjustedScale = textScale * guiScale * 0.5f

        val backgroundColorInt = if (renderBackground) {
            Color(0, 0, 0, 150).rgb
        } else {
            0
        }

        RenderUtils
            .pushMatrix()
//            .resetColor()
//            .disableCull()
//            .enableBlend()
//            .tryBlendFuncSeparate(770, 771, 1, 0)
//            .disableDepth()
            .translate(xPosition, yPosition, zOffset)
//            .scale(adjustedScale, adjustedScale, 1f)

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

    class ScreenWrapper {
        fun getWidth(): Int = Client.getMinecraft().window.scaledWidth

        fun getHeight(): Int = Client.getMinecraft().window.scaledHeight

        fun getScale(): Double = Client.getMinecraft().window.scaleFactor.toDouble()
    }
}
//#endif
