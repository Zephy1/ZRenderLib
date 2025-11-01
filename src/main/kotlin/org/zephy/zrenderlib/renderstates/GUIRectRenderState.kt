package org.zephy.zrenderlib.renderstates

//#if MC>=12106
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import org.jetbrains.annotations.Nullable
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.texture.TextureSetup
import org.joml.Matrix3x2f
import org.zephy.zrenderlib.RenderUtils.RenderColor

//#if MC>=12110
import org.joml.Matrix4f
//#endif

data class GUIRectRenderState(
    val pipeline: RenderPipeline,
    val textureSetup: TextureSetup,
    val pose: Matrix3x2f,
    val xPosition: Float,
    val yPosition: Float,
    val width: Float,
    val height: Float,
    val color: RenderColor,
    val scissorArea: ScreenRect?,
    val bounds: ScreenRect?
) : SimpleGuiElementRenderState {
    constructor(
        pipeline: RenderPipeline,
        textureSetup: TextureSetup,
        pose: Matrix3x2f,
        xPosition: Float,
        yPosition: Float,
        width: Float,
        height: Float,
        color: RenderColor,
        scissorArea: ScreenRect?
    ) : this(
        pipeline,
        textureSetup,
        pose,
        xPosition,
        yPosition,
        width,
        height,
        color,
        scissorArea,
        createBounds(xPosition, yPosition, xPosition + width, yPosition + height, pose, scissorArea)
    )

    //#if MC<12110
    //$$override fun setupVertices(vertices: VertexConsumer, depth: Float) {
    //$$val newPose = pose
    //#else
    override fun setupVertices(vertices: VertexConsumer) {
        val newPose: Matrix4f =
            Matrix4f().set(
                pose.m00(), pose.m01(), 0f, 0f,
                pose.m10(), pose.m11(), 0f, 0f,
                0f,    0f,    1f, 0f,
                pose.m20(), pose.m21(), 0f, 1f
            )
        val depth = 0f
    //#endif
        val (r, g, b, a) = color.getIntComponentsRGBA()

        vertices.vertex(newPose, xPosition, yPosition, depth).color(r, g, b, a)
        vertices.vertex(newPose, xPosition, yPosition + height, depth).color(r, g, b, a)
        vertices.vertex(newPose, xPosition + width, yPosition + height, depth).color(r, g, b, a)
        vertices.vertex(newPose, xPosition + width, yPosition, depth).color(r, g, b, a)
    }

    override fun pipeline(): RenderPipeline = pipeline
    override fun textureSetup(): TextureSetup = textureSetup
    override fun scissorArea(): ScreenRect? = scissorArea
    override fun bounds(): ScreenRect? = bounds

    companion object {
        @Nullable
        private fun createBounds(
            x0: Float,
            y0: Float,
            x1: Float,
            y1: Float,
            pose: Matrix3x2f,
            @Nullable scissorArea: ScreenRect?
        ): ScreenRect? {
            val screenRect = ScreenRect(
                x0.toInt(),
                y0.toInt(),
                (x1 - x0).toInt(),
                (y1 - y0).toInt()
            ).transformEachVertex(pose)
            return scissorArea?.intersection(screenRect) ?: screenRect
        }
    }
}
//#endif
