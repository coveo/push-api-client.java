package com.coveo.pushapiclient;

/**
 * Available Platform regions to connect to
 */
public enum Region {
    US("us"),
    EU("eu"),
    AU("au");

    private String value;

    Region(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}