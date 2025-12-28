package org.zephy.zrenderlib.renderstates

//#if MC>=12106
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.gui.render.state.GuiElementRenderState
import org.zephy.zrenderlib.RenderUtils

class TexturedGUIRenderState(
    private val base: GUIRenderState,
    val textureSetup: TextureSetup,
    val uvList: List<Pair<Float, Float>>,
) : GuiElementRenderState {
    //#if MC<12110
    //$$override fun buildVertices(vertices: VertexConsumer, depth: Float) {
    //$$    val zPosition = depth + base.zOffset
    //#else
    override fun buildVertices(vertices: VertexConsumer) {
        val zPosition = base.zOffset
    //#endif
        val newMatrix = RenderUtils.getGUIMatrix(base.matrix)
        val (r, g, b, a) = base.color.getIntComponentsRGBA()

        base.vertexList.forEachIndexed { index, (x, y) ->
            val (u, v) = uvList.getOrNull(index) ?: Pair(0f, 0f)
            vertices
                //#if MC<12110
                //$$.addVertexWith2DPose(newMatrix, x, y, zPosition)
                //#else
                .addVertex(newMatrix, x, y, zPosition)
                //#endif
                .setColor(r, g, b, a)
                .setUv(u, v)
        }
    }

    override fun pipeline(): RenderPipeline = base.pipeline
    override fun textureSetup(): TextureSetup = textureSetup
    override fun scissorArea(): ScreenRectangle? = base.scissorArea
    override fun bounds(): ScreenRectangle? = base.bounds()
}
//#endif
