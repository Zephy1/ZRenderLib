package org.zephy.zrenderlib

//#if MC==10809 || MC>=12100
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.cos

//#if MC<12100
//$$import net.minecraft.client.renderer.GlStateManager
//$$import net.minecraft.client.renderer.Tessellator
//$$import net.minecraft.client.renderer.entity.RenderManager
//$$import javax.vecmath.Vector3d
//$$import java.nio.FloatBuffer
//$$import net.minecraft.client.gui.ScaledResolution
//$$import javax.vecmath.Vector3f
//#else
import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.platform.DestFactor
import com.mojang.blaze3d.platform.SourceFactor
import com.mojang.blaze3d.systems.RenderSystem
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import net.minecraft.client.render.RenderLayer
import net.minecraft.text.Text
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.Optional
import net.minecraft.text.Style
//#endif

//#if MC>=12100
    //#if MC<=12105
    //$$import com.mojang.blaze3d.textures.GpuTexture
    //$$import net.minecraft.client.util.math.MatrixStack
    //#else
    import com.mojang.blaze3d.textures.GpuTextureView
    import org.joml.Matrix3x2fStack
    import org.joml.Matrix3x2f
    import org.joml.Matrix4f
//#endif
//#endif

object RenderUtils {
    @JvmField
    val screen = ScreenWrapper()

    @JvmStatic
    //#if MC<12100
    //$$fun getTextRenderer() = Client.getMinecraft().fontRendererObj
    //#else
    fun getTextRenderer() = Client.getMinecraft().textRenderer
    //#endif

    @JvmField
    var colorized: Long? = null

    @JvmField
    var vertexColor: Color? = null

    private var firstVertex = true
    private var began = false

    //#if MC<12100
    //$$@JvmStatic val renderManager: RenderManager = Client.getMinecraft().renderManager
    //$$@JvmStatic val tessellator: Tessellator = Tessellator.getInstance()
    //$$@JvmStatic val worldRenderer: net.minecraft.client.renderer.WorldRenderer? = tessellator.worldRenderer
    //$$@JvmStatic fun getCameraPos(): Vector3d {
    //$$    return Vector3d(
    //$$        renderManager.viewerPosX,
    //$$        renderManager.viewerPosY,
    //$$        renderManager.viewerPosZ
    //$$    )
    //$$}
    //#else
    private val ucRenderer = UGraphics.getFromTessellator()
    internal lateinit var matrixStack: UMatrixStack
    private val matrixStackStack = ArrayDeque<UMatrixStack>()
    internal var matrixPushCounter = 0

    @JvmStatic
    fun setMatrixStack(stack: UMatrixStack) = apply {
        matrixStack = stack
    }

    @JvmStatic
    //#if MC<=12105
    //$$fun setMatrixStack(stack: MatrixStack) = apply {
    //#else
    fun setMatrixStack(stack: Matrix3x2fStack) = apply {
    //#endif
        matrixStack = UMatrixStack(stack)
    }
    //#endif

    @JvmField val BLACK = RGBAColor(0, 0, 0, 255).getLong()

    @JvmField val DARK_BLUE = RGBAColor(0, 0, 190, 255).getLong()

    @JvmField val DARK_GREEN = RGBAColor(0, 190, 0, 255).getLong()

    @JvmField val DARK_AQUA = RGBAColor(0, 190, 190, 255).getLong()

    @JvmField val DARK_RED = RGBAColor(190, 0, 0, 255).getLong()

    @JvmField val DARK_PURPLE = RGBAColor(190, 0, 190, 255).getLong()

    @JvmField val GOLD = RGBAColor(217, 163, 52, 255).getLong()

    @JvmField val GRAY = RGBAColor(190, 190, 190, 255).getLong()

    @JvmField val DARK_GRAY = RGBAColor(63, 63, 63, 255).getLong()

    @JvmField val BLUE = RGBAColor(63, 63, 254, 255).getLong()

    @JvmField val GREEN = RGBAColor(63, 254, 63, 255).getLong()

    @JvmField val AQUA = RGBAColor(63, 254, 254, 255).getLong()

    @JvmField val RED = RGBAColor(254, 63, 63, 255).getLong()

    @JvmField val LIGHT_PURPLE = RGBAColor(254, 63, 254, 255).getLong()

    @JvmField val YELLOW = RGBAColor(254, 254, 63, 255).getLong()

    @JvmField val WHITE = RGBAColor(255, 255, 255, 255).getLong()

    @JvmStatic
    fun color(color: Int): Long = when (color) {
        0 -> BLACK
        1 -> DARK_BLUE
        2 -> DARK_GREEN
        3 -> DARK_AQUA
        4 -> DARK_RED
        5 -> DARK_PURPLE
        6 -> GOLD
        7 -> GRAY
        8 -> DARK_GRAY
        9 -> BLUE
        10 -> GREEN
        11 -> AQUA
        12 -> RED
        13 -> LIGHT_PURPLE
        14 -> YELLOW
        else -> WHITE
    }

    @JvmStatic
    //#if MC<12100
    //$$fun getStringWidth(text: String) = getTextRenderer().getStringWidth(addColor(text))
    //#else
    fun getStringWidth(text: String) = getTextRenderer().getWidth(addColor(text))
    //#endif

