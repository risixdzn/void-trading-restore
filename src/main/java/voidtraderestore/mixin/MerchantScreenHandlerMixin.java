package voidtraderestore.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.village.Merchant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import voidtraderestore.VoidTradeRestoreMod;

@Mixin(MerchantScreenHandler.class)
public class MerchantScreenHandlerMixin {
    @Shadow
    private Merchant merchant;

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/village/Merchant;)V", at = @At("TAIL"))
    private void onInit(int syncId, PlayerInventory playerInventory, Merchant merchant, CallbackInfo ci) {
        VoidTradeRestoreMod.onMerchantOpened(playerInventory.player, merchant);
    }

    @Inject(method = "onClosed", at = @At("HEAD"))
    private void onOnClosed(PlayerEntity player, CallbackInfo ci) {
        VoidTradeRestoreMod.onMerchantClosed(player, this.merchant);
    }
}
