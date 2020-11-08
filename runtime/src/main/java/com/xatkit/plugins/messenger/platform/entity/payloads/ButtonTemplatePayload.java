package com.xatkit.plugins.messenger.platform.entity.payloads;

import com.xatkit.plugins.messenger.platform.entity.buttons.Button;
import lombok.Getter;

import java.util.List;

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
