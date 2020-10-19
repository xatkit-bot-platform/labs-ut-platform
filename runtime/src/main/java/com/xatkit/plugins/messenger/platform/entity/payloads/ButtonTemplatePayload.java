package com.xatkit.plugins.messenger.platform.entity.payloads;

import com.google.gson.annotations.SerializedName;
import com.xatkit.plugins.messenger.platform.entity.buttons.Button;
import lombok.Getter;

import java.util.List;

public class ButtonTemplatePayload implements GeneralPayload {
    @SerializedName(value = "template_type")
    @Getter
    private final String templateType = "button";
    @Getter
    private final String text; //UTF-8 encoded 640 character limit
    @Getter
    private final List<Button> buttons; //Maximum size 3

    public ButtonTemplatePayload(String text, List<Button> buttons) {
        this.text = text;
        this.buttons = buttons;
    }
}
