package com.coveo.testlocally;

import com.coveo.pushapiclient.DocumentBuilder;
import com.coveo.pushapiclient.Source;
import com.coveo.pushapiclient.SourceVisibility;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Date;

public class TestingLocally {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        DocumentBuilder doc = new DocumentBuilder("https://perdu.com", "the title").withData("this is searchable").withDate(new Date());
        Source source = new Source(dotenv.get("API_KEY"), dotenv.get("ORG_ID"));
        try {
            HttpResponse res = source.create("testlocaljava", SourceVisibility.SECURED);
            System.out.println(String.format("Status: %s %s", res.statusCode(), res.body().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
