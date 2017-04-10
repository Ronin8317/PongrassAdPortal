package adportal.pongrass.com.au.pongrassadportal;

import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

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

    // field valdiation result is a map of fields and the reason


    public static Map<String, String> validateUserInfo(Bundle bundle) {

        Map<String, String> errors = new HashMap<>();


        if (!FieldValidation.validateEmail(bundle.getString("email", "")))
        {
            errors.put("email", "Email is not valid");
        }
        if (FieldValidation.validateName(bundle.getString("name", "")))
        {
            errors.put("name", "Name is empty");
        }

        return errors;


    }
}
