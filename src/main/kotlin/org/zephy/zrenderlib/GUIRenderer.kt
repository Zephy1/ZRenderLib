package org.zephy.zrenderlib

//#if MC==10809 || MC>=12100
//#if MC<12100
//$$import net.minecraft.client.renderer.texture.DynamicTexture
//$$import org.lwjgl.opengl.GL11
//#else
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.client.texture.NativeImageBackedTexture
//#if MC<=12105
//$$import net.minecraft.client.font.TextRenderer
//#else
import org.zephy.zrenderlib.renderstates.GUIRenderState
import org.zephy.zrenderlib.renderstates.GradientGUIRenderState
import org.zephy.zrenderlib.renderstates.TexturedGUIRenderState
import net.minecraft.client.gui.render.state.TextGuiElementRenderState
import net.minecraft.client.texture.TextureSetup
import org.joml.Matrix3x2f
//#endif
//#endif

object GUIRenderer : BaseGUIRenderer() {
    override fun drawString(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        text: String,
        xPosition: Float,
        yPosition: Float,
        color: Long,
        textScale: Float,
        renderBackground: Boolean,
        textShadow: Boolean,
        maxWidth: Int,
        zOffset: Float
    ) {
        //#if MC<12100
        //$$val (a, r, g, b) = RenderUtils.RGBAColor.fromLongRGBA(color).getIntComponentsARGB()
        //$$if (a == 0) return
        //$$val safeAlpha = if (a in 1..3) 4 else a
        //$$val safeColorIntARGB = RenderUtils.ARGBColor(r, g, b, safeAlpha).getIntARGB()
        //$$val backgroundColorLong = RenderUtils.ARGBColor(0, 0, 0, 150).getLongRGBA()
        //$$val fontRenderer = RenderUtils.getTextRenderer()
        //$$var currentY = 0f
        //$$RenderUtils
        //$$    .pushMatrix()
        //$$    .translate(xPosition, yPosition, zOffset)
        //$$    .scale(textScale, textScale, 1f)
        //$$    .addColor(text).split("\n").forEach { line ->
        //$$        if (renderBackground) {
        //$$            val textWidth = fontRenderer.getStringWidth(line)
        //$$            drawRect(
        //$$                -1f,
        //$$                currentY - 1f,
        //$$                textWidth + 1f,
        //$$                fontRenderer.FONT_HEIGHT + 1f,
        //$$                backgroundColorLong,
        //$$                0f,
        //$$            )
        //$$        }
        //$$        fontRenderer.drawString(line, 0f, currentY, safeColorIntARGB, textShadow)
        //$$        currentY += fontRenderer.FONT_HEIGHT
        //$$    }
        //$$RenderUtils.popMatrix()
        //#else
        drawString(drawContext, Text.of(text), xPosition, yPosition, color, textScale, renderBackground, textShadow, maxWidth, zOffset)
        //#endif
    }

