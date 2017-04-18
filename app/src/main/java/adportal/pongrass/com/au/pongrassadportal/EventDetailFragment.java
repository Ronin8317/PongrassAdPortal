package adportal.pongrass.com.au.pongrassadportal;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import adportal.pongrass.com.au.pongrassadportal.data.Events;

/**
 * A fragment representing a single Events detail screen.
 * This fragment is either contained in a {@link EventListActivity}
 * in two-pane mode (on tablets) or a {@link EventDetailActivity}
 * on handsets.
 */
public class EventDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    // get the various text views and map views
    public EditText mEventTitle;

    public EditText mEventDetails;

    public MapView mMapView;
    protected EditText mAddressField;

    protected Button mDeleteButton;
    protected Button mSaveButton;
    protected DateTimeFragment mStartFragment;
    protected DateTimeFragment mStopFragment;

    protected GoogleMap mGMap;

    /**
     * The dummy content this fragment is presenting.
     */
    private Events.EventItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the map view..
        Activity activity = this.getActivity();




        if (getArguments().containsKey(ARG_ITEM_ID)) {



            mItem = Events.getItemByID(getArguments().getString(ARG_ITEM_ID));


            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getDisplayString());
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMapView != null) {
            mMapView.onStart();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
       if (mMapView != null) {
           mMapView.onResume();
       }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMapView != null) {
            mMapView.onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_detail, container, false);

        Activity activity = this.getActivity();
        MapsInitializer.initialize(activity);
        mMapView = (MapView)rootView.findViewById(R.id.mapEventView);
        mAddressField = (EditText)rootView.findViewById(R.id.editEventLocation);


        mSaveButton = (Button)rootView.findViewById(R.id.eventSaveButton);
        mDeleteButton = (Button)rootView.findViewById(R.id.eventDeleteButton);
        mStartFragment = (DateTimeFragment)getFragmentManager().findFragmentById(R.id.start_fragment);
        mStopFragment = (DateTimeFragment)getFragmentManager().findFragmentById(R.id.stop_fragment);






        mMapView.onCreate(savedInstanceState);


        // set the address
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                try {
                    googleMap.setMyLocationEnabled(true);
                    mGMap = googleMap;
                    // move the camera
                    Activity f_activity = getActivity();
                    LocationManager lm = (LocationManager) f_activity.getSystemService(Context.LOCATION_SERVICE);
                    if (lm != null) {



                        Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        LatLng currentLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
                        CameraUpdate camera = CameraUpdateFactory.newLatLng(currentLocation);

                        googleMap.moveCamera(camera);
                        // zoom in
                        camera = CameraUpdateFactory.zoomTo(15);
                        googleMap.animateCamera(camera);

                    }
                }
                catch (SecurityException se)
                {
                    se.printStackTrace();

                }

            }
        });



        return rootView;
    }

    protected void MapItemToScreen()
    {
        Bundle bundle = mItem.getBundle();
        Bundle location = bundle.getBundle(Events.EVENT_LOCATION);
        // map the location
        if (location != null)
        {
            if (mGMap != null) {
                double lat = location.getDouble(Events.EVENT_LATITUDE, 0.0);
                double lng = location.getDouble(Events.EVENT_LONGITUDE, 0.0);
                LatLng latlng = new LatLng(lat, lng);
            };



        }

        mAddressField.setText(bundle.getString(Events.EVENT_ADDRESS, ""));
        mEventDetails.setText(bundle.getString(Events.EVENT_DESCRIPTION));
        mEventTitle.setText(bundle.getString(Events.EVENT_TITLE));


    }

    protected Events.EventItem MapScreenToItem()
    {
        return mItem;

    }


}
