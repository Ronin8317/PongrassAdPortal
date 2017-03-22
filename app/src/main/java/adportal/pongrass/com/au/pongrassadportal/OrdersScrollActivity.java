package adportal.pongrass.com.au.pongrassadportal;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OrdersScrollActivity extends FragmentActivity {

    private static final int NUM_PAGES = 5;

    private ViewPager mPager;
    private PagerAdapter mPagerAdaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_scroll);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdaptor = new DemoCollectionPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdaptor);

    }

    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new DemoObjectFragment();
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DemoObjectFragment extends Fragment {

        public static final String ARG_OBJECT = "object";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.order_scroll_view, container, false);
            Bundle args = getArguments();
            TextView tv = (TextView) rootView.findViewById(R.id.text_view_id);
            if (tv != null) {
                String fullText = Integer.toString(args.getInt(ARG_OBJECT));
                fullText += getString(R.string.random_long_string);
                tv.setText(
                        fullText);
            };
            return rootView;
        }
    }

}



