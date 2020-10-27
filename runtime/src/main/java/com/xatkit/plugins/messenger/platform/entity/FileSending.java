package com.xatkit.plugins.messenger.platform.entity;
import lombok.Getter;

public class FileSending {
    @Getter
    private final Attachment attachment;

    public FileSending(Attachment attachment) {
        this.attachment = attachment;
    }
}
