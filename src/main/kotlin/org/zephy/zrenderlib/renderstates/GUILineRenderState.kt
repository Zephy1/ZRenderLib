package org.zephy.zrenderlib.renderstates

//#if MC>=12106
import com.mojang.blaze3d.pipeline.RenderPipeline
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.texture.TextureSetup
import org.joml.Matrix3x2f
import org.zephy.zrenderlib.RenderUtils
import kotlin.math.max
import kotlin.math.min

data class GUILineRenderState(
    val matrix: Matrix3x2f,
    val startX: Float, val endX: Float, val offsetX: Float,
    val startY: Float, val endY: Float, val offsetY: Float,
    val zOffset: Float,
    val lineThickness: Float,
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
        vertices.vertex(newMatrix, startX + offsetX, startY + offsetY, zPosition).color(r, g, b, a)
        vertices.vertex(newMatrix, endX + offsetX, endY + offsetY, zPosition).color(r, g, b, a)
        vertices.vertex(newMatrix, endX - offsetX, endY - offsetY, zPosition).color(r, g, b, a)
        vertices.vertex(newMatrix, startX - offsetX, startY - offsetY, zPosition).color(r, g, b, a)
    }

    override fun getBounds(): ScreenRect? {
        val halfThickness = lineThickness / 2f
        val minX = (min(startX, endX) - halfThickness)
        val maxX = (max(startX, endX) + halfThickness)
        val minY = (min(startY, endY) - halfThickness)
        val maxY = (max(startY, endY) + halfThickness)

        val rect = ScreenRect(
            minX.toInt(),
            minY.toInt(),
            (maxX - minX + 1).toInt(),
            (maxY - minY + 1).toInt(),
        ).transformEachVertex(matrix)
        return scissorArea?.intersection(rect) ?: rect
    }
}
//#endif
