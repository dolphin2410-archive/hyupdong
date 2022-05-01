package io.github.dolphin2410.hyupdong.mixins;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameOptions.class)
public interface GameOptionsMixin {
    @Accessor("forwardKey")
    @Mutable
    void setForwardKey(KeyBinding forwardKey);

    @Accessor("leftKey")
    @Mutable
    void setLeftKey(KeyBinding leftKey);

    @Accessor("backKey")
    @Mutable
    void setBackKey(KeyBinding backKey);

    @Accessor("rightKey")
    @Mutable
    void setRightKey(KeyBinding rightKey);

    @Accessor("jumpKey")
    @Mutable
    void setJumpKey(KeyBinding jumpKey);

    @Accessor("sneakKey")
    @Mutable
    void setSneakKey(KeyBinding sneakKey);

    @Accessor("sprintKey")
    @Mutable
    void setSprintKey(KeyBinding sprintKey);
}
