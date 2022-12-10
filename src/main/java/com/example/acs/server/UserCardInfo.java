package com.example.acs.server;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Objects;

public class UserCardInfo {
    private int card_number;
    private Date card_expiration_date;
    private int card_cvv;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCardInfo that = (UserCardInfo) o;
        return card_number == that.card_number && card_cvv == that.card_cvv && Objects.equals(card_expiration_date, that.card_expiration_date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(card_number, card_expiration_date, card_cvv);
    }

    public UserCardInfo(@JsonProperty("card_number") int card_number, @JsonProperty("card_expiration_date") Date card_expiration_date, @JsonProperty("card_cvv") int card_cvv) {
        this.card_number = card_number;
        this.card_expiration_date = card_expiration_date;
        this.card_cvv = card_cvv;
    }

    public int getCard_number() {
        return card_number;
    }

    public void setCard_number(int card_number) {
        this.card_number = card_number;
    }

    public Date getCard_expiration_date() {
        return card_expiration_date;
    }

    public void setCard_expiration_date(Date card_expiration_date) {
        this.card_expiration_date = card_expiration_date;
    }

    public int getCard_cvv() {
        return card_cvv;
    }

    public void setCard_cvv(int card_cvv) {
        this.card_cvv = card_cvv;
    }
}
