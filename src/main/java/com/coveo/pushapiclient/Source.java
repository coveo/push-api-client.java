package com.coveo.pushapiclient;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.http.HttpResponse;

public class Source {
    PlatformClient platformClient;

    /**
     * @param apiKey         An apiKey capable of pushing documents and managing sources in a Coveo organization. See [Manage API Keys](https://docs.coveo.com/en/1718).
     * @param organizationId The Coveo Organization identifier.
     */
    public Source(String apiKey, String organizationId) {
        this.platformClient = new PlatformClient(apiKey, organizationId);
    }

    /**
     * Create a new push source.
     *
     * @param name             The name of the source to create
     * @param sourceVisibility The security option that should be applied to the content of the source. See [Content Security](https://docs.coveo.com/en/1779).
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> create(String name, SourceVisibility sourceVisibility) throws IOException, InterruptedException {
        return this.platformClient.createSource(name, sourceVisibility);
    }

    /**
     * Create or update a security identity. See [Adding a Single Security Identity](https://docs.coveo.com/en/167) and [Security Identity Models](https://docs.coveo.com/en/139).
     *
     * @param securityProviderId
     * @param securityIdentityModel
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> createOrUpdateSecurityIdentity(String securityProviderId, SecurityIdentityModel securityIdentityModel) throws IOException, InterruptedException {
        return this.platformClient.createOrUpdateSecurityIdentity(securityProviderId, securityIdentityModel);
    }

    /**
     * Create or update a security identity alias. See [Adding a Single Alias](https://docs.coveo.com/en/142) and [User Alias Definition Examples](https://docs.coveo.com/en/46).
     *
     * @param securityProviderId
     * @param securityIdentityAliasModel
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> createOrUpdateSecurityIdentityAlias(String securityProviderId, SecurityIdentityAliasModel securityIdentityAliasModel) throws IOException, InterruptedException {
        return this.platformClient.createOrUpdateSecurityIdentityAlias(securityProviderId, securityIdentityAliasModel);
    }

    /**
     * Delete a security identity. See [Disabling a Single Security Identity](https://docs.coveo.com/en/84).
     *
     * @param securityProviderId
     * @param securityIdentityDelete
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> deleteSecurityIdentity(String securityProviderId, SecurityIdentityDelete securityIdentityDelete) throws IOException, InterruptedException {
        return this.platformClient.deleteSecurityIdentity(securityProviderId, securityIdentityDelete);
    }

    /**
     * Delete old security identities. See [Disabling Old Security Identities](https://docs.coveo.com/en/33).
     *
     * @param securityProviderId
     * @param batchDelete
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> deleteOldSecurityIdentities(String securityProviderId, SecurityIdentityDeleteOptions batchDelete) throws IOException, InterruptedException {
        return this.platformClient.deleteOldSecurityIdentities(securityProviderId, batchDelete);
    }

    /**
     * Manage batches of security identities. See [Manage Batches of Security Identities](https://docs.coveo.com/en/55).
     *
     * @param securityProviderId
     * @param batchConfig
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> manageSecurityIdentities(String securityProviderId, SecurityIdentityBatchConfig batchConfig) throws IOException, InterruptedException {
        return this.platformClient.manageSecurityIdentities(securityProviderId, batchConfig);
    }

    /**
     * Adds or updates an individual item in a push source. See [Adding a Single Item in a Push Source](https://docs.coveo.com/en/133).
     *
     * @param sourceId
     * @param docBuilder
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> addOrUpdateDocument(String sourceId, DocumentBuilder docBuilder) throws IOException, InterruptedException {
        CompressionType compressionType = docBuilder.getDocument().compressedBinaryData != null ? docBuilder.getDocument().compressedBinaryData.getCompressionType() : CompressionType.UNCOMPRESSED;
        return this.platformClient.pushDocument(sourceId, docBuilder.marshal(), docBuilder.getDocument().uri, compressionType);
    }

    /**
     * Deletes a specific item from a Push source. Optionally, the child items of that item can also be deleted. See [Deleting an Item in a Push Source](https://docs.coveo.com/en/171).
     *
     * @param sourceId
     * @param documentId
     * @param deleteChildren
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> deleteDocument(String sourceId, String documentId, Boolean deleteChildren) throws IOException, InterruptedException {
        return this.platformClient.deleteDocument(sourceId, documentId, deleteChildren);
    }

    /**
     * Manage batches of items in a push source. See [Manage Batches of Items in a Push Source](https://docs.coveo.com/en/90)
     *
     * @param sourceId
     * @param batchUpdate
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> batchUpdateDocuments(String sourceId, BatchUpdate batchUpdate) throws IOException, InterruptedException {
        HttpResponse<String> resFileContainer = this.platformClient.createFileContainer();
        FileContainer fileContainer = new Gson().fromJson(resFileContainer.body(), FileContainer.class);
        this.platformClient.uploadContentToFileContainer(sourceId, fileContainer, batchUpdate.marshal());
        return this.platformClient.pushFileContainerContent(sourceId, fileContainer);
    }
}
