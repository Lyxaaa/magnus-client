package com.deco.magnus.ProjectNet.Messages;

public class InitialiseResult extends Message {
    Result result;
    String error;

    public InitialiseResult(Result result, String error) {
        this.type = Type.InitialiseResult.getValue();
        this.result = result;
        this.error = error;
    }
}
