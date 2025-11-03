package org.zephy.zrenderlib

//#if MC>=12100
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import java.awt.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

//#if MC<=12105
//$$import net.minecraft.client.font.TextRenderer
//#else
import net.minecraft.client.gui.render.state.TextGuiElementRenderState
import net.minecraft.client.texture.TextureSetup
import org.joml.Matrix3x2f
import org.zephy.zrenderlib.renderstates.*
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
        zOffset: Float = 0f, // Useless in 1.21.6+, text is drawn on top of all elements
    ) {
        // TextRender.tweakTransparency gets called in TextRender.draw resetting the alpha value to 255 if it's less than 4
        val (a, r, g, b) = RenderUtils.RGBAColor.fromLongRGBA(color).getIntComponentsARGB()
        if (a == 0) {
            return
        }
        val safeAlpha = if (a in 1..3) 4 else a
        val safeColorARGB = RenderUtils.ARGBColor(r, g, b, safeAlpha).getIntARGB()
        val backgroundColorInt = if (renderBackground) {
            Color(0, 0, 0, 150).rgb
        } else {
            0
        }

        val fontRenderer = RenderUtils.getTextRenderer()
        var currentY = 0f
        val lines = RenderUtils.splitText(text, maxWidth).lines

        //#if MC<=12105
        //$$val vertexConsumers = Client.getMinecraft().bufferBuilders.entityVertexConsumers
        //$$RenderUtils
        //$$    .pushMatrix()
        //$$    .translate(xPosition, yPosition, zOffset)
        //$$val positionMatrix = RenderUtils.matrixStack.peek().model
        //$$positionMatrix.scale(textScale, textScale, 1f)
        //$$lines.forEach { line ->
        //$$    fontRenderer.draw(
        //$$        line,
        //$$        0f,
        //$$        currentY,
        //$$        safeColorARGB,
        //$$        textShadow,
        //$$        positionMatrix,
        //$$        vertexConsumers,
        //$$        TextRenderer.TextLayerType.NORMAL,
        //$$        backgroundColorInt,
        //$$        0xF000F0,
        //$$    )
        //$$    currentY += fontRenderer.fontHeight
        //$$}
        //$$vertexConsumers.draw()
        //$$positionMatrix.scale(1f / textScale, 1f / textScale, 1f)
        //$$RenderUtils.guiEndDraw()
        //#else
        lines.forEach { line ->
            val matrix = Matrix3x2f()
            matrix.translate(xPosition, yPosition + currentY)
            matrix.scale(textScale, textScale)

            val textState = TextGuiElementRenderState(
                fontRenderer,
                line,
                matrix,
                0,
                0,
                safeColorARGB,
                backgroundColorInt,
                textShadow,
                drawContext.scissorStack.peekLast()
            )
            drawContext.state.addText(textState)
            currentY += fontRenderer.fontHeight * textScale
        }
        //#endif
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

        val vertexList = listOf(
            Pair(startX + offsetX, startY + offsetY),
            Pair(endX + offsetX, endY + offsetY),
            Pair(endX - offsetX, endY - offsetY),
            Pair(startX - offsetX, startY - offsetY)
        )

        //#if MC<=12105
        //$$RenderUtils
        //$$    .guiStartDraw()
        //$$    .begin(RenderLayers.QUADS_ESP())
        //$$    .colorizeRGBA(color)
        //$$    .translate(0f, 0f, zOffset)
        //$$    .cameraPosList(vertexList, 0f)
        //$$    .draw()
        //$$    .guiEndDraw()
        //#else
        val boundsList = vertexList.toList()
        drawContext.state.addSimpleElement(
            GUIRenderState(
                drawContext.matrices,
                vertexList,
                boundsList,
                zOffset,
                RenderUtils.RGBAColor.fromLongRGBA(color),
                RenderPipelines.QUADS_ESP().build(),
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
        val x1 = xPosition
        val x2 = xPosition + width
        val y1 = yPosition
        val y2 = yPosition + height
        val vertexList = listOf(
            Pair(x1, y1),
            Pair(x2, y1),
            Pair(x2, y2),
            Pair(x1, y2)
        )

        //#if MC<=12105
        //$$RenderUtils
        //$$    .guiStartDraw()
        //$$    .begin(RenderLayers.QUADS())
        //$$    .colorizeRGBA(color)
        //$$    .translate(0f, 0f, zOffset)
        //$$    .cameraPosList(vertexList, 0f)
        //$$    .draw()
        //$$    .guiEndDraw()
        //#else
        val boundsList = vertexList.toList()
        drawContext.state.addSimpleElement(
            GUIRenderState(
                drawContext.matrices,
                vertexList,
                boundsList,
                zOffset,
                RenderUtils.RGBAColor.fromLongRGBA(color),
                RenderPipelines.QUADS_ESP().build(),
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
        val x1 = xPosition
        val y1 = yPosition
        val x2 = xPosition + width
        val y2 = yPosition + height

        val centerX = (x1 + x2) / 2f
        val centerY = (y1 + y2) / 2f
        val clampedRadius = radius.coerceAtMost(minOf(width, height) / 2f)
        val flatCornersSet = flatCorners.toSet()

        val edgeVertices = mutableListOf<Pair<Float, Float>>()
        fun addCornerVertices(cx: Float, cy: Float, r: Float, startAngle: Float, endAngle: Float, segs: Int) {
            val angleStep = (endAngle - startAngle) / segs
            for (i in 1..segs) {
                val angle = Math.toRadians((startAngle + angleStep * i).toDouble())
                val x = cx + (r * cos(angle)).toFloat()
                val y = cy + (r * sin(angle)).toFloat()
                edgeVertices.add(Pair(x, y))
            }
        }

        edgeVertices.add(Pair(x2 - clampedRadius, y1))
        if (RenderUtils.FlattenRoundedRectCorner.TOP_RIGHT in flatCornersSet) {
            edgeVertices.add(Pair(x2, y1))
        } else {
            addCornerVertices(x2 - clampedRadius, y1 + clampedRadius, clampedRadius, 270f, 360f, segments)
            edgeVertices.add(Pair(x2, y1 + clampedRadius))
        }

        edgeVertices.add(Pair(x2, y2 - clampedRadius))
        if (RenderUtils.FlattenRoundedRectCorner.BOTTOM_RIGHT in flatCornersSet) {
            edgeVertices.add(Pair(x2, y2))
        } else {
            addCornerVertices(x2 - clampedRadius, y2 - clampedRadius, clampedRadius, 0f, 90f, segments)
            edgeVertices.add(Pair(x2 - clampedRadius, y2))
        }

        edgeVertices.add(Pair(x1 + clampedRadius, y2))
        if (RenderUtils.FlattenRoundedRectCorner.BOTTOM_LEFT in flatCornersSet) {
            edgeVertices.add(Pair(x1, y2))
        } else {
            addCornerVertices(x1 + clampedRadius, y2 - clampedRadius, clampedRadius, 90f, 180f, segments)
            edgeVertices.add(Pair(x1, y2 - clampedRadius))
        }

        edgeVertices.add(Pair(x1, y1 + clampedRadius))
        if (RenderUtils.FlattenRoundedRectCorner.TOP_LEFT in flatCornersSet) {
            edgeVertices.add(Pair(x1, y1))
        } else {
            addCornerVertices(x1 + clampedRadius, y1 + clampedRadius, clampedRadius, 180f, 270f, segments)
            edgeVertices.add(Pair(x1 + clampedRadius, y1))
        }
        edgeVertices.add(Pair(x2 - clampedRadius, y1))

        val vertexList = mutableListOf<Pair<Float, Float>>()
        for (i in 0 until edgeVertices.size - 1) {
            vertexList.add(Pair(centerX, centerY))
            vertexList.add(edgeVertices[i])
            vertexList.add(edgeVertices[i + 1])
            vertexList.add(edgeVertices[i])
        }

        //#if MC<=12105
        //$$RenderUtils
        //$$    .guiStartDraw()
        //$$    .begin(RenderLayers.QUADS_ESP())
        //$$    .colorizeRGBA(color)
        //$$    .translate(0f, 0f, zOffset)
        //$$    .cameraPosList(vertexList, 0f)
        //$$    .draw()
        //$$    .guiEndDraw()
        //#else
        val boundsList = listOf(
            Pair(x1, y1),
            Pair(x2, y1),
            Pair(x2, y2),
            Pair(x1, y2)
        )

        drawContext.state.addSimpleElement(
            GUIRenderState(
                drawContext.matrices,
                vertexList,
                boundsList,
                zOffset,
                RenderUtils.RGBAColor.fromLongRGBA(color),
                RenderPipelines.QUADS_ESP().build(),
                drawContext.scissorStack.peekLast(),
            )
        )
        //#endif
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
        val x2 = x + width
        val y2 = y + height

        // This is really bad, but it works
        val vertexAndColorList = when (direction) {
            RenderUtils.GradientDirection.TOP_TO_BOTTOM,
            RenderUtils.GradientDirection.RIGHT_TO_LEFT,
            RenderUtils.GradientDirection.TOP_RIGHT_TO_BOTTOM_LEFT -> {
                listOf(
                    Triple(x, y, topLeftColor),
                    Triple(x, y2, bottomLeftColor),
                    Triple(x2, y2, bottomRightColor),
                    Triple(x2, y, topRightColor),
                )
            }

            RenderUtils.GradientDirection.BOTTOM_TO_TOP,
            RenderUtils.GradientDirection.BOTTOM_RIGHT_TO_TOP_LEFT -> {
                listOf(
                    Triple(x, y2, bottomLeftColor),
                    Triple(x, y, topLeftColor),
                    Triple(x2, y, topRightColor),
                    Triple(x2, y2, bottomRightColor),
                )
            }

            RenderUtils.GradientDirection.LEFT_TO_RIGHT,
            RenderUtils.GradientDirection.BOTTOM_LEFT_TO_TOP_RIGHT -> {
                listOf(
                    Triple(x, y, topLeftColor),
                    Triple(x2, y, topRightColor),
                    Triple(x2, y2, bottomRightColor),
                    Triple(x, y2, bottomLeftColor),
                )
            }

            RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT -> {
                listOf(
                    Triple(x2, y, topRightColor),
                    Triple(x2, y2, bottomRightColor),
                    Triple(x, y2, bottomLeftColor),
                    Triple(x, y, topLeftColor),
                )
            }
        }

        //#if MC<=12105
        //$$RenderUtils
        //$$    .guiStartDraw()
        //$$    .begin(RenderLayers.QUADS_ESP())
        //$$    .translate(0f, 0f, zOffset)
        //$$vertexAndColorList.forEach { (x, y, color) ->
        //$$    RenderUtils
        //$$        .colorizeRGBA(color)
        //$$        .cameraPos(x, y, 0f)
        //$$}
        //$$RenderUtils
        //$$    .draw()
        //$$    .guiEndDraw()
        //#else
        val boundsList = listOf(
            Pair(x, y),
            Pair(x2, y),
            Pair(x2, y2),
            Pair(x, y2)
        )

        drawContext.state.addSimpleElement(
            GradientGUIRenderState(
                GUIRenderState(
                    drawContext.matrices,
                    listOf(),
                    boundsList,
                    zOffset,
                    RenderUtils.RGBAColor(255, 255, 255, 255),
                    RenderPipelines.QUADS_ESP().build(),
                    drawContext.scissorStack.peekLast(),
                ),
                vertexAndColorList,
            )
        )
        //#endif
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
        val angleStep = 2f * PI.toFloat() / edges
        val rotationRadians = rotationDegrees * PI.toFloat() / 180f
        val cos = cos(rotationRadians)
        val sin = sin(rotationRadians)

        val centerX = xPosition + xRotationOffset
        val centerY = yPosition + yRotationOffset

        val vertexList = mutableListOf<Pair<Float, Float>>()

        val edgeVertices = Array(edges) { i ->
            val angle = i * angleStep
            val baseX = cos(angle) * xScale
            val baseY = sin(angle) * yScale
            val relX = baseX - xRotationOffset
            val relY = baseY - yRotationOffset
            val x = relX * cos - relY * sin + xRotationOffset + xPosition
            val y = relX * sin + relY * cos + yRotationOffset + yPosition
            Pair(x, y)
        }

        for (i in 0 until edges) {
            val nextIndex = (i + 1) % edges
            vertexList.add(Pair(centerX, centerY))
            vertexList.add(edgeVertices[i])
            vertexList.add(edgeVertices[nextIndex])
            vertexList.add(edgeVertices[i])
        }

        //#if MC<=12105
        //$$RenderUtils
        //$$    .guiStartDraw()
        //$$    .pushMatrix()
        //$$    .begin(RenderLayers.QUADS_ESP())
        //$$    .colorizeRGBA(color)
        //$$    .translate(0f, 0f, zOffset)
        //$$    .cameraPosList(vertexList, 0f)
        //$$    .draw()
        //$$    .popMatrix()
        //$$    .guiEndDraw()
        //#else
        val minX = xPosition - xScale
        val maxX = xPosition + xScale
        val minY = yPosition - yScale
        val maxY = yPosition + yScale
        val boundsList = listOf(
            Pair(minX, minY),
            Pair(maxX, minY),
            Pair(maxX, maxY),
            Pair(minX, maxY)
        )

        drawContext.state.addSimpleElement(
            GUIRenderState(
                drawContext.matrices,
                vertexList,
                boundsList,
                zOffset,
                RenderUtils.RGBAColor.fromLongRGBA(color),
                RenderPipelines.QUADS_ESP().build(),
                drawContext.scissorStack.peekLast(),
            )
        )
        //#endif
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
        val texture = image.getTexture() ?: throw IllegalStateException("Image is null.")
        val (drawWidth, drawHeight) = image.getImageSize(width, height)

        val vertexList = listOf(
            Pair(xPosition, yPosition + drawHeight),
            Pair(xPosition + drawWidth, yPosition + drawHeight),
            Pair(xPosition + drawWidth, yPosition),
            Pair(xPosition, yPosition)
        )
        val uvList = listOf(
            Pair(0f, 1f),
            Pair(1f, 1f),
            Pair(1f, 0f),
            Pair(0f, 0f)
        )

        //#if MC<=12105
        //$$RenderUtils
        //$$    .guiStartDraw()
        //$$    .setShaderTexture(0, texture.glTexture)
        //$$    .scale(1f, 1f, 50f)
        //$$    .begin(RenderLayers.TEXTURED_QUADS_ESP(textureIdentifier = image.getIdentifier()!!))
        //$$    .colorizeRGBA(color)
        //$$    .translate(0f, 0f, zOffset)
        //$$    .cameraPos(vertexList[0].first, vertexList[0].second, 0f).tex(uvList[0].first, uvList[0].second)
        //$$    .cameraPos(vertexList[1].first, vertexList[1].second, 0f).tex(uvList[1].first, uvList[1].second)
        //$$    .cameraPos(vertexList[2].first, vertexList[2].second, 0f).tex(uvList[2].first, uvList[2].second)
        //$$    .cameraPos(vertexList[3].first, vertexList[3].second, 0f).tex(uvList[3].first, uvList[3].second)
        //$$    .draw()
        //$$    .guiEndDraw()
        //#else
        val boundsList = vertexList.toList()
        drawContext.state.addSimpleElement(
            TexturedGUIRenderState(
                GUIRenderState(
                    drawContext.matrices,
                    vertexList,
                    boundsList,
                    zOffset,
                    RenderUtils.RGBAColor.fromLongRGBA(color),
                    RenderPipelines.TEXTURED_QUADS_ESP().build(),
                    drawContext.scissorStack.peekLast()
                ),
                TextureSetup.withoutGlTexture(texture.glTextureView),
                uvList,
            )
        )
        //#endif
    }
}
//#endif