    @JvmStatic
    fun baseStartDraw() = apply {
        pushMatrix()
            .disableCull()
            .enableBlend()
            .tryBlendFuncSeparate(
                770, // SRC_ALPHA
                771, // ONE_MINUS_SRC_ALPHA
                1, // ONE
                0 // ZERO
            )
    }
    @JvmStatic
    fun guiStartDraw() = apply {
        baseStartDraw()
            .depthMask(false)
//            .disableDepth()
            .enableLineSmooth()
            .disableTexture2D()
    }

    @JvmStatic
    fun baseEndDraw() = apply {
        enableCull()
            .enableDepth()
            .disableLineSmooth()
            .resetLineWidth()
            .disableBlend()
            .resetColor()
            .popMatrix()
    }
    @JvmStatic
    fun guiEndDraw() = apply {
        depthMask(true)
            .enableTexture2D()
            .baseEndDraw()
    }
    @JvmStatic
    fun worldEndDraw() = apply {
        enableTexture2D()
            .depthMask(true)
            .baseEndDraw()
    }

    private fun _begin() = apply {
        colorized = null
        vertexColor = null
        firstVertex = true
        began = true
    }

    //#if MC>=12100
    @JvmStatic
    fun begin(renderLayer: RenderLayer = RenderLayers.QUADS()) = apply {
        _begin()
        ucRenderer.beginRenderLayer(renderLayer)
    }

    @JvmStatic
    fun begin(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ) = apply {
        begin(PipelineBuilder.begin(drawMode, vertexFormat, snippet).layer())
    }
    //#endif

    @JvmStatic
    fun begin(
        //#if MC<12100
        //$$drawMode: Int = GL11.GL_QUADS,
        //#else
        drawMode: DrawMode = DrawMode.QUADS,
        //#endif
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
    ) = apply {
        //#if MC<12100
        //$$_begin()
        //$$worldRenderer?.let {
        //$$    it.begin(drawMode, vertexFormat.toMC())
        //$$}
        //#else
        RenderLayers.getRenderLayer(drawMode, vertexFormat)?.let { renderLayer ->
            begin(renderLayer)
        }
        //#endif
    }

    @JvmStatic
    fun draw() = apply {
        if (!began) return this
        began = false

        endVertex()
        //#if MC<12100
        //$$tessellator.draw()
        //#else
        ucRenderer.drawDirect()
        //#endif
    }

