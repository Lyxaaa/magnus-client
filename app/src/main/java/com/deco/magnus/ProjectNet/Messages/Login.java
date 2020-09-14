package com.deco.magnus.ProjectNet.Messages;

/**
 * Requests the logging in Users' Data
 * Should receive a LoginResult in return
 */
public class Login extends Message {
    public String email;
    public String password;

    public Login (String email, String password) {
        this.type = Type.Login.getValue();
        this.email = email;
        this.password = password;
    }
}
