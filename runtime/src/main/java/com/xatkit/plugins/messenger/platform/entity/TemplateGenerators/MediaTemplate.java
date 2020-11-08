package com.xatkit.plugins.messenger.platform.entity.TemplateGenerators;

import com.xatkit.plugins.messenger.platform.entity.MediaElement;
import com.xatkit.plugins.messenger.platform.entity.buttons.Button;
import com.xatkit.plugins.messenger.platform.entity.buttons.PostbackButton;
import com.xatkit.plugins.messenger.platform.entity.buttons.URLButton;
import com.xatkit.plugins.messenger.platform.entity.buttons.WebviewHeightRatio;
import com.xatkit.plugins.messenger.platform.entity.payloads.MediaTemplatePayload;

import java.util.Collections;

public class MediaTemplate {
    MediaElement mediaElement;
    Button button;

    public MediaTemplate() {
    }

    public MediaTemplate constructElementUsingUrl(MediaElement.MediaType mediaType, String url) {
        if (button == null) mediaElement = new MediaElement(mediaType, url, true);
        else mediaElement = new MediaElement(mediaType, url, true, Collections.singletonList(button));
        return this;
    }

    public MediaTemplate constructElementUsingAttachmentId(MediaElement.MediaType mediaType, String attachmentId) {
        if (button == null) mediaElement = new MediaElement(mediaType, attachmentId, false);
        else mediaElement = new MediaElement(mediaType, attachmentId, false, Collections.singletonList(button));
        return this;
    }

    public MediaTemplate constructUrlButton(String title, String url, WebviewHeightRatio webviewHeightRatio) {
        Button button;
        if (webviewHeightRatio == null) button = new URLButton(url, title);
        else button = new URLButton(url, title, webviewHeightRatio);

        this.button = button;
        return this;
    }

    public MediaTemplate constructPostbackButton(int elementID, String title, String payload) {
        Button button;
        if (payload == null) button = new PostbackButton(title);
        else button = new PostbackButton(title, payload);

        this.button = button;
        return this;
    }

    public MediaTemplatePayload getPayload() {
        return new MediaTemplatePayload(Collections.singletonList(mediaElement));
    }
}
