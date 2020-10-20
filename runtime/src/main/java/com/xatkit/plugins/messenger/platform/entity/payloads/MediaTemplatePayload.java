package com.xatkit.plugins.messenger.platform.entity.payloads;

import com.xatkit.plugins.messenger.platform.entity.MediaElement;
import lombok.Getter;

import java.util.List;

public class MediaTemplatePayload extends TemplatePayload {
    @Getter
    private final List<MediaElement> elements; // Maximum size is 1
    //This also has sharable field, but I don't know if implementation has point

    public MediaTemplatePayload(List<MediaElement> elements) {
        super("media");
        this.elements = elements;
    }


}
