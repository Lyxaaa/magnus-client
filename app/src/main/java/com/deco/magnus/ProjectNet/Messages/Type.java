package com.deco.magnus.ProjectNet.Messages;

import android.util.SparseArray;

public enum Type {
    Ack(0),

    Heartbeat(1),

    Disconnect(2),

    Initialise(3),
    InitialiseResult(4),

    RegisterUser(5),
    RegisterUserResult(6),

    UpdateUserProfile(7),

    SendFriendRequest(8),
    AddFriend(9),

    GetMyFriendRequests(10),
    GetFriendsRequestingMe(11),

    GetFriends(12),

    SendMessage(13),

    RetrieveUserProfile(14),

    GetMatchDetails(15),

    Login(16),
    LoginResult(17),

    UserInfo(18),

    EnterMatchQueue(19),
    MatchFound(20),
    Unknown(Integer.MAX_VALUE);

    private int value;

    private static final SparseArray<Type> intToTypeMap = new SparseArray<>();

    static {
        for (Type type : Type.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    public static Type fromInt(int i) {
        Type type = intToTypeMap.get(i);
        if (type == null)
            return Unknown;
        return type;
    }

    Type(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    }
