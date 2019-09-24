package ninjaphenix.fatxporbs.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin extends Entity
{
    @Shadow private int amount;

    @Shadow public int orbAge;

    public ExperienceOrbEntityMixin(EntityType<?> type, World world) { super(type, world); }

    @Inject(at = @At("TAIL"), method = "tick", cancellable = true)
    private void tick(CallbackInfo info)
    {
        if (world.isClient) return;
        if (world.getTime() % 5 == 0)
        {
            BlockPos pos = getBlockPos();
            List<ExperienceOrbEntity> entities = world.getEntities(ExperienceOrbEntity.class,
                    new Box(pos.west(2).north(2).up(2), pos.east(2).south(2).down(2)), e -> e.isAlive() && !e.getUuid().equals(uuid));
            if (entities.isEmpty()) return;
            ExperienceOrbEntity orb = entities.get(0);
            amount += orb.getExperienceAmount();
            orb.kill();
            orbAge = 0;
        }
    }
}
