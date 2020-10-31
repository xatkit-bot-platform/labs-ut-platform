package com.xatkit.plugins.messenger.platform.entity.response;

import lombok.Getter;

public abstract class Response {
    @Getter
    private final int status;

    public Response(int status) {
        this.status = status;
    }
}
