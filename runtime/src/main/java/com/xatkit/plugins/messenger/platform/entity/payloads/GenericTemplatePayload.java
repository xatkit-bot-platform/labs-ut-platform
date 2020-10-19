package com.xatkit.plugins.messenger.platform.entity.payloads;

import com.google.gson.annotations.SerializedName;
import com.xatkit.plugins.messenger.platform.entity.GenericElement;

import lombok.Getter;

import java.util.List;


public class GenericTemplatePayload implements GeneralPayload {
    @SerializedName(value = "template_type")
    @Getter
    private final String templateType = "generic";
    @SerializedName(value = "image_aspect_ratio")
    @Getter
    private final ImageAspectRatio imageAspectRatio;
    @SerializedName(value = "elements")
    @Getter
    private final List<GenericElement> genericElements; //Maximum size 10

    public GenericTemplatePayload(final List<GenericElement> genericElements) {
        this.genericElements = genericElements;
        this.imageAspectRatio = ImageAspectRatio.horizontal;
    }

    public GenericTemplatePayload(final List<GenericElement> genericElements, ImageAspectRatio imageAspectRatio) {
        this.genericElements = genericElements;
        this.imageAspectRatio = imageAspectRatio;
    }

    public enum ImageAspectRatio {
        horizontal,
        square
    }


}
