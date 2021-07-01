package com.coveo.pushapiclient;

import java.util.ArrayList;

public class BatchUpdate {
    private final ArrayList<DocumentBuilder> addOrUpdate;
    private final ArrayList<DocumentBuilder> delete;

    public BatchUpdate(ArrayList<DocumentBuilder> addOrUpdate, ArrayList<DocumentBuilder> delete) {
        this.addOrUpdate = addOrUpdate;
        this.delete = delete;
    }
}
