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
    @SerializedName("19") RetrieveMessages(19),
    @SerializedName("20") RetrieveMessagesResult(20),


    @SerializedName("21") RetrieveUserProfile(21),
    @SerializedName("22") RetrieveUserProfileResult(22),

    @SerializedName("23") GetMatchDetails(23),
    @SerializedName("24") GetMatchDetailsResult(24),

    @SerializedName("25") Login(25),
    @SerializedName("26") LoginResult(26),

    @SerializedName("27") EnterMatchQueue(27),
    @SerializedName("28") MatchFound(28),
    @SerializedName("29") SendChallenge(29),

    @SerializedName("30") CreateMatch(30),
    @SerializedName("31") CreateMatchResult(31),

    @SerializedName("32") GetMatchHistory(32),
    @SerializedName("33") GetMatchHistoryResult(33),

    @SerializedName("34") AcceptChallenge(34),
    @SerializedName("35") UpdateBoard(35),
    @SerializedName("36") GetBoardState(36),
    @SerializedName("37") BoardResult(37),

    //added 11/10/2020
    @SerializedName("38") RetrieveOtherUsers(38),
    @SerializedName("39") RetrieveOtherUsersResult(39),

    //18/10/2020
    @SerializedName("40") ExitMatchQueue(40),

    @SerializedName("41") ByteClientProfileImage(41),
    @SerializedName("42") ByteUpdateProfileImage(42),

    @SerializedName("43") MatchStart(43),
    @SerializedName("44") EndMatch(44),
    @SerializedName("45") AcceptMatch(45),

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
