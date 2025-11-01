package org.zephy.zrenderlib

//#if MC>=12100
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import java.awt.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

//#if MC>=12106
import net.minecraft.client.texture.TextureSetup
import org.zephy.zrenderlib.renderstates.*
import net.minecraft.client.gl.RenderPipelines
//#endif

object GUIRenderer : BaseGUIRenderer() {
    override fun drawString(drawContext: DrawContext, text: String, xPosition: Float, yPosition: Float, color: Long, textScale: Float, renderBackground: Boolean, textShadow: Boolean, maxWidth: Int, zOffset: Float) {
        drawString(drawContext, Text.of(text), xPosition, yPosition, color, textScale, renderBackground, textShadow, maxWidth, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawStringWithShadowRGBA(drawContext: DrawContext, text: Text, xPosition: Float, yPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, textScale: Float = 1f, renderBackground: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawString(drawContext, text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, true, maxWidth, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawStringWithShadow(drawContext: DrawContext, text: Text, xPosition: Float, yPosition: Float, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, textScale: Float = 1f, renderBackground: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawString(drawContext, text, xPosition, yPosition, color, textScale, renderBackground, true, maxWidth, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawStringRGBA(drawContext: DrawContext, text: Text, xPosition: Float, yPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, textScale: Float = 1f, renderBackground: Boolean = false, textShadow: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawString(drawContext, text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, textShadow, maxWidth, zOffset)
    }

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

    override fun drawLine(
        drawContext: DrawContext,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        color: Long,
        lineThickness: Float,
        zOffset: Float,
    ) {
        val dx = endX - startX
        val dy = endY - startY
        val len = sqrt(dx * dx + dy * dy)
        val halfThickness = lineThickness / 2f
        val offsetX = if (len > 0) -dy / len * halfThickness else 0f
        val offsetY = if (len > 0) dx / len * halfThickness else 0f

        //#if MC<=12105
        //$$RenderUtils
        //$$    .guiStartDraw()
        //$$    .begin(RenderLayers.QUADS_ESP())
        //$$    .colorizeRGBA(color)
        //$$    .translate(0f, 0f, zOffset)
        //$$    .cameraPos(startX + offsetX, startY + offsetY, 0f)
        //$$    .cameraPos(endX + offsetX, endY + offsetY, 0f)
        //$$    .cameraPos(endX - offsetX, endY - offsetY, 0f)
        //$$    .cameraPos(startX - offsetX, startY - offsetY, 0f)
        //$$    .draw()
        //$$    .guiEndDraw()
        //#else
        drawContext.state.addSimpleElement(
            GUILineRenderState(
                drawContext.matrices,
                startX, endX, offsetX,
                startY, endY, offsetY,
                zOffset,
                lineThickness,
                RenderUtils.RGBAColor.fromLongRGBA(color),
                RenderPipelines.GUI,
                TextureSetup.empty(),
                drawContext.scissorStack.peekLast(),
            )
        )
        //#endif
    }

    override fun drawRect(
        drawContext: DrawContext,
        xPosition: Float,
        yPosition: Float,
        width: Float,
        height: Float,
        color: Long,
        zOffset: Float,
    ) {
        //#if MC<=12105
        //$$val x1 = xPosition
        //$$val x2 = xPosition + width
        //$$val y1 = yPosition
        //$$val y2 = yPosition + height
        //$$RenderUtils
        //$$    .guiStartDraw()
        //$$    .begin(RenderLayers.QUADS())
        //$$    .colorizeRGBA(color)
        //$$    .translate(0f, 0f, zOffset)
        //$$    .cameraPos(x1, y2, 0f)
        //$$    .cameraPos(x2, y2, 0f)
        //$$    .cameraPos(x2, y1, 0f)
        //$$    .cameraPos(x1, y1, 0f)
        //$$    .draw()
        //$$    .guiEndDraw()
        //#else
        drawContext.state.addSimpleElement(
            GUIRectRenderState(
                drawContext.matrices,
                xPosition, yPosition, zOffset,
                width, height,
                RenderUtils.RGBAColor.fromLongRGBA(color),
                RenderPipelines.GUI,
                TextureSetup.empty(),
                drawContext.scissorStack.peekLast(),
            )
        )
        //#endif
    }

    override fun drawRoundedRect(
        drawContext: DrawContext,
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

    override fun drawGradient(
        drawContext: DrawContext,
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
//        !! fix with drawContext
        val x2 = x + width
        val y2 = y + height
        RenderUtils
            .guiStartDraw()

            .begin(RenderLayers.QUADS())
            .translate(0f, 0f, zOffset)

        // This is really bad, but it works
        when (direction) {
            RenderUtils.GradientDirection.TOP_TO_BOTTOM,
            RenderUtils.GradientDirection.RIGHT_TO_LEFT,
            RenderUtils.GradientDirection.TOP_RIGHT_TO_BOTTOM_LEFT -> {
                RenderUtils
                    .colorizeRGBA(topLeftColor)
                    .cameraPos(x, y, 0f)
                    .colorizeRGBA(bottomLeftColor)
                    .cameraPos(x, y2, 0f)
                    .colorizeRGBA(bottomRightColor)
                    .cameraPos(x2, y2, 0f)
                    .colorizeRGBA(topRightColor)
                    .cameraPos(x2, y, 0f)
            }

            RenderUtils.GradientDirection.BOTTOM_TO_TOP,
            RenderUtils.GradientDirection.BOTTOM_RIGHT_TO_TOP_LEFT -> {
                RenderUtils
                    .colorizeRGBA(bottomLeftColor)
                    .cameraPos(x, y2, 0f)
                    .colorizeRGBA(topLeftColor)
                    .cameraPos(x, y, 0f)
                    .colorizeRGBA(topRightColor)
                    .cameraPos(x2, y, 0f)
                    .colorizeRGBA(bottomRightColor)
                    .cameraPos(x2, y2, 0f)
            }

            RenderUtils.GradientDirection.LEFT_TO_RIGHT,
            RenderUtils.GradientDirection.BOTTOM_LEFT_TO_TOP_RIGHT -> {
                RenderUtils
                    .colorizeRGBA(topLeftColor)
                    .cameraPos(x, y, 0f)
                    .colorizeRGBA(topRightColor)
                    .cameraPos(x2, y, 0f)
                    .colorizeRGBA(bottomRightColor)
                    .cameraPos(x2, y2, 0f)
                    .colorizeRGBA(bottomLeftColor)
                    .cameraPos(x, y2, 0f)
            }

            RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT -> {
                RenderUtils
                    .colorizeRGBA(topRightColor)
                    .cameraPos(x2, y, 0f)
                    .colorizeRGBA(bottomRightColor)
                    .cameraPos(x2, y2, 0f)
                    .colorizeRGBA(bottomLeftColor)
                    .cameraPos(x, y2, 0f)
                    .colorizeRGBA(topLeftColor)
                    .cameraPos(x, y, 0f)
            }
        }

        RenderUtils
            .draw()
            .guiEndDraw()
    }

    override fun drawCircle(
        drawContext: DrawContext,
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
            .pushMatrix()
            .translate(xPosition + xRotationOffset, yPosition + yRotationOffset, 0f)
            .rotate(rotationDegrees % 360, 0f, 0f, 1f)
            .translate(-xPosition + -xRotationOffset, -yPosition + -yRotationOffset, 0f)
            .begin(RenderLayers.TRIANGLE_STRIP_ESP())
            .colorizeRGBA(color)
            .translate(0f, 0f, zOffset)

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
            .popMatrix()
            .guiEndDraw()
    }

    override fun drawImage(
        drawContext: DrawContext,
        image: Image,
        xPosition: Float,
        yPosition: Float,
        width: Float?,
        height: Float?,
        color: Long,
        zOffset: Float,
    ) {
//        !! fix with drawContext
        val texture = image.getTexture() ?: throw IllegalStateException("Image is null.")

        val identifier = image.getIdentifier()
        val (drawWidth, drawHeight) = image.getImageSize(width, height)

        RenderUtils
            .guiStartDraw()
            //#if MC<12106
            //$$.setShaderTexture(0, texture.glTexture)
            //#else
            .setShaderTexture(0, texture.glTextureView)
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
            .guiEndDraw()
    }
}
//#endif
