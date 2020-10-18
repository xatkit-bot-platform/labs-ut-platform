package com.xatkit.plugins.messenger.platform.entity;

import com.xatkit.plugins.messenger.platform.entity.buttons.Button;
import com.xatkit.plugins.messenger.platform.entity.buttons.DefaultActionButton;
import com.xatkit.plugins.messenger.platform.entity.buttons.URLButton;
import com.xatkit.plugins.messenger.platform.entity.buttons.WebviewHeightRatio;
import com.xatkit.plugins.messenger.platform.entity.payloads.GenericTemplatePayload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericTemplate {

    private List<Element> elements;
    private Map<Integer, DefaultActionButton> elementButtons;
    private Map<Integer, List<Button>> buttons;
    private GenericTemplatePayload.ImageAspectRatio imageAspectRatio;

    public GenericTemplate() {
        this.elements = new ArrayList<>();
        this.elementButtons = new HashMap<>();
        this.buttons = new HashMap<>();
        this.imageAspectRatio = GenericTemplatePayload.ImageAspectRatio.horizontal;
    }

    public void setDefaultElementButton(int elementID, String url, WebviewHeightRatio webviewHeightRatio) {
        DefaultActionButton elementButton;
        if (webviewHeightRatio == null) elementButton = new DefaultActionButton(url);
        else elementButton = new DefaultActionButton(url, webviewHeightRatio);

        elementButtons.put(elementID, elementButton);
    }

    public void addUrlButtonToElement(int elementID, String title, String url, WebviewHeightRatio webviewHeightRatio) {
        Button button;
        if (webviewHeightRatio == null) button = new URLButton(url,title);
        else button = new URLButton(url,title,webviewHeightRatio);

        if (!buttons.containsKey(elementID)) buttons.put(elementID, new ArrayList<>());
        buttons.get(elementID).add(button);
    }

    public void constructElement(int elementID, String title, String subtitle, String imageURL) {
        DefaultActionButton elementButton = null;
        if (this.elementButtons.containsKey(elementID)) elementButton = this.elementButtons.get(elementID);
        List<Button> buttons = null;
        if (this.buttons.containsKey(elementID)) buttons = this.buttons.get(elementID);

        elements.add(new Element(title, subtitle, imageURL, elementButton, buttons));
    }

    public void setImageAspectRatio(GenericTemplatePayload.ImageAspectRatio imageAspectRatio) {
        this.imageAspectRatio = imageAspectRatio;
    }

    public GenericTemplatePayload getPayload() {
        return new GenericTemplatePayload(elements, GenericTemplatePayload.ImageAspectRatio.horizontal);
    }
}
