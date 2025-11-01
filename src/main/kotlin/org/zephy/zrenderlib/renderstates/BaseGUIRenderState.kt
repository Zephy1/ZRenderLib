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

    private var cachedBounds: ScreenRect? = null
    override fun bounds(): ScreenRect? {
        if (cachedBounds == null) {
            cachedBounds = getBounds()
        }
        return cachedBounds
    }

    override fun pipeline(): RenderPipeline = pipeline
    override fun textureSetup(): TextureSetup = textureSetup
    override fun scissorArea(): ScreenRect? = scissorArea

    abstract fun getBounds(): ScreenRect?
}
//#endif
