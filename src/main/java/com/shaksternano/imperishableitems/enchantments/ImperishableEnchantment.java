package com.shaksternano.imperishableitems.enchantments;

import com.shaksternano.imperishableitems.ImperishableItems;
import com.shaksternano.imperishableitems.registry.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ImperishableEnchantment extends Enchantment {

    public ImperishableEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.VANISHABLE, EquipmentSlot.values());
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMinPower(int level) {
        return Math.max(ImperishableItems.config.imperishableMinLevel, 0);
    }

    @Override
    public int getMaxPower(int level) {
        return getMinPower(level) + Math.max(ImperishableItems.config.imperishableMaxLevelsAboveMin, 0);
    }

    @Override
    public boolean isTreasure() {
        return ImperishableItems.config.imperishableIsTreasure;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return ImperishableItems.config.imperishableSoldByVillagers;
    }

    public static String itemNameRemoveBroken(Text textName, ItemStack stack) {
        String returnName = textName.getString();

        if (stack.isDamageable()) {
            if (EnchantmentHelper.getLevel(ModEnchantments.IMPERISHABLE, stack) > 0) {
                if (stack.getDamage() >= stack.getMaxDamage()) {
                    TranslatableText broken = new TranslatableText("item.name." + ImperishableItems.MOD_ID + ".imperishableBroken");
                    int brokenLength = broken.getString().length();
                    returnName = returnName.substring(0, returnName.length() - brokenLength);
                }
            }
        }

        return returnName;
    }
}