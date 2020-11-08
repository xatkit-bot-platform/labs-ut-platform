package com.xatkit.plugins.messenger.platform.entity.response;

import lombok.Getter;

public class ErrorResponse extends Response {
    @Getter
    private final Integer code;
    @Getter
    private final Integer subcode;
    @Getter
    private final String fbtraceId;
    @Getter
    private final String message;

    public ErrorResponse(Integer status, Integer code, Integer subcode, String fbtraceId, String message) {
        super(status);
        this.code = code;
        this.subcode = subcode;
        this.fbtraceId = fbtraceId;
        this.message = message;
    }
}
