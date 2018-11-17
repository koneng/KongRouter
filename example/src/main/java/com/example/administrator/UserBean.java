package com.example.administrator;

import android.os.Parcel;
import android.os.Parcelable;

public class UserBean implements Parcelable {

    public int userId;

    public UserBean(int userId) {
        this.userId = userId;
    }

    protected UserBean(Parcel in) {
        userId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };
}
