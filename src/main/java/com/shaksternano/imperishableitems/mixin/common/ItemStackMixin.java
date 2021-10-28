package com.shaksternano.imperishableitems.mixin.common;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.shaksternano.imperishableitems.common.ImperishableItems;
import com.shaksternano.imperishableitems.common.network.ModNetworkHandler;
import com.shaksternano.imperishableitems.common.registry.ModEnchantments;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Random;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    private ItemStackMixin() {}

    @Shadow public abstract void setDamage(int damage);

    @Shadow public abstract int getMaxDamage();

    @Shadow public abstract Item getItem();

    @Shadow public abstract int getDamage();

    @Shadow public abstract boolean isDamageable();

    // Tools don't break when they reach 0 durability.
    @Inject(method = "damage(ILjava/util/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void imperishableDurability(int amount, Random random, @Nullable ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir, int i) {
        if (ImperishableItems.getConfig().imperishablePreventsBreaking) {
            if (!(getItem() instanceof ElytraItem)) {
                if (isDamageable()) {
                    ItemStack stack = (ItemStack) (Object) this;

                    if (EnchantmentHelper.getLevel(ModEnchantments.IMPERISHABLE, stack) > 0) {
                        if (i > getMaxDamage()) {
                            setDamage(getMaxDamage());
                        } else {
                            if (player != null) {
                                if (getDamage() < getMaxDamage() && i == getMaxDamage()) {
                                    PacketByteBuf buf = PacketByteBufs.create();
                                    int itemId = Item.getRawId(getItem());
                                    buf.writeInt(itemId);
                                    ServerPlayNetworking.send(player, ModNetworkHandler.EQUIPMENT_BREAK_EFFECTS, buf);
                                }
                            }

                            setDamage(i);
                        }

                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }

    // Tool specific drops such cobblestone do not drop when mined by a Tool with Imperishable at 0 durability.
    @Inject(method = "isSuitableFor", at = @At("HEAD"), cancellable = true)
    private void imperishableSuitableFor(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (ImperishableItems.getConfig().imperishablePreventsBreaking) {
            if (isDamageable()) {
                ItemStack stack = (ItemStack) (Object) this;

                if (EnchantmentHelper.getLevel(ModEnchantments.IMPERISHABLE, stack) > 0) {
                    if (stack.getDamage() >= stack.getMaxDamage()) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }

    // Tools with Imperishable do not have increased mining speed when at 0 durability.
    @Inject(method = "getMiningSpeedMultiplier", at = @At("HEAD"), cancellable = true)
    private void imperishableNoDurabilitySpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
        if (ImperishableItems.getConfig().imperishablePreventsBreaking) {
            if (isDamageable()) {
                ItemStack stack = (ItemStack) (Object) this;

                if (EnchantmentHelper.getLevel(ModEnchantments.IMPERISHABLE, stack) > 0) {
                    if (stack.getDamage() >= stack.getMaxDamage()) {
                        cir.setReturnValue(1.0F);
                    }
                }
            }
        }
    }

    // Tools with Imperishable do not give bonus attributes such as attack damage on a sword when at 0 durability.
    @Inject(method = "getAttributeModifiers", at = @At("HEAD"), cancellable = true)
    private void imperishableAttributeModifiers(EquipmentSlot equipmentSlot, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir) {
        if (ImperishableItems.getConfig().imperishablePreventsBreaking) {
            if (isDamageable()) {
                ItemStack stack = (ItemStack) (Object) this;

                if (EnchantmentHelper.getLevel(ModEnchantments.IMPERISHABLE, stack) > 0) {
                    if (stack.getDamage() >= stack.getMaxDamage()) {
                        cir.setReturnValue(ImmutableMultimap.of());
                    }
                }
            }
        }
    }

    // Tool specific right click actions are cancelled if the tool has Imperishable and is at 0 durability.
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void imperishableUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (ImperishableItems.getConfig().imperishablePreventsBreaking) {
            if (!user.isCreative()) {
                if (!(getItem() instanceof Wearable)) {
                    if (isDamageable()) {
                        ItemStack stack = (ItemStack) (Object) this;

                        if (EnchantmentHelper.getLevel(ModEnchantments.IMPERISHABLE, stack) > 0) {
                            if (stack.getDamage() >= stack.getMaxDamage()) {
                                cir.setReturnValue(TypedActionResult.pass(user.getStackInHand(hand)));
                            }
                        }
                    }
                }
            }
        }
    }

    // Tool specific right click block actions are cancelled if the tool has Imperishable and is at 0 durability.
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void imperishableUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (ImperishableItems.getConfig().imperishablePreventsBreaking) {
            PlayerEntity player = context.getPlayer();
            boolean userIsCreative = false;
            if (player != null) {
                if (player.isCreative()) {
                    userIsCreative = true;
                }
            }

            if (!userIsCreative) {
                if (isDamageable()) {
                    ItemStack stack = (ItemStack) (Object) this;

                    if (EnchantmentHelper.getLevel(ModEnchantments.IMPERISHABLE, stack) > 0) {
                        if (stack.getDamage() >= stack.getMaxDamage()) {
                            cir.setReturnValue(ActionResult.PASS);
                        }
                    }
                }
            }
        }
    }

    // Tool specific right click entity are cancelled if the tool has Imperishable and is at 0 durability.
    @Inject(method = "useOnEntity", at = @At("HEAD"), cancellable = true)
    private void imperishableUseOnEntity(PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (ImperishableItems.getConfig().imperishablePreventsBreaking) {
            if (!user.isCreative()) {
                if (isDamageable()) {
                    ItemStack stack = (ItemStack) (Object) this;

                    if (EnchantmentHelper.getLevel(ModEnchantments.IMPERISHABLE, stack) > 0) {
                        if (stack.getDamage() >= stack.getMaxDamage()) {
                            cir.setReturnValue(ActionResult.PASS);
                        }
                    }
                }
            }
        }
    }

    // Adds "(Broken)" to the name of a tool with Imperishable at 0 durability.
    @Inject(method = "getName", at = @At("RETURN"), cancellable = true)
    private void imperishableBrokenName(CallbackInfoReturnable<Text> cir) {
        if (ImperishableItems.getConfig().imperishablePreventsBreaking) {
            if (isDamageable()) {
                ItemStack stack = (ItemStack) (Object) this;

                if (EnchantmentHelper.getLevel(ModEnchantments.IMPERISHABLE, stack) > 0) {
                    if (stack.getDamage() >= stack.getMaxDamage()) {
                        TranslatableText broken = new TranslatableText("item.name." + ImperishableItems.MOD_ID + ".imperishableBroken");
                        broken.formatted(Formatting.RED);

                        Text brokenName = ((MutableText) cir.getReturnValue()).append(broken);
                        cir.setReturnValue(brokenName);
                    }
                }
            }
        }
    }

    // Adds a message to the tooltip for tools with Imperishable at 0 durability.
    @Inject(method = "getTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/item/TooltipContext;isAdvanced()Z"
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z"
                    ),
                    to = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/item/ItemStack;isDamaged()Z"
                    )
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void imperishableBrokenTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> list) {
        if (ImperishableItems.getConfig().imperishablePreventsBreaking) {
            if (isDamageable()) {
                ItemStack stack = (ItemStack) (Object) this;

                if (EnchantmentHelper.getLevel(ModEnchantments.IMPERISHABLE, stack) > 0) {
                    if (stack.getDamage() >= stack.getMaxDamage()) {
                        list.add(LiteralText.EMPTY);
                        list.add(new TranslatableText("item.tooltip." + ImperishableItems.MOD_ID + ".imperishableBroken").formatted(Formatting.RED));
                    }
                }
            }
        }
    }
}