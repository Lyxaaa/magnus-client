package com.deco.magnus.ProjectNet.Messages;

import android.util.SparseArray;

public enum Type {
    Ack(0),
    Heartbeat(1),

    Disconnect(2),
    Initialise(3),         //not sure how we are handling this
    InitialiseResult(4),   //not sure how we are handling this

    RegisterUser(5),
    MessageResult(6),

    UpdateUserProfile(7),
    UpdateUserPassword(8),

    SendFriendRequest(9),
    AcceptFriend(10),
    AcceptFriendResult(11),

    GetMyFriendRequests(12),
    GetMyFriendRequestsResult(13),

    GetFriendsRequestingMe(14),
    GetFriendsRequestingMeResult(15),

    GetFriends(16),
    GetFriendsResult(17),

    SendMessage(18),
    RetrieveMessages(19),
    RetrieveMessagesResult(20),


    RetrieveUserProfile(21),
    RetrieveUserProfileResult(22),

    GetMatchDetails(23),
    GetMatchDetailsResult(24),

    Login(25),
    LoginResult(26),

    EnterMatchQueue(27),
    MatchFound(28),
    SendChallenge(29),

    CreateMatch(30),
    CreateMatchResult(31),

    GetMatchHistory(32),
    GetMatchHistoryResult(33),

    GenericResult(34),

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
