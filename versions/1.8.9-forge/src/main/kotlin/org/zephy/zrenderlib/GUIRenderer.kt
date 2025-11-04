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
            .addColor(text).split("\n").forEach {
                fontRenderer.drawString(it, xPosition, newY, RenderUtils.RGBAColor.fromLongRGBA(color).getLongARGB().toInt(), textShadow)
                newY += fontRenderer.FONT_HEIGHT
            }
        RenderUtils.popMatrix()
    }

    override fun _drawLine(
        vertexList: List<Pair<Float, Float>>,
        color: Long,
        zOffset: Float,
    ) {
        RenderUtils
            .guiStartDraw()
            .disableTexture2D()
            .begin(GL11.GL_QUADS, VertexFormat.POSITION)
            .colorizeRGBA(color)
            .translate(0f, 0f, zOffset)
            .posList(vertexList, 0f)
            .draw()
            .enableTexture2D()
            .guiEndDraw()
    }

    override fun _drawRect(
        vertexList: List<Pair<Float, Float>>,
        color: Long,
        zOffset: Float,
    ) {
        RenderUtils
            .guiStartDraw()
            .disableTexture2D()
            .begin(GL11.GL_QUADS, VertexFormat.POSITION)
            .colorizeRGBA(color)
            .translate(0f, 0f, zOffset)
            .posList(vertexList)
            .draw()
            .enableTexture2D()
            .guiEndDraw()
    }

    override fun _drawRoundedRect(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        vertexList: List<Pair<Float, Float>>,
        color: Long,
        zOffset: Float,
    ) {
        RenderUtils
            .guiStartDraw()
            .disableTexture2D()
            .begin(GL11.GL_QUADS, VertexFormat.POSITION)
            .colorizeRGBA(color)
            .translate(0f, 0f, zOffset)
            .posList(vertexList, 0f)
            .draw()
            .enableTexture2D()
            .guiEndDraw()
    }

    override fun _drawGradient(
        vertexAndColorList: List<Pair<Float, Float>>,
        zOffset: Float,
    ) {
        RenderUtils
            .guiStartDraw()
            .disableTexture2D()
            .begin(GL11.GL_QUADS, VertexFormat.POSITION)
            .translate(0f, 0f, zOffset)
        vertexAndColorList.forEach { (x, y, color) ->
            RenderUtils
                .colorizeRGBA(color)
                .pos(x, y, 0f)
        }
        RenderUtils
            .draw()
            .enableTexture2D()
            .guiEndDraw()
    }

    override fun _drawCircle(
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
