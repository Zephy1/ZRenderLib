package org.zephy.zrenderlib

//#if MC>=12100
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier

object RenderLayers {
    @JvmStatic
    fun getRenderLayer(
        drawMode: DrawMode,
        vertexFormat: VertexFormat? = null,
    ): RenderLayer? {
        if (vertexFormat != null) {
            return getRenderLayerFunction_DrawModeVertexFormat(drawMode)?.invoke(drawMode, vertexFormat)
        }
        return getRenderLayerFunction_DrawMode(drawMode)?.invoke(drawMode)
    }

    private fun getRenderLayerFunction_DrawModeVertexFormat(drawMode: DrawMode): ((DrawMode, VertexFormat) -> RenderLayer)? {
        return when (drawMode) {
            DrawMode.LINES -> ::LINES
            DrawMode.LINE_STRIP -> ::LINE_STRIP
            DrawMode.TRIANGLES -> ::TRIANGLES
            DrawMode.TRIANGLE_STRIP -> ::TRIANGLE_STRIP
            DrawMode.TRIANGLE_FAN -> ::TRIANGLE_FAN
            DrawMode.QUADS -> ::QUADS
        }
    }

    private fun getRenderLayerFunction_DrawMode(drawMode: DrawMode): ((DrawMode) -> RenderLayer)? {
        return when (drawMode) {
            DrawMode.LINES -> ::LINES
            DrawMode.LINE_STRIP -> ::LINE_STRIP
            DrawMode.TRIANGLES -> ::TRIANGLES
            DrawMode.TRIANGLE_STRIP -> ::TRIANGLE_STRIP
            DrawMode.TRIANGLE_FAN -> ::TRIANGLE_FAN
            DrawMode.QUADS -> ::QUADS
        }
    }

    private fun getRenderLayerFunction(drawMode: DrawMode): (() -> RenderLayer)? {
        return when (drawMode) {
            DrawMode.LINES -> ::LINES
            DrawMode.LINE_STRIP -> ::LINE_STRIP
            DrawMode.TRIANGLES -> ::TRIANGLES
            DrawMode.TRIANGLE_STRIP -> ::TRIANGLE_STRIP
            DrawMode.TRIANGLE_FAN -> ::TRIANGLE_FAN
            DrawMode.QUADS -> ::QUADS
        }
    }

    @JvmStatic
    fun LINES(
        drawMode: DrawMode = DrawMode.LINES,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR_NORMAL,
        snippet: RenderSnippet = RenderSnippet.RENDERTYPE_LINES_SNIPPET,
        lineThickness: Float = 1.0f,
    ): RenderLayer {
        return RenderPipelines
            .LINES(drawMode, vertexFormat, snippet)
            .setLineWidth(lineThickness)
            .layer()
    }

    @JvmStatic
    fun LINES_ESP(
        drawMode: DrawMode = DrawMode.LINES,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR_NORMAL,
        snippet: RenderSnippet = RenderSnippet.RENDERTYPE_LINES_SNIPPET,
        lineThickness: Float = 1.0f,
    ): RenderLayer {
        return RenderPipelines
            .LINES_ESP(drawMode, vertexFormat, snippet)
            .setLineWidth(lineThickness)
            .layer()
    }

    @JvmStatic
    fun LINE_STRIP(
        drawMode: DrawMode = DrawMode.LINE_STRIP,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR_NORMAL,
        snippet: RenderSnippet = RenderSnippet.RENDERTYPE_LINES_SNIPPET,
        lineThickness: Float = 1.0f,
    ): RenderLayer {
        return RenderPipelines
            .LINE_STRIP(drawMode, vertexFormat, snippet)
            .setLineWidth(lineThickness)
            .layer()
    }

    @JvmStatic
    fun LINE_STRIP_ESP(
        drawMode: DrawMode = DrawMode.LINE_STRIP,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR_NORMAL,
        snippet: RenderSnippet = RenderSnippet.RENDERTYPE_LINES_SNIPPET,
        lineThickness: Float = 1.0f,
    ): RenderLayer {
        return RenderPipelines
            .LINE_STRIP_ESP(drawMode, vertexFormat, snippet)
            .setLineWidth(lineThickness)
            .layer()
    }

    @JvmStatic
    fun TRIANGLES(
        drawMode: DrawMode = DrawMode.TRIANGLES,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderLayer {
        return RenderPipelines
            .TRIANGLES(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun TRIANGLES_ESP(
        drawMode: DrawMode = DrawMode.TRIANGLES,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderLayer {
        return RenderPipelines
            .TRIANGLES_ESP(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun TRIANGLE_STRIP(
        drawMode: DrawMode = DrawMode.TRIANGLE_STRIP,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderLayer {
        return RenderPipelines
            .TRIANGLE_STRIP(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun TRIANGLE_STRIP_ESP(
        drawMode: DrawMode = DrawMode.TRIANGLE_STRIP,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderLayer {
        return RenderPipelines
            .TRIANGLE_STRIP_ESP(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun TRIANGLE_FAN(
        drawMode: DrawMode = DrawMode.TRIANGLE_FAN,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderLayer {
        return RenderPipelines
            .TRIANGLE_FAN(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun TRIANGLE_FAN_ESP(
        drawMode: DrawMode = DrawMode.TRIANGLE_FAN,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderLayer {
        return RenderPipelines
            .TRIANGLE_FAN_ESP(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun QUADS(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderLayer {
        return RenderPipelines
            .QUADS(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun QUADS_ESP(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderLayer {
        return RenderPipelines
            .QUADS_ESP(drawMode, vertexFormat, snippet)
            .layer()
    }

    fun TEXTURED_QUADS(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_TEXTURE_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_TEX_COLOR_SNIPPET,
        textureIdentifier: Identifier,
    ): RenderLayer {
        return RenderPipelines
            .TEXTURED_QUADS(drawMode, vertexFormat, snippet)
            .setTexture(textureIdentifier)
            .layer()
    }

    fun TEXTURED_QUADS_ESP(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_TEXTURE_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_TEX_COLOR_SNIPPET,
        textureIdentifier: Identifier,
    ): RenderLayer {
        return RenderPipelines
            .TEXTURED_QUADS(drawMode, vertexFormat, snippet)
            .setTexture(textureIdentifier)
            .layer()
    }
}
//#endif
