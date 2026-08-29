package org.zephy.zrenderlib.mixins;

//#if MC>=26.2
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zephy.zrenderlib.RenderUtils;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @ModifyExpressionValue(
        method = "submitFeatures",
        at = @At(
            value = "NEW",
            target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"
        )
    )
    private PoseStack onMatrixStack(PoseStack original) {
        RenderUtils.setMatrixStackInternal(original);
        return original;
    }
}
//#endif
