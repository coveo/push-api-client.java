package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpResponse;

public class PushOneDocument {
    public static void main(String[] args) throws MalformedURLException {
        URL sourceUrl = new URL("https://apidev.cloud.coveo.com/push/v1/organizations/dbrooke5jqo0pu0/sources/dbrooke5jqo0pu0-qkiuev56p6e6wpfzpwqw74ci6i");
        PushSource source = new PushSource("xx766207a0-4041-4e45-a953-455b1b028a71", sourceUrl);

        DocumentBuilder documentBuilder = new DocumentBuilder("https://my.document.uri", "My document title")
                .withData("these words will be searchable");

        try {
            HttpResponse<String> response = source.addOrUpdateDocument(documentBuilder);
            System.out.println(String.format("Push document status: %s", response.statusCode()));
            System.out.println(String.format("Push document response: %s", response.body()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
