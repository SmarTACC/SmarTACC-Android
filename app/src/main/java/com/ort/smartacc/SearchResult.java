package com.ort.smartacc;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchResult implements Parcelable {

    int id;
    String name;
    String image;

    public SearchResult(int id, String name, String image) {
        this.name = name;
        this.image = image;
        this.id = id;
    }

    protected SearchResult(Parcel in) {
        id = in.readInt();
        name = in.readString();
        image = in.readString();
    }

    public static final Creator<SearchResult> CREATOR = new Creator<SearchResult>() {
        @Override
        public SearchResult createFromParcel(Parcel in) {
            return new SearchResult(in);
        }

        @Override
        public SearchResult[] newArray(int size) {
            return new SearchResult[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(image);
    }
}
