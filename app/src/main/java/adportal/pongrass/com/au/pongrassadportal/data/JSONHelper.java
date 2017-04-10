package adportal.pongrass.com.au.pongrassadportal.data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 6/04/2017.
 */

public class JSONHelper {

    public static List<String> ConvertJSONArrayToList(JSONArray ja)
    {
        List<String> result = new ArrayList<>();
        for (int i=0;i<ja.size();i++) {
            String ss = (String) ja.get(i);
            result.add(ss);
        }
        return result;
    }

    public static JSONArray ConvertListToJSONArray(List<String> list)
    {
        JSONArray ja = new JSONArray();
        for (String s : list)
        {
            ja.add(s);
        }
        return ja;
    }

    public static Map<String, String> ConvertObjectToStringMap(JSONObject object)
    {
        Map<String, String> result = new HashMap<>();
        Iterator<String> it = object.keySet().iterator();
        while (it.hasNext())
        {
            String key = it.next();
            String res = (String)object.get(key);
            if (res != null) {
                result.put(key, res);
            };
        }

        return result;
    }

    public static JSONObject ConvertStringMapToObject(Map<String, String> map)
    {
        JSONObject jo = new JSONObject();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext())
        {
            String key = it.next();
            String value = map.get(key);
            jo.put(key, value);
        }
        return jo;
    }


}
