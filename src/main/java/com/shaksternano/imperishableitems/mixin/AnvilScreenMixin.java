package com.shaksternano.imperishableitems.mixin;

import com.shaksternano.imperishableitems.ImperishableItems;
import com.shaksternano.imperishableitems.enchantments.ImperishableEnchantment;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ForgingScreen<AnvilScreenHandler> {

    private AnvilScreenMixin(AnvilScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
        super(handler, playerInventory, title, texture);
    }

    @Redirect(method = "onSlotUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;getString()Ljava/lang/String;"))
    private String imperishableBrokenOnSlotUpdate(Text getName, ScreenHandler handler, int slotId, ItemStack stack) {
        String returnName = getName.getString();

        if (ImperishableItems.config.imperishablePreventsBreaking) {
            returnName = ImperishableEnchantment.itemNameRemoveBroken(getName, stack);
        }

        return returnName;
    }

    @Redirect(method = "onRenamed", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;getString()Ljava/lang/String;"))
    private String imperishableBrokenOnRenamed(Text getName) {
        String returnName = getName.getString();

        if (ImperishableItems.config.imperishablePreventsBreaking) {
            Slot slot = handler.getSlot(0);

            if (slot != null && slot.hasStack()) {
                ItemStack stack = slot.getStack();
                returnName = ImperishableEnchantment.itemNameRemoveBroken(getName, stack);
            }

        }

        return returnName;
    }
}