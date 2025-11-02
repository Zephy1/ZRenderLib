package org.zephy.zrenderlib.renderstates

//#if MC>=12106
import com.mojang.blaze3d.pipeline.RenderPipeline
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.texture.TextureSetup
import org.zephy.zrenderlib.RenderUtils

class GradientGUIRenderState(
    private val base: GUIRenderState,
    val vertexAndColorList: List<Triple<Float, Float, Long>>,
) : SimpleGuiElementRenderState {
    //#if MC<12110
    //$$override fun setupVertices(vertices: VertexConsumer, depth: Float) {
    //$$    val zPosition = depth + base.zOffset
    //#else
    override fun setupVertices(vertices: VertexConsumer) {
        val zPosition = base.zOffset
    //#endif
        val newMatrix = RenderUtils.getGUIMatrix(base.matrix)
        vertexAndColorList.forEach { (x, y, color) ->
            val (r, g, b, a) = RenderUtils.RGBAColor.fromLongRGBA(color).getIntComponentsRGBA()
            vertices
                .vertex(newMatrix, x, y, zPosition)
                .color(r, g, b, a)
        }
    }

    override fun pipeline(): RenderPipeline = base.pipeline
    override fun textureSetup(): TextureSetup = TextureSetup.empty()
    override fun scissorArea(): ScreenRect? = base.scissorArea
    override fun bounds(): ScreenRect? = base.bounds()
}
//#endif
