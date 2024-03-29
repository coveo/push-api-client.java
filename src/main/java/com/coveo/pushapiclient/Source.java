package com.coveo.pushapiclient;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.http.HttpResponse;

// TODO: LENS-844 - Deprecate class
public class Source {
  PlatformClient platformClient;

  /**
   * @param apiKey An apiKey capable of pushing documents and managing sources in a Coveo
   *     organization.
   * @see <a href="https://docs.coveo.com/en/1718">Manage API Keys</a>.
   * @param organizationId The Coveo Organization identifier.
   */
  public Source(String apiKey, String organizationId) {
    this.platformClient = new PlatformClient(apiKey, organizationId);
  }

  /**
   * @param apiKey An apiKey capable of pushing documents and managing sources in a Coveo
   *     organization.
   * @see <a href="https://docs.coveo.com/en/1718">Manage API Keys</a>.
   * @param organizationId The Coveo Organization identifier.
   * @param platformUrl
   */
  public Source(String apiKey, String organizationId, PlatformUrl platformUrl) {
    this.platformClient = new PlatformClient(apiKey, organizationId, platformUrl);
  }

  /**
   * @deprecated Please now use PlatformUrl to define your Platform environment
   * @see PlatformUrl Construct a PlatformUrl
   * @param apiKey An apiKey capable of pushing documents and managing sources in a Coveo
   *     organization.
   * @see <a href="https://docs.coveo.com/en/1718">Manage API Keys</a>.
   * @param organizationId The Coveo Organization identifier.
   * @param environment The Environment to be used.
   */
  @Deprecated
  public Source(String apiKey, String organizationId, Environment environment) {
    this.platformClient = new PlatformClient(apiKey, organizationId, environment);
  }

