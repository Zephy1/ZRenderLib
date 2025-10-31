package org.zephy.zrenderlib

import org.lwjgl.opengl.GL11
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object GUIRenderer : BaseGUIRenderer() {
    override fun drawString(
        text: String,
        xPosition: Float,
        yPosition: Float,
        color: Long,
        textScale: Float,
        renderBackground: Boolean,
        textShadow: Boolean,
        maxWidth: Int,
        zOffset: Float,
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

    override fun drawLine(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        color: Long,
        lineThickness: Float,
        zOffset: Float,
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

    override fun drawRect(
        xPosition: Float,
        yPosition: Float,
        width: Float,
        height: Float,
        color: Long,
        zOffset: Float,
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

    override fun drawRoundedRect(
        xPosition: Float,
        yPosition: Float,
        width: Float,
        height: Float,
        radius: Float,
        color: Long,
        flatCorners: List<RenderUtils.FlattenRoundedRectCorner>,
        segments: Int,
        zOffset: Float,
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

    override fun drawGradient(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        topLeftColor: Long,
        topRightColor: Long,
        bottomLeftColor: Long,
        bottomRightColor: Long,
        direction: RenderUtils.GradientDirection,
        zOffset: Float,
    ) {
        val x2 = x + width
        val y2 = y + height
        RenderUtils
            .guiStartDraw()
            .disableTexture2D()

            .begin(GL11.GL_QUADS, VertexFormat.POSITION)
            .translate(0f, 0f, zOffset)
            // This is really bad, but it works
            when (direction) {
                RenderUtils.GradientDirection.TOP_TO_BOTTOM,
                RenderUtils.GradientDirection.RIGHT_TO_LEFT,
                RenderUtils.GradientDirection.TOP_RIGHT_TO_BOTTOM_LEFT -> {
                    RenderUtils
                        .colorizeRGBA(topLeftColor)
                        .pos(x, y, 0f)
                        .colorizeRGBA(bottomLeftColor)
                        .pos(x, y2, 0f)
                        .colorizeRGBA(bottomRightColor)
                        .pos(x2, y2, 0f)
                        .colorizeRGBA(topRightColor)
                        .pos(x2, y, 0f)
                }

                RenderUtils.GradientDirection.BOTTOM_TO_TOP,
                RenderUtils.GradientDirection.BOTTOM_RIGHT_TO_TOP_LEFT -> {
                    RenderUtils
                        .colorizeRGBA(bottomLeftColor)
                        .pos(x, y2, 0f)
                        .colorizeRGBA(topLeftColor)
                        .pos(x, y, 0f)
                        .colorizeRGBA(topRightColor)
                        .pos(x2, y, 0f)
                        .colorizeRGBA(bottomRightColor)
                        .pos(x2, y2, 0f)
                }

                RenderUtils.GradientDirection.LEFT_TO_RIGHT,
                RenderUtils.GradientDirection.BOTTOM_LEFT_TO_TOP_RIGHT -> {
                    RenderUtils
                        .colorizeRGBA(topLeftColor)
                        .pos(x, y, 0f)
                        .colorizeRGBA(topRightColor)
                        .pos(x2, y, 0f)
                        .colorizeRGBA(bottomRightColor)
                        .pos(x2, y2, 0f)
                        .colorizeRGBA(bottomLeftColor)
                        .pos(x, y2, 0f)
                }

                RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT -> {
                    RenderUtils
                        .colorizeRGBA(topRightColor)
                        .pos(x2, y, 0f)
                        .colorizeRGBA(bottomRightColor)
                        .pos(x2, y2, 0f)
                        .colorizeRGBA(bottomLeftColor)
                        .pos(x, y2, 0f)
                        .colorizeRGBA(topLeftColor)
                        .pos(x, y, 0f)
                }
            }

            .draw()
            .enableTexture2D()
            .guiEndDraw()
    }

    override fun drawCircle(
        xPosition: Float,
        yPosition: Float,
        xScale: Float,
        yScale: Float,
        color: Long,
        edges: Int,
        rotationDegrees: Float,
        xRotationOffset: Float,
        yRotationOffset: Float,
        zOffset: Float,
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
            .pushMatrix()
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
            .popMatrix()
            .enableTexture2D()
            .guiEndDraw()
    }

    override fun drawImage(
        image: Image,
        xPosition: Float,
        yPosition: Float,
        width: Float?,
        height: Float?,
        color: Long,
        zOffset: Float,
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
