package org.zephy.zrenderlib

//#if MC>=12100
import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.resources.Identifier
import java.util.OptionalDouble

//#if MC<=12105
//$$import net.minecraft.util.TriState
//#endif

//#if MC<=12110
//$$import net.minecraft.client.renderer.RenderStateShard
//#else
import net.minecraft.client.renderer.rendertype.LayeringTransform
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.rendertype.RenderSetup
import java.util.OptionalInt
import com.mojang.blaze3d.textures.GpuTexture
import com.mojang.blaze3d.systems.RenderSystem
//#endif

object PipelineBuilder {
    private val layerList = mutableMapOf<String, RenderType>()
    private val pipelineList = mutableMapOf<String, RenderPipeline>()
    private var cull: Boolean? = null
    private var depthTestFunction: DepthTestFunction? = null
    private var blendFunction: BlendFunction? = null
    //#if MC<=12110
    //$$private var lineWidth: Float? = null
    //$$private var layering: RenderStateShard.LayeringStateShard? = null
    //#else
    private var layering: LayeringTransform? = null
    private var texture: GpuTexture? = null
    //#endif
    private var textureIdentifier: Identifier? = null
    private var drawMode = DrawMode.QUADS
    private var vertexFormat = VertexFormat.POSITION_COLOR
    private var snippet = RenderSnippet.POSITION_COLOR_SNIPPET
    private var location: String? = null
    private var bufferSize: Int? = null

    @JvmStatic
    @JvmOverloads
    fun begin(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ) = apply {
        this.drawMode = drawMode
        this.vertexFormat = vertexFormat
        this.snippet = snippet
    }

    @JvmStatic
    fun enableBlend() = apply {
        setBlendFunction(BlendFunction.TRANSLUCENT)
    }

    @JvmStatic
    fun disableBlend() = apply {
        blendFunction = null
    }

    @JvmStatic
    fun enableCull() = apply {
        cull = true
    }

    @JvmStatic
    fun disableCull() = apply {
        cull = false
    }

    @JvmStatic
    fun enableDepth() = apply {
        setDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
    }

    @JvmStatic
    fun disableDepth() = apply {
        setDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
    }

    @JvmStatic
    fun setLocation(newValue: String?) = apply {
        location = newValue
    }

    @JvmStatic
    //#if MC<=12110
    //$$fun setLayering(newValue: RenderStateShard.LayeringStateShard?) = apply {
    //#else
    fun setLayering(newValue: LayeringTransform?) = apply {
        //#endif
        layering = newValue
    }

    @JvmStatic
    fun setLineWidth(newValue: Float?) = apply {
        //#if MC<=12110
        //$$lineWidth = newValue
        //#endif
    }

    @JvmStatic
    fun setTexture(newValue: Identifier?) = apply {
        textureIdentifier = newValue
    }
    //#if MC>=12111
    @JvmStatic
    fun setTexture(newValue: GpuTexture?) = apply {
        texture = newValue
    }
    //#endif

    @JvmStatic
    fun setDepthTestFunction(newValue: DepthTestFunction) = apply {
        depthTestFunction = newValue
    }

    @JvmStatic
    fun setBlendFunction(newValue: BlendFunction) = apply {
        blendFunction = newValue
    }

    @JvmStatic
    fun setBufferSize(size: Int) = apply {
        bufferSize = size
    }

    @JvmStatic
    fun build(): RenderPipeline {
        if (pipelineList.containsKey(state())) return pipelineList[state()]!!

        val basePipeline = RenderPipeline
            .builder(snippet.toMC())
            .withLocation("zrenderlib/custom/pipelines/${location ?: hashCode()}")
            .withVertexFormat(vertexFormat.toMC(), drawMode.toMC())

        blendFunction?.let {
            basePipeline.withBlend(it)
        } ?: basePipeline.withoutBlend()

        cull?.let {
            basePipeline.withCull(cull!!)
        }

        depthTestFunction?.let {
            when (it) {
                DepthTestFunction.NO_DEPTH_TEST -> basePipeline.withDepthWrite(false)
                else -> basePipeline.withDepthWrite(true)
            }
            basePipeline.withDepthTestFunction(it)
        }

        val pipeline = basePipeline.build()
        pipelineList[state()] = pipeline

        return pipeline
    }

