package com.example.ameer.comp4985_assignmen3;

/*------------------------------------------------------------------------------------------------------------------
-- SOURCE FILE: UserInformation.java - This is a class that holds the data to be converted into a JSON object and then
--                                     send the object to server
--
-- PROGRAM: COMP4985 Assignment3 - Android
--
-- MTHODS:
-- public UserInformation(String name)
-- public void setUserLongitude(double longitude)
-- public void setUserLatitude(double latitude)
-- public void setTimeStamp(String timeStamp)
--
-- DATE: March 22, 2020
--
-- DESIGNER: Amir Kbah
--
-- PROGRAMMER: Amir Kbah
--
-- NOTES:
-- Holds info in an object that is then converted into a JSON object string to be sent to server
----------------------------------------------------------------------------------------------------------------------*/
public class UserInformation {
    private String userName;
    private double userLongitude;
    private double userLatitude;
    private String timeStamp;


    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: TCPClient
    --
    -- DATE: March 22, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: public UserInformation(String name)
    --
    -- RETURNS: none.
    --
    -- PARAMETERS:
    -- String name - the name of the user
    --
    -- NOTES:
    -- This is the only constructor of the UserInformation class
    ----------------------------------------------------------------------------------------------------------------------*/
    public UserInformation(String name){
        this.userName = name;
        userLatitude = 0;
        userLongitude = 0;
    }

    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: setUserLongitude
    --
    -- DATE: March 22, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: public void setUserLongitude(double longitude)
    --
    -- RETURNS: void.
    --
    -- PARAMETERS:
    -- double longitude - the longitude of the user phone
    --
    -- NOTES:
    -- the setter for the UserInformation longitude instance variable
    ----------------------------------------------------------------------------------------------------------------------*/
    public void setUserLongitude(double longitude){
        this.userLongitude = longitude;
    }

    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: setUserLatitude
    --
    -- DATE: March 22, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: public void setUserLatitude(double latitude)
    --
    -- RETURNS: void.
    --
    -- PARAMETERS:
    -- double latitude - the latitude of the user phone
    --
    -- NOTES:
    -- the setter for the UserInformation latitude instance variable
    ----------------------------------------------------------------------------------------------------------------------*/
    public void setUserLatitude(double latitude){
        this.userLatitude = latitude;
    }

    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: setTimeStamp
    --
    -- DATE: March 22, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: public void setTimeStamp(double latitude)
    --
    -- RETURNS: void.
    --
    -- PARAMETERS:
    -- String timeStamp- the formatted string of the time stamp in format ("hh:mm:ss a")
    --
    -- NOTES:
    -- the setter for the UserInformation timeStamp instance variable
    ----------------------------------------------------------------------------------------------------------------------*/
    public void setTimeStamp(String timeStamp){
        this.timeStamp = timeStamp;
    }

}
