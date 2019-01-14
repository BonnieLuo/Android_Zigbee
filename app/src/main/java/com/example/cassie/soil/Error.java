package com.example.cassie.soil;

import android.util.Log;

/**
 * Created by Cassie on 2018/12/17.
 */

public class Error {

    private static final String TAG = "hahahahaha";
    private static boolean debug = true;

    public static void error(String msg){
        if (debug)
            Log.e(TAG,msg);
    }

}
