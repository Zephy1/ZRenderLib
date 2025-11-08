package org.zephy.zrenderlib

//#if MC==10809 || MC>=12100
//#if MC<12100
//$$import org.lwjgl.opengl.GL11
//#else
import java.awt.Color
import org.joml.Matrix4f
import net.minecraft.text.Text
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.font.TextRenderer
//#endif

object WorldRenderer : BaseWorldRenderer() {
    override fun drawString(text: String, xPosition: Float, yPosition: Float, zPosition: Float, color: Long, scale: Float, renderBackground: Boolean, centered: Boolean, textShadow: Boolean, disableDepth: Boolean, maxWidth: Int) {
        //#if MC<12100
        //$$val fontRenderer = RenderUtils.getTextRenderer()
        //$$val renderManager = RenderUtils.renderManager
        //$$val x = xPosition - renderManager.viewerPosX
        //$$val y = yPosition - renderManager.viewerPosY
        //$$val z = zPosition - renderManager.viewerPosZ
        //$$val xMultiplier = if (Client.getMinecraft().gameSettings.thirdPersonView == 2) -1 else 1
        //$$val adjustedScale = (scale * 0.05).toFloat()
        //$$val textWidth = fontRenderer.getStringWidth(text)
        //$$val j = textWidth / 2f
        //$$RenderUtils.pushMatrix()
        //$$GL11.glNormal3f(0f, 1f, 0f)
        //$$if (disableDepth) RenderUtils.disableDepth()
        //$$RenderUtils
        //$$    .colorize_01(1f, 1f, 1f, 0.5f)
        //$$    .disableCull()
        //$$    .translate(x, y, z)
        //$$    .rotate(-RenderUtils.renderManager.playerViewY, 0f, 1f, 0f)
        //$$    .rotate(
        //$$        RenderUtils.renderManager.playerViewX * xMultiplier,
        //$$        1f,
        //$$        0f,
        //$$        0f,
        //$$    )
        //$$    .scale(-adjustedScale, -adjustedScale, adjustedScale)
        //$$    .disableLighting()
        //$$    .depthMask(false)
        //$$    .enableBlend()
        //$$    .tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
        //$$val xOffset = if (centered) -j else 0f
        //$$if (renderBackground) {
        //$$    RenderUtils
        //$$        .disableTexture2D()
        //$$        .begin(GL11.GL_QUADS, VertexFormat.POSITION_COLOR)
        //$$        .colorize_01(0.0f, 0.0f, 0.0f, 0.25f)
        //$$        .pos(xOffset - 1f, -1f, 0f)
        //$$        .pos(xOffset - 1f, 8f, 0f)
        //$$        .pos(xOffset + 1f, 8f, 0f)
        //$$        .pos(xOffset + 1f, -1f, 0f)
        //$$        .draw()
        //$$        .enableTexture2D()
        //$$}
        //$$fontRenderer.drawString(
        //$$    text,
        //$$    xOffset,
        //$$    0f,
        //$$    RenderUtils.ARGBColor.fromLongRGBA(color).getLong().toInt(),
        //$$    textShadow
        //$$)
        //$$RenderUtils.worldEndDraw()
        //#else
        drawString(Text.of(text), xPosition, yPosition, zPosition, color, scale, renderBackground, centered, textShadow, disableDepth, maxWidth)
        //#endif
    }

    //#if MC>=12100
    @JvmStatic
    fun getLineRenderLayer(disableDepth: Boolean) = if (disableDepth) RenderLayers.LINES_ESP() else RenderLayers.LINES()

    @JvmStatic
    fun getQuadRenderLayer(disableDepth: Boolean) = if (disableDepth) RenderLayers.QUADS_ESP() else RenderLayers.QUADS()

    @JvmStatic
    fun getTriangleStripRenderLayer(disableDepth: Boolean) = if (disableDepth) RenderLayers.TRIANGLE_STRIP_ESP() else RenderLayers.TRIANGLE_STRIP()

    @JvmStatic
    fun getTriangleRenderLayer(disableDepth: Boolean) = if (disableDepth) RenderLayers.TRIANGLES_ESP() else RenderLayers.TRIANGLES()

