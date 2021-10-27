package com.shaksternano.imperishableitems.mixin;

import com.shaksternano.imperishableitems.access.BlockEntityAccess;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements BlockEntityAccess {

    private BlockEntityMixin() {}

    private NbtElement enchantments;
    private Integer repairCost;

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void getEnchantmentsForNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        if (enchantments != null) {
            nbt.put("Enchantments", enchantments);
        }

        if (repairCost != null) {
            nbt.putInt("RepairCost", repairCost);
        }
    }

    @Inject(method = "fromTag", at = @At("TAIL"))
    private void setEnchantmentsFromNbt(BlockState state, NbtCompound tag, CallbackInfo ci) {
        NbtElement enchantments = tag.get("Enchantments");
        if (enchantments != null) {
            this.enchantments = enchantments.copy();
        }

        if (tag.contains("RepairCost", 3)) {
            repairCost = tag.getInt("RepairCost");
        }
    }

    @Override
    public NbtElement getEnchantments() {
        return enchantments;
    }

    @Override
    public void setEnchantments(NbtElement enchantments) {
        this.enchantments = enchantments.copy();
    }

    @Override
    public Integer getRepairCost() {
        return repairCost;
    }

    @Override
    public void setRepairCost(Integer repairCost) {
        this.repairCost = repairCost;
    }
}