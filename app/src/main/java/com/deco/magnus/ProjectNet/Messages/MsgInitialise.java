package com.deco.magnus.ProjectNet.Messages;

public class MsgInitialise extends Message {
    public String id;

    public MsgInitialise(String id) {
        this.type = Type.Initialise.getValue();
        this.id = id;
    }
}
