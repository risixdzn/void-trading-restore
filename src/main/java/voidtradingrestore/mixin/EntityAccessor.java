package voidtradingrestore.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes the {@link Entity#world} field so the mod can cast players
 * to their server world context without requiring a separate lookup.
 */
@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("world")
    World getWorld();
}
