package com.coveo.testlocally;

import com.coveo.pushapiclient.*;
import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.HashMap;

public class TestingLocally {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();


        Source source = new Source(dotenv.get("API_KEY"), dotenv.get("ORG_ID"));
        try {
            HttpResponse res = source.create("testlocaljava", SourceVisibility.SECURED);
            System.out.println(String.format("Status: %s %s", res.statusCode(), res.body().toString()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        testManageIdentities(source);
        testPushDocument(dotenv.get("SOURCE_ID"), source);
    }

    public static void testPushDocument(String sourceId, Source source) {
        DocumentBuilder doc = new DocumentBuilder("https://perdu.com", "the title").withData("this is searchable").withDate(new Date());
        DocumentBuilder docWithMetadata = new DocumentBuilder("https://perdu.com/3", "the title 3").withMetadata(new HashMap<>() {{
            put("foo", "bar");
            put("my_field_1", "1");
            put("my_field_2", false);
            put("my_field_3", 1234);
            put("my_field_4", new String[]{"a", "b", "c"});
        }});
        System.out.println(doc.marshal());
        System.out.println(docWithMetadata.marshal());
        try {
            HttpResponse<String> resAddSimpleDoc = source.addOrUpdateDocument(sourceId, doc);
            HttpResponse<String> resAddOrUpdateMetadata = source.addOrUpdateDocument(sourceId, docWithMetadata);
            HttpResponse<String> resDelete = source.deleteDocument(sourceId, doc.getDocument().uri, true);

            System.out.println(resAddSimpleDoc.statusCode());
            System.out.println(resAddOrUpdateMetadata.statusCode());
            System.out.println(resDelete.statusCode());
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }
    }

    public static void testManageIdentities(Source source) {
        IdentityModel identityModel = new IdentityModel("the_name", SecurityIdentityType.USER, new HashMap() {
        });

        IdentityModel[] identityModels = {
                identityModel,
        };

        AliasMapping[] aliasMapping = {new AliasMapping("the_provider_name", "the_name", SecurityIdentityType.USER, new HashMap<>())};
        try {
            SecurityIdentityModel securityIdentityModel = new SecurityIdentityModel(identityModels, identityModel, identityModels);
            String jsonSecurityIdentityModel = new Gson().toJson(securityIdentityModel);
            System.out.println(jsonSecurityIdentityModel);
            HttpResponse<String> resCreateOrUpdateSecurityIdentity = source.createOrUpdateSecurityIdentity("the_provider_id", securityIdentityModel);
            System.out.println(resCreateOrUpdateSecurityIdentity.body());


            SecurityIdentityAliasModel securityIdentityAliasModel = new SecurityIdentityAliasModel(aliasMapping, identityModel, identityModels);
            String jsonSecurityIdentityAliasModel = new Gson().toJson(securityIdentityAliasModel);
            System.out.println(jsonSecurityIdentityAliasModel);
            HttpResponse<String> resCreateOrUpdateSecurityIdentityAlias = source.createOrUpdateSecurityIdentityAlias("the_provider_id_mappings", securityIdentityAliasModel);
            System.out.println(resCreateOrUpdateSecurityIdentityAlias.body());


            SecurityIdentityDelete securityIdentityDelete = new SecurityIdentityDelete(identityModel);
            String jsonSecurityIdentityDelete = new Gson().toJson(securityIdentityDelete);
            System.out.println(jsonSecurityIdentityDelete);
            HttpResponse<String> resDeleteSecurityIdentity = source.deleteSecurityIdentity("the_provider_id_delete", securityIdentityDelete);
            System.out.println(resDeleteSecurityIdentity.body());

            SecurityIdentityDeleteOptions batchDelete = new SecurityIdentityDeleteOptions(123, 987l);
            HttpResponse<String> resDeleteOlderThan = source.deleteOldSecurityIdentities("the_provider_id_delete_older_than", batchDelete);
            System.out.println(resDeleteOlderThan.body());

            SecurityIdentityBatchConfig batchConfig = new SecurityIdentityBatchConfig("the_file_id", 123l);
            HttpResponse<String> resBatchConfig = source.manageSecurityIdentities("the_provider_id_batch_config", batchConfig);
            System.out.println(resBatchConfig.body());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
