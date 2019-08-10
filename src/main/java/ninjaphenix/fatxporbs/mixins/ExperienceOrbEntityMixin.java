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

    @Shadow
    public int health;

    @Shadow
    public int orbAge;

    public ExperienceOrbEntityMixin(EntityType<?> entityType_1, World world_1)
    {
        super(entityType_1, world_1);
    }

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
                        this.amount += entity.getExperienceAmount();
                        this.health = 5;
                        this.orbAge = 0;
                        entity.remove();
                    }
                }
                if(amount != old)
                {
                    Entity newOrb = new ExperienceOrbEntity(world, pos.getX(), pos.getY(), pos.getZ(), this.amount);
                    world.spawnEntity(newOrb);
                    newOrb.setVelocity(0, 0, 0);
                    remove();
                }
            }
        }
    }
}
