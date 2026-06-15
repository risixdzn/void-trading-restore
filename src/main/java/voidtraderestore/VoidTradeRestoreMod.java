package voidtraderestore;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import voidtraderestore.mixin.EntityAccessor;
import voidtraderestore.mixin.TradeOfferAccessor;

import java.util.*;

public class VoidTradeRestoreMod implements ModInitializer {
    public static final String MOD_ID = "voidtraderestore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final Map<UUID, TradeOfferList> SNAPSHOTS = new HashMap<>();

    @Override
    public void onInitialize() {
        LOGGER.info("VoidTradeRestore loaded");
    }

    public static void onMerchantOpened(PlayerEntity player, Merchant merchant) {
        if (merchant.isClient()) return;
        TradeOfferList offers = merchant.getOffers();
        if (offers != null && !offers.isEmpty()) {
            SNAPSHOTS.put(player.getUuid(), offers.copy());
        }
    }

    public static void onMerchantClosed(PlayerEntity player, Merchant merchant) {
        if (merchant.isClient()) return;
        UUID playerId = player.getUuid();
        TradeOfferList snapshot = SNAPSHOTS.remove(playerId);
        if (snapshot == null) return;

        if (!(merchant instanceof MerchantEntity entity)) return;
        if (!(((EntityAccessor) player).getWorld() instanceof ServerWorld world)) return;

        int simDist = 8;
        double range = simDist * 16.0 + 48.0;
        double rangeSq = range * range;

        boolean isolated = true;
        for (ServerPlayerEntity other : world.getPlayers()) {
            if (other == player) continue;
            if (entity.squaredDistanceTo(other) <= rangeSq) {
                isolated = false;
                break;
            }
        }

        if (isolated) {
            restoreSnapshot(merchant, snapshot);
        }
    }

    private static void restoreSnapshot(Merchant merchant, TradeOfferList snapshot) {
        TradeOfferList current = merchant.getOffers();
        if (current == null) return;

        for (int i = 0; i < current.size() && i < snapshot.size(); i++) {
            TradeOffer currentOffer = current.get(i);
            TradeOffer saved = snapshot.get(i);
            TradeOfferAccessor accessor = (TradeOfferAccessor) currentOffer;
            accessor.setUses(0);
            accessor.setDemandBonus(0);
            currentOffer.setSpecialPrice(0);
        }

        merchant.setOffersFromServer(current);
    }

    public static void clearSnapshotsForPlayer(PlayerEntity player) {
        SNAPSHOTS.remove(player.getUuid());
    }
}
