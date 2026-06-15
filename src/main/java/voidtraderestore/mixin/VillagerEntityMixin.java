package voidtraderestore.mixin;

import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides the proximity check in {@link MerchantEntity#canInteract}
 * so players can open the trading UI from any distance, as long as
 * the entity is alive. This is necessary because the mod restores
 * trades upon closing the screen, not upon moving away.
 */
@Mixin(MerchantEntity.class)
public class VillagerEntityMixin {
    @Inject(method = "canInteract", at = @At("HEAD"), cancellable = true)
    private void onCanInteract(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(((MerchantEntity) (Object) this).isAlive());
    }
}
