package com.xatkit.plugins.messenger.platform.entity;

import com.google.gson.Gson;
import com.xatkit.plugins.messenger.platform.entity.payloads.FilePayload;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class File {
    private static final Gson gson = new Gson();

    @Getter
    private final Attachment attachment;
    @Getter
    private final java.io.File file;
    @Getter
    private final String type;

    public File(Attachment.AttachmentType attachmentType, java.io.File file, String fileExtension) {
        String type = attachmentType.name();
        if (!StringUtils.isEmpty(fileExtension)) type += "/" + fileExtension.toLowerCase();
        this.type = type;
        this.file = file;
        this.attachment = new Attachment(attachmentType, new FilePayload(true));
    }

    public String getContentType() {
        return type;
    }
}
