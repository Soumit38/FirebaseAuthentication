package com.soumit.firebaseauthentication;

/**
 * Created by SOUMIT on 10/20/2017.
 */

public class Doctor {

    public String name;
    public String email;
    public String address;
    public String qualifications;
    public String verificationCode;


    public Doctor() {
    }


    public Doctor(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Doctor(String name, String email, String address,
                  String qualifications, String verificationCode) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.qualifications = qualifications;
        this.verificationCode = verificationCode;
    }
}
