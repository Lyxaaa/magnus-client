package com.deco.magnus.ProjectNet.Messages;

public class GetUserResult extends Message {
    public String email;
    public String userName;
    public String uniqueId;
    public String bio;
    public byte[] profile;
}
