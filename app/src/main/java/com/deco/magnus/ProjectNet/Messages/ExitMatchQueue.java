package com.deco.magnus.ProjectNet.Messages;

public class ExitMatchQueue extends Message {
    public String email;

    public ExitMatchQueue(String email) {
        this.type = Type.ExitMatchQueue.getValue();
        this.email = email;
    }
}
