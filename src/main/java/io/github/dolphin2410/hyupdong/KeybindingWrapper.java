package io.github.dolphin2410.hyupdong;

import net.minecraft.client.option.KeyBinding;

public class KeybindingWrapper extends KeyBinding {

    public KeybindingWrapper(String translationKey, int code, String category) {
        super(translationKey, code, category);
    }

    @Override
    public void setPressed(boolean pressed) {

    }

    public void setPressedAlt(boolean pressed) {
        super.setPressed(pressed);
    }
}
