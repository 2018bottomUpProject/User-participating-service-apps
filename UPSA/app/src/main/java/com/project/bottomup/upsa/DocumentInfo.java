package com.project.bottomup.upsa;

import android.os.Parcel;
import android.os.Parcelable;

public class DocumentInfo implements Parcelable{
    private int placeId;
    private String placeName;

    public DocumentInfo(Parcel in){
    }
    public DocumentInfo(int placeId, String placeName){
        this.placeId = placeId;
        this.placeName = placeName;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public static final Creator<DocumentInfo> CREATOR = new Creator<DocumentInfo>() {
        @Override
        public DocumentInfo createFromParcel(Parcel in) {
            return new DocumentInfo(in);
        }

        @Override
        public DocumentInfo[] newArray(int size) {
            return new DocumentInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(placeId);
        dest.writeString(placeName);
    }
}
