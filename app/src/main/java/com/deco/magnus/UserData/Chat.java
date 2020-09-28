package com.deco.magnus.UserData;

import com.deco.magnus.ProjectNet.Messages.MessageResult.Result;

public class Chat {
    public String userId;
    public String email;
    public String text;
    public long dateTime;
    public Result result = Result.Success;

    public Chat(String userId, String email, String text, long dateTime) {
        this.userId = userId;
        this.email = email;
        this.text = text;
        this.dateTime = dateTime;
    }

    public Chat(String userId, String email, String text, long dateTime, Result result) {
        this.userId = userId;
        this.email = email;
        this.text = text;
        this.dateTime = dateTime;
        this.result = result;
    }
}
