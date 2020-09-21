package com.deco.magnus.ProjectNet.Messages;

/**
 * {@link UpdateUserProfile} Result is being reused as {@link LoginResult}
 */
public class UpdateUserProfile extends Message {
    String email;
    String name;
    String bio;
    byte[] profile;

    public UpdateUserProfile(String email, String name, String bio, byte[] profile) {
        this.type = Type.UpdateUserProfile.getValue();
        this.email = email;
        this.name = name;
        this.bio = bio;
        this.profile = profile;
    }
}
