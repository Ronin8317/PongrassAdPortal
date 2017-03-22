package adportal.pongrass.com.au.pongrassadportal;

import java.security.SecureRandom;

/**
 *
 * Helper class to create a random password, using a static singleton SecureRandom
 * Created by user on 15/03/2017.
 *
 * @see java.security.SecureRandom
 */

public class RandomPasswordGenerator {

    // have a singleon random
    protected SecureRandom _singleton_Random = new SecureRandom();
    protected static char[] _alpha = null;
    protected static char[] _digit = null;
    protected static char[] _alphanumeric = null;
    protected static char[] _uppercase = null;
    protected static char[] _lowercase = null;
    protected static char[] _printable = null;


    // the static init..
    static {
        StringBuilder tmp_digit = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch)
            tmp_digit.append(ch);

        StringBuilder tmp_lowercase = new StringBuilder();
        for (char ch = 'a'; ch <= 'z'; ++ch)
        {
            tmp_lowercase.append(ch);
        }

        StringBuilder tmp_uppercase = new StringBuilder();
        for (char ch = 'A'; ch <= 'Z'; ++ch)
        {
            tmp_uppercase.append(ch);
        }

        StringBuilder tmp_printable = new StringBuilder();
        for (int i=32;i<127;i++)
        {
            tmp_printable.append((char) i);
        };

        _digit = tmp_digit.toString().toCharArray();
        _alpha = (tmp_lowercase.toString() + tmp_uppercase.toString()).toCharArray();
        _alphanumeric = (tmp_lowercase.toString() + tmp_uppercase.toString() + tmp_digit.toString()).toCharArray();
        _lowercase = tmp_lowercase.toString().toCharArray();
        _uppercase = tmp_uppercase.toString().toCharArray();
        _printable = tmp_printable.toString().toCharArray();



    }

    protected String _Password = "";

    public RandomPasswordGenerator()
    {

    }

    protected RandomPasswordGenerator doRandom(int password_length, char[] symbols)
    {
        if (password_length < 1)
        {
            return this;
        }
        char[] buf =  new char[password_length];
        for (int idx=0;idx<buf.length;idx++) {
            buf[idx] = symbols[_singleton_Random.nextInt(symbols.length)];
        }
        _Password += buf.toString();
        return this;

    }

    public RandomPasswordGenerator Alpha(int password_length)
    {
        return doRandom(password_length, _alpha);
    }

    public RandomPasswordGenerator Digit(int password_length)
    {
        return doRandom(password_length, _digit);
    }

    public RandomPasswordGenerator AlphaNumeric(int password_length)
    {
        return doRandom(password_length, _alphanumeric);
    }
    public RandomPasswordGenerator UpperCase(int password_length)
    {
        return doRandom(password_length, _uppercase);
    }

    public RandomPasswordGenerator LowerCase(int password_length)
    {
        return doRandom(password_length, _lowercase);
    }

    public RandomPasswordGenerator Printable(int password_length)
    {
        return doRandom(password_length, _printable);
    }

    public String value()
    {
        return _Password;
    }

    public String DefaultPassword()
    {
        return UpperCase(1).Alpha(7).Digit(4).value();
    }
}
