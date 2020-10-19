package com.xatkit.plugins.messenger.platform.entity.TemplateGenerators;

import com.xatkit.plugins.messenger.platform.entity.buttons.Button;
import com.xatkit.plugins.messenger.platform.entity.buttons.PostbackButton;
import com.xatkit.plugins.messenger.platform.entity.buttons.URLButton;
import com.xatkit.plugins.messenger.platform.entity.buttons.WebviewHeightRatio;
import com.xatkit.plugins.messenger.platform.entity.payloads.ButtonTemplatePayload;
import com.xatkit.plugins.messenger.platform.entity.payloads.GenericTemplatePayload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ButtonTemplate {

    private List<Button> buttons;
    private String text;

    public ButtonTemplate() {
        this.buttons = new ArrayList<>();
    }

    public void addUrlButton(String title, String url, WebviewHeightRatio webviewHeightRatio) {
        Button button;
        if (webviewHeightRatio == null) button = new URLButton(url, title);
        else button = new URLButton(url, title, webviewHeightRatio);
        buttons.add(button);
    }

    public void addPostbackButton(String title, String payload) {
        Button button;
        if (payload == null) button = new PostbackButton(title);
        else button = new PostbackButton(title, payload);
        buttons.add(button);
    }

    public void setText(String text) {
        this.text = text;
    }


    public ButtonTemplatePayload getPayload() {
        return new ButtonTemplatePayload(text, buttons);
    }
}
