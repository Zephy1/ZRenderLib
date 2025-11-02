package org.zephy.zrenderlib.renderstates

//#if MC>=12106
import com.mojang.blaze3d.pipeline.RenderPipeline
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.texture.TextureSetup
import org.zephy.zrenderlib.RenderUtils

class TexturedGUIRenderState(
    private val base: GUIRenderState,
    val textureSetup: TextureSetup,
    val uvList: List<Pair<Float, Float>>,
) : SimpleGuiElementRenderState {
    //#if MC<12110
    //$$override fun setupVertices(vertices: VertexConsumer, depth: Float) {
    //$$    val zPosition = depth + base.zOffset
    //#else
    override fun setupVertices(vertices: VertexConsumer) {
        val zPosition = base.zOffset
    //#endif
        val newMatrix = RenderUtils.getGUIMatrix(base.matrix)
        val (r, g, b, a) = base.color.getIntComponentsRGBA()

        base.vertexList.forEachIndexed { index, (x, y) ->
            val (u, v) = uvList.getOrNull(index) ?: Pair(0f, 0f)
            vertices
                .vertex(newMatrix, x, y, zPosition)
                .color(r, g, b, a)
                .texture(u, v)
        }
    }

    override fun pipeline(): RenderPipeline = base.pipeline
    override fun textureSetup(): TextureSetup = textureSetup
    override fun scissorArea(): ScreenRect? = base.scissorArea
    override fun bounds(): ScreenRect? = base.bounds()
}
//#endif
