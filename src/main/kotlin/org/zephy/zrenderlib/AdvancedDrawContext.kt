package org.zephy.zrenderlib

//#if MC>=12109
import com.mojang.blaze3d.ProjectionType
import com.mojang.blaze3d.systems.RenderSystem

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics
//$$import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer
//#else
//#if MC<26.2
//$$import net.minecraft.client.renderer.Projection
//#else
import org.joml.Matrix4f
//#endif
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.ProjectionMatrixBuffer
//#endif

//#if MC>26.3
import com.mojang.renderpearl.api.commands.RenderPass
import org.joml.Vector4f
import java.util.Optional
import java.util.OptionalDouble
//#endif

/**
 * Allows rendering of raw OpenGL into [DrawContext] by drawing to a temporary texture which is then submitted as a
 * plain textured quad to [DrawContext].
 *
 * You **MUST** call [nextFrame] before the next frame begins but no sooner than MC's GuiRenderer actually using the
 * submitted textures. Repeated calls to [drawImmediate] without [nextFrame] or [close] inbetween will keep allocating
 * more and more gpu memory!
 * If you cannot guarantee further calls to [nextFrame], you must call [close] to release all resources.
 * The [AdvancedDrawContext] remains usable, [close] merely frees all resources, further calls to [drawImmediate] will
 * simply re-allocate them.
 */
class AdvancedDrawContext : AutoCloseable {
    //#if MC<=12111
    //$$private var allocatedProjectionMatrix: CachedOrthoProjectionMatrixBuffer? = null
    //#else
    //#if MC<26.2
    //$$private val projection = Projection()
    //#else
    private val projection = Matrix4f()
    //#endif
    private var allocatedProjectionMatrix: ProjectionMatrixBuffer? = null
    //#endif

    private val textureAllocator = TemporaryTextureAllocator {
        allocatedProjectionMatrix?.close()
        allocatedProjectionMatrix = null
    }

    //#if MC<=12111
    //$$fun drawImmediate(context: GuiGraphics, block: (UMatrixStack) -> Unit) {
    //#elseif MC<=26.2
    //$$fun drawImmediate(context: GuiGraphicsExtractor, block: (UMatrixStack) -> Unit) {
    //#else
    fun drawImmediate(context: GuiGraphicsExtractor, block: (UMatrixStack, RenderPass) -> Unit) {
    //#endif
        val scaleFactor = ZRenderLib.getMinecraft().window.guiScale
        val width = ZRenderLib.getMinecraft().window.width
        val height = ZRenderLib.getMinecraft().window.height

        val texture = textureAllocator.allocate(width, height)

        var projectionMatrix = allocatedProjectionMatrix
        if (projectionMatrix == null) {
            //#if MC<=12111
            //$$projectionMatrix = CachedOrthoProjectionMatrixBuffer("pre-rendered screen", 1000f, 21000f, true)
            //#else
            projectionMatrix = ProjectionMatrixBuffer("pre-rendered screen")
            //#endif
            allocatedProjectionMatrix = projectionMatrix
        }
        //#if MC<=12111
        //$$val projectionMatrixBuffer = projectionMatrix.getBuffer(width.toFloat() / scaleFactor, height.toFloat() / scaleFactor)
        //#else
        //#if MC<26.2
        //$$projection.setupOrtho(1000f, 21000f, width.toFloat() / scaleFactor, height.toFloat() / scaleFactor, true)
        //#else
        projection.setOrtho(
            0f, width.toFloat() / scaleFactor,
            height.toFloat() / scaleFactor, 0f,
            1000f, 21000f,
            RenderSystem.getDevice().deviceInfo.isZZeroToOne,
        )
        //#endif
        val projectionMatrixBuffer = projectionMatrix.getBuffer(projection)
        //#endif
        RenderSystem.setProjectionMatrix(projectionMatrixBuffer, ProjectionType.ORTHOGRAPHIC)

        val matrixStack = UMatrixStack()
        matrixStack.translate(0f, 0f, -10000f)
        //#if MC<26.3
        //$$val orgOutputColorTextureOverride = RenderSystem.outputColorTextureOverride
        //$$val orgOutputDepthTextureOverride = RenderSystem.outputDepthTextureOverride
        //$$RenderSystem.outputColorTextureOverride = texture.textureView
        //$$RenderSystem.outputDepthTextureOverride = texture.depthTextureView
        //$$block(matrixStack)
        //$$RenderSystem.outputColorTextureOverride = orgOutputColorTextureOverride
        //$$RenderSystem.outputDepthTextureOverride = orgOutputDepthTextureOverride
        //#else
        RenderUtils.withRenderPass(
            "Advanced draw context",
            texture.textureView,
            texture.depthTextureView,
            Optional.of(Vector4f(0f, 0f, 0f, 0f)),
            OptionalDouble.of(1.0),
        ) { renderPass ->
            block(matrixStack, renderPass)
        }
        //#endif
        RenderUtils.drawTexture(context, texture)
    }

    fun nextFrame() {
        textureAllocator.nextFrame()
    }

    override fun close() {
        textureAllocator.close()
    }
}
//#endif
