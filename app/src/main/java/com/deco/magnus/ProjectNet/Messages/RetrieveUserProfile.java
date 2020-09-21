package com.deco.magnus.ProjectNet.Messages;

public class RetrieveUserProfile extends Message {
    String email;

    public RetrieveUserProfile(String email) {
        this.type = Type.RetrieveUserProfile.getValue();
        this.email = email;
    }
}
