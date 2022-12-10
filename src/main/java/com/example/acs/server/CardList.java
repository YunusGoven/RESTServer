package com.example.acs.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CardList {
    private List<UserCardInfo> userCardInfoList;
    public CardList() {
        this.userCardInfoList = new ArrayList<>();
        UserCardInfo u0 = new UserCardInfo(1234567890, new Date(), 111);
        UserCardInfo u1 = new UserCardInfo(1134567890, new Date(), 121);
        UserCardInfo u2 = new UserCardInfo(1224567890, new Date(), 131);
        UserCardInfo u3 = new UserCardInfo(1333567890, new Date(), 141);
        UserCardInfo u4 = new UserCardInfo(1444567890, new Date(), 151);
        UserCardInfo u5 = new UserCardInfo(1555567890, new Date(), 161);
        UserCardInfo u6 = new UserCardInfo(1666667890, new Date(), 171);
        UserCardInfo u7 = new UserCardInfo(1777777890, new Date(), 181);
        UserCardInfo u8 = new UserCardInfo(1888887890, new Date(1670691977298L), 191);
        userCardInfoList.add(u0);
        userCardInfoList.add(u1);
        userCardInfoList.add(u2);
        userCardInfoList.add(u3);
        userCardInfoList.add(u4);
        userCardInfoList.add(u5);
        userCardInfoList.add(u6);
        userCardInfoList.add(u7);
        userCardInfoList.add(u8);
    }

    public boolean contains(UserCardInfo userCardInfo) {
        return userCardInfoList.contains(userCardInfo);
    }
}
