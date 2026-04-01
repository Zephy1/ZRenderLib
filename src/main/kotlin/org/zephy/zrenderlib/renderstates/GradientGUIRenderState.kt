package org.zephy.zrenderlib.renderstates

//#if MC>=12106
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import org.zephy.zrenderlib.RenderUtils

//#if MC<=12111
//$$import net.minecraft.client.gui.render.state.GuiElementRenderState
//#else
import net.minecraft.client.renderer.state.gui.GuiElementRenderState
//#endif

class GradientGUIRenderState(
    private val base: GUIRenderState,
    val vertexAndColorList: List<Triple<Float, Float, Long>>,
) : GuiElementRenderState {
    //#if MC<12110
    //$$override fun buildVertices(vertices: VertexConsumer, depth: Float) {
    //$$    val zPosition = depth + base.zOffset
    //#else
    override fun buildVertices(vertices: VertexConsumer) {
        val zPosition = base.zOffset
    //#endif
        val newMatrix = RenderUtils.getGUIMatrix(base.matrix)
        vertexAndColorList.forEach { (x, y, color) ->
            val (r, g, b, a) = RenderUtils.RGBAColor.fromLongRGBA(color).getIntComponentsRGBA()
            vertices
                //#if MC<12110
                //$$.addVertexWith2DPose(newMatrix, x, y, zPosition)
                //#else
                .addVertex(newMatrix, x, y, zPosition)
                //#endif
                .setColor(r, g, b, a)
        }
    }

    override fun pipeline(): RenderPipeline = base.pipeline
    override fun textureSetup(): TextureSetup = TextureSetup.noTexture()
    override fun scissorArea(): ScreenRectangle? = base.scissorArea
    override fun bounds(): ScreenRectangle? = base.bounds()
}
//#endif
