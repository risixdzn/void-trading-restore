package voidtraderestore.mixin;

import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes mutable fields on {@link TradeOffer} so the mod can
 * reset trade usage count, maximum uses, and demand bonus when
 * restoring a trade offer snapshot.
 */
@Mixin(TradeOffer.class)
public interface TradeOfferAccessor {
    @Accessor("uses")
    void setUses(int uses);

    @Accessor("maxUses")
    void setMaxUses(int maxUses);

    @Accessor("demandBonus")
    void setDemandBonus(int demandBonus);
}
