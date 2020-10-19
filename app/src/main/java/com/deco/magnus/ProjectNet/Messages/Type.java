package com.deco.magnus.ProjectNet.Messages;

import android.util.SparseArray;

public enum Type {
    Ack(0),
    Heartbeat(1),

    Disconnect(2),
    Initialise(3),
    InitialiseResult(4),

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

    AcceptChallenge(34),
    UpdateBoard(35),
    GetBoardState(36),
    BoardResult(37),

    //added 11/10/2020
    RetrieveOtherUsers(38),
    RetrieveOtherUsersResult(39),

    //18/10/2020
    ExitMatchQueue (40),

    ByteClientProfileImage(41),

    ByteUpdateProfileImage(42),


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
