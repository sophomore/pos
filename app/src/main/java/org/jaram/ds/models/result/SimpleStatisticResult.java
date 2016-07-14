package org.jaram.ds.models.result;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by jdekim43 on 2016. 5. 13..
 */
public class SimpleStatisticResult {

    @SerializedName("cash_total") int cashTotal;
    @SerializedName("card_total") int cardTotal;
    @SerializedName("service_total") int serviceTotal;
    @SerializedName("credit_total") int creditTotal;
    @SerializedName("start") private Date startDate;
    @SerializedName("end") private Date endDate;
    @SerializedName("value") List<Item> values;

    public int getCashTotal() {
        return cashTotal;
    }

    public int getCardTotal() {
        return cardTotal;
    }

    public int getServiceTotal() {
        return serviceTotal;
    }

    public int getCreditTotal() {
        return creditTotal;
    }

    public int getTotal() {
        return cashTotal + cardTotal + serviceTotal + creditTotal;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public List<Item> getValues() {
        return values;
    }

    public static class Item {

        @SerializedName("key") String key;
        @SerializedName("cash_total") int cashTotal;
        @SerializedName("card_total") int cardTotal;
        @SerializedName("service_total") int serviceTotal;
        @SerializedName("credit_total") int creditTotal;

        public String getKey() {
            return key;
        }

        public int getCashTotal() {
            return cashTotal;
        }

        public int getCardTotal() {
            return cardTotal;
        }

        public int getServiceTotal() {
            return serviceTotal;
        }

        public int getCreditTotal() {
            return creditTotal;
        }

        public int getTotal() {
            return cashTotal + cardTotal + serviceTotal + creditTotal;
        }
    }
}
