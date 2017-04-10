package adportal.pongrass.com.au.pongrassadportal.data;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * The interface for aggregating data
 *
 * Created by Ronin on 5/04/2017.
 */

public class FirebaseDataAggregator implements IFirebaseDataReady {

    protected List<String> _QueryPaths = new ArrayList<>();
    protected List<FirebaseData> _Results = new ArrayList<>();
    // context is saved..
    protected Context _currentContext = null;
    protected IFirebaseDataReady _callback = null;

    protected final static String TAG = "FirebaseDataAggregator";

    public FirebaseDataAggregator()
    {

    }

    public FirebaseDataAggregator AddQuery(String Path)
    {
        _QueryPaths.add(Path);
        return this;
    }

    public boolean Execute(Context context, IFirebaseDataReady callback)
    {
        // go through all the query paths..
        // call the service..
        _currentContext = context;
        _callback = callback;
        int size = _QueryPaths.size();
        if (size > 0) {
            String Q = _QueryPaths.get(size - 1);
            _QueryPaths.remove(size - 1);
            FirebaseData.FirebaseID id = new FirebaseData.FirebaseID(Q);
            FirebaseData.extractData(id, context, callback);

            return true;
        }

        // all queries are done..
        FinalizeResults();

        return false;

    }

    // may be overriden

    public void FinalizeResults()
    {
        // default is do nothing..
        if (_Results.size() == 1)
        {
            _callback.Success(_Results.get(0));
        }
        else {
            _callback.Success(_Results);
        }
    }


    @Override
    public void Success(FirebaseData data) {
        _Results.add(data);
        Execute(_currentContext, _callback);
    }

    @Override
    public void Success(List<FirebaseData> data) {
        // should not come here
        _Results = data;
    }

    @Override
    public void OnFailure(int ErrorCode, String Message) {
        Log.d(TAG, Message);
        _callback.OnFailure(ErrorCode, Message);

        //Execute(_currentContext, _callback);
    }
}
