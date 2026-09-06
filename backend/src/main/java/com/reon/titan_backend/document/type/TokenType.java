package com.reon.titan_backend.document.type;

public enum TokenType {
    ACCESS("access"),
    REFRESH("refresh");

    private final String tokenName;

    TokenType(String access) {
        this.tokenName = access;
    }

    public String getTokenName() {
        return tokenName;
    }
}
