package com.coveo.pushapiclient;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PlatformClientTest {
    private PlatformClient client;
    private HttpClient httpClient;
    private ArgumentCaptor<HttpRequest> argument;

    public void assertAuthorizationHeader() {
        assertTrue(this.argument.getValue().headers().map().get("Authorization").contains("Bearer the_api_key"));
    }

    public void assertApplicationJsonHeader() {
        assertTrue(this.argument.getValue().headers().map().get("Content-Type").contains("application/json"));
        assertTrue(this.argument.getValue().headers().map().get("Accept").contains("application/json"));
    }

    public SecurityIdentityModel securityIdentityModel() {
        return new SecurityIdentityModel(identityModels(), identityModel(), identityModels());
    }

    public SecurityIdentityAliasModel securityIdentityAliasModel() {
        return new SecurityIdentityAliasModel(aliasMappings(), identityModel(), identityModels());
    }

    public SecurityIdentityDelete securityIdentityDelete() {
        return new SecurityIdentityDelete(identityModel());
    }

    public SecurityIdentityBatchConfig securityIdentityBatchConfig() {
        return new SecurityIdentityBatchConfig("the_file_id", 1234L);
    }

    public DocumentBuilder documentBuilder() {
        return new DocumentBuilder("the_uri", "the_title");
    }

    public DeleteDocument deleteDocument() {
        return new DeleteDocument("12345");
    }

    public Document document() {
        return documentBuilder().getDocument();
    }

    public String documentString() {
        return documentBuilder().marshal();
    }

    public FileContainer fileContainer() {
        FileContainer fileContainer = new FileContainer();
        fileContainer.fileId = "the_file_id";
        fileContainer.requiredHeaders = new HashMap<>() {{
            put("foo", "bar");
        }};
        fileContainer.uploadUri = "https://upload.uri";
        return fileContainer;
    }

    public BatchUpdateRecord batchUpdateRecord() {
        BatchUpdate batchUpdate = new BatchUpdate(new ArrayList<>() {{
            add(documentBuilder());
        }}, new ArrayList<>() {{
            add(deleteDocument());
        }});
        return batchUpdate.marshal();
    }

    public IdentityModel identityModel() {
        return new IdentityModel("the_name_identity_model", SecurityIdentityType.USER, new HashMap<>() {{
            put("foo", "bar");
        }});
    }

    public IdentityModel[] identityModels() {
        return new IdentityModel[]{identityModel()};
    }

    public AliasMapping[] aliasMappings() {
        return new AliasMapping[]{new AliasMapping("the_provider_alias", "the_name_alias", SecurityIdentityType.USER, new HashMap<>() {{
            put("foo", "bar");
        }})};
    }

    @Before
    public void setupClient() {
        this.httpClient = mock(HttpClient.class);
        this.client = new PlatformClient("the_api_key", "the_org_id", this.httpClient);
        this.argument = ArgumentCaptor.forClass(HttpRequest.class);
    }

    @Test
    public void testCreateSource() throws IOException, InterruptedException {
        client.createSource("the_name", SourceVisibility.SECURED);
        verify(httpClient).send(argument.capture(), any(HttpResponse.BodyHandlers.ofString().getClass()));

        assertEquals("POST", argument.getValue().method());
        assertTrue(argument.getValue().uri().getPath().contains("the_org_id/sources"));
        assertAuthorizationHeader();
        assertApplicationJsonHeader();

        Map requestBody = StringSubscriber.toMap(argument.getValue().bodyPublisher());
        assertEquals("the_name", requestBody.get("name"));
        assertEquals(SourceVisibility.SECURED.toString(), requestBody.get("sourceVisibility"));
        assertEquals("PUSH", requestBody.get("sourceType"));
        assertEquals(true, requestBody.get("pushEnabled"));
    }

    @Test
    public void testCreateOrUpdateSecurityIdentity() throws IOException, InterruptedException {
        client.createOrUpdateSecurityIdentity("my_provider", securityIdentityModel());
        verify(httpClient).send(argument.capture(), any(HttpResponse.BodyHandlers.ofString().getClass()));

        assertEquals("PUT", argument.getValue().method());
        assertTrue(argument.getValue().uri().getPath().contains("the_org_id/providers/my_provider/permissions"));
        assertAuthorizationHeader();
        assertApplicationJsonHeader();

        Map requestBody = StringSubscriber.toMap(argument.getValue().bodyPublisher());

        ArrayList<Map> members = (ArrayList<Map>) requestBody.get("members");
        ArrayList<Map> wellKnowns = (ArrayList<Map>) requestBody.get("wellKnowns");
        Map identity = (Map) requestBody.get("identity");

        assertEquals(identityModel().name, members.get(0).get("name"));
        assertEquals(identityModel().additionalInfo.get("foo"), ((Map) members.get(0).get("additionalInfo")).get("foo"));
        assertEquals(identityModel().name, wellKnowns.get(0).get("name"));
        assertEquals(identityModel().additionalInfo.get("foo"), ((Map) wellKnowns.get(0).get("additionalInfo")).get("foo"));
        assertEquals(identityModel().name, identity.get("name"));
        assertEquals(identityModel().additionalInfo.get("foo"), ((Map) identity.get("additionalInfo")).get("foo"));
    }

    @Test
    public void testCreateOrUpdateSecurityIdentityAlias() throws IOException, InterruptedException {
        client.createOrUpdateSecurityIdentityAlias("my_provider", securityIdentityAliasModel());
        verify(httpClient).send(argument.capture(), any(HttpResponse.BodyHandlers.ofString().getClass()));

        assertEquals("PUT", argument.getValue().method());
        assertTrue(argument.getValue().uri().getPath().contains("the_org_id/providers/my_provider/mappings"));
        assertAuthorizationHeader();
        assertApplicationJsonHeader();

        Map requestBody = StringSubscriber.toMap(argument.getValue().bodyPublisher());

        ArrayList<Map> mappings = (ArrayList<Map>) requestBody.get("mappings");
        ArrayList<Map> wellKnowns = (ArrayList<Map>) requestBody.get("wellKnowns");
        Map identity = (Map) requestBody.get("identity");

        assertEquals(aliasMappings()[0].name, mappings.get(0).get("name"));
        assertEquals(aliasMappings()[0].additionalInfo.get("foo"), ((Map) mappings.get(0).get("additionalInfo")).get("foo"));
        assertEquals(identityModel().name, wellKnowns.get(0).get("name"));
        assertEquals(identityModel().additionalInfo.get("foo"), ((Map) wellKnowns.get(0).get("additionalInfo")).get("foo"));
        assertEquals(identityModel().name, identity.get("name"));
        assertEquals(identityModel().additionalInfo.get("foo"), ((Map) identity.get("additionalInfo")).get("foo"));
    }

    @Test
    public void testDeleteSecurityIdentity() throws IOException, InterruptedException {
        client.deleteSecurityIdentity("my_provider", securityIdentityDelete());
        verify(httpClient).send(argument.capture(), any(HttpResponse.BodyHandlers.ofString().getClass()));

        assertEquals("DELETE", argument.getValue().method());
        assertTrue(argument.getValue().uri().getPath().contains("the_org_id/providers/my_provider/permissions"));
        assertAuthorizationHeader();
        assertApplicationJsonHeader();

        Map requestBody = StringSubscriber.toMap(argument.getValue().bodyPublisher());
        Map identity = (Map) requestBody.get("identity");

        assertEquals(identityModel().name, identity.get("name"));
    }

    @Test
    public void testManageSecurityIdentities() throws IOException, InterruptedException {
        client.manageSecurityIdentities("my_provider", securityIdentityBatchConfig());
        verify(httpClient).send(argument.capture(), any(HttpResponse.BodyHandlers.ofString().getClass()));

        assertEquals("PUT", argument.getValue().method());
        assertTrue(argument.getValue().uri().getPath().contains("the_org_id/providers/my_provider/permissions/batch"));
        assertTrue(argument.getValue().uri().getQuery().contains(String.format("fileId=%s", securityIdentityBatchConfig().getFileId())));
        assertTrue(argument.getValue().uri().getQuery().contains(String.format("orderingId=%s", securityIdentityBatchConfig().getOrderingId())));
        assertAuthorizationHeader();
        assertApplicationJsonHeader();
    }

    @Test
    public void testAppendOrderingId() throws IOException, InterruptedException {
        String standardOrderingParam = client.appendOrderingId(1234L);
        String noOrderingParam = client.appendOrderingId(0L);

        assertTrue(standardOrderingParam.contains(String.format("orderingId=%s", "1234")));
        assertEquals("", noOrderingParam);
    }

    @Test
    public void testPushDocument() throws IOException, InterruptedException {
        client.pushDocument("my_source", documentString(), document().uri, CompressionType.UNCOMPRESSED);
        verify(httpClient).send(argument.capture(), any(HttpResponse.BodyHandlers.ofString().getClass()));

        assertEquals("PUT", argument.getValue().method());
        assertTrue(argument.getValue().uri().getPath().contains("the_org_id/sources/my_source/documents"));
        assertTrue(argument.getValue().uri().getQuery().contains(String.format("documentId=%s", document().uri)));
        assertTrue(argument.getValue().uri().getQuery().contains(String.format("compressionType=%s", CompressionType.UNCOMPRESSED.toString())));
        assertAuthorizationHeader();
        assertApplicationJsonHeader();

        Map requestBody = StringSubscriber.toMap(argument.getValue().bodyPublisher());
        assertEquals(document().title, requestBody.get("title"));
    }

    @Test
    public void testCreateFileContainer() throws IOException, InterruptedException {
        client.createFileContainer();
        verify(httpClient).send(argument.capture(), any(HttpResponse.BodyHandlers.ofString().getClass()));

        assertEquals("POST", argument.getValue().method());
        assertTrue(argument.getValue().uri().getPath().contains("the_org_id/files"));
        assertAuthorizationHeader();
        assertApplicationJsonHeader();
    }

    @Test
    public void testUploadContentToFileContainer() throws IOException, InterruptedException {
        client.uploadContentToFileContainer(fileContainer(), new Gson().toJson(batchUpdateRecord()));
        verify(httpClient).send(argument.capture(), any(HttpResponse.BodyHandlers.ofString().getClass()));

        assertEquals("PUT", argument.getValue().method());
        assertTrue(argument.getValue().uri().toString().equals(fileContainer().uploadUri));
        assertEquals(argument.getValue().headers().map().get("foo").get(0), fileContainer().requiredHeaders.get("foo"));

        Map requestBody = StringSubscriber.toMap(argument.getValue().bodyPublisher());
        ArrayList<Map> addOrUpdate = (ArrayList<Map>) requestBody.get("addOrUpdate");
        ArrayList<Map> delete = (ArrayList<Map>) requestBody.get("delete");

        assertEquals(document().uri, addOrUpdate.get(0).get("documentId"));
        assertEquals(deleteDocument().documentId, delete.get(0).get("documentId"));
    }

    @Test
    public void testPushFileContainerContent() throws IOException, InterruptedException {
        client.pushFileContainerContent("my_source", fileContainer());
        verify(httpClient).send(argument.capture(), any(HttpResponse.BodyHandlers.ofString().getClass()));

        assertEquals("PUT", argument.getValue().method());
        assertTrue(argument.getValue().uri().getPath().contains("the_org_id/sources/my_source/documents/batch"));
        assertTrue(argument.getValue().uri().getQuery().contains(String.format("fileId=%s", fileContainer().fileId)));
        assertApplicationJsonHeader();
        assertAuthorizationHeader();
    }

    @Test
    public void testDeleteDocument() throws IOException, InterruptedException {
        client.deleteDocument("my_source", document().uri, true);
        verify(httpClient).send(argument.capture(), any(HttpResponse.BodyHandlers.ofString().getClass()));

        assertEquals("DELETE", argument.getValue().method());
        assertTrue(argument.getValue().uri().getPath().contains("the_org_id/sources/my_source/documents"));
        assertTrue(argument.getValue().uri().getQuery().contains("deleteChildren=true"));
        assertTrue(argument.getValue().uri().getQuery().contains(String.format("documentId=%s", document().uri)));
        assertApplicationJsonHeader();
        assertAuthorizationHeader();
    }
}