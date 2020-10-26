package com.deco.magnus.ProjectNet.Messages;

public class RetrieveUserProfile extends Message {
    String email;
    int imageRequestId;

    public RetrieveUserProfile(String email, int imageRequestId) {
        this.type = Type.RetrieveUserProfile.getValue();
        this.email = email;
        this.imageRequestId = imageRequestId;
    }
}
