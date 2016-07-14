package org.jaram.ds.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by chulwoo on 15. 7. 2..
 */
public class PaginationData<Data> {

    @SerializedName("count") private int mCount;
    @SerializedName("next") private String mNext;
    @SerializedName("prev") private String mPrev;
    @SerializedName("results") private List<Data> mResults;

    public PaginationData(List<Data> results) {
        mResults = results;
        mCount = results.size();
        mNext = "";
        mPrev = "";
    }

    public int getCount() {
        return mCount;
    }

    public String getNext() {
        return mNext;
    }

    public String getPrev() {
        return mPrev;
    }

    public List<Data> getResults() {
        return mResults;
    }

    public void setmNext(String mNext) {
        this.mNext = mNext;
    }
}
