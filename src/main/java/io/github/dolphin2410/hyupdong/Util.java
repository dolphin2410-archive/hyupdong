package io.github.dolphin2410.hyupdong;

import io.github.dolphin2410.hyupdong.mixins.GameOptionsMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import org.lwjgl.glfw.GLFW;

public class Util {
    public static void replaceKeybinding(MinecraftClient client) {
        ((GameOptionsMixin) client.options).setForwardKey(new KeybindingWrapper("key.forward", GLFW.GLFW_KEY_W, KeyBinding.MOVEMENT_CATEGORY));
        ((GameOptionsMixin) client.options).setLeftKey(new KeybindingWrapper("key.left", GLFW.GLFW_KEY_A, KeyBinding.MOVEMENT_CATEGORY));
        ((GameOptionsMixin) client.options).setBackKey(new KeybindingWrapper("key.back", GLFW.GLFW_KEY_S, KeyBinding.MOVEMENT_CATEGORY));
        ((GameOptionsMixin) client.options).setRightKey(new KeybindingWrapper("key.right", GLFW.GLFW_KEY_D, KeyBinding.MOVEMENT_CATEGORY));
        ((GameOptionsMixin) client.options).setJumpKey(new KeybindingWrapper("key.jump", GLFW.GLFW_KEY_SPACE, KeyBinding.MOVEMENT_CATEGORY));
        ((GameOptionsMixin) client.options).setSneakKey(new KeybindingWrapper("key.sneak", GLFW.GLFW_KEY_LEFT_SHIFT, KeyBinding.MOVEMENT_CATEGORY));
        ((GameOptionsMixin) client.options).setSprintKey(new KeybindingWrapper("key.sprint", GLFW.GLFW_KEY_LEFT_CONTROL, KeyBinding.MOVEMENT_CATEGORY));
    }

    public static void revertKeybinding(MinecraftClient client) {
        ((GameOptionsMixin) client.options).setForwardKey(new KeyBinding("key.forward", GLFW.GLFW_KEY_W, KeyBinding.MOVEMENT_CATEGORY));
        ((GameOptionsMixin) client.options).setLeftKey(new KeyBinding("key.left", GLFW.GLFW_KEY_A, KeyBinding.MOVEMENT_CATEGORY));
        ((GameOptionsMixin) client.options).setBackKey(new KeyBinding("key.back", GLFW.GLFW_KEY_S, KeyBinding.MOVEMENT_CATEGORY));
        ((GameOptionsMixin) client.options).setRightKey(new KeyBinding("key.right", GLFW.GLFW_KEY_D, KeyBinding.MOVEMENT_CATEGORY));
        ((GameOptionsMixin) client.options).setJumpKey(new KeyBinding("key.jump", GLFW.GLFW_KEY_SPACE, KeyBinding.MOVEMENT_CATEGORY));
        ((GameOptionsMixin) client.options).setSneakKey(new StickyKeyBinding("key.sneak", GLFW.GLFW_KEY_LEFT_SHIFT, KeyBinding.MOVEMENT_CATEGORY, () -> client.options.sneakToggled));
        ((GameOptionsMixin) client.options).setSprintKey(new StickyKeyBinding("key.sprint", GLFW.GLFW_KEY_LEFT_CONTROL, KeyBinding.MOVEMENT_CATEGORY, () -> client.options.sprintToggled));
    }
}
