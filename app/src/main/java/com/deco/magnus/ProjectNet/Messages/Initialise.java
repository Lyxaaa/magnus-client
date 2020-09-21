package com.deco.magnus.ProjectNet.Messages;

public class Initialise extends Message {
    public String id;

    public Initialise(String id) {
        this.type = Type.Initialise.getValue();
        this.id = id;
    }
}