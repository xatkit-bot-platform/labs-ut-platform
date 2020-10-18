package com.xatkit.plugins.messenger.platform.entity.payloads;

import com.google.gson.annotations.SerializedName;
import com.xatkit.plugins.messenger.platform.entity.Element;

import lombok.Getter;

import java.util.List;


public class GenericTemplatePayload implements GeneralPayload {
    @SerializedName(value = "template_type")
    @Getter
    private final String templateType = "generic";
    @SerializedName(value = "image_aspect_ratio")
    @Getter
    private final ImageAspectRatio imageAspectRatio;
    @Getter
    private final List<Element> elements; //Maximum size 10

    public GenericTemplatePayload(final List<Element> elements) {
        this.elements = elements;
        this.imageAspectRatio = ImageAspectRatio.horizontal;
    }

    public GenericTemplatePayload(final List<Element> elements, ImageAspectRatio imageAspectRatio) {
        this.elements = elements;
        this.imageAspectRatio = imageAspectRatio;
    }

    public enum ImageAspectRatio {
        horizontal,
        square
    }


}
