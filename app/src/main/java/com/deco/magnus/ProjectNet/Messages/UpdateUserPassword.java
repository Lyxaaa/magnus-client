package com.deco.magnus.ProjectNet.Messages;

/**
 * {@link UpdateUserPassword} Result is being reused as {@link LoginResult}
 */
public class UpdateUserPassword extends Message {
    String email;
    String oldPassword;
    String newPassword;

    public UpdateUserPassword(String email, String oldPassword, String newPassword) {
        this.type = Type.UpdateUserPassword.getValue();
        this.email = email;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
