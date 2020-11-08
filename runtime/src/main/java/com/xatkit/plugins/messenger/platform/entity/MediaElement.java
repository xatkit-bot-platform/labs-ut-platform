package com.xatkit.plugins.messenger.platform.entity;

import com.google.gson.annotations.SerializedName;
import com.xatkit.plugins.messenger.platform.entity.buttons.Button;
import lombok.Getter;

import java.util.List;

public class MediaElement {
    @SerializedName(value = "media_type")
    @Getter
    private final MediaType mediaType;

    public enum MediaType {
        @SerializedName("image")
        image,
        @SerializedName("video")
        video
    }

    @SerializedName(value = "attachment_id")
    @Getter
    private final String attachmentId;
    @Getter
    private final String url; //Facebook URL
    @Getter
    private final List<Button> buttons; //Max size 1

    public MediaElement(MediaType mediaType, String attachmentIdORUrl, boolean isUrl) {
        this.mediaType = mediaType;
        this.buttons = null;
        if (isUrl) {
            this.attachmentId = null;
            this.url = attachmentIdORUrl;
        } else {
            this.attachmentId = attachmentIdORUrl;
            this.url = null;
        }

    }

    public MediaElement(MediaType mediaType, String attachmentIdORUrl, boolean isUrl, List<Button> buttons) {
        this.mediaType = mediaType;
        this.buttons = buttons;
        if (isUrl) {
            this.attachmentId = null;
            this.url = attachmentIdORUrl;
        } else {
            this.attachmentId = attachmentIdORUrl;
            this.url = null;
        }

    }
}
