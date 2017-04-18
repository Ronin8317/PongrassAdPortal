package adportal.pongrass.com.au.pongrassadportal;

import android.content.Context;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DateTimeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DateTimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateTimeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "date_value";


    // TODO: Rename and change types of parameters
    private Date mDate;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;


    private OnFragmentInteractionListener mListener;

    public DateTimeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param date_in_milliseconds The date in milliseconds.
     *
     * @return A new instance of fragment DateTimeFragment.
     */

    public static DateTimeFragment newInstance(long date_in_milliseconds) {
        DateTimeFragment fragment = new DateTimeFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, date_in_milliseconds);

        fragment.setArguments(args);
        return fragment;
    }

    public Date getCurrentSelectedDate(boolean includeDate)
    {
        int y  = mDatePicker.getYear();
        int m = mDatePicker.getMonth();
        int d = mDatePicker.getDayOfMonth();

        Calendar cal = Calendar.getInstance();
        if (includeDate == false) {
            cal.set(y, m, d);
        }
        else {
            int hour = mTimePicker.getCurrentHour();
            int min = mTimePicker.getCurrentMinute();
            cal.set(y,m,d,hour, min, 0);
        }
        return cal.getTime();
    }

    public void showDate(boolean showDate)
    {
        mDatePicker.setVisibility(showDate ? View.VISIBLE : View.GONE);
    }

    public void showTime(boolean showTime)
    {
        mTimePicker.setVisibility(showTime ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDate = new Date(getArguments().getLong(ARG_PARAM1));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView =  inflater.inflate(R.layout.fragment_date_time, container, false);
        mDatePicker = (DatePicker)mainView.findViewById(R.id.fragmentDatePicker);
        mTimePicker = (TimePicker)mainView.findViewById(R.id.fragmentTimePicker);
        // look at the date..
        if (mDate != null)
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(mDate);
            int y = cal.get(Calendar.YEAR);
            int m = cal.get(Calendar.MONTH);
            int d = cal.get(Calendar.DAY_OF_MONTH);
            mDatePicker.updateDate(y, m, d);
            // hour
            int hour = cal.get(Calendar.HOUR);
            int min = cal.get(Calendar.MINUTE);
            int sec = cal.get(Calendar.SECOND);

            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(min);

        }


        return mainView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
