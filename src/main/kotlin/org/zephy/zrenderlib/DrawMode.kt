package org.zephy.zrenderlib

//#if MC>=12100
import com.mojang.blaze3d.vertex.VertexFormat

enum class DrawMode(private val mcValue: VertexFormat.Mode) {
    LINES(VertexFormat.Mode.LINES),
    //#if MC<=12110
    //$$LINE_STRIP(VertexFormat.Mode.LINE_STRIP),
    //#else
    LINE_STRIP(VertexFormat.Mode.DEBUG_LINE_STRIP),
    //#endif
    TRIANGLES(VertexFormat.Mode.TRIANGLES),
    TRIANGLE_STRIP(VertexFormat.Mode.TRIANGLE_STRIP),
    TRIANGLE_FAN(VertexFormat.Mode.TRIANGLE_FAN),
    QUADS(VertexFormat.Mode.QUADS);

    fun toMC() = mcValue

    companion object {
        @JvmStatic
        fun fromMC(mcValue: VertexFormat.Mode) = entries.first { it.mcValue == mcValue }
    }
}
//#endif
