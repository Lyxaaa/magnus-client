package com.deco.magnus.ProjectNet.Messages;

import android.util.SparseArray;

public enum Type {
	Ack(0)
    Heartbeat(1)

    Initialise(2)         //not sure how we are handling this
    InitialiseResult(3)   //not sure how we are handling this

    RegisterUser(4)
    GenericResponse(5)

    UpdateUserProfile(6)
    UpdateUserPassword(7)

    SendFriendRequest(8)
    AcceptFriend(9)
    AcceptFriendResult(10)

    GetMyFriendRequests(11)
    GetMyFriendRequestsResult(12)

    GetFriendsRequestingMe(13)
    GetFriendsRequestingMeResult(14) 

    GetFriends(15)
    GetFriendsResult(16)

    SendMessage(17)
    RetrieveMessages(18)
    RetrieveMessagesResult(19)


    RetrieveUserProfile(20)
    RetrieveUserProfileResult(21)

    GetMatchDetails(22)
    GetMatchDetailsResult(23)

    Login(24)
    LoginResult(25)

    EnterMatchQueue(26)   //not sure how we are handling this
    MatchFound(27)        //not sure how we are handling this
    SendChallenge(28)


    Disconnect(29)        //not sure how we are handling this

    
    CreateMatch(30)
    CreateMatchResult(31)

    GetMatchHistory(32)
    GetMatchHistoryResult(33)

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
