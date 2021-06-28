package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.http.HttpResponse;

public class Source {
    PlatformClient platformClient;

    public Source(String apiKey, String organizationId) {
        this.platformClient = new PlatformClient(apiKey, organizationId);
    }

    public HttpResponse<String> create(String name, SourceVisibility sourceVisibility) throws IOException, InterruptedException {
        return this.platformClient.createSource(name, sourceVisibility);
    }

    public HttpResponse<String> createOrUpdateSecurityIdentity(String securityProviderId, SecurityIdentityModel securityIdentityModel) throws IOException, InterruptedException {
        return this.platformClient.createOrUpdateSecurityIdentity(securityProviderId, securityIdentityModel);
    }

    public HttpResponse<String> createOrUpdateSecurityIdentityAlias(String securityProviderId, SecurityIdentityAliasModel securityIdentityAliasModel) throws IOException, InterruptedException {
        return this.platformClient.createOrUpdateSecurityIdentityAlias(securityProviderId, securityIdentityAliasModel);
    }

    public HttpResponse<String> deleteSecurityIdentity(String securityProviderId, SecurityIdentityDelete securityIdentityDelete) throws IOException, InterruptedException {
        return this.platformClient.deleteSecurityIdentity(securityProviderId, securityIdentityDelete);
    }

    public HttpResponse<String> deleteOldSecurityIdentities(String securityProviderId, SecurityIdentityDeleteOptions batchDelete) throws IOException, InterruptedException {
        return this.platformClient.deleteOldSecurityIdentities(securityProviderId, batchDelete);
    }

    public HttpResponse<String> manageSecurityIdentities(String securityProviderId, SecurityIdentityBatchConfig batchConfig) throws IOException, InterruptedException {
        return this.platformClient.manageSecurityIdentities(securityProviderId, batchConfig);
    }
}
