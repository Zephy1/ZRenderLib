package org.zephy.zrenderlib

import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object GUIRenderer {
    @JvmStatic
    @JvmOverloads
    fun drawSquareRGBA(xPosition: Float, yPosition: Float, size: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, zOffset: Float = 0f) {
        drawRect(xPosition, yPosition, size, size, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSquare(xPosition: Float, yPosition: Float, size: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, zOffset: Float = 0f) {
        drawRect(xPosition, yPosition, size, size, color, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawRectRGBA(xPosition: Float, yPosition: Float, width: Float = 1f, height: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, zOffset: Float = 0f) {
        drawRect(xPosition, yPosition, width, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), zOffset)
    }

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
            .guiStartDraw()
            .disableTexture2D()

            .begin(GL11.GL_QUADS, VertexFormat.POSITION)
            .colorizeRGBA(color)
            .translate(0f, 0f, zOffset)
            .pos(x1, y2, 0f)
            .pos(x2, y2, 0f)
            .pos(x2, y1, 0f)
            .pos(x1, y1, 0f)
            .draw()

            .enableTexture2D()
            .guiEndDraw()
    }

    @JvmStatic
    @JvmOverloads
    fun drawRoundedRectRGBA(xPosition: Float, yPosition: Float, width: Float, height: Float, radius: Float = 4f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, flatCorners: List<RenderUtils.FlattenRoundedRectCorner> = emptyList(), segments: Int = 16, zOffset: Float = 0f) {
        drawRoundedRect(xPosition, yPosition, width, height, radius, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), flatCorners, segments, zOffset)
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
        flatCorners: List<RenderUtils.FlattenRoundedRectCorner> = emptyList(),
        segments: Int = 16,
        zOffset: Float = 0f,
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
            .guiStartDraw()
            .disableTexture2D()

            .begin(GL11.GL_TRIANGLE_FAN, VertexFormat.POSITION)
            .colorizeRGBA(color)
            .translate(0f, 0f, zOffset)
            .pos(centerX, centerY, 0f)

        RenderUtils.pos(x2 - clampedRadius, y1, 0f)
        if (RenderUtils.FlattenRoundedRectCorner.TOP_RIGHT in flatCornersSet) {
            RenderUtils.pos(x2, y1, 0f)
        } else {
            addCornerVertices(x2 - clampedRadius, y1 + clampedRadius, clampedRadius, 270f, 360f, segments)
            RenderUtils.pos(x2, y1 + clampedRadius, 0f)
        }

        // right edge
        RenderUtils.pos(x2, y2 - clampedRadius, 0f)
        if (RenderUtils.FlattenRoundedRectCorner.BOTTOM_RIGHT in flatCornersSet) {
            RenderUtils.pos(x2, y2, 0f)
        } else {
            addCornerVertices(x2 - clampedRadius, y2 - clampedRadius, clampedRadius, 0f, 90f, segments)
            RenderUtils.pos(x2 - clampedRadius, y2, 0f)
        }

        // bottom edge
        RenderUtils.pos(x1 + clampedRadius, y2, 0f)
        if (RenderUtils.FlattenRoundedRectCorner.BOTTOM_LEFT in flatCornersSet) {
            RenderUtils.pos(x1, y2, 0f)
        } else {
            addCornerVertices(x1 + clampedRadius, y2 - clampedRadius, clampedRadius, 90f, 180f, segments)
            RenderUtils.pos(x1, y2 - clampedRadius, 0f)
        }

        // left edge
        RenderUtils.pos(x1, y1 + clampedRadius, 0f)
        if (RenderUtils.FlattenRoundedRectCorner.TOP_LEFT in flatCornersSet) {
            RenderUtils.pos(x1, y1, 0f)
        } else {
            addCornerVertices(x1 + clampedRadius, y1 + clampedRadius, clampedRadius, 180f, 270f, segments)
            RenderUtils.pos(x1 + clampedRadius, y1, 0f)
        }

        // top edge
        RenderUtils
            .pos(x2 - clampedRadius, y1, 0f)

            .draw()
            .enableTexture2D()
            .guiEndDraw()
    }
    private fun addCornerVertices(
        centerX: Float,
        centerY: Float,
        radius: Float,
        startAngle: Float,
        endAngle: Float,
        segments: Int,
    ) {
        val angleStep = (endAngle - startAngle) / segments
        for (i in 1..segments) {
            val angle = Math.toRadians((startAngle + angleStep * i).toDouble())
            val x = centerX + (radius * cos(angle)).toFloat()
            val y = centerY + (radius * sin(angle)).toFloat()
            RenderUtils.pos(x, y, 0f)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleGradient(x: Float, y: Float, width: Float, height: Float, startColor: Color, endColor: Color, direction: RenderUtils.GradientDirection = RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT, zOffset: Float = 0f,) {
        val startColorLong = RenderUtils.RGBAColor(startColor.red, startColor.green, startColor.blue, startColor.alpha).getLong()
        val endColorLong = RenderUtils.RGBAColor(endColor.red, endColor.green, endColor.blue, endColor.alpha).getLong()
        drawSimpleGradient(x, y, width, height, startColorLong, endColorLong, direction, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleGradientRGBA(x: Float, y: Float, width: Float, height: Float, startRed: Int = 255, startGreen: Int = 255, startBlue: Int = 255, startAlpha: Int = 255, endRed: Int = 0, endGreen: Int = 0, endBlue: Int = 0, endAlpha: Int = 255, direction: RenderUtils.GradientDirection = RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT, zOffset: Float = 0f,) {
        val startColor = RenderUtils.RGBAColor(startRed, startGreen, startBlue, startAlpha).getLong()
        val endColor = RenderUtils.RGBAColor(endRed, endGreen, endBlue, endAlpha).getLong()
        drawSimpleGradient(x, y, width, height, startColor, endColor, direction, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleGradient(x: Float, y: Float, width: Float, height: Float, startColor: Long = RenderUtils.WHITE, endColor: Long = RenderUtils.BLACK, direction: RenderUtils.GradientDirection = RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT, zOffset: Float = 0f,) {
        val gradientColors = RenderUtils.getGradientColors(direction, startColor, endColor)
        drawGradient(x, y, width, height, gradientColors.topLeft, gradientColors.topRight, gradientColors.bottomLeft, gradientColors.bottomRight, zOffset)
    }

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
        zOffset: Float = 0f,
    ) {
        val x2 = x + width
        val y2 = y + height
        RenderUtils
            .guiStartDraw()
            .disableTexture2D()

            .begin(GL11.GL_TRIANGLES, VertexFormat.POSITION)
            .translate(0f, 0f, zOffset)
            .colorizeRGBA(topLeftColor).pos(x, y, 0f)
            .colorizeRGBA(topRightColor).pos(x2, y, 0f)
            .colorizeRGBA(bottomLeftColor).pos(x, y2, 0f)

            .colorizeRGBA(bottomLeftColor).pos(x, y2, 0f)
            .colorizeRGBA(topRightColor).pos(x2, y, 0f)
            .colorizeRGBA(bottomRightColor).pos(x2, y2, 0f)

            .draw()
            .enableTexture2D()
            .guiEndDraw()
    }

    @JvmStatic
    @JvmOverloads
    fun drawLineRGBA(startX: Float, startY: Float, endX: Float, endY: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, lineThickness: Float = 1f, zOffset: Float = 0f,) {
        drawLine(startX, startY, endX, endY, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), lineThickness, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawLine(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        lineThickness: Float = 1f,
        zOffset: Float = 0f,
    ) {
        val theta = -atan2(endY - startY, endX - startX)
        val i = sin(theta) * (lineThickness / 2)
        val j = cos(theta) * (lineThickness / 2)

        RenderUtils
            .guiStartDraw()
            .disableTexture2D()

            .begin(GL11.GL_QUADS, VertexFormat.POSITION)
            .colorizeRGBA(color)
            .translate(0f, 0f, zOffset)
            .pos(startX + i, startY + j, 0f)
            .pos(endX + i, endY + j, 0f)
            .pos(endX - i, endY - j, 0f)
            .pos(startX - i, startY - j, 0f)
            .draw()

            .enableTexture2D()
            .guiEndDraw()
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleCircleRGBA(xPosition: Float, yPosition: Float, radius: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, edges: Int = 32, zOffset: Float = 0f) {
        drawCircle(xPosition, yPosition, radius, radius, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), edges, 0f, 0f, 0f, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleCircle(xPosition: Float, yPosition: Float, radius: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, edges: Int = 32, zOffset: Float = 0f) {
        drawCircle(xPosition, yPosition, radius, radius, color, edges, 0f, 0f, 0f, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawCircleRGBA(xPosition: Float, yPosition: Float, xScale: Float = 1f, yScale: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, edges: Int = 32, rotationDegrees: Float = 0f, xRotationOffset: Float = 0f, yRotationOffset: Float = 0f, zOffset: Float = 0f) {
        drawCircle(xPosition, yPosition, xScale, yScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), edges, rotationDegrees, xRotationOffset, yRotationOffset, zOffset)
    }

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
        zOffset: Float = 0f,
    ) {
        val theta = 2 * PI / edges
        val cos = cos(theta).toFloat()
        val sin = sin(theta).toFloat()

        var xHolder: Float
        var circleX = 1f
        var circleY = 0f

        // rotation from circle's center
        RenderUtils
            .guiStartDraw()
            .disableTexture2D()
            .translate(xPosition + xRotationOffset, yPosition + yRotationOffset, 0f)
            .rotate(rotationDegrees % 360, 0f, 0f, 1f)
            .translate(-xPosition + -xRotationOffset, -yPosition + -yRotationOffset, 0f)
            .begin(GL11.GL_TRIANGLE_STRIP, VertexFormat.POSITION)
            .colorizeRGBA(color)
            .translate(0f, 0f, zOffset)

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
            .enableTexture2D()
            .guiEndDraw()
    }

    @JvmStatic
    @JvmOverloads
    fun drawStringWithShadowRGBA(text: String, xPosition: Float, yPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, textScale: Float = 1f, renderBackground: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawString(text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, true, maxWidth, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawStringWithShadow(text: String, xPosition: Float, yPosition: Float, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, textScale: Float = 1f, renderBackground: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawString(text, xPosition, yPosition, color, textScale, renderBackground, true, maxWidth, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawStringRGBA(text: String, xPosition: Float, yPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, textScale: Float = 1f, renderBackground: Boolean = false, textShadow: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawString(text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, textShadow, maxWidth, zOffset)
    }

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
        val fontRenderer = RenderUtils.getTextRenderer()
        var newY = yPosition

        RenderUtils
            .pushMatrix()
            .addColor(text)
            .split("\n")
            .forEach {
                fontRenderer.drawString(it, xPosition, newY, RenderUtils.RGBAColor.fromLongRGBA(color).getLongARGB().toInt(), textShadow)
                newY += fontRenderer.FONT_HEIGHT
            }
        RenderUtils.popMatrix()
    }

    @JvmStatic
    @JvmOverloads
    fun drawImageRGBA(image: Image, xPosition: Float, yPosition: Float, width: Float? = null, height: Float? = null, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, zOffset: Float = 0f) {
        drawImage(image, xPosition, yPosition, width, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawImage(
        image: Image,
        xPosition: Float,
        yPosition: Float,
        width: Float? = null,
        height: Float? = null,
        color: Long = RenderUtils.WHITE,
        zOffset: Float = 0f,
    ) {
        val (drawWidth, drawHeight) = image.getImageSize(width, height)
        RenderUtils
            .guiStartDraw()
            .scale(1f, 1f, 50f)
            .bindTexture(image.getTexture().getGlTextureId())
            .enableTexture2D()

            .begin(GL11.GL_QUADS, VertexFormat.POSITION_TEX)
            .colorizeRGBA(color)
            .translate(0f, 0f, zOffset)
            .pos(xPosition, yPosition + drawHeight, 0f).tex(0f, 1f)
            .pos(xPosition + drawWidth, yPosition + drawHeight, 0f).tex(1f, 1f)
            .pos(xPosition + drawWidth, yPosition, 0f).tex(1f, 0f)
            .pos(xPosition, yPosition, 0f).tex(0f, 0f)

            .draw()
            .disableTexture2D()
            .guiEndDraw()
    }
}