package com.toetracker.fitnesstrainer;

/**
 * Created by rajmarappan on 11/11/15.
 */
public class Trainee {

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    public String mId;
    @com.google.gson.annotations.SerializedName("username")
    public String username ;
    public String password ;
    @com.google.gson.annotations.SerializedName("firstName")
    public String FirstName ;
    @com.google.gson.annotations.SerializedName("lastName")
    public String LastName ;
    @com.google.gson.annotations.SerializedName("phone")
    public String Phone ;
    @com.google.gson.annotations.SerializedName("address")
    public String Address ;
    @com.google.gson.annotations.SerializedName("email")
    public String Email ;
    public Boolean Trainer ;
}
