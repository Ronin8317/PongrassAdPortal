package adportal.pongrass.com.au.pongrassadportal.testhelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by user on 5/12/2016.
 */

public class TestHelper {

    public static JSONArray ReturnSampleAccountOrdersResult(String AccountID, int number_of_entries)
    {
        // create the base object..
        JSONArray ja_result = new JSONArray();
        JSONObject jo_criteria = new JSONObject();
        try {
            jo_criteria.put("accountid", AccountID);



            for (int i = 0; i < number_of_entries; i++) {
                JSONObject jo = ReturnSampleOrders(jo_criteria);
                ja_result.put(i, jo);
            }
        }
        catch (JSONException e) {
          e.printStackTrace();

         }
        return ja_result;

    }

    public static boolean InsertValue(JSONObject obj, JSONObject jo_criteria, String fieldname, IRandomValueGenerator randomValueGenerator)
    {
        boolean hasValue = false;
        try {
            String org_value = (String) jo_criteria.get(fieldname);
            if ((org_value == null) || (org_value.isEmpty())) {
                obj.put(fieldname, randomValueGenerator.GetRandomValue(fieldname));
            } else {
                obj.put(fieldname, org_value);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return hasValue;
    }

    public static JSONObject ReturnSampleOrders(JSONObject jo_criteria)
    {
        JSONObject jo_order = new JSONObject();

        // site, orderid, custname,
        InsertValue(jo_order, jo_criteria, "site", new ConstantGenerator("ZZ"));
        InsertValue(jo_order, jo_criteria, "orderid", new IDGenerator());
        InsertValue(jo_order, jo_criteria, "accountid", new IDGenerator());
        InsertValue(jo_order, jo_criteria, "custname", new RandomName());
        InsertValue(jo_order, jo_criteria, "statusid", new ConstantGenerator("40"));
        InsertValue(jo_order, jo_criteria, "adtype", new ConstantGenerator("R"));
        InsertValue(jo_order, jo_criteria, "address", new AddressGenerator());
        InsertValue(jo_order, jo_criteria, "paytypeid", new ConstantGenerator("22"));







        return jo_order;
    }
}
