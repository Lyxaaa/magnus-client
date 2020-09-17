package com.deco.magnus.ProjectNet.Messages;

import android.util.SparseArray;

public enum Type {
	Ack(0)
    Heartbeat(1)

    Initialise(2)
    InitialiseResult(3)

    RegisterUser(4)
    RegisterUserResult(5)

    UpdateUserProfile(6)

    SendFriendRequest(7)
    AcceptFriend(8)
    AcceptFriendResult(9

    GetMyFriendRequests(10)
    GetMyFriendRequestsResult(11)

    GetFriendsRequestingMe(12)
    GetFriendsRequestingMeResult(13) 

    GetFriends(14)
    GetFriendsResult(15)

    SendMessage(16)
    RetrieveMessages(17)
    RetrieveMessagesResult(18)


    RetrieveUserProfile(19)
    RetrieveUserProfileResult(20)

    GetMatchDetails(21)

    Login(22)
    LoginResult(23)

    EnterMatchQueue(24)
    MatchFound(25)


    Disconnect(26)

    UpdateUserPassword(27)
    CreateMatch(28)
    CreateMatchResult(29)


    Unknown(int.MaxValue);

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
