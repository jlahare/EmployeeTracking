package com.yash.employeetrack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PathMeasure;
import android.provider.Settings;

/**
 * Created by jayesh.lahare on 4/30/2018.
 */

public class Utils {

    public static final int THUMB_SIZE = 200;
    public static Bitmap byteBitmap = null;
    public static String base64Image = null;

    public static String getDeviceId(Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
