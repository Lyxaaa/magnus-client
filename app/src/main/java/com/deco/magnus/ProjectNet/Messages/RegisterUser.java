package com.deco.magnus.ProjectNet.Messages;

/**
 * {@link RegisterUser} Result is being reused as {@link LoginResult}
 */
public class RegisterUser extends Message {
    String email;
    String password;
    String name;
    String bio;

    public RegisterUser(String email, String password, String name, String bio) {
        this.type = Type.RegisterUser.getValue();
        this.email = email;
        this.password = password;
        this.name = name;
        this.bio = bio;
    }
}
