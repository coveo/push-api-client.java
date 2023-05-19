package com.coveo.pushapiclient;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpResponse;

// TODO: LENS-851 - Make public when ready
class PushSource implements PushEnabledSource {
    private final PlatformClient platformClient;
    private final String sourceId;

    @Override
    public PlatformClient getPlatformClient() {
        return this.platformClient;
    }

    @Override
    public String getId() {
        return this.sourceId;
    }

    /**
     * Create a Push source instance from its
     * <a href="https://docs.coveo.com/en/1546#push-api-url">Push API URL</a>
     *
     * @param apiKey    The API key used for all operations regarding your source.
     *                  <p>
     *                  Ensure your API key has the required privileges for the
     *                  operation you will be performing
     *                  *
     *                  <p>
     *                  For more information about which privileges are required,
     *                  see
     *                  <a href=
     *                  "https://docs.coveo.com/en/1707#sources-domain">Privilege
     *                  Reference.</a>
     *
     * @param sourceUrl The URL available when you edit your source in the <a href=
     *                  "https://docs.coveo.com/en/183/glossary/coveo-administration-console">Coveo
     *                  Administration Console</a>. The URL should contain your
     *                  <code>ORGANIZATION_ID</code> and <code>SOURCE_ID</code>,
     *                  which are required parameters for all operations regarding
     *                  your source.
     *                  <p>
     *                  Some examples of valid source URLs:
     *
     *                  <pre>
     * https://api.cloud.coveo.com/push/v1/organizations/my-org-if/sources/my-source-id/documents
     * https://api-eu.cloud.coveo.com/push/v1/organizations/my-org-if/sources/my-source-id/documents
     *                  </pre>
     *
     * @throws MalformedURLException
     */
    public PushSource(String apiKey, URL sourceUrl) throws MalformedURLException {
        ApiUrl parser = new ApiUrl(sourceUrl);
        PlatformUrl platformUrl = parser.getPlatformUrl();
        String organizationId = parser.getOrganizationId();
        this.sourceId = parser.getSourceId();
        this.platformClient = new PlatformClient(apiKey, organizationId,
                platformUrl);
    }

    /**
     * Create a Push source instance from its
     * <a href="https://docs.coveo.com/en/3295#stream-api-url">Stream API URL</a>
     *
     * @param apiKey         The API key used for all operations regarding your
     *                       source.
     *                       <p>
     *                       Ensure your API key has the required privileges for the
     *                       operation you will be performing
     *                       *
     *                       <p>
     *                       For more information about which privileges are
     *                       required,
     *                       see
     *                       <a href=
     *                       "https://docs.coveo.com/en/1707#sources-domain">Privilege
     *                       Reference.</a>
     *
     * @param organizationId The unique identifier of your organization.
     *                       <p>
     *                       The Organization Id can be retrieved in the URL of your
     *                       Coveo organization.
     *
     * @param sourceId       The unique identifier of the target Push source.
     *                       <p>
     *                       The Source Id can be retrieved when you edit your
     *                       source in the <a href=
     *                       "https://docs.coveo.com/en/183/glossary/coveo-administration-console">Coveo
     *                       Administration Console</a>
     *
     */
    public PushSource(String apiKey, String organizationId, String sourceId) {
        PlatformUrl platformUrl = new PlatformUrl(PlatformUrl.DEFAULT_ENVIRONMENT, PlatformUrl.DEFAULT_REGION);
        this.sourceId = sourceId;
        this.platformClient = new PlatformClient(apiKey, organizationId,
                platformUrl);
    }

    /**
     * Create a Push source instance from its
     * <a href="https://docs.coveo.com/en/3295#stream-api-url">Stream API URL</a>
     *
     * @param apiKey         The API key used for all operations regarding your
     *                       source.
     *                       <p>
     *                       Ensure your API key has the required privileges for the
     *                       operation you will be performing
     *                       *
     *                       <p>
     *                       For more information about which privileges are
     *                       required,
     *                       see
     *                       <a href=
     *                       "https://docs.coveo.com/en/1707#sources-domain">Privilege
     *                       Reference.</a>
     *
     * @param organizationId The unique identifier of your organization.
     *                       <p>
     *                       The Organization Id can be retrieved in the URL of your
     *                       Coveo organization.
     *
     * @param sourceId       The unique identifier of the target Push source.
     *                       <p>
     *                       The Source Id can be retrieved when you edit your
     *                       source in the <a href=
     *                       "https://docs.coveo.com/en/183/glossary/coveo-administration-console">Coveo
     *                       Administration Console</a>
     *
     * @param platformUrl    The object containing additional information on the
     *                       URL endpoint.
     *                       You can use the {@link PlatformUrl} when your
     *                       organization is located in a non-default Coveo
     *                       environement and/or region.
     *
     */
    public PushSource(String apiKey, String organizationId, String sourceId, PlatformUrl platformUrl) {
        this.sourceId = sourceId;
        this.platformClient = new PlatformClient(apiKey, organizationId,
                platformUrl);
    }

