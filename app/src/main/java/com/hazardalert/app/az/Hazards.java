package com.hazardalert.app.az;

public class Hazards {


    String pic,date,userid,reason,lat,lng,number,geoHash,status;

    public Hazards() {
    }

    public String getGeoHash() {
        return geoHash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Hazards(String pic, String date, String userid, String reason, String lat, String lng, String number) {
        this.pic = pic;
        this.date = date;
        this.userid = userid;
        this.reason = reason;
        this.lat = lat;
        this.lng = lng;
        this.number = number;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
