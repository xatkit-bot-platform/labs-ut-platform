package com.xatkit.plugins.messenger.platform.entity.buttons;

import lombok.Getter;

/**
 * Abstract class for buttons.
 */
public abstract class Button {
    @Getter
    private final String type;

    public Button(String type) {
        this.type = type;
    }
}
