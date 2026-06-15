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

/**
 * Restores villager trade offers to their original state when the trading
 * screen is closed, provided the villager is outside every player's
 * simulation distance. This recreates the void trading mechanic where
 * transporting a villager far away (e.g. through a portal) rewards the
 * effort with infinite trades.
 */
public class VoidTradeRestoreMod implements ModInitializer {
    public static final String MOD_ID = "voidtraderestore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final Map<UUID, TradeOfferList> SNAPSHOTS = new HashMap<>();

    @Override
    public void onInitialize() {
        LOGGER.info("VoidTradeRestore loaded");
    }

    /**
     * Called when a player opens a merchant screen. Saves a copy of the
     * current trade offers so they can be restored later.
     */
    public static void onMerchantOpened(PlayerEntity player, Merchant merchant) {
        if (merchant.isClient()) return;
        TradeOfferList offers = merchant.getOffers();
        if (offers != null && !offers.isEmpty()) {
            SNAPSHOTS.put(player.getUuid(), offers.copy());
        }
    }

    /**
     * Called when a player closes a merchant screen. Restores the saved
     * trade offers only if the merchant entity is outside every player's
     * simulation distance, replicating the void trading mechanic.
     */
    public static void onMerchantClosed(PlayerEntity player, Merchant merchant) {
        if (merchant.isClient()) return;
        UUID playerId = player.getUuid();
        TradeOfferList snapshot = SNAPSHOTS.remove(playerId);
        if (snapshot == null) return;

        if (!(merchant instanceof MerchantEntity entity)) return;
        if (!(((EntityAccessor) player).getWorld() instanceof ServerWorld world)) return;

        int simDist = Math.max(1, world.getServer().getPlayerManager().getSimulationDistance());
        double range = simDist * 16.0 + 48.0;
        double rangeSq = range * range;

        boolean isolated = true;
        for (ServerPlayerEntity other : world.getPlayers()) {
            if (entity.squaredDistanceTo(other) <= rangeSq) {
                isolated = false;
                break;
            }
        }

        if (isolated) {
            restoreSnapshot(merchant, snapshot);
        }
    }

    /**
     * Applies a saved trade offer snapshot to the merchant, resetting uses,
     * demand bonus, and special price to their original values.
     */
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

    /**
     * Removes any stored trade snapshot for the given player.
     */
    public static void clearSnapshotsForPlayer(PlayerEntity player) {
        SNAPSHOTS.remove(player.getUuid());
    }
}
