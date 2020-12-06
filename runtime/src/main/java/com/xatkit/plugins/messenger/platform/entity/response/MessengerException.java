package com.xatkit.plugins.messenger.platform.entity.response;

import com.xatkit.core.XatkitException;
import lombok.Getter;

/**
 * An exception containing error information from Facebook
 */
public class MessengerException extends XatkitException {
    @Getter
    private final Integer status;
    @Getter
    private final Integer code;
    @Getter
    private final Integer subcode;
    @Getter
    private final String fbtraceId;

    public MessengerException(Integer status, Integer code, Integer subcode, String fbtraceId, String message) {
        super(message);
        this.status = status;
        this.code = code;
        this.subcode = subcode;
        this.fbtraceId = fbtraceId;
    }
}
