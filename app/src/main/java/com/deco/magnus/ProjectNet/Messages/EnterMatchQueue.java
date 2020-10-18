package com.deco.magnus.ProjectNet.Messages;

public class EnterMatchQueue extends Message {
    public String email;

    public EnterMatchQueue(String email) {
        this.type = Type.EnterMatchQueue.getValue();
        this.email = email;
    }
}
