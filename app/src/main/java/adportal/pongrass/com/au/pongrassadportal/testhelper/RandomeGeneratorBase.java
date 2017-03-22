package adportal.pongrass.com.au.pongrassadportal.testhelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 5/12/2016.
 */

public abstract class RandomeGeneratorBase implements IRandomValueGenerator {

    @Override
    public void SetValue(JSONObject jo_orders, String fieldname)
    {
        try {
            jo_orders.put(fieldname, GetRandomValue(fieldname));
        }
        catch (JSONException je)
        {
            je.printStackTrace();
        }
    }
}
