package com.uides.buyanywhere.utils;

/**
 * Created by TranThanhTung on 24/11/2017.
 */

public class GenderUtils {

    public static String getGenderVi(String gender) {
        if(gender != null) {
            switch (gender.toLowerCase()) {
                case "male": {
                    return "Nam";
                }

                case "female": {
                    return "Nữ";
                }

                default:{
                    break;
                }
            }
        }
        return "Không";
    }

    public static String getGenderEn(String gender) {
        if(gender != null) {
            switch (gender.toLowerCase()) {
                case "nam": {
                    return "Male";
                }

                case "nữ": {
                    return "Female";
                }

                default:{
                    break;
                }
            }
        }
        return null;
    }
}
