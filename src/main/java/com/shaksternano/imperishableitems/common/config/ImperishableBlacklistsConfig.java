package com.shaksternano.imperishableitems.common.config;

import com.google.common.collect.Lists;
import com.shaksternano.imperishableitems.common.ImperishableItems;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.List;

@Config(name = ImperishableItems.MOD_ID + "blacklists")
public class ImperishableBlacklistsConfig implements ConfigData {

    @Comment("An item won't be protected by Imperishable at all if that item's ID is in this list, for example \"minecraft:stone\".")
    public final List<String> globalBlacklist = Lists.newArrayList();

    @Comment("An item will still despawn even if it has Imperishable if that item's ID is in this list.")
    public final List<String> despawnProtectionBlacklist = Lists.newArrayList();

    @Comment("An item will still be destroyed by things like fire even if it has Imperishable if that item's ID is in this list.")
    public final List<String> damageProtectionBlacklist = Lists.newArrayList();

    @Comment("An item will still be destroyed by the void even if it has Imperishable if that item's ID is in this list.")
    public final List<String> voidProtectionBlacklist = Lists.newArrayList();

    @Comment("An item will still break when it reaches 0 durability even if it has Imperishable if that item's ID is in this list.")
    public final List<String> breakProtectionBlacklist = Lists.newArrayList();
}