    @JvmStatic
    fun layer(): RenderType {
        try {
            if (layerList.containsKey(state())) return layerList[state()]!!

            //#if MC<=12110
            //$$val layerBuilder = RenderType.CompositeState.builder()
            //$$if (textureIdentifier != null) {
            //#if MC<=12105
            //$$    layerBuilder.setTextureState(RenderStateShard.TextureStateShard(textureIdentifier!!, TriState.FALSE, false))
            //#else
            //$$    layerBuilder.setTextureState(RenderStateShard.TextureStateShard(textureIdentifier!!, false))
            //#endif
            //$$}
            //$$if (lineWidth != null) {
            //$$    layerBuilder.setLineState(RenderStateShard.LineStateShard(OptionalDouble.of(lineWidth!!.toDouble())))
            //$$}
            //$$if (layering != null) {
            //$$    layerBuilder.setLayeringState(layering!!)
            //$$}
            //$$val layer = RenderType.create(
            //$$    "zrenderlib/custom/layers/${location ?: hashCode()}",
            //$$    bufferSize ?: RenderType.TRANSIENT_BUFFER_SIZE,
            //$$    build(),
            //$$    layerBuilder.createCompositeState(false),
            //$$)
            //#else
            val layerBuilder = RenderSetup.builder(build())
            if (textureIdentifier != null) {
                layerBuilder.withTexture("zrenderlib/custom/textures/${location ?: hashCode()}", textureIdentifier!!)
            }
            if (texture != null) {
                val mc = Client.getMinecraft().mainRenderTarget.let { fb ->
                    RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                        { "Immediate draw for $textureIdentifier" },
                        RenderSystem.outputColorTextureOverride ?: fb.colorTextureView!!,
                        OptionalInt.empty(),
                        RenderSystem.outputDepthTextureOverride ?: fb.depthTextureView,
                        OptionalDouble.empty(),
                    )
                }
                mc.bindTexture("zrenderlib/custom/textures/${location ?: hashCode()}", RenderSystem.getDevice().createTextureView(texture!!), RenderTypes.MOVING_BLOCK_SAMPLER.get())
            }
            if (layering != null) {
                layerBuilder.setLayeringTransform(layering!!)
            }
            if (blendFunction != null) {
                layerBuilder.sortOnUpload()
            }
            val layer = createRenderLayer(
                "zrenderlib/custom/layers/${location ?: hashCode()}",
                layerBuilder.createRenderSetup(),
            )
            //#endif

            layerList[state()] = layer
            return layer
        } finally {
            reset()
        }
    }

    //#if MC>=12111
    private fun createRenderLayer(name: String, renderSetup: RenderSetup) =
        RenderType::class.java.declaredMethods.first {
            it.returnType == RenderType::class.java && it.parameterTypes.contentEquals(arrayOf(
                String::class.java,
                RenderSetup::class.java,
            ))
        }.apply { isAccessible = true }.invoke(null, name, renderSetup) as RenderType
    //#endif

    @JvmStatic
    private fun reset() {
        cull = null
        depthTestFunction = null
        blendFunction = null
        layering = null
        textureIdentifier = null
        drawMode = DrawMode.QUADS
        vertexFormat = VertexFormat.POSITION_COLOR
        snippet = RenderSnippet.POSITION_COLOR_SNIPPET
        location = null
        bufferSize = null
        //#if MC<=12110
        //$$lineWidth = null
        //#else
        texture = null
        //#endif
    }

    @JvmStatic
    fun state(): String {
        return (
            "PipelineBuilder[" +
                "location=$location, " +
                "cull=$cull, " +
                "depth=$depthTestFunction, " +
                "blend=$blendFunction, " +
                "layering=$layering, " +
                "drawMode=${drawMode.name}, " +
                "vertexFormat=${vertexFormat.name}, " +
                "snippet=${snippet.name}, " +
                "textureIdentifier=${textureIdentifier}, " +
                "bufferSize=${bufferSize}, " +
                //#if MC<=12110
                //$$"lineWidth=${lineWidth}" +
                //#else
                "texture=${texture}" +
                //#endif
            "]"
        )
    }
}
//#endif