    /**
     * Create or update a security identity. See [Adding a Single Security
     * Identity](https://docs.coveo.com/en/167) and [Security Identity
     * Models](https://docs.coveo.com/en/139).
     *
     * @param securityProviderId
     * @param securityIdentityModel
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> createOrUpdateSecurityIdentity(String securityProviderId,
            SecurityIdentityModel securityIdentityModel) throws IOException, InterruptedException {
        return this.platformClient.createOrUpdateSecurityIdentity(securityProviderId, securityIdentityModel);
    }

    /**
     * Create or update a security identity alias. See [Adding a Single
     * Alias](https://docs.coveo.com/en/142) and [User Alias Definition
     * Examples](https://docs.coveo.com/en/46).
     *
     * @param securityProviderId
     * @param securityIdentityAliasModel
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> createOrUpdateSecurityIdentityAlias(String securityProviderId,
            SecurityIdentityAliasModel securityIdentityAliasModel) throws IOException, InterruptedException {
        return this.platformClient.createOrUpdateSecurityIdentityAlias(securityProviderId, securityIdentityAliasModel);
    }

    /**
     * Delete a security identity. See [Disabling a Single Security
     * Identity](https://docs.coveo.com/en/84).
     *
     * @param securityProviderId
     * @param securityIdentityDelete
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> deleteSecurityIdentity(String securityProviderId,
            SecurityIdentityDelete securityIdentityDelete) throws IOException, InterruptedException {
        return this.platformClient.deleteSecurityIdentity(securityProviderId, securityIdentityDelete);
    }

    /**
     * Update the status of a Push source. See [Updating the Status of a Push
     * Source](https://docs.coveo.com/en/35).
     *
     * @param status
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> updateSourceStatus(PushAPIStatus status)
            throws IOException, InterruptedException {
        return this.platformClient.updateSourceStatus(this.sourceId, status);
    }

    /**
     * Delete old security identities. See [Disabling Old Security
     * Identities](https://docs.coveo.com/en/33).
     *
     * @param securityProviderId
     * @param batchDelete
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> deleteOldSecurityIdentities(String securityProviderId,
            SecurityIdentityDeleteOptions batchDelete) throws IOException, InterruptedException {
        return this.platformClient.deleteOldSecurityIdentities(securityProviderId, batchDelete);
    }

    /**
     * Manage batches of security identities. See [Manage Batches of Security
     * Identities](https://docs.coveo.com/en/55).
     *
     * @param securityProviderId
     * @param batchConfig
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> manageSecurityIdentities(String securityProviderId,
            SecurityIdentityBatchConfig batchConfig) throws IOException, InterruptedException {
        return this.platformClient.manageSecurityIdentities(securityProviderId, batchConfig);
    }

    /**
     * Manages pushing batches of Security Identities to a File Container, then into
     * Coveo. See [Manage Batches of Security
     * Identities](https://docs.coveo.com/en/55)
     *
     * @param securityProviderId
     * @param batchIdentity
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public SecurityIdentityBatchResponse batchUpdateSecurityIdentities(String securityProviderId,
            BatchIdentity batchIdentity) throws IOException, InterruptedException {
        SecurityIdentityBatchResponse securityIdentityBatchResponse = new SecurityIdentityBatchResponse();
        HttpResponse<String> resFileContainer = this.platformClient.createFileContainer();
        FileContainer fileContainer = new Gson().fromJson(resFileContainer.body(), FileContainer.class);
        String batchIdJson = new Gson().toJson(batchIdentity.marshal());
        securityIdentityBatchResponse.s3Response = this.platformClient.uploadContentToFileContainer(fileContainer,
                batchIdJson);
        if (securityIdentityBatchResponse.s3Response.statusCode() >= 200
                && securityIdentityBatchResponse.s3Response.statusCode() <= 299) { // maybe just 200 or 202
            SecurityIdentityBatchConfig batchConfig = new SecurityIdentityBatchConfig(fileContainer.fileId, 0l);
            securityIdentityBatchResponse.batchResponse = this.manageSecurityIdentities(securityProviderId,
                    batchConfig);
        }
        return securityIdentityBatchResponse;
    }

    /**
     * Adds or updates an individual item in a push source. See [Adding a Single
     * Item in a Push Source](https://docs.coveo.com/en/133).
     *
     * @param docBuilder
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> addOrUpdateDocument(DocumentBuilder docBuilder)
            throws IOException, InterruptedException {
        CompressionType compressionType = docBuilder.getDocument().compressedBinaryData != null
                ? docBuilder.getDocument().compressedBinaryData.getCompressionType()
                : CompressionType.UNCOMPRESSED;
        return this.platformClient.pushDocument(this.sourceId, docBuilder.marshal(), docBuilder.getDocument().uri,
                compressionType);
    }

    /**
     * Deletes a specific item from a Push source. Optionally, the child items of
     * that item can also be deleted. See [Deleting an Item in a Push
     * Source](https://docs.coveo.com/en/171).
     *
     * @param documentId
     * @param deleteChildren
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> deleteDocument(String documentId, Boolean deleteChildren)
            throws IOException, InterruptedException {
        return this.platformClient.deleteDocument(this.sourceId, documentId, deleteChildren);
    }

}
