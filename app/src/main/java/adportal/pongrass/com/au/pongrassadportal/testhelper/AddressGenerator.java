package adportal.pongrass.com.au.pongrassadportal.testhelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 5/12/2016.
 */

public class AddressGenerator implements IRandomValueGenerator {



    @Override
    public String GetRandomValue(String fieldname) {
        return null;
    }

    @Override
    public void SetValue(JSONObject jo_orders, String fieldname) {
        // set it...
        try {
            jo_orders.put("address1", "Unit 14 5-11 Hollywood Avenue");
            jo_orders.put("cityname", "Bondi Junction");
            jo_orders.put("state", "NSW");
            jo_orders.put("postcode","2002");
            jo_orders.put("phoneprefix01", "02");
            jo_orders.put("phone01","93696108");

        }
        catch (JSONException je)
        {
            je.printStackTrace();
        }


    }
}