    @JvmStatic
    fun pos(x: Float, y: Float, z: Float, endVertex: Boolean = true) = apply {
        pos(x.toDouble(), y.toDouble(), z.toDouble(), endVertex)
    }
    @JvmStatic
    fun pos(x: Double, y: Double, z: Double, endVertex: Boolean = true) = apply {
        if (!began) begin()
        if (!firstVertex && endVertex) endVertex()

        //#if MC<12100
        //$$worldRenderer?.pos(x, y, z)
        //#else
        val camera = Client.getMinecraft().gameRenderer.camera.pos
        ucRenderer.pos(matrixStack, x - camera.x, y - camera.y, z - camera.z)
        //#endif

        firstVertex = false
        vertexColor?.let {
            color(vertexColor!!)
        }
    }
    @JvmStatic
    fun posList(positions: List<Triple<Float, Float, Float>>) = apply {
        for (pos in positions) {
            pos(pos.first, pos.second, pos.third)
        }
    }
    @JvmStatic
    fun posList(positions: List<Pair<Float, Float>>, zPosition: Float) = apply {
        for (pos in positions) {
            pos(pos.first, pos.second, zPosition)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun cameraPos(x: Float, y: Float, z: Float = 0f, endVertex: Boolean = true) = apply {
        cameraPos(x.toDouble(), y.toDouble(), z.toDouble(), endVertex)
    }
    @JvmStatic
    @JvmOverloads
    fun cameraPos(x: Double, y: Double, z: Double = 0.0, endVertex: Boolean = true) = apply {
        //#if MC<12100
        //$$pos(x, y, z, endVertex)
        //#else
        val camera = Client.getMinecraft().gameRenderer.camera.pos
        pos(x + camera.x, y + camera.y, z + camera.z, endVertex)
        //#endif
    }
    @JvmStatic
    fun cameraPosList(positions: List<Triple<Float, Float, Float>>) = apply {
        for (pos in positions) {
            cameraPos(pos.first, pos.second, pos.third)
        }
    }
    @JvmStatic
    fun cameraPosList(positions: List<Pair<Float, Float>>, zPosition: Float) = apply {
        for (pos in positions) {
            cameraPos(pos.first, pos.second, zPosition)
        }
    }

    @JvmStatic
    fun endVertex() = apply {
        //#if MC<12100
        //$$worldRenderer?.endVertex()
        //#endif
    }

    @JvmStatic
    fun tex(u: Float, v: Float) = apply {
        tex(u.toDouble(), v.toDouble())
    }
    @JvmStatic
    fun tex(u: Double, v: Double) = apply {
        //#if MC<12100
        //$$worldRenderer?.tex(u, v)
        //#else
        ucRenderer.tex(u, v)
        //#endif
    }

    @JvmStatic
    fun normal(x: Float, y: Float, z: Float) = apply {
        //#if MC<12100
        //$$worldRenderer?.normal(x, y, z)
        //#else
        ucRenderer.norm(matrixStack, x, y, z)
        //#endif
    }
    @JvmStatic
    fun normal(x: Double, y: Double, z: Double) = apply {
        normal(x.toFloat(), y.toFloat(), z.toFloat())
    }
    @JvmStatic
    fun normal(vector: Vector3f?) = apply {
        vector?.let {
            normal(it.x, it.y, it.z)
        }
    }

    @JvmStatic
    fun overlay(u: Int, v: Int) = apply {
        //#if MC<12100
        //$$tex(u.toDouble(), v.toDouble())
        //#else
        ucRenderer.overlay(u, v)
        //#endif
    }

    @JvmStatic
    fun light(u: Int, v: Int) = apply {
        //#if MC<12100
        //$$worldRenderer?.lightmap(u, v)
        //#else
        ucRenderer.light(u, v)
        //#endif
    }

    @JvmStatic
    fun lineWidth(width: Float) = apply {
        //#if MC<12100
        //$$GL11.glLineWidth(width)
        //#else
        RenderSystem.lineWidth(width)
        //#endif
    }

    @JvmStatic
    fun resetLineWidth() = apply {
        lineWidth(1f)
    }

    @JvmStatic
    fun enableCull() = apply {
        //#if MC<12100
        //$$GlStateManager.enableCull()
        //#else
        PipelineBuilder.enableCull()
        //#endif
    }

    @JvmStatic
    fun disableCull() = apply {
        //#if MC<12100
        //$$GlStateManager.disableCull()
        //#else
        PipelineBuilder.disableCull()
        //#endif
    }

    @JvmStatic
    fun enableLighting() = apply {
        //#if MC<12100
        //$$GlStateManager.enableLighting()
        //#else
        UGraphics.enableLighting()
        //#endif
    }

    @JvmStatic
    fun disableLighting() = apply {
        //#if MC<12100
        //$$GlStateManager.disableLighting()
        //#else
        UGraphics.disableLighting()
        //#endif
    }

    @JvmStatic
    fun enableDepth() = apply {
        //#if MC<12100
        //$$GlStateManager.enableDepth()
        //#else
        PipelineBuilder.enableDepth()
        //#endif
    }

    @JvmStatic
    fun disableDepth() = apply {
        //#if MC<12100
        //$$GlStateManager.disableDepth()
        //#else
        PipelineBuilder.disableDepth()
        //#endif
    }

    //#if MC>=12100
    @JvmStatic
    fun getDepthTestFunctionFromInt(value: Int): DepthTestFunction {
        return when (value) {
//            0x200 -> !! // GL_NEVER
            0x201 -> DepthTestFunction.LESS_DEPTH_TEST // GL_LESS
            0x202 -> DepthTestFunction.EQUAL_DEPTH_TEST // GL_EQUAL
            0x203 -> DepthTestFunction.LEQUAL_DEPTH_TEST // GL_LEQUAL
            0x204 -> DepthTestFunction.GREATER_DEPTH_TEST // GL_GREATER
//            0x205 -> !! // GL_NOTEQUAL
//            0x206 -> !! // GL_GEQUAL
            0x207 -> DepthTestFunction.NO_DEPTH_TEST // GL_ALWAYS
            else -> throw IllegalArgumentException("Invalid depth test function value: $value")
        }
    }

    @JvmStatic
    fun depthFunc(function: DepthTestFunction) = apply {
        PipelineBuilder.setDepthTestFunction(function)
    }
    //#endif

    @JvmStatic
    fun depthFunc(depthFunc: Int) = apply {
        //#if MC<12100
        //$$GlStateManager.depthFunc(depthFunc)
        //#else
        depthFunc(getDepthTestFunctionFromInt(depthFunc))
        //#endif
    }

    @JvmStatic
    fun depthMask(mask: Boolean) = apply {
        //#if MC<12100
        //$$GlStateManager.depthMask(mask)
        //#endif
    }

    @JvmStatic
    fun enableTexture2D() = apply {
        //#if MC<12100
        //$$GlStateManager.enableTexture2D()
        //#endif
    }
    @JvmStatic
    fun disableTexture2D() = apply {
        //#if MC<12100
        //$$GlStateManager.disableTexture2D()
        //#endif
    }

    @JvmStatic
    fun enableLineSmooth() = apply {
        //#if MC<12100
        //$$GL11.glEnable(GL11.GL_LINE_SMOOTH)
        //#endif
    }
    @JvmStatic
    fun disableLineSmooth() = apply {
        //#if MC<12100
        //$$GL11.glDisable(GL11.GL_LINE_SMOOTH)
        //#endif
    }

    @JvmStatic
    fun enableBlend() = apply {
        //#if MC<12100
        //$$GlStateManager.enableBlend()
        //#else
        PipelineBuilder.enableBlend()
        //#endif
    }
    @JvmStatic
    fun disableBlend() = apply {
        //#if MC<12100
        //$$GlStateManager.disableBlend()
        //#else
        PipelineBuilder.disableBlend()
        //#endif
    }

    @JvmStatic
    fun blendFunc(srcFactor: Int, dstFactor: Int) = apply {
        //#if MC<12100
        //$$GlStateManager.blendFunc(srcFactor, dstFactor)
        //#else
        blendFunc(getSourceFactorFromInt(srcFactor), getDestFactorFromInt(dstFactor))
        //#endif
    }

    @JvmStatic
    fun shadeModel(model: Int) = apply {
        //#if MC<12100
        //$$GlStateManager.shadeModel(model)
        //#endif
    }

    //#if MC >= 12100
    @JvmStatic
    fun blendFunc(function: BlendFunction) = apply {
        PipelineBuilder.setBlendFunction(function)
    }
    @JvmStatic
    fun blendFunc(srcFactor: SourceFactor, dstFactor: DestFactor) = apply {
        PipelineBuilder.setBlendFunction(BlendFunction(srcFactor, dstFactor))
    }

    @JvmStatic
    fun getSourceFactorFromInt(value: Int): SourceFactor {
        return when (value) {
            0 -> SourceFactor.ZERO
            1 -> SourceFactor.ONE
            768 -> SourceFactor.SRC_COLOR
            769 -> SourceFactor.ONE_MINUS_SRC_COLOR
            774 -> SourceFactor.DST_COLOR
            775 -> SourceFactor.ONE_MINUS_DST_COLOR
            32769 -> SourceFactor.CONSTANT_COLOR
            32770 -> SourceFactor.ONE_MINUS_CONSTANT_COLOR
            770 -> SourceFactor.SRC_ALPHA
            771 -> SourceFactor.ONE_MINUS_SRC_ALPHA
            772 -> SourceFactor.DST_ALPHA
            773 -> SourceFactor.ONE_MINUS_DST_ALPHA
            32771 -> SourceFactor.CONSTANT_ALPHA
            32772 -> SourceFactor.ONE_MINUS_CONSTANT_ALPHA
            776 -> SourceFactor.SRC_ALPHA_SATURATE
            else -> throw IllegalArgumentException("Invalid source factor value: $value")
        }
    }
    @JvmStatic
    fun getDestFactorFromInt(value: Int): DestFactor {
        return when (value) {
            0 -> DestFactor.ZERO
            1 -> DestFactor.ONE
            768 -> DestFactor.SRC_COLOR
            769 -> DestFactor.ONE_MINUS_SRC_COLOR
            774 -> DestFactor.DST_COLOR
            775 -> DestFactor.ONE_MINUS_DST_COLOR
            32769 -> DestFactor.CONSTANT_COLOR
            32770 -> DestFactor.ONE_MINUS_CONSTANT_COLOR
            770 -> DestFactor.SRC_ALPHA
            771 -> DestFactor.ONE_MINUS_SRC_ALPHA
            772 -> DestFactor.DST_ALPHA
            773 -> DestFactor.ONE_MINUS_DST_ALPHA
            32771 -> DestFactor.CONSTANT_ALPHA
            32772 -> DestFactor.ONE_MINUS_CONSTANT_ALPHA
//            776 -> DestFactor.SRC_ALPHA_SATURATE
            else -> throw IllegalArgumentException("Invalid source factor value: $value")
        }
    }
    @JvmStatic
    fun tryBlendFuncSeparate(
        sourceFactor: SourceFactor,
        destFactor: DestFactor,
        sourceFactorAlpha: SourceFactor,
        destFactorAlpha: DestFactor,
    ) = apply {
        blendFunc(BlendFunction(sourceFactor, destFactor, sourceFactorAlpha, destFactorAlpha))
    }
    //#endif

    @JvmStatic
    fun tryBlendFuncSeparate(
        sourceFactor: Int,
        destFactor: Int,
        sourceFactorAlpha: Int,
        destFactorAlpha: Int,
    ) = apply {
        //#if MC<12100
        //$$GlStateManager.tryBlendFuncSeparate(sourceFactor, destFactor, sourceFactorAlpha, destFactorAlpha)
        //#else
        tryBlendFuncSeparate(
            getSourceFactorFromInt(sourceFactor),
            getDestFactorFromInt(destFactor),
            getSourceFactorFromInt(sourceFactorAlpha),
            getDestFactorFromInt(destFactorAlpha)
        )
        //#endif
    }

    @JvmStatic
    //#if MC<12100
    //$$fun bindTexture(textureId: Int) = apply {
    //$$    GlStateManager.bindTexture(textureId)
    //$$}
    //#else
    @JvmOverloads
    fun bindTexture(textureImage: Image, textureIndex: Int = 0) = apply {
        UGraphics.bindTexture(textureIndex, textureImage.getTexture()?.image?.imageId()?.toInt() ?: 0)
    }
    //#endif

    @JvmStatic
    //#if MC<12100
    //$$fun deleteTexture(textureId: Int) = apply {
    //$$    GlStateManager.deleteTexture(textureId)
    //$$}
    //#else
    fun deleteTexture(texture: Image) = apply {
        GL11.glDeleteTextures(texture.getTexture()?.image?.imageId()?.toInt() ?: 0)
    }
    //#endif

    //#if MC>=12100
    @JvmStatic
    //#if MC<=12105
    //$$fun setShaderTexture(textureIndex: Int, texture: GpuTexture?) = apply {
    //#else
    fun setShaderTexture(textureIndex: Int, texture: GpuTextureView?) = apply {
        //#endif
        RenderSystem.setShaderTexture(textureIndex, texture)
    }
    //#endif

    //#if MC >= 12100
    @JvmStatic
    fun setShaderTexture(textureIndex: Int, textureImage: Image) = apply {
        val gpuTexture = textureImage.getTexture()
        gpuTexture?.let {
            //#if MC<=12105
            //$$RenderSystem.setShaderTexture(textureIndex, gpuTexture.glTexture)
            //#else
            RenderSystem.setShaderTexture(textureIndex, gpuTexture.glTextureView)
            //#endif
        }
    }
    //#endif

    @JvmStatic
    //#if MC<12100
    //$$fun pushMatrix() = apply { GlStateManager.pushMatrix() }
    //#else
    fun pushMatrix(stack: UMatrixStack = matrixStack) = apply {
        matrixPushCounter++
        matrixStackStack.addLast(stack)
        matrixStack = stack
        stack.push()
    }
    //#endif

    @JvmStatic
    fun popMatrix() = apply {
        //#if MC<12100
        //$$GlStateManager.popMatrix()
        //#else
        matrixPushCounter--
        matrixStackStack.removeLast()
        matrixStack.pop()
        //#endif
    }

    @JvmStatic
    @JvmOverloads
    fun translate(x: Float, y: Float, z: Float = 0.0F) = apply {
        //#if MC<12100
        //$$translate(x.toDouble(), y.toDouble(), z.toDouble())
        //#else
        matrixStack.translate(x, y, z)
        //#endif
    }
    @JvmStatic
    @JvmOverloads
    fun translate(x: Double, y: Double, z: Double = 0.0) = apply {
        //#if MC<12100
        //$$GlStateManager.translate(x, y, z)
        //#else
        translate(x.toFloat(), y.toFloat(), z.toFloat())
        //#endif
    }

    @JvmStatic
    @JvmOverloads
    fun scale(scaleX: Float, scaleY: Float = scaleX, scaleZ: Float = scaleX) = apply {
        //#if MC<12100
        //$$scale(scaleX.toDouble(), scaleY.toDouble(), scaleZ.toDouble())
        //#else
        matrixStack.scale(scaleX, scaleY, scaleZ)
        //#endif
    }
    @JvmStatic
    @JvmOverloads
    fun scale(scaleX: Double, scaleY: Double = scaleX, scaleZ: Double = scaleX) = apply {
        //#if MC<12100
        //$$GlStateManager.scale(scaleX, scaleY, scaleZ)
        //#else
        scale(scaleX.toFloat(), scaleY.toFloat(), scaleZ.toFloat())
        //#endif
    }

    @JvmStatic
    @JvmOverloads
    fun rotate(angle: Float, x: Float = 0f, y: Float = 0f, z: Float = 1f) = apply {
        //#if MC<12100
        //$$GlStateManager.rotate(angle, x, y, z)
        //#else
        matrixStack.rotate(angle, x, y, z)
        //#endif
    }
    @JvmStatic
    @JvmOverloads
    fun rotate(angle: Double, x: Double = 0.0, y: Double = 0.0, z: Double = 1.0) = apply {
        rotate(angle.toFloat(), x.toFloat(), y.toFloat(), z.toFloat())
    }

    @JvmStatic
    //#if MC<12100
    //$$fun multiply(floatBuffer: FloatBuffer) = apply {
    //$$    GlStateManager.multMatrix(floatBuffer)
    //#else
    fun multiply(quaternion: Quaternionf) = apply {
        matrixStack.multiply(quaternion)
    //#endif
    }

    @JvmStatic
    fun colorizeRGBA(color: Long) = apply {
        val (r, g, b, a) = RGBAColor.fromLongRGBA(color)
        colorize_255(r, g, b, a)
    }

    @JvmStatic
    fun colorizeARGB(color: Long) = apply {
        val (a, r, g, b) = ARGBColor.fromLongARGB(color)
        colorize_255(r, g, b, a)
    }

    @JvmStatic
    @JvmOverloads
    fun colorize_01(r: Float, g: Float, b: Float, a: Float = 1f) = apply {
        val red = (r * 255f).coerceIn(0f, 255f).toInt()
        val green = (g * 255f).coerceIn(0f, 255f).toInt()
        val blue = (b * 255f).coerceIn(0f, 255f).toInt()
        val alpha = (a * 255f).coerceIn(0f, 255f).toInt()
        colorized = RGBAColor(red, green, blue, alpha).getLong()
        vertexColor = Color(red, green, blue, alpha)

        //#if MC<12100
        //$$//GlStateManager.color(r, g, b, a)
        //#elseif MC<=12105
        //$$//RenderSystem.setShaderColor(
        //$$//    vertexColor!!.red / 255f,
        //$$//    vertexColor!!.green / 255f,
        //$$//    vertexColor!!.blue / 255f,
        //$$//    vertexColor!!.alpha / 255f,
        //$$//)
        //#endif
    }

    @JvmStatic
    fun colorize_255(r: Int, g: Int, b: Int, a: Int = 255) = apply {
        colorize_01(
            r.coerceIn(0, 255) / 255f,
            g.coerceIn(0, 255) / 255f,
            b.coerceIn(0, 255) / 255f,
            a.coerceIn(0, 255) / 255f,
        )
    }

    @JvmStatic
    @JvmOverloads
    fun color_01(r: Float, g: Float, b: Float, a: Float = 1f) = apply {
        //#if MC<12100
        //$$worldRenderer?.color(r, g, b, a)
        //#else
        ucRenderer.color(r, g, b, a)
        //#endif
    }

    @JvmStatic
    @JvmOverloads
    fun color_255(r: Int, g: Int, b: Int, a: Int = 255) = apply {
        color_01(r / 255f, g / 255f, b / 255f, a / 255f)
    }

    @JvmStatic
    fun color(color: Color) = apply {
        color_255(color.red, color.green, color.blue, color.alpha)
    }

    @JvmStatic
    fun colorRGBA(color: Long) = apply {
        val (r, g, b, a) = RGBAColor.fromLongRGBA(color).getIntComponentsRGBA()
        color_255(r, g, b, a)
    }

    @JvmStatic
    fun colorARGB(color: Long) = apply {
        val (a, r, g, b) = RGBAColor.fromLongRGBA(color).getIntComponentsRGBA()
        color_255(r, g, b, a)
    }

    //#if MC>=12106
    fun Matrix3x2f.toMatrix4f(): Matrix4f = Matrix4f(
        m00(), m01(), 0f, 0f,
        m10(), m11(), 0f, 0f,
        0f, 0f, 1f, 0f,
        m20(), m21(), 0f, 1f
    )

    @JvmStatic
    //#if MC<12110
    //$$fun getGUIMatrix(matrix: Matrix3x2f): Matrix3x2f {
    //$$    val newMatrix = matrix
    //#else
    fun getGUIMatrix(matrix: Matrix3x2f): Matrix4f {
        val newMatrix = matrix.toMatrix4f()
    //#endif
        return newMatrix
    }
    //#endif

    @JvmStatic
    fun resetColor() = apply {
        colorize_01(1f, 1f, 1f, 1f)
    }

    @JvmStatic
    @JvmOverloads
    fun getRainbowColors(step: Float, speed: Float = 1f): IntArray {
        val red = ((sin(step / speed) + 0.75) * 170).toInt()
        val green = ((sin(step / speed + 2 * PI / 3) + 0.75) * 170).toInt()
        val blue = ((sin(step / speed + 4 * PI / 3) + 0.75) * 170).toInt()
        return intArrayOf(red, green, blue)
    }

    @JvmStatic
    @JvmOverloads
    fun getRainbow(step: Float, speed: Float = 1f): Long {
        val (r, g, b) = getRainbowColors(step, speed)
        return RGBAColor(r, g, b).getLong()
    }

    @JvmStatic
    fun calculateCenter(
        x1: Float,
        y1: Float,
        z1: Float,
        x2: Float,
        y2: Float,
        z2: Float,
    ) : Any {
        val cx = (x1 + x2) / 2
        val cy =  if (y1 > y2) y2 else y1
        val cz = (z1 + z2) / 2

        val wx = abs(x2 - x1)
        val h = abs(y2 - y1)
        val wz = abs(z2 - z1)

        return object {
            val cx = cx
            val cy = cy
            val cz = cz
            val wx = wx
            val h = h
            val wz = wz
        }
    }

    @JvmStatic
    fun addColor(message: String?): String {
        return message.toString().replace("(?<!\\\\)&(?![^0-9a-fk-or]|$)".toRegex(), "\u00a7")
    }

    data class WorldPositionVertex(
        val x: Float,
        val y: Float,
        val z: Float,
        val normal: Vector3f?,
    )

    //#if MC>=12100
    data class TextLines(val lines: List<Text>, val width: Float, val height: Float)

    @JvmStatic
    fun splitText(text: Text, maxWidth: Int): TextLines {
        val renderer = getTextRenderer()
        val wrappedLines = renderer.textHandler.wrapLines(text, maxWidth, Style.EMPTY)

        val textLines = wrappedLines.map { visitable ->
            val builder = Text.empty()
            visitable.visit({ style, content ->
                if (content != null) {
                    builder.append(Text.literal(content).setStyle(style))
                }
                Optional.empty<Unit>()
            }, Style.EMPTY)
            builder
        }

        val width = textLines.maxOfOrNull { renderer.getWidth(it).toFloat() } ?: 0f
        val height = (renderer.fontHeight * textLines.size + (textLines.size - 1)).toFloat()

        return TextLines(textLines, width, height)
    }
    //#endif

    fun blendColorsRGBA(color1: Long, color2: Long): RGBAColor {
        return blendColorsRGBA(
            RGBAColor.fromLongRGBA(color1),
            RGBAColor.fromLongRGBA(color2)
        )
    }
    fun blendColorsRGBA(color1: RenderColor, color2: RenderColor): RGBAColor {
        val (r1, g1, b1, a1) = color1.getIntComponentsRGBA()
        val (r2, g2, b2, a2) = color2.getIntComponentsRGBA()
        return RGBAColor(
            (r1 + r2) / 2,
            (g1 + g2) / 2,
            (b1 + b2) / 2,
            (a1 + a2) / 2
        )
    }

    abstract class RenderColor {
        abstract val r: Int
        abstract val g: Int
        abstract val b: Int
        abstract val a: Int

        operator fun component1() = r
        operator fun component2() = g
        operator fun component3() = b
        operator fun component4() = a

        abstract fun getLong(): Long
        fun Long.toColorInt(): Int = (this and 0xFFFFFFFFL).toInt()

        fun getIntRGBA(): Int = getLongRGBA().toColorInt()
        fun getIntComponentsRGBA(): IntArray = intArrayOf(r, g, b, a)
        fun getLongRGBA(): Long {
            return ((r.coerceIn(0, 255) shl 24) or
                    (g.coerceIn(0, 255) shl 16) or
                    (b.coerceIn(0, 255) shl 8) or
                    a.coerceIn(0, 255)).toLong() and 0xFFFFFFFFL
        }
        fun getRGBA(): FloatArray = floatArrayOf(
            r.coerceIn(0, 255) / 255f,
            g.coerceIn(0, 255) / 255f,
            b.coerceIn(0, 255) / 255f,
            a.coerceIn(0, 255) / 255f,
        )

        fun getIntARGB(): Int = getLongARGB().toColorInt()
        fun getIntComponentsARGB(): IntArray = intArrayOf(a, r, g, b)
        fun getLongARGB(): Long {
            return ((a.coerceIn(0, 255) shl 24) or
                    (r.coerceIn(0, 255) shl 16) or
                    (g.coerceIn(0, 255) shl 8) or
                    b.coerceIn(0, 255)).toLong() and 0xFFFFFFFFL
        }
        fun getARGB(): FloatArray = floatArrayOf(
            a.coerceIn(0, 255) / 255f,
            r.coerceIn(0, 255) / 255f,
            g.coerceIn(0, 255) / 255f,
            b.coerceIn(0, 255) / 255f,
        )

        fun getHex(): String {
            val hex = StringBuilder("")
            hex.append(r.coerceIn(0, 255).toString(16).padStart(2, '0'))
            hex.append(g.coerceIn(0, 255).toString(16).padStart(2, '0'))
            hex.append(b.coerceIn(0, 255).toString(16).padStart(2, '0'))
            return hex.toString().uppercase()
        }

        override fun toString(): String = "Color(r=$r, g=$g, b=$b, a=$a)"

        companion object {
            internal fun parseHex(hex: String): IntArray {
                val clean = hex.removePrefix("#")
                val len = if (clean.length == 8) 8 else 6
                val padded = clean.padStart(len, '0')
                val r = Integer.parseInt(padded.substring(0, 2), 16)
                val g = Integer.parseInt(padded.substring(2, 4), 16)
                val b = Integer.parseInt(padded.substring(4, 6), 16)
                val a = if (len == 8) Integer.parseInt(padded.substring(6, 8), 16) else 255
                return intArrayOf(r, g, b, a)
            }

            internal fun parseLongRGBA(color: Long): IntArray {
                val intColor = color.toInt()
                val r = (intColor shr 24) and 0xFF
                val g = (intColor shr 16) and 0xFF
                val b = (intColor shr 8) and 0xFF
                val a = intColor and 0xFF
                return intArrayOf(r, g, b, a)
            }
            internal fun parseLongARGB(color: Long): IntArray {
                val (r, g, b, a) = parseLongRGBA(color)
                return intArrayOf(a, r, g, b)
            }
        }
    }

    class ARGBColor(
        override val r: Int,
        override val g: Int,
        override val b: Int,
        override val a: Int = 255
    ) : RenderColor() {
        override fun getLong(): Long = getLongARGB()

        companion object {
            @JvmStatic
            fun fromHex(hex: String): ARGBColor {
                val (r, g, b, a) = parseHex(hex)
                return ARGBColor(r, g, b, a)
            }

            @JvmStatic
            fun fromLongRGBA(color: Long): ARGBColor {
                val (r, g, b, a) = parseLongRGBA(color)
                return ARGBColor(r, g, b, a)
            }
            @JvmStatic
            fun fromLongARGB(color: Long): ARGBColor {
                val (r, g, b, a) = parseLongARGB(color)
                return ARGBColor(r, g, b, a)
            }
        }
    }

    class RGBAColor(
        override val r: Int,
        override val g: Int,
        override val b: Int,
        override val a: Int = 255
    ) : RenderColor() {
        override fun getLong(): Long = getLongRGBA()

        companion object {
            @JvmStatic
            fun fromHex(hex: String): RGBAColor {
                val (r, g, b, a) = parseHex(hex)
                return RGBAColor(r, g, b, a)
            }

            @JvmStatic
            fun fromLongRGBA(color: Long): RGBAColor {
                val (r, g, b, a) = parseLongRGBA(color)
                return RGBAColor(r, g, b, a)
            }
            @JvmStatic
            fun fromLongARGB(color: Long): RGBAColor {
                val (r, g, b, a) = parseLongARGB(color)
                return RGBAColor(r, g, b, a)
            }
        }
    }

    class TrigCache(segments: Int) {
        val cosTheta = FloatArray(segments * 2 + 1)
        val sinTheta = FloatArray(segments * 2 + 1)
        val cosPhi = FloatArray(segments + 1)
        val sinPhi = FloatArray(segments + 1)

        init {
            val thetaStep = 2.0 * Math.PI / (segments * 2)
            for (i in 0..(segments * 2)) {
                val angle = thetaStep * i
                cosTheta[i] = cos(angle).toFloat()
                sinTheta[i] = sin(angle).toFloat()
            }

            val phiStep = Math.PI / segments
            for (i in 0..segments) {
                val angle = phiStep * i
                cosPhi[i] = cos(angle).toFloat()
                sinPhi[i] = sin(angle).toFloat()
            }
        }
    }

    private val trigCaches = mutableMapOf<Int, TrigCache>()
    fun getTrigCache(segments: Int): TrigCache {
        return trigCaches.getOrPut(segments) { TrigCache(segments) }
    }

    val tempNormal = Vector3f()
    fun Vector3f.setAndNormalize(x: Float, y: Float, z: Float): Vector3f {
        //#if MC<12100
        //$$this.set(x, y, z)
        //$$this.normalize()
        //$$return this
        //#else
        return this.set(x, y, z).normalize()
        //#endif
    }
    fun Vector3f.setAndNormalize(vec: Vector3f): Vector3f {
        //#if MC<12100
        //$$this.set(vec)
        //$$this.normalize()
        //$$return this
        //#else
        return this.set(vec).normalize()
        //#endif
    }
    fun Vector3f.setAndNormalize(from: Vector3f, to: Vector3f): Vector3f {
        //#if MC<12100
        //$$this.set(to)
        //$$this.sub(from)
        //$$this.normalize()
        //$$return this
        //#else
        return this.set(to).sub(from).normalize()
        //#endif
    }

    enum class FlattenRoundedRectCorner {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT;
    }

    fun getGradientColors(gradientDirection: GradientDirection, startColor: Long, endColor: Long): GradientColors {
        val startRGBA = RGBAColor.fromLongRGBA(startColor).getLong()
        val endRGBA = RGBAColor.fromLongRGBA(endColor).getLong()
        val blendedRGBA = blendColorsRGBA(startRGBA, endRGBA).getLong()
        return when (gradientDirection) {
            GradientDirection.TOP_TO_BOTTOM -> GradientColors(startRGBA, startRGBA, endRGBA, endRGBA)
            GradientDirection.BOTTOM_TO_TOP -> GradientColors(endRGBA, endRGBA, startRGBA, startRGBA)
            GradientDirection.LEFT_TO_RIGHT -> GradientColors(startRGBA, endRGBA, startRGBA, endRGBA)
            GradientDirection.RIGHT_TO_LEFT -> GradientColors(endRGBA, startRGBA, endRGBA, startRGBA)
            GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT -> GradientColors(
                topLeft = startRGBA,
                topRight = blendedRGBA,
                bottomLeft = blendedRGBA,
                bottomRight = endRGBA,
            )
            GradientDirection.TOP_RIGHT_TO_BOTTOM_LEFT -> GradientColors(
                topLeft = blendedRGBA,
                topRight = startRGBA,
                bottomLeft = endRGBA,
                bottomRight = blendedRGBA,
            )
            GradientDirection.BOTTOM_LEFT_TO_TOP_RIGHT -> GradientColors(
                topLeft = blendedRGBA,
                topRight = endRGBA,
                bottomLeft = startRGBA,
                bottomRight = blendedRGBA,
            )
            GradientDirection.BOTTOM_RIGHT_TO_TOP_LEFT -> GradientColors(
                topLeft = endRGBA,
                topRight = blendedRGBA,
                bottomLeft = blendedRGBA,
                bottomRight = startRGBA,
            )
        }
    }
    enum class GradientDirection {
        TOP_TO_BOTTOM,
        BOTTOM_TO_TOP,
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        TOP_LEFT_TO_BOTTOM_RIGHT,
        TOP_RIGHT_TO_BOTTOM_LEFT,
        BOTTOM_LEFT_TO_TOP_RIGHT,
        BOTTOM_RIGHT_TO_TOP_LEFT;
    }
    data class GradientColors(
        val topLeft: Long,
        val topRight: Long,
        val bottomLeft: Long,
        val bottomRight: Long,
    )

    class ScreenWrapper {
        //#if MC<12100
        //$$fun getWidth(): Int = ScaledResolution(Client.getMinecraft()).scaledWidth
        //$$fun getHeight(): Int = ScaledResolution(Client.getMinecraft()).scaledHeight
        //$$fun getScale(): Double = ScaledResolution(Client.getMinecraft()).scaleFactor.toDouble()
        //#else
        fun getWidth(): Int = Client.getMinecraft().window.scaledWidth
        fun getHeight(): Int = Client.getMinecraft().window.scaledHeight
        fun getScale(): Double = Client.getMinecraft().window.scaleFactor.toDouble()
        //#endif
    }
}
//#endif
