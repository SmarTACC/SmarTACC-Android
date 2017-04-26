package com.ort.smartacc;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchResult implements Parcelable {

    long id;
    String name;

    //TODO eliminar imagen
    public SearchResult(long id, String name) {
        this.name = name;
        this.id = id;
    }

    protected SearchResult(Parcel in) {
        id = in.readLong();
        name = in.readString();
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
    }
}
