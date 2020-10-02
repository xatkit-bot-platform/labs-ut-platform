package com.xatkit.plugins.messenger.platform.action;

import lombok.Getter;

public class Message {

    @Getter
    private final String text;
    @Getter
    private final Attachment attachment;

    public Message(final String text) {
        this.text = text;
        this.attachment = null;
    }

    public Message(final Attachment attachment) {
        this.attachment = attachment;
        this.text = null;
    }

    public Message(final Attachment.AttachmentType attachmentType, final String payloadUrl, final boolean isPayloadReusable) {
        this(new Attachment(attachmentType, new Payload(payloadUrl, isPayloadReusable)));
    }
}
