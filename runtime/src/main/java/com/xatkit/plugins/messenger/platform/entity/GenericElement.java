package com.xatkit.plugins.messenger.platform.entity;

import com.google.gson.annotations.SerializedName;
import com.xatkit.plugins.messenger.platform.entity.buttons.Button;
import com.xatkit.plugins.messenger.platform.entity.buttons.DefaultActionButton;
import lombok.Getter;

import java.util.List;

//TODO Add javadoc once Media templates (which also use Element, but with different fields) are done.
public class GenericElement {
    @Getter
    private final String title; //Mandatory along with 1 more field | 80 character limit (the rest will get shortened with ...)
    @Getter
    private final String subtitle; //80 character limit (the rest will get shortened with ...)
    @SerializedName(value = "image_url")
    @Getter
    private final String imageURL;
    @SerializedName(value = "default_action")
    @Getter
    private final DefaultActionButton defaultActionButton;
    @Getter
    private final List<Button> buttons; //Max 3 buttons

    public GenericElement(String title, String subtitle, String imageURL, DefaultActionButton defaultActionButton, List<Button> buttons) {
        this.title = title;
        this.subtitle = subtitle;
        this.imageURL = imageURL;
        this.defaultActionButton = defaultActionButton;
        this.buttons = buttons;
    }
}
