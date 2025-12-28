package org.zephy.zrenderlib

//#if MC==10809 || MC>=12100
//#if MC<12100
//$$import net.minecraft.client.renderer.vertex.VertexFormat
//$$import net.minecraft.client.renderer.vertex.DefaultVertexFormats
//$$enum class VertexFormat(private val mcValue: net.minecraft.client.renderer.vertex.VertexFormat) {
//$$    BLOCK(DefaultVertexFormats.BLOCK),
//$$    ITEM(DefaultVertexFormats.ITEM),
//$$    OLDMODEL_POSITION_TEX_NORMAL(DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL),
//$$    PARTICLE_POSITION_TEX_COLOR_LMAP(DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP),
//$$    POSITION(DefaultVertexFormats.POSITION),
//$$    POSITION_COLOR(DefaultVertexFormats.POSITION_COLOR),
//$$    POSITION_TEX(DefaultVertexFormats.POSITION_TEX),
//$$    POSITION_NORMAL(DefaultVertexFormats.POSITION_NORMAL),
//$$    POSITION_TEX_COLOR(DefaultVertexFormats.POSITION_TEX_COLOR),
//$$    POSITION_TEX_NORMAL(DefaultVertexFormats.POSITION_TEX_NORMAL),
//$$    POSITION_TEX_LMAP_COLOR(DefaultVertexFormats.POSITION_TEX_LMAP_COLOR),
//$$    POSITION_TEX_COLOR_NORMAL(DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
//#else
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.DefaultVertexFormat
enum class VertexFormat(private val mcValue: VertexFormat) {
    //#if MC<=12108
    //$$BLIT_SCREEN(DefaultVertexFormat.BLIT_SCREEN),
    //#endif
    //#if MC>=12111
    POSITION_COLOR_LINE_WIDTH(DefaultVertexFormat.POSITION_COLOR_LINE_WIDTH),
    POSITION_COLOR_NORMAL_LINE_WIDTH(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH),
    //#endif

    POSITION_COLOR_TEXTURE_LIGHT_NORMAL(DefaultVertexFormat.BLOCK),
    POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL(DefaultVertexFormat.NEW_ENTITY),
    POSITION_TEXTURE_COLOR_LIGHT(DefaultVertexFormat.PARTICLE),
    POSITION(DefaultVertexFormat.POSITION),
    POSITION_COLOR(DefaultVertexFormat.POSITION_COLOR),
    POSITION_COLOR_NORMAL(DefaultVertexFormat.POSITION_COLOR_NORMAL),
    POSITION_COLOR_LIGHT(DefaultVertexFormat.POSITION_COLOR_LIGHTMAP),
    POSITION_TEXTURE(DefaultVertexFormat.POSITION_TEX),
    POSITION_TEXTURE_COLOR(DefaultVertexFormat.POSITION_TEX_COLOR),
    POSITION_COLOR_TEXTURE_LIGHT(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),
    POSITION_TEXTURE_LIGHT_COLOR(DefaultVertexFormat.POSITION_TEX_LIGHTMAP_COLOR),
    POSITION_TEXTURE_COLOR_NORMAL(DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL),
    ;
//#endif
    fun toMC() = mcValue

    companion object {
        @JvmStatic
        fun fromMC(ucValue: VertexFormat) = entries.first { it.mcValue == ucValue }
    }
}
//#endif
