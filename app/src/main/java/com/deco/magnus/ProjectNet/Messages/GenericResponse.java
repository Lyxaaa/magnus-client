package com.deco.magnus.ProjectNet.Messages;

public class GenericResponse extends Message {
    Result success;

    public GenericResponse(Result result) {
        this.type = Type.GenericResponse.getValue();

    }
}