    //#if MC>=12105
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
        val (a, r, g, b) = RenderUtils.RGBAColor.fromLongRGBA(color).getIntComponentsARGB()
        if (a == 0) return
        val safeAlpha = if (a in 1..3) 4 else a
        val safeColorIntARGB = RenderUtils.ARGBColor(r, g, b, safeAlpha).getIntARGB()
        val backgroundColor = if (renderBackground) {
            RenderUtils.ARGBColor(0, 0, 0, 150)
        } else {
            RenderUtils.ARGBColor(0, 0, 0, 0)
        }
        val backgroundColorInt = backgroundColor.getIntARGB()

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
        //$$        safeColorIntARGB,
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
        val backgroundColorLong = backgroundColor.getLongRGBA()
        lines.forEach { line ->
            val matrix = Matrix3x2f()
            matrix.translate(xPosition, yPosition + currentY)
            matrix.scale(textScale, textScale)

            // backgroundColor isn't rendered on 1.21.9+?
            //#if MC>=12109
            if (renderBackground) {
                val textWidth = fontRenderer.getWidth(line)
                drawRect(
                    drawContext,
                    xPosition - (1f * textScale),
                    yPosition + currentY - (1f * textScale),
                    (textWidth + 1f) * textScale,
                    (fontRenderer.fontHeight + 1f) * textScale,
                    backgroundColorLong,
                    0f,
                )
            }
            //#endif

            val textState = TextGuiElementRenderState(
                fontRenderer,
                line.asOrderedText(),
                matrix,
                0,
                0,
                safeColorIntARGB,
                backgroundColorInt,
                textShadow,
                drawContext.scissorStack.peekLast()
            )
            drawContext.state.addText(textState)
            currentY += fontRenderer.fontHeight * textScale
        }
        //#endif
    }
    //#endif

    override fun _drawLine(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        vertexList: List<Pair<Float, Float>>,
        color: Long,
        zOffset: Float,
    ) {
        //#if MC<=12105
        //$$RenderUtils
        //$$    .guiStartDraw()
        //#if MC<12100
        //$$    .begin(GL11.GL_QUADS, VertexFormat.POSITION_COLOR)
        //#else
        //$$    .begin(RenderLayers.QUADS_ESP())
        //#endif
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

    override fun _drawRect(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        vertexList: List<Pair<Float, Float>>,
        color: Long,
        zOffset: Float,
    ) {
        //#if MC<=12105
        //$$RenderUtils
        //$$    .guiStartDraw()
        //#if MC<12100
        //$$    .begin(GL11.GL_QUADS, VertexFormat.POSITION_COLOR)
        //#else
        //$$    .begin(RenderLayers.QUADS_ESP())
        //#endif
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

    override fun _drawRoundedRect(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        vertexList: List<Pair<Float, Float>>,
        color: Long,
        zOffset: Float,
    ) {
        //#if MC<=12105
        //$$RenderUtils
        //$$    .guiStartDraw()
        //#if MC<12100
        //$$    .begin(GL11.GL_QUADS, VertexFormat.POSITION_COLOR)
        //#else
        //$$    .begin(RenderLayers.QUADS_ESP())
        //#endif
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

    override fun _drawGradient(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        vertexAndColorList: List<Triple<Float, Float, Long>>,
        zOffset: Float,
    ) {
        //#if MC<=12105
        //$$RenderUtils
        //$$    .guiStartDraw()
        //#if MC<12100
        //$$    .begin(GL11.GL_QUADS, VertexFormat.POSITION_COLOR)
        //$$    .shadeModel(GL11.GL_SMOOTH)
        //#else
        //$$    .begin(RenderLayers.QUADS_ESP())
        //#endif
        //$$vertexAndColorList.forEach { (x, y, color) ->
        //$$    RenderUtils
        //$$        .colorizeRGBA(color)
        //$$        .cameraPos(x, y, zOffset)
        //$$}
        //$$RenderUtils
        //$$    .draw()
        //#if MC<12100
        //$$    .shadeModel(GL11.GL_FLAT)
        //#endif
        //$$    .guiEndDraw()
        //#else
        val boundsList = vertexAndColorList.map { (x, y, _) -> Pair(x, y) }
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

    override fun _drawCircle(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        minX: Float,
        maxX: Float,
        minY: Float,
        maxY: Float,
        vertexList: List<Pair<Float, Float>>,
        color: Long,
        zOffset: Float,
    ) {
        //#if MC<=12105
        //$$RenderUtils
        //$$    .guiStartDraw()
        //$$    .pushMatrix()
        //#if MC<12100
        //$$    .begin(GL11.GL_QUADS, VertexFormat.POSITION_COLOR)
        //#else
        //$$    .begin(RenderLayers.QUADS_ESP())
        //#endif
        //$$    .colorizeRGBA(color)
        //$$    .translate(0f, 0f, zOffset)
        //$$    .cameraPosList(vertexList, 0f)
        //$$    .draw()
        //$$    .popMatrix()
        //$$    .guiEndDraw()
        //#else
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

    override fun _drawImage(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        //#if MC<12100
        //$$texture: DynamicTexture,
        //#else
        image: Image,
        texture: NativeImageBackedTexture,
        //#endif
        vertexList: List<Pair<Float, Float>>,
        uvList: List<Pair<Float, Float>>,
        color: Long,
        zOffset: Float,
    ) {
        //#if MC<=12105
        //$$RenderUtils
        //$$    .guiStartDraw()
        //$$    .scale(1f, 1f, 50f)
        //#if MC<12100
        //$$    .bindTexture(texture.getGlTextureId())
        //$$    .enableTexture2D()
        //$$    .resetColor()
        //$$    .begin(GL11.GL_QUADS, VertexFormat.POSITION_TEX_COLOR)
        //#else
        //$$    .setShaderTexture(0, texture.glTexture)
        //$$    .begin(RenderLayers.TEXTURED_QUADS_ESP(textureIdentifier = image.getIdentifier()!!))
        //#endif
        //$$    .translate(0f, 0f, zOffset)
        //$$uvList.forEachIndexed { index, (u, v) ->
        //$$    val (x, y) = vertexList[index]
        //$$    RenderUtils
        //$$        .cameraPos(x, y, 0f, false)
        //$$        .tex(u, v)
        //$$        .colorRGBA(color)
        //$$        .endVertex()
        //$$}
        //$$RenderUtils
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
