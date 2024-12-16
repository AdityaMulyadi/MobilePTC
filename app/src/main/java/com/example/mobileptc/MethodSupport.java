package com.example.mobileptc;

public class MethodSupport {

    public static String potongEmail(String email) {
        int indexAt = email.indexOf("@");
        if (indexAt != -1) {
            return  email.substring(0, indexAt);
        } else {
            return "Alamat email salah";
        }
    }
}