    @JvmStatic
    @JvmOverloads
    fun drawStringRGBA(text: Text, xPosition: Float, yPosition: Float, zPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, scale: Float = 1f, renderBackground: Boolean = false, centered: Boolean = false, textShadow: Boolean = true, disableDepth: Boolean = false, maxWidth: Int = 512) {
        drawString(text, xPosition, yPosition, zPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), scale, renderBackground, centered, textShadow, disableDepth, maxWidth)
    }

    @JvmStatic
    @JvmOverloads
    fun drawString(
        text: Text,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        scale: Float = 1f,
        renderBackground: Boolean = false,
        centered: Boolean = false,
        textShadow: Boolean = true,
        disableDepth: Boolean = false,
        maxWidth: Int = 512,
    ) {
        val (lines, width, height) = RenderUtils.splitText(text, maxWidth)
        val fontRenderer = RenderUtils.getTextRenderer()
        val camera = Client.getMinecraft().gameRenderer.camera
        val cameraPos = camera.pos
        val vertexConsumers = Client.getMinecraft().bufferBuilders.entityVertexConsumers

        val matrix = Matrix4f()
        val adjustedScale = (scale * 0.05).toFloat()
        val xShift = -width / 2
        val yShift = -height / 2
        var yOffset = 0
        val backgroundColorInt = if (renderBackground) {
            Color(0, 0, 0, 150).rgb
        } else {
            Color(0, 0, 0, 0).rgb
        }

        RenderUtils.baseStartDraw()
        for (line in lines) {
            matrix
                .translate(
                    (xPosition - cameraPos.getX()).toFloat(),
                    (yPosition - cameraPos.getY() + yOffset * adjustedScale).toFloat(),
                    (zPosition - cameraPos.getZ()).toFloat(),
                )
                .rotate(Client.getMinecraft().gameRenderer.camera.rotation)
                .scale(adjustedScale, -adjustedScale, adjustedScale)

            val centerShift = if (centered) {
                xShift + (fontRenderer.getWidth(line) / 2f)
            } else {
                0f
            }

            fontRenderer.draw(
                line,
                xShift - centerShift,
                yShift + yOffset,
                RenderUtils.ARGBColor.fromLongRGBA(color).getLong().toInt(),
                textShadow,
                matrix,
                vertexConsumers,
                if (disableDepth) TextRenderer.TextLayerType.SEE_THROUGH else TextRenderer.TextLayerType.NORMAL,
                backgroundColorInt,
                LightmapTextureManager.MAX_LIGHT_COORDINATE,
            )

            yOffset += fontRenderer.fontHeight + 1
        }
        RenderUtils.worldEndDraw()
    }
    //#endif

    override fun _drawLine(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
        lineThickness: Float,
    ) {
        //#if MC<12100
        //$$val drawMode = GL11.GL_LINE_STRIP
        //$$val cameraPos = RenderUtils.getCameraPos()
        //#else
        val renderLayer = getLineRenderLayer(disableDepth)
        //#endif

        RenderUtils.baseStartDraw()
        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils
            .lineWidth(lineThickness)
            //#if MC<12100
            //$$.disableTexture2D()
            //$$.translate(
            //$$    -cameraPos.x,
            //$$    -cameraPos.y,
            //$$    -cameraPos.z,
            //$$)
            //$$.begin(drawMode, VertexFormat.POSITION_COLOR)
            //#else
            .begin(renderLayer)
            //#endif
            .colorizeRGBA(color)
        vertexAndNormalList.forEach { (x, y, z, normalVector) ->
            RenderUtils
                .pos(x, y, z)
                //#if MC>=12100
                .normal(normalVector)
                //#endif
        }
        RenderUtils
            .draw()
            .worldEndDraw()
    }

    override fun _drawBox(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
        lineThickness: Float,
    ) {
        //#if MC<12100
        //$$val drawMode = if (wireframe) GL11.GL_LINES else GL11.GL_TRIANGLE_STRIP
        //$$val cameraPos = RenderUtils.getCameraPos()
        //#else
        val renderLayer = when {
            !wireframe -> getTriangleStripRenderLayer(disableDepth)
            else -> getLineRenderLayer(disableDepth)
        }
        //#endif

        RenderUtils.baseStartDraw()
        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils
            .lineWidth(lineThickness)
            //#if MC<12100
            //$$.disableTexture2D()
            //$$.translate(
            //$$    -cameraPos.x,
            //$$    -cameraPos.y,
            //$$    -cameraPos.z,
            //$$)
            //$$.begin(drawMode, VertexFormat.POSITION_COLOR)
            //#else
            .begin(renderLayer)
            //#endif
            .colorizeRGBA(color)
        vertexAndNormalList.forEach { (x, y, z, normalVector) ->
            RenderUtils
                .pos(x, y, z)
                //#if MC>=12100
                .normal(normalVector)
                //#endif
        }
        RenderUtils
            .draw()
            .worldEndDraw()
    }

    // Normals are a bit wrong here, fine enough
    override fun _drawSphere(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
        lineThickness: Float,
    ) {
        //#if MC<12100
        //$$val drawMode = if (wireframe) GL11.GL_LINES else GL11.GL_QUADS
        //$$val cameraPos = RenderUtils.getCameraPos()
        //#else
        val renderLayer = when {
            !wireframe -> getQuadRenderLayer(disableDepth)
            else -> getLineRenderLayer(disableDepth)
        }
        //#endif

        RenderUtils.baseStartDraw()
        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils
            .lineWidth(lineThickness)
            //#if MC<12100
            //$$.disableTexture2D()
            //$$.translate(
            //$$    -cameraPos.x,
            //$$    -cameraPos.y,
            //$$    -cameraPos.z,
            //$$)
            //$$.begin(drawMode, VertexFormat.POSITION_COLOR)
            //#else
            .begin(renderLayer)
            //#endif
            .colorizeRGBA(color)
        vertexAndNormalList.forEach { (x, y, z, normalVector) ->
            RenderUtils
                .pos(x, y, z)
                //#if MC>=12100
                .normal(normalVector)
                //#endif
        }
        RenderUtils
            .draw()
            .worldEndDraw()
    }

    // Normals are a bit wrong here, fine enough
    override fun _drawCylinder(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
        lineThickness: Float,
    ) {
        //#if MC<12100
        //$$val drawMode = if (wireframe) GL11.GL_LINES else GL11.GL_QUADS
        //$$val cameraPos = RenderUtils.getCameraPos()
        //#else
        val renderLayer = when {
            !wireframe -> getQuadRenderLayer(disableDepth)
            else -> getLineRenderLayer(disableDepth)
        }
        //#endif

        RenderUtils.baseStartDraw()
        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils
            .lineWidth(lineThickness)
            //#if MC<12100
            //$$.disableTexture2D()
            //$$.translate(
            //$$    -cameraPos.x,
            //$$    -cameraPos.y,
            //$$    -cameraPos.z,
            //$$)
            //$$.begin(drawMode, VertexFormat.POSITION_COLOR)
            //#else
            .begin(renderLayer)
            //#endif
            .colorizeRGBA(color)
        vertexAndNormalList.forEach { (x, y, z, normalVector) ->
            RenderUtils
                .pos(x, y, z)
                //#if MC>=12100
                .normal(normalVector)
                //#endif
        }
        RenderUtils
            .draw()
            .worldEndDraw()
    }

    override fun _drawPyramid(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
        lineThickness: Float,
    ) {
        //#if MC<12100
        //$$val drawMode = if (wireframe) GL11.GL_LINES else GL11.GL_TRIANGLES
        //$$val cameraPos = RenderUtils.getCameraPos()
        //#else
        val renderLayer = when {
            !wireframe -> getTriangleRenderLayer(disableDepth)
            else -> getLineRenderLayer(disableDepth)
        }
        //#endif

        RenderUtils.baseStartDraw()
        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils
            .lineWidth(lineThickness)
            //#if MC<12100
            //$$.disableTexture2D()
            //$$.translate(
            //$$    -cameraPos.x,
            //$$    -cameraPos.y,
            //$$    -cameraPos.z,
            //$$)
            //$$.begin(drawMode, VertexFormat.POSITION_COLOR)
            //#else
            .begin(renderLayer)
            //#endif
            .colorizeRGBA(color)
        vertexAndNormalList.forEach { (x, y, z, normalVector) ->
            RenderUtils
                .pos(x, y, z)
                //#if MC>=12100
                .normal(normalVector)
                //#endif
        }
        RenderUtils
            .draw()
            .worldEndDraw()
    }

    override fun _drawTracer(
        partialTicks: Float,
        startPosX: Float,
        startPosY: Float,
        startPosZ: Float,
        endPosX: Float,
        endPosY: Float,
        endPosZ: Float,
        color: Long,
        disableDepth: Boolean,
        lineThickness: Float,
    ) {
        drawLine(startPosX, startPosY, startPosZ, endPosX, endPosY, endPosZ, color, disableDepth, lineThickness)
    }
}
//#endif
