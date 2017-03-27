package adportal.pongrass.com.au.pongrassadportal;

import android.os.Bundle;

/**
 * Created by user on 27/03/2017.
 */

public class FieldValidation {

    public static boolean isEmpty(String field)
    {
        return ((field == null) || field.isEmpty());
    }

    public static boolean notEmpty(String field)
    {
        return !isEmpty(field);
    }

    public static boolean containsChar(String field, char ch)
    {
        if (isEmpty(field)) return false;

        return (field.indexOf(ch) >= 0);
    }

    public static boolean validateEmail(String email)
    {
        boolean res = true;
        res &= containsChar(email, '@');
        res &= containsChar(email, '.');

        return res;
    }

    public static boolean validateName(String name)
    {
        boolean res = true;
        res &= notEmpty(name);

        return res;
    }


    public static boolean validateUserInfo(Bundle bundle) {


        boolean res = FieldValidation.validateEmail(bundle.getString("email", ""));
        res &= FieldValidation.validateName(bundle.getString("name", ""));
        // does not need address, or display name, or image icon
        return res;


    }
}
