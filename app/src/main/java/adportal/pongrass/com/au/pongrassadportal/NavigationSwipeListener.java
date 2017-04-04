package adportal.pongrass.com.au.pongrassadportal;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by user on 31/03/2017.
 */

public class NavigationSwipeListener extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    // the mode..
    private static boolean _captureMode = false;
    private static String TAG = "NavigationGesture";

    private SwipeListener _SwipeActionCallback = null;

    public NavigationSwipeListener(SwipeListener listener)
    {
        _SwipeActionCallback = listener;
    }


    public void setCaptureMode(boolean capture)
    {
        _captureMode = capture;
    }

    public void setSwipeListener(SwipeListener listener)
    {
        _SwipeActionCallback = listener;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;

        if ((!_captureMode) || (_SwipeActionCallback == null))
            return super.onFling(e1, e2, velocityX, velocityY); // if not capturing, ignore

        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();

            boolean isDown = (diffY >= 0);
            boolean isLeft = (diffX >= 0);


            if (Math.abs(diffY) > Math.abs(diffX)) {
                if ((Math.abs(diffY) > SWIPE_THRESHOLD) && (Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD)) {
                    if (isDown)
                    {
                        _SwipeActionCallback.onSwipeDown();
                    }
                    else {
                        _SwipeActionCallback.onSwipeUp();
                    }

                    result = true;
                }
            }
            else {
                if ((Math.abs(diffX) > SWIPE_THRESHOLD) && (Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD)) {
                    if (!isLeft)
                    {
                        _SwipeActionCallback.onSwipeRight();
                    }
                    else {
                        _SwipeActionCallback.onSwipeLeft();
                    }
                    result = true;
                }
            }

        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

        return result;
    }


}
