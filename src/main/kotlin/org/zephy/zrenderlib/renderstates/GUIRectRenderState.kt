package org.zephy.zrenderlib.renderstates

//#if MC>=12106
import com.mojang.blaze3d.pipeline.RenderPipeline
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.texture.TextureSetup
import org.joml.Matrix3x2f
import org.zephy.zrenderlib.RenderUtils

data class GUIRectRenderState(
    val matrix: Matrix3x2f,
    val xPosition: Float, val yPosition: Float, val zOffset: Float,
    val width: Float, val height: Float,
    val color: RenderUtils.RenderColor,
    override val pipeline: RenderPipeline,
    override val textureSetup: TextureSetup,
    override val scissorArea: ScreenRect?,
) : BaseGUIRenderState() {
    //#if MC<12110
    //$$override fun setupVertices(vertices: VertexConsumer, depth: Float) {
    //$$    val zPosition = depth + zOffset
    //#else
    override fun setupVertices(vertices: VertexConsumer) {
        val zPosition = zOffset
    //#endif
        val newMatrix = RenderUtils.getGUIMatrix(matrix)
        val (r, g, b, a) = color.getIntComponentsRGBA()
        val x2 = xPosition + width
        val y2 = yPosition + height

        vertices.vertex(newMatrix, xPosition, yPosition, zPosition).color(r, g, b, a)
        vertices.vertex(newMatrix, xPosition, y2, zPosition).color(r, g, b, a)
        vertices.vertex(newMatrix, x2, y2, zPosition).color(r, g, b, a)
        vertices.vertex(newMatrix, x2, yPosition, zPosition).color(r, g, b, a)
    }

    override fun getBounds(): ScreenRect? {
        val rect = ScreenRect(
            this.xPosition.toInt(),
            this.yPosition.toInt(),
            width.toInt(),
            height.toInt()
        ).transformEachVertex(matrix)
        return scissorArea?.intersection(rect) ?: rect
    }
}
//#endif
