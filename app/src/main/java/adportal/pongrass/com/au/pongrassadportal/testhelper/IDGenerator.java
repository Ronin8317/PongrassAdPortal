package adportal.pongrass.com.au.pongrassadportal.testhelper;

import org.json.JSONObject;

/**
 * Created by user on 5/12/2016.
 */

public class IDGenerator extends RandomeGeneratorBase implements IRandomValueGenerator {

    public static int _LastOrderID = 100000;


    @Override
    public String GetRandomValue(String fieldname) {
        String res = Integer.toString(_LastOrderID);
        _LastOrderID++;
        return res;
    }


}
