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
    @Shadow
    public int amount;

    public ExperienceOrbEntityMixin(EntityType<?> type, World world) { super(type, world); }

    @Inject(at = @At("TAIL"), method = "tick", cancellable = true)
    public void tick(CallbackInfo info)
    {
        if (world.getTime() % 40 == 0 && !world.isClient)
        {
            BlockPos pos = getBlockPos();
            List<ExperienceOrbEntity> entities = world.getEntities(ExperienceOrbEntity.class,
                    new Box(pos.west(2).north(2).up(2), pos.east(2).south(2).down(2)), (entity) -> !entity.removed);
            if (entities.size() > 0)
            {
                int old = amount;
                for (ExperienceOrbEntity entity : entities)
                {
                    if (entity.getEntityId() != this.getEntityId() && !entity.removed)
                    {
                        amount += entity.getExperienceAmount();
                        entity.remove();
                    }
                }
                if(amount != old)
                {
                    Entity newOrb = new ExperienceOrbEntity(world, pos.getX(), pos.getY(), pos.getZ(), amount);
                    newOrb.setVelocity(newOrb.getVelocity().multiply(0.5));
                    world.spawnEntity(newOrb);
                    remove();
                }
            }
        }
    }
}
