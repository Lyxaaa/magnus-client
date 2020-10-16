package com.deco.magnus.ProjectNet.Messages;

public class RetrieveUserProfileResult extends MessageResult {
    public String userId;
    public String email;
    public String name;
    public String bio;
    public byte[] profile;

    public RetrieveUserProfileResult(String userId, String email, String name, String bio, byte[] profile) {
        this.type = Type.RetrieveUserProfileResult.getValue();
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.bio = bio;
        this.profile = profile;
    }
}
