package com.deco.magnus.ProjectNet.Messages;

public class LoginResult extends Message {
    public Result result;
    public String error;
    public String email;
    public String userName;
    public String uniqueId;
    public String bio;
    public byte[] profile;
}
