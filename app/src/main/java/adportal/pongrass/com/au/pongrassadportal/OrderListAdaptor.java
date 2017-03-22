package adportal.pongrass.com.au.pongrassadportal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 2/12/2016.
 */

public class OrderListAdaptor extends ArrayAdapter<JSONObject> {



    public OrderListAdaptor(Context context)
    {
        super(context, R.layout.item_layout,R.id.firstLine);


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // assume that the JSONObject will be the same.
        JSONObject order_obj = getItem(position);

        if (order_obj != null) {

            try {
                LayoutInflater inflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View rowView = inflator.inflate(R.layout.item_layout, parent, false);
                TextView firstline_tv = (TextView) rowView.findViewById(R.id.firstLine);
                TextView secondline_tv = (TextView) rowView.findViewById(R.id.secondLine);

                // let's try the custname
                String Name = (String) order_obj.get("name");


                firstline_tv.setText(Name);

                // get the item list..
                JSONArray itemList = (JSONArray)order_obj.get("_itemlist");

                // count the number of items
                String itemCountText = "Number of inserts is " + itemList.length();
                secondline_tv.setText(itemCountText);


                ImageView image_vw = (ImageView) rowView.findViewById(R.id.class_image);


                return rowView;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;


    }
}
