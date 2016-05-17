package org.jaram.ds.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jdekim43 on 2016. 5. 13..
 */
public enum Pay {
    @SerializedName("1") CASH(1),
    @SerializedName("2") CARD(2),
    @SerializedName("3") SERVICE(3),
    @SerializedName("4") CREDIT(4);

    private int value;

    Pay(int value) {
        this.value = value;
    }

    public static Pay valueOf(int value) {
        for (Pay pay : values()) {
            if (pay.value == value) {
                return pay;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        switch (this) {
            case CASH:
                return "현금";
            case CARD:
                return "카드";
            case SERVICE:
                return "서비스";
            case CREDIT:
                return "외상";
        }
        return "";
    }
}
