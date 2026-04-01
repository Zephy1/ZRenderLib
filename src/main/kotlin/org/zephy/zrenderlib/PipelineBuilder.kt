package org.zephy.zrenderlib

//#if MC>=12100
import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.RenderPipeline
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.resources.Identifier

//#if MC<=12105
//$$import net.minecraft.util.TriState
//$$import java.util.OptionalDouble
//#endif

//#if MC<=12110
//$$import net.minecraft.client.renderer.RenderStateShard
//#else
import net.minecraft.client.renderer.rendertype.LayeringTransform
import net.minecraft.client.renderer.rendertype.RenderSetup
import com.mojang.blaze3d.textures.GpuTexture
//#endif

//#if MC<=12111
//$$import com.mojang.blaze3d.platform.DepthTestFunction
//#else
import com.mojang.blaze3d.pipeline.ColorTargetState
import com.mojang.blaze3d.pipeline.DepthStencilState
import java.util.Optional
//#endif

object PipelineBuilder {
    private val layerList = mutableMapOf<String, RenderType>()
    private val pipelineList = mutableMapOf<String, RenderPipeline>()
    private var cull: Boolean? = null
    //#if MC<=12111
    //$$private var depthTestFunction: DepthTestFunction? = null
    //$$private var blendFunction: BlendFunction? = null
    //#else
    private var depthTestFunction: Optional<DepthStencilState> = Optional.empty()
    private var blendFunction: Optional<BlendFunction> = Optional.empty()
    //#endif
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
        //#if MC<=12111
        //$$blendFunction = null
        //#else
        blendFunction = Optional.empty()
        //#endif
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
        RenderUtils.depthFunc(RenderUtils.getDepthTestFunctionFromInt(0x203)) // LEQUAL_DEPTH_TEST, LESS_THAN_OR_EQUAL
    }

    @JvmStatic
    fun disableDepth() = apply {
        RenderUtils.depthFunc(RenderUtils.getDepthTestFunctionFromInt(0x207)) // NO_DEPTH_TEST, ALWAYS_PASS
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
    //#if MC<=12111
    //$$fun setDepthTestFunction(newValue: DepthTestFunction) = apply {
    //$$    depthTestFunction = newValue
    //#else
    fun setDepthTestFunction(newValue: DepthStencilState?) = apply {
        depthTestFunction = Optional.ofNullable(newValue)
    //#endif
    }

    @JvmStatic
    fun setBlendFunction(newValue: BlendFunction) = apply {
        //#if MC<=12111
        //$$blendFunction = newValue
        //#else
        blendFunction = Optional.of(newValue)
        //#endif
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

        //#if MC<=12111
        //$$blendFunction?.let {
        //$$    basePipeline.withBlend(it)
        //$$} ?: basePipeline.withoutBlend()
        //#else
        basePipeline.withColorTargetState(ColorTargetState(blendFunction, 15))
        //#endif

        cull?.let {
            basePipeline.withCull(cull!!)
        }

        //#if MC<=12111
        //$$depthTestFunction?.let {
        //$$    when (it) {
        //$$        DepthTestFunction.NO_DEPTH_TEST -> basePipeline.withDepthWrite(false)
        //$$        else -> basePipeline.withDepthWrite(true)
        //$$    }
        //$$    basePipeline.withDepthTestFunction(it)
        //$$}
        //#else
        basePipeline.withDepthStencilState(depthTestFunction)
        //#endif

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
            //#else
            val layerBuilder = RenderSetup.builder(build())
            //#endif

            if (textureIdentifier != null) {
                //#if MC<=12105
                //$$layerBuilder.setTextureState(RenderStateShard.TextureStateShard(textureIdentifier!!, TriState.FALSE, false))
                //#elseif MC<=12110
                //$$layerBuilder.setTextureState(RenderStateShard.TextureStateShard(textureIdentifier!!, false))
                //#else
                layerBuilder.withTexture("zrenderlib/custom/textures/${location ?: hashCode()}", textureIdentifier!!)
                //#endif
            }

            //#if MC<=12105
            //$$if (lineWidth != null) {
            //$$    layerBuilder.setLineState(RenderStateShard.LineStateShard(OptionalDouble.of(lineWidth!!.toDouble())))
            //$$}
            //#endif

            if (layering != null) {
                //#if MC<=12110
                //$$layerBuilder.setLayeringState(layering!!)
                //#else
                layerBuilder.setLayeringTransform(layering!!)
                //#endif
            }

            //#if MC>=12111
            //#if MC<=12111
            //$$if (blendFunction != null) {
            //#else
            if (blendFunction.isPresent) {
            //#endif
                layerBuilder.sortOnUpload()
            }
            //#endif

            //#if MC<=12110
            //$$val layer = RenderType.create(
            //$$    "zrenderlib/custom/layers/${location ?: hashCode()}",
            //$$    bufferSize ?: RenderType.TRANSIENT_BUFFER_SIZE,
            //$$    build(),
            //$$    layerBuilder.createCompositeState(false),
            //$$)
            //#else
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
        //#if MC<=12111
        //$$depthTestFunction = null
        //$$blendFunction = null
        //#else
        depthTestFunction = Optional.empty()
        blendFunction = Optional.empty()
        //#endif
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
