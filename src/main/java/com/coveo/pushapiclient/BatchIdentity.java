package com.coveo.pushapiclient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * See [Manage Batches of Security Identities](https://docs.coveo.com/en/55)
 */
public class BatchIdentity {

    private List<SecurityIdentityModel> members;
    private List<SecurityIdentityAliasModel> mappings;
    private List<IdentityModel> deleted;

    public BatchIdentity(List<SecurityIdentityModel> members, List<SecurityIdentityAliasModel> mappings, List<IdentityModel> deleted) {
        this.members = members;
        this.mappings = mappings;
        this.deleted = deleted;
    }

    public BatchIdentityRecord marshal() {
        return new BatchIdentityRecord(
                this.members.stream().map(s -> new Gson().toJsonTree(s).getAsJsonObject()).toArray(JsonObject[]::new),
                this.mappings.stream().map(s -> new Gson().toJsonTree(s).getAsJsonObject()).toArray(JsonObject[]::new),
                this.deleted.stream().map(s -> new Gson().toJsonTree(s).getAsJsonObject()).toArray(JsonObject[]::new)
        );
    }

    public List<SecurityIdentityModel> getMembers() {
        return members;
    }

    public List<SecurityIdentityAliasModel> getMappings() {
        return mappings;
    }

    public List<IdentityModel> getDeleted() {
        return deleted;
    }
}
