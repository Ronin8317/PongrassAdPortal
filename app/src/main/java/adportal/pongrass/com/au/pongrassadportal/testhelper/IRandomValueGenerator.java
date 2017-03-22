package adportal.pongrass.com.au.pongrassadportal.testhelper;

import org.json.JSONObject;

/**
 * Created by user on 5/12/2016.
 */
public interface IRandomValueGenerator {

    public String GetRandomValue(String fieldname);

    public void SetValue(JSONObject jo_orders, String fieldname);

}
