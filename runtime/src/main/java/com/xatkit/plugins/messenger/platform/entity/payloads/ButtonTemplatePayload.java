package com.xatkit.plugins.messenger.platform.entity.payloads;

import com.xatkit.plugins.messenger.platform.entity.buttons.Button;
import lombok.Getter;

import java.util.List;

/**
 * Used for creating templates with buttons.
 * Only Android and iOS support by Facebook for European users since 16th of December 2020.
 */
public class ButtonTemplatePayload extends TemplatePayload {
    @Getter
    private final String text; //UTF-8 encoded 640 character limit
    @Getter
    private final List<Button> buttons; //Maximum size 3

    public ButtonTemplatePayload(String text, List<Button> buttons) {
        super("button");
        this.text = text;
        this.buttons = buttons;
    }
}
