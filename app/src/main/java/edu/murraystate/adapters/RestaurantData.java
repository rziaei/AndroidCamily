package edu.murraystate.adapters;

public class RestaurantData {

    private long idx;
    private String restaurantName;
    private String restaurantAddr;
    private String placeId;
    private String lat;
    private String lon;

    public long getIdx() {
        return idx;
    }

    public void setIdx(long idx) {
        this.idx = idx;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantAddr() {
        return restaurantAddr;
    }

    public void setRestaurantAddr(String restaurantAddr) {
        this.restaurantAddr = restaurantAddr;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
