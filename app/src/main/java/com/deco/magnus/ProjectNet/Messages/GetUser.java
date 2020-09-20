package com.deco.magnus.ProjectNet.Messages;

/**
 * Requests any Users' Data (e.g. For use in displaying friends)
 * Should receive a LoginResult in return
 */
public class GetUser extends Message {
    public String email;

    public GetUser(String email) {
        this.type = Type.RetrieveUserProfile.getValue();
        this.email = email;
    }
}
