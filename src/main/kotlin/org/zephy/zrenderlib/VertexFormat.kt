package org.zephy.zrenderlib

//#if MC == 10809 || MC >= 12100

//#if MC < 12100
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
import net.minecraft.client.render.VertexFormats
enum class VertexFormat(private val mcValue: VertexFormat) {
    //#if MC<=12108
    BLIT_SCREEN(VertexFormats.BLIT_SCREEN),
    //#endif
    POSITION_COLOR_TEXTURE_LIGHT_NORMAL(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL),
    POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
    POSITION_TEXTURE_COLOR_LIGHT(VertexFormats.POSITION_TEXTURE_COLOR_LIGHT),
    POSITION(VertexFormats.POSITION),
    POSITION_COLOR(VertexFormats.POSITION_COLOR),
    POSITION_COLOR_NORMAL(VertexFormats.POSITION_COLOR_NORMAL),
    POSITION_COLOR_LIGHT(VertexFormats.POSITION_COLOR_LIGHT),
    POSITION_TEXTURE(VertexFormats.POSITION_TEXTURE),
    POSITION_TEXTURE_COLOR(VertexFormats.POSITION_TEXTURE_COLOR),
    POSITION_COLOR_TEXTURE_LIGHT(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT),
    POSITION_TEXTURE_LIGHT_COLOR(VertexFormats.POSITION_TEXTURE_LIGHT_COLOR),
    POSITION_TEXTURE_COLOR_NORMAL(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
//#endif
    fun toMC() = mcValue

    companion object {
        @JvmStatic
        fun fromMC(ucValue: VertexFormat) = entries.first { it.mcValue == ucValue }
    }
}
//#endif
