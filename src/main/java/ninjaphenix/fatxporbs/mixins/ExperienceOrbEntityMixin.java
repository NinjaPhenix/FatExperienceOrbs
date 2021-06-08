package ninjaphenix.fatxporbs.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbEntityMixin extends Entity {
    @Shadow
    private int count;

    @Shadow
    private int value;

    @Shadow
    protected abstract int repairPlayerItems(Player player, int i);

    public ExperienceOrbEntityMixin(final EntityType<?> type, final Level world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "canMerge(Lnet/minecraft/world/entity/ExperienceOrb;II)Z", cancellable = true)
    private static void feo_canMerge(ExperienceOrb experienceOrb, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!experienceOrb.isRemoved() && j == experienceOrb.getValue());
    }

    @Inject(method = "playerTouch(Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"))
    private void feo_playerTouch(Player player, CallbackInfo ci) {
        if (!this.level.isClientSide()) {
            player.take(this, count);
            int experience = this.repairPlayerItems(player, value * count);
            if (experience > 0) {
                player.giveExperiencePoints(experience);
            }
            this.discard();
        }
    }
}
