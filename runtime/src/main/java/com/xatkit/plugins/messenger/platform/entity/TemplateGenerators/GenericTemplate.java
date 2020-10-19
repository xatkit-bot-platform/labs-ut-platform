package com.xatkit.plugins.messenger.platform.entity.TemplateGenerators;

import com.xatkit.plugins.messenger.platform.entity.GenericElement;
import com.xatkit.plugins.messenger.platform.entity.buttons.*;
import com.xatkit.plugins.messenger.platform.entity.payloads.GenericTemplatePayload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericTemplate {

    private List<GenericElement> genericElements;
    private Map<Integer, DefaultActionButton> elementButtons;
    private Map<Integer, List<Button>> buttons;
    private GenericTemplatePayload.ImageAspectRatio imageAspectRatio;

    public GenericTemplate() {
        this.genericElements = new ArrayList<>();
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

    //Needs testing as it was added before Postback receiving was implemented
    public void addPostbackButtonToElement(int elementID, String title, String payload) {
        Button button;
        if (payload == null) button = new PostbackButton(title);
        else button =  new PostbackButton(title,payload);

        if (!buttons.containsKey(elementID)) buttons.put(elementID, new ArrayList<>());
        buttons.get(elementID).add(button);
    }

    public void constructElement(int elementID, String title, String subtitle, String imageURL) {
        DefaultActionButton elementButton = null;
        if (this.elementButtons.containsKey(elementID)) elementButton = this.elementButtons.get(elementID);
        List<Button> buttons = null;
        if (this.buttons.containsKey(elementID)) buttons = this.buttons.get(elementID);

        genericElements.add(new GenericElement(title, subtitle, imageURL, elementButton, buttons));
    }

    public void setImageAspectRatio(GenericTemplatePayload.ImageAspectRatio imageAspectRatio) {
        this.imageAspectRatio = imageAspectRatio;
    }

    public GenericTemplatePayload getPayload() {
        return new GenericTemplatePayload(genericElements, GenericTemplatePayload.ImageAspectRatio.horizontal);
    }
}
