package org.zephy.zrenderlib.renderstates

//#if MC>=12106
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.texture.TextureSetup

abstract class BaseGUIRenderState : SimpleGuiElementRenderState {
    abstract val pipeline: RenderPipeline
    abstract val textureSetup: TextureSetup
    abstract val scissorArea: ScreenRect?
    abstract val bounds: ScreenRect?

    override fun pipeline(): RenderPipeline = pipeline
    override fun textureSetup(): TextureSetup = textureSetup
    override fun scissorArea(): ScreenRect? = scissorArea
    override fun bounds(): ScreenRect? = bounds
}
//#endif
