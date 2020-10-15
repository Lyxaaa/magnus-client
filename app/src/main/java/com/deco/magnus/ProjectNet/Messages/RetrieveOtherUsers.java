package com.deco.magnus.ProjectNet.Messages;

public class RetrieveOtherUsers extends Message {
    String email;
    String search;
    int limit;
    int offset;

    public RetrieveOtherUsers(String email,String search, int limit, int offset) {
        this.type = Type.RetrieveOtherUsers.getValue();
        this.email = email;
        this.search = search;
        this.limit = limit;
        this.offset = offset;
    }
}