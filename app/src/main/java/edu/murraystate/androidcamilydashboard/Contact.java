package edu.murraystate.androidcamilydashboard;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

class Contact implements Serializable, Parcelable {
    private String name;
    private String phone;
    private double latitude;
    private double longitude;

    public Contact() {
    }

    public Contact(String name, String phone, double lat, double lon) {
        this.name = name;
        this.phone = phone;
        this.latitude = lat;
        this.longitude = lon;
    }

    protected Contact(Parcel in) {
        name = in.readString();
        phone = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String toString() {
        return this.getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
