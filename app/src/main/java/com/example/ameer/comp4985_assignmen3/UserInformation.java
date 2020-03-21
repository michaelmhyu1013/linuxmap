package com.example.ameer.comp4985_assignmen3;


public class UserInformation {
    private String userName;
    private double userLongitude;
    private double userLatitude;
    private String timeStamp;

    public UserInformation(String name){
        this.userName = name;
        userLatitude = 0;
        userLongitude = 0;
    }

    public void setUserLongitude(double longitude){
        this.userLongitude = longitude;
    }

    public void setUserLatitude(double latitude){
        this.userLatitude = latitude;
    }

    public void setTimeStamp(String timeStamp){
        this.timeStamp = timeStamp;
    }

}
