package com.deco.magnus.ProjectNet.Messages;

import com.deco.magnus.UserData.Chat;

public class RetrieveMessagesResult extends MessageResult {
    Chat[] chat;

    public RetrieveMessagesResult(Chat[] chat) {
        this.type = Type.RetrieveMessagesResult.getValue();
        this.chat = chat;
    }
}
