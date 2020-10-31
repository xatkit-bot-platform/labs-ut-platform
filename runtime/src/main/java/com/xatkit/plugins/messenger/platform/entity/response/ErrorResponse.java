package com.xatkit.plugins.messenger.platform.entity.response;

import lombok.Getter;

public class ErrorResponse extends Response {
    @Getter
    private final int code;
    @Getter
    private final int subcode;
    @Getter
    private final String fbtraceId;
    @Getter
    private final String message;

    public ErrorResponse(int status, int code, int subcode, String fbtraceId, String message) {
        super(status);
        this.code = code;
        this.subcode = subcode;
        this.fbtraceId = fbtraceId;
        this.message = message;
    }
}
