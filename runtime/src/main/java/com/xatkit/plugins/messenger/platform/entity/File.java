package com.xatkit.plugins.messenger.platform.entity;

import com.xatkit.plugins.messenger.platform.entity.payloads.FilePayload;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;

public class File {
    @Getter
    private final Attachment attachment;
    @Getter
    private final java.io.File file;
    @Getter
    private final String mimeType;
    @Getter
    @Setter
    private String attachmentId;

    public File(@NonNull Attachment.AttachmentType attachmentType, @NonNull java.io.File file) throws IOException {
        this(attachmentType, file, Files.probeContentType(file.toPath()));
    }

    public File(@NonNull Attachment.AttachmentType attachmentType, @NonNull java.io.File file, @NonNull String mimeType) {
        this.mimeType = mimeType;
        this.file = file;
        this.attachment = new Attachment(attachmentType, new FilePayload(true));
    }
}
