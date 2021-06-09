package com.coveo.testlocally;

import com.coveo.pushapiclient.DocumentBuilder;

import java.util.Date;

public class TestingLocally {
    public static void main(String[] args) {
        DocumentBuilder doc = new DocumentBuilder("https://perdu.com", "the title").withData("this is searchable").withDate(new Date());
        System.out.println(doc.marshal());
    }
}
