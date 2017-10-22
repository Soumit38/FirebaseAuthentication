package com.soumit.firebaseauthentication;

import android.content.Intent;

/**
 * Created by SOUMIT on 10/20/2017.
 */

public class Patient {

    public String name;
    public String email;
    public String  age;
    public String address;

    public Patient(){

    }

    public Patient(String name, String email){
        this.name = name;
        this.email = email;
    }

    public Patient(String name, String email, String age, String address){
        this.name = name;
        this.email = email;
        this.age = age;
        this.address = address;
    }

}
