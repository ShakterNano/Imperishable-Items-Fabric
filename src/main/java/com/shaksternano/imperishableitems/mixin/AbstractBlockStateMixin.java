package com.shaksternano.imperishableitems.mixin;

import com.shaksternano.imperishableitems.ImperishableItems;
import com.shaksternano.imperishableitems.registry.ModEnchantments;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    @Shadow public abstract Block getBlock();

    private AbstractBlockStateMixin() {}

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void imperishableShearsUseOnBlock(World world, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (ImperishableItems.getConfig().imperishablePreventsBreaking) {
            if (!player.isCreative()) {
                ItemStack stack = player.getStackInHand(hand);

                if (stack.getItem() instanceof ShearsItem) {
                    if (getBlock() instanceof BeehiveBlock || getBlock() instanceof PumpkinBlock) {
                        if (stack.isDamageable()) {
                            if (EnchantmentHelper.getLevel(ModEnchantments.IMPERISHABLE, stack) > 0) {
                                if (stack.getDamage() >= stack.getMaxDamage()) {
                                    cir.setReturnValue(ActionResult.PASS);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}