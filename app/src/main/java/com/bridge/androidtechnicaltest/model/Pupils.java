package com.bridge.androidtechnicaltest.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/* Data model for Pupil complete response */
public class Pupils {

    @SerializedName("items")
    private List<PupilDetails> mPupilDetails = null;

    @SerializedName("pageNumber")
    private Integer mPageNumber;

    @SerializedName("itemCount")
    private Integer mPupilDetailsCount;

    @SerializedName("totalPages")
    private Integer mTotalPages;

    public List<PupilDetails> getPupilDetails() {
        return mPupilDetails;
    }

    public void setPupilDetailss(List<PupilDetails> PupilDetailss) {
        this.mPupilDetails = PupilDetailss;
    }

    public Pupils withPupilDetailss(List<PupilDetails> PupilDetailss) {
        this.mPupilDetails = PupilDetailss;
        return this;
    }

    public Integer getPageNumber() {
        return mPageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.mPageNumber = pageNumber;
    }

    public Pupils withPageNumber(Integer pageNumber) {
        this.mPageNumber = pageNumber;
        return this;
    }

    public Integer getPupilDetailsCount() {
        return mPupilDetailsCount;
    }

    public void setPupilDetailsCount(Integer PupilDetailsCount) {
        this.mPupilDetailsCount = PupilDetailsCount;
    }

    public Pupils withPupilDetailsCount(Integer PupilDetailsCount) {
        this.mPupilDetailsCount = PupilDetailsCount;
        return this;
    }

    public Integer getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.mTotalPages = totalPages;
    }

    public Pupils withTotalPages(Integer totalPages) {
        this.mTotalPages = totalPages;
        return this;
    }

}

