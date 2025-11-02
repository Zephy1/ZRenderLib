package org.zephy.zrenderlib.renderstates

//#if MC>=12106
import com.mojang.blaze3d.pipeline.RenderPipeline
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.texture.TextureSetup
import org.joml.Matrix3x2f
import org.zephy.zrenderlib.RenderUtils

data class GUIRenderState(
    val matrix: Matrix3x2f,
    val vertexList: List<Pair<Float, Float>>,
    val boundsList: List<Pair<Float, Float>>,
    val zOffset: Float,
    val color: RenderUtils.RenderColor,
    val pipeline: RenderPipeline,
    val textureSetup: TextureSetup,
    val scissorArea: ScreenRect?
) : SimpleGuiElementRenderState {
    //#if MC<12110
    //$$override fun setupVertices(vertices: VertexConsumer, depth: Float) {
    //$$    val zPosition = depth + zOffset
    //#else
    override fun setupVertices(vertices: VertexConsumer) {
        val zPosition = zOffset
    //#endif
        val newMatrix = RenderUtils.getGUIMatrix(matrix)
        val (r, g, b, a) = color.getIntComponentsRGBA()
        vertexList.forEach { (x, y) ->
            vertices.vertex(newMatrix, x, y, zPosition).color(r, g, b, a)
        }
    }

    override fun pipeline(): RenderPipeline = pipeline
    override fun textureSetup(): TextureSetup = textureSetup
    override fun scissorArea(): ScreenRect? = scissorArea
    override fun bounds(): ScreenRect? {
        if (boundsList.isEmpty()) return null

        val (minX, minY) = boundsList[0]
        val (maxX, maxY) = boundsList[2]

        val rect = ScreenRect(
            minX.toInt(),
            minY.toInt(),
            (maxX - minX).toInt(),
            (maxY - minY + 1).toInt(),
        ).transformEachVertex(matrix)

        return scissorArea?.intersection(rect) ?: rect
    }
}
//#endif
