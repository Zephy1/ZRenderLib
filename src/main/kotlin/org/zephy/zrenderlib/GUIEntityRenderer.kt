package org.zephy.zrenderlib

//#if MC>=12110
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.entity.state.AvatarRenderState

//#if MC>=12110
//$$import net.minecraft.client.gui.GuiGraphics
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.model.HumanoidModel
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.Pose
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
//#endif

object GUIEntityRenderer {
    @JvmStatic
    fun drawPlayer(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        scale: Float = 1f,
        entity: AbstractClientPlayer? = null,
        cacheTag: String? = null,
    ) {
        val playerEntity = entity ?: Client.getMinecraft().player ?: return
        val renderState = GUIRenderer.getBaseEntityRenderState<AbstractClientPlayer, AvatarRenderState>(playerEntity)
        val renderStateResult = GUIRenderer.getEntityRenderStateResult(renderState, height * 0.5f * scale, cacheTag = cacheTag)
        renderState.scale = 0.95f
        GUIRenderer.drawEntity(drawContext, renderState, x, y, width, height, renderStateResult)
    }

    fun drawStaticPlayerWithArmor(
        drawContext: GuiGraphicsExtractor,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        scale: Float = 1f,
        wornItems: Map<EquipmentSlot, ItemStack> = emptyMap(),
        cacheTag: String? = null,
    ) {
        val mc = Client.getMinecraft()
        val playerEntity = mc.player ?: return
        val renderState = GUIRenderer.getBaseEntityRenderState<AbstractClientPlayer, AvatarRenderState>(playerEntity)
        val renderStateResult = GUIRenderer.getEntityRenderStateResult(renderState, height * 0.5f * scale, cacheTag = cacheTag)
        renderState.walkAnimationSpeed = 0f
        renderState.walkAnimationPos = 0f
        renderState.isAutoSpinAttack = false
        renderState.pose = Pose.STANDING
        renderState.attackTime = 0f
        renderState.isFallFlying = false
        renderState.isVisuallySwimming = false
        renderState.isCrouching = false
        renderState.swimAmount = 0f
        renderState.ticksUsingItem = 0f
        renderState.isPassenger = false
        renderState.capeFlap = 0f
        renderState.capeLean = 0f
        renderState.capeLean2 = 0f
        renderState.arrowCount = 0
        renderState.shouldApplyFlyingYRot = false
        renderState.chestEquipment = wornItems[EquipmentSlot.CHEST] ?: ItemStack.EMPTY
        renderState.legsEquipment = wornItems[EquipmentSlot.LEGS] ?: ItemStack.EMPTY
        renderState.feetEquipment = wornItems[EquipmentSlot.FEET] ?: ItemStack.EMPTY
        renderState.leftArmPose = HumanoidModel.ArmPose.EMPTY
        renderState.rightArmPose = HumanoidModel.ArmPose.EMPTY
        renderState.leftHandItemState.clear()
        renderState.rightHandItemState.clear()
        renderState.scale = 0.95f

        renderState.wornHeadType = null
        val headItem = wornItems[EquipmentSlot.HEAD] ?: ItemStack.EMPTY
        val headEquippable = headItem.item.components().get(DataComponents.EQUIPPABLE)
        val hasArmorModel = headEquippable?.assetId()?.isPresent == true
        if (hasArmorModel) {
            renderState.headEquipment = headItem
            renderState.headItem.clear()
        } else if (!headItem.isEmpty) {
            renderState.headEquipment = ItemStack.EMPTY
            mc.itemModelResolver.updateForLiving(
                renderState.headItem,
                headItem,
                ItemDisplayContext.HEAD,
                playerEntity,
            )
        } else {
            renderState.headEquipment = ItemStack.EMPTY
            renderState.headItem.clear()
        }

        GUIRenderer.drawEntity(drawContext, renderState, x, y, width, height, renderStateResult)
    }
}
//#endif
