package com.xatkit.plugins.messenger.platform.entity;

import lombok.Getter;

public abstract class Response {
    @Getter
    private final int status;

    public Response(int status) {
        this.status = status;
    }
}
