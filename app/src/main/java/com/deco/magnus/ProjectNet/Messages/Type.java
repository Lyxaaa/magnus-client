package com.deco.magnus.ProjectNet.Messages;

import android.util.SparseArray;

import com.google.gson.annotations.SerializedName;

public enum Type {
    @SerializedName("0") Ack(0),
    @SerializedName("1") Heartbeat(1),

    @SerializedName("2") Disconnect(2),
    @SerializedName("3") Initialise(3),
    @SerializedName("4") InitialiseResult(4),

    @SerializedName("5") RegisterUser(5),
    @SerializedName("6") MessageResult(6),

    @SerializedName("7") UpdateUserProfile(7),
    @SerializedName("8") UpdateUserPassword(8),

    @SerializedName("9") SendFriendRequest(9),
    @SerializedName("10") AcceptFriend(10),
    @SerializedName("11") AcceptFriendResult(11),

    @SerializedName("12") GetMyFriendRequests(12),
    @SerializedName("13") GetMyFriendRequestsResult(13),

    @SerializedName("14") GetFriendsRequestingMe(14),
    @SerializedName("15") GetFriendsRequestingMeResult(15),

    @SerializedName("16") GetFriends(16),
    @SerializedName("17") GetFriendsResult(17),

    @SerializedName("18") SendMessage(18),
    @SerializedName("19") SendMessageResult(19),
    @SerializedName("20") RetrieveMessages(20),
    @SerializedName("21") RetrieveMessagesResult(21),


    @SerializedName("22") RetrieveUserProfile(22),
    @SerializedName("23") RetrieveUserProfileResult(23),

    @SerializedName("24") GetMatchDetails(24),
    @SerializedName("25") GetMatchDetailsResult(25),

    @SerializedName("26") Login(26),
    @SerializedName("27") LoginResult(27),

    @SerializedName("28") EnterMatchQueue(28),
    @SerializedName("29") MatchFound(29),
    @SerializedName("30") SendChallenge(30),

    @SerializedName("31") CreateMatch(31),
    @SerializedName("32") CreateMatchResult(32),

    @SerializedName("33") GetMatchHistory(33),
    @SerializedName("34") GetMatchHistoryResult(34),

    @SerializedName("35") AcceptChallenge(35),
    @SerializedName("36") UpdateBoard(36),
    @SerializedName("37") GetBoardState(37),
    @SerializedName("38") BoardResult(38),

    //added 11/10/2020
    @SerializedName("39") RetrieveOtherUsers(39),
    @SerializedName("40") RetrieveOtherUsersResult(40),

    //18/10/2020
    @SerializedName("41") ExitMatchQueue(41),

    @SerializedName("42") ByteClientProfileImage(42),
    @SerializedName("43") ByteUpdateProfileImage(43),

    @SerializedName("44") MatchStart(44),
    @SerializedName("45") EndMatch(45),
    @SerializedName("46") AcceptMatch(46),

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
