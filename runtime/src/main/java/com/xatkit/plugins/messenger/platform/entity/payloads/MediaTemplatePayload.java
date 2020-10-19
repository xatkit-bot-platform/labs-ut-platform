package com.xatkit.plugins.messenger.platform.entity.payloads;

import com.google.gson.annotations.SerializedName;
import com.xatkit.plugins.messenger.platform.entity.MediaElement;
import lombok.Getter;

import java.util.List;

public class MediaTemplatePayload implements GeneralPayload {
    @SerializedName(value = "template_type")
    @Getter
    private final String templateType = "media";
    @Getter
    private final List<MediaElement> elements; // Maximum size is 1
    //This also has sharable field, but I don't know if implementation has point

    public MediaTemplatePayload(List<MediaElement> elements) {
        this.elements = elements;
    }


}
