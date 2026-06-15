package voidtraderestore.mixin;

import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffer.class)
public interface TradeOfferAccessor {
    @Accessor("uses")
    void setUses(int uses);

    @Accessor("maxUses")
    void setMaxUses(int maxUses);

    @Accessor("demandBonus")
    void setDemandBonus(int demandBonus);
}