  /**
   * Create a new push source.
   *
   * @param name The name of the source to create
   * @param sourceVisibility The security option that should be applied to the content of the
   *     source.
   * @see <a href="https://docs.coveo.com/en/1779">Content Security</a>
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> create(String name, SourceVisibility sourceVisibility)
      throws IOException, InterruptedException {
    return this.platformClient.createSource(name, SourceType.PUSH, sourceVisibility);
  }

  /**
   * Create or update a security identity.
   *
   * @see <a href="https://docs.coveo.com/en/167">Adding a Single Security Identity</a>
   * @see <a href="https://docs.coveo.com/en/139">Security Identity Models</a>
   * @param securityProviderId
   * @param securityIdentityModel
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> createOrUpdateSecurityIdentity(
      String securityProviderId, SecurityIdentityModel securityIdentityModel)
      throws IOException, InterruptedException {
    return this.platformClient.createOrUpdateSecurityIdentity(
        securityProviderId, securityIdentityModel);
  }

  /**
   * Create or update a security identity alias.
   *
   * @see <a href="https://docs.coveo.com/en/142">Adding a Single Alias</a>
   * @see <a href="https://docs.coveo.com/en/46">User Alias Definition Examples</a>
   * @param securityProviderId
   * @param securityIdentityAliasModel
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> createOrUpdateSecurityIdentityAlias(
      String securityProviderId, SecurityIdentityAliasModel securityIdentityAliasModel)
      throws IOException, InterruptedException {
    return this.platformClient.createOrUpdateSecurityIdentityAlias(
        securityProviderId, securityIdentityAliasModel);
  }

  /**
   * Delete a security identity.
   *
   * @see <a href="https://docs.coveo.com/en/84">Disabling a Single Security Identity</a>
   * @param securityProviderId
   * @param securityIdentityDelete
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> deleteSecurityIdentity(
      String securityProviderId, SecurityIdentityDelete securityIdentityDelete)
      throws IOException, InterruptedException {
    return this.platformClient.deleteSecurityIdentity(securityProviderId, securityIdentityDelete);
  }

  /**
   * Update the status of a Push source.
   *
   * @see <a href="https://docs.coveo.com/en/35">Updating the Status of a Push Source</a>
   * @param sourceId
   * @param status
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> updateSourceStatus(String sourceId, PushAPIStatus status)
      throws IOException, InterruptedException {
    return this.platformClient.updateSourceStatus(sourceId, status);
  }

  /**
   * Delete old security identities.
   *
   * @see <a href="https://docs.coveo.com/en/33">Disabling Old Security Identities</a>
   * @param securityProviderId
   * @param batchDelete
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> deleteOldSecurityIdentities(
      String securityProviderId, SecurityIdentityDeleteOptions batchDelete)
      throws IOException, InterruptedException {
    return this.platformClient.deleteOldSecurityIdentities(securityProviderId, batchDelete);
  }

  /**
   * Manage batches of security identities.
   *
   * @see <a href="https://docs.coveo.com/en/55">Manage Batches of Security Identities</a>
   * @param securityProviderId
   * @param batchConfig
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> manageSecurityIdentities(
      String securityProviderId, SecurityIdentityBatchConfig batchConfig)
      throws IOException, InterruptedException {
    return this.platformClient.manageSecurityIdentities(securityProviderId, batchConfig);
  }

  /**
   * Adds or updates an individual item in a push source.
   *
   * @see <a href="https://docs.coveo.com/en/133">Adding a Single Item in a Push Source</a>
   * @param sourceId
   * @param docBuilder
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> addOrUpdateDocument(String sourceId, DocumentBuilder docBuilder)
      throws IOException, InterruptedException {
    CompressionType compressionType =
        docBuilder.getDocument().compressedBinaryData != null
            ? docBuilder.getDocument().compressedBinaryData.getCompressionType()
            : CompressionType.UNCOMPRESSED;
    return this.platformClient.pushDocument(
        sourceId, docBuilder.marshal(), docBuilder.getDocument().uri, compressionType);
  }

  /**
   * Deletes a specific item from a Push source. Optionally, the child items of that item can also
   * be deleted.
   *
   * @see <a href="https://docs.coveo.com/en/171">Deleting an Item in a Push Source</a>
   * @param sourceId
   * @param documentId
   * @param deleteChildren
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> deleteDocument(
      String sourceId, String documentId, Boolean deleteChildren)
      throws IOException, InterruptedException {
    return this.platformClient.deleteDocument(sourceId, documentId, deleteChildren);
  }

  /**
   * Manage batches of items in a push source.
   *
   * @see <a href="https://docs.coveo.com/en/90">Manage Batches of Items in a Push Source</a>
   * @param sourceId
   * @param batchUpdate
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> batchUpdateDocuments(String sourceId, BatchUpdate batchUpdate)
      throws IOException, InterruptedException {
    HttpResponse<String> resFileContainer = this.platformClient.createFileContainer();
    FileContainer fileContainer = new Gson().fromJson(resFileContainer.body(), FileContainer.class);
    this.platformClient.uploadContentToFileContainer(
        fileContainer, new Gson().toJson(batchUpdate.marshal()));
    return this.platformClient.pushFileContainerContent(sourceId, fileContainer);
  }

  /**
   * Manages pushing batches of Security Identities to a File Container, then into Coveo.
   *
   * @see <a href="https://docs.coveo.com/en/55">Manage Batches of Security Identities</a>
   * @param securityProviderId
   * @param batchIdentity
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public SecurityIdentityBatchResponse batchUpdateSecurityIdentities(
      String securityProviderId, BatchIdentity batchIdentity)
      throws IOException, InterruptedException {
    SecurityIdentityBatchResponse securityIdentityBatchResponse =
        new SecurityIdentityBatchResponse();
    HttpResponse<String> resFileContainer = this.platformClient.createFileContainer();
    FileContainer fileContainer = new Gson().fromJson(resFileContainer.body(), FileContainer.class);
    String batchIdJson = new Gson().toJson(batchIdentity.marshal());
    securityIdentityBatchResponse.s3Response =
        this.platformClient.uploadContentToFileContainer(fileContainer, batchIdJson);
    if (securityIdentityBatchResponse.s3Response.statusCode() >= 200
        && securityIdentityBatchResponse.s3Response.statusCode() <= 299) { // maybe just 200 or 202
      SecurityIdentityBatchConfig batchConfig =
          new SecurityIdentityBatchConfig(fileContainer.fileId, 0l);
      securityIdentityBatchResponse.batchResponse =
          this.manageSecurityIdentities(securityProviderId, batchConfig);
    }
    return securityIdentityBatchResponse;
  }

  /**
   * Creates a File Container.
   *
   * @see <a href="https://docs.coveo.com/en/43">Creating a File Container</a>
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public FileContainer createFileContainer() throws IOException, InterruptedException {
    HttpResponse<String> resFileContainer = this.platformClient.createFileContainer();
    return new Gson().fromJson(resFileContainer.body(), FileContainer.class);
  }

  /**
   * Push file to a File Container.
   *
   * @see <a href="https://docs.coveo.com/en/69">Using the compressedBinaryDataFileId Property</a>
   * @param fileContainer
   * @param fileAsBytes
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> pushBinaryToFileContainer(
      FileContainer fileContainer, byte[] fileAsBytes) throws IOException, InterruptedException {
    return this.platformClient.pushBinaryToFileContainer(fileContainer, fileAsBytes);
  }
}
