package com.example.acs.server;

import java.util.Date;
import java.util.Objects;

public class SavedCode {
    private int cardNumber;
    private String code;
    private Date createdTime;

    public SavedCode(int cardNumber, String code, Date createdTime) {
        this.cardNumber = cardNumber;
        this.code = code;
        this.createdTime = createdTime;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SavedCode savedCode = (SavedCode) o;
        return cardNumber == savedCode.cardNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber);
    }
}
