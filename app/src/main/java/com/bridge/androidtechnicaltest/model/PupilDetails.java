package com.bridge.androidtechnicaltest.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/* Data model for Pupil */
public class PupilDetails implements Parcelable {

    @SerializedName("pupilId")
    private Integer mPupilId;

    @SerializedName("country")
    private String mCountry;

    @SerializedName("name")
    private String mName;

    @SerializedName("image")
    private String mImage;

    @SerializedName("latitude")
    private Double mLatitude;

    @SerializedName("longitude")
    private Double mLongitude;

    public PupilDetails() {

    }

    protected PupilDetails(Parcel in) {
        if (in.readByte() == 0) {
            mPupilId = null;
        } else {
            mPupilId = in.readInt();
        }
        mCountry = in.readString();
        mName = in.readString();
        mImage = in.readString();
        if (in.readByte() == 0) {
            mLatitude = null;
        } else {
            mLatitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            mLongitude = null;
        } else {
            mLongitude = in.readDouble();
        }
    }

    public static final Creator<PupilDetails> CREATOR = new Creator<PupilDetails>() {
        @Override
        public PupilDetails createFromParcel(Parcel in) {
            return new PupilDetails(in);
        }

        @Override
        public PupilDetails[] newArray(int size) {
            return new PupilDetails[size];
        }
    };

    public Integer getPupilId() {
        return mPupilId;
    }

    public void setPupilId(Integer pupilId) {
        this.mPupilId = pupilId;
    }

    public PupilDetails withPupilId(Integer pupilId) {
        this.mPupilId = pupilId;
        return this;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        this.mCountry = country;
    }

    public PupilDetails withCountry(String country) {
        this.mCountry = country;
        return this;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public PupilDetails withName(String name) {
        this.mName = name;
        return this;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        this.mImage = image;
    }

    public PupilDetails withImage(String image) {
        this.mImage = image;
        return this;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Double latitude) {
        this.mLatitude = latitude;
    }

    public PupilDetails withLatitude(Double latitude) {
        this.mLatitude = latitude;
        return this;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(Double longitude) {
        this.mLongitude = longitude;
    }

    public PupilDetails withLongitude(Double longitude) {
        this.mLongitude = longitude;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (mPupilId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(mPupilId);
        }
        parcel.writeString(mCountry);
        parcel.writeString(mName);
        parcel.writeString(mImage);
        if (mLatitude == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(mLatitude);
        }
        if (mLongitude == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(mLongitude);
        }
    }
}

