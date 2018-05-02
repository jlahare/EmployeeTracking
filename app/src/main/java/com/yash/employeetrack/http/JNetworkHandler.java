package com.yash.employeetrack.http;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Pair;

import com.yash.employeetrack.R;

import java.util.HashMap;

/**
 * Created by jayesh.lahare on 4/30/2018.
 */

public class JNetworkHandler extends AsyncTask<String, String, Pair<String, String>>
{

    /**
     * The constant METHOD_GET.
     */
    public static short METHOD_GET = 1;
    /**
     * The constant METHOD_POST.
     */
    public static short METHOD_POST = 2;
    /**
     * The constant METHOD_POST_JSON.
     */
    public static short METHOD_POST_JSON = 3;
    /**
     * The constant METHOD_DELETE.
     */
    public static short METHOD_DELETE = 4;

    private Context context;
    private String url;
    private HashMap<String, String> paramaters;

    private String jsonData =null;
    private short methodType;

    private NetworkListener listener;
    private ProgressDialog progressBar;
    private boolean playSilent = false;

    public void setSilentMode(boolean silent)
    {
        this.playSilent = silent;
    }



    public JNetworkHandler(Context context, String url, HashMap<String, String> paramaters, short methodType)
    {
        this.url = url;
        this.paramaters = paramaters;
        this.context = context;
        this.methodType = methodType;

        if ((methodType != METHOD_GET) && (methodType != METHOD_POST) && (methodType != METHOD_DELETE) )
            throw new IllegalArgumentException("Method Required");
    }



    public JNetworkHandler(Context context, String url, HashMap<String, String> paramaters, short methodType, NetworkListener networkListener)
    {
        this.url = url;
        this.paramaters = paramaters;
        this.context = context;
        this.listener = networkListener;
        this.methodType = methodType;

        if ((methodType != METHOD_GET) && (methodType != METHOD_POST)  && (methodType != METHOD_DELETE) )
            throw new IllegalArgumentException("Method Required.");
    }


    /**
     * Instantiates a new Network handler.
     *
     * @param context         the context
     * @param url             the url
     * @param jsonData        the json data
     * @param networkListener the network listener
     */
    public JNetworkHandler(Context context, String url, String jsonData, NetworkListener networkListener)
    {
        this.url = url;
        this.jsonData = jsonData;
        this.context = context;
        this.listener = networkListener;
        this.methodType = METHOD_POST_JSON;
    }


    private void initView()
    {
        try
        {
            /*progressBar = new ProgressDialog(context);
            progressBar.show();
            progressBar.setCancelable(false);
            progressBar.setContentView(R.layout.network_progress);
            progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));*/

            if(playSilent == false) {
                if (progressBar == null) {
                    progressBar = new ProgressDialog(context);
                    progressBar.show();
                    progressBar.setCancelable(false);
                    progressBar.setContentView(R.layout.network_progress);
                    progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                } else
                    progressBar.show();
            }

        } catch (Throwable t)
        {
            //Util.trace("ProgressBar" , "Progress Init Issue " + t.toString());
        }

    }


    /**
     * Sets network listener.
     *
     * @param listener : A network listener interface used a callback for network response.
     * @see NetworkListener
     */
    public void setNetworkListener(NetworkListener listener)
    {
        this.listener = listener;
    }


    /**
     * On pre execute.
     */
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        initView();
    }

    /**
     * Do in background pair.
     *
     * @param strings the strings
     * @return the pair as response.
     */
    @Override
    protected Pair<String, String> doInBackground(String... strings)
    {
        Pair<String, String> pair = null;

      /*  try
        {
            Thread.sleep(200);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }*/



        if(isInternetAvailable(context))
        {
            /*if (this.methodType == METHOD_GET)
                pair = JNetworkService.sendHttpGet(url, this.paramaters);
            else*/
            if (methodType == METHOD_POST)
                pair = JNetworkService.sendHttpPost(url, this.paramaters);
            else if (this.methodType == METHOD_POST_JSON)
                pair = JNetworkService.sendHttpPostJSON(url, jsonData);
            else if (this.methodType == METHOD_DELETE)
                pair = JNetworkService.sendHttpDelete(url, this.paramaters);

        }else
        {
            pair = Pair.create(JNetworkConstants.NETWORK_INTERNET_ERROR, "Please connect with internet first." );
        }

        return pair;
    }

    /**
     * On post execute.
     *
     * @param pair the pair as response.
     */
    @Override
    protected void onPostExecute(Pair<String, String> pair)
    {
        try
        {
            if (progressBar != null)
                progressBar.dismiss();
        }catch (Throwable t)
        {
            //Util.trace("ProgressBar" , "Progress Hide Issue " + t.toString());
        }

        if (listener != null) {
            listener.onNetworkResponse(pair);


        }
    }

    private boolean isInternetAvailable(Context context)
    {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public interface NetworkListener
    {
        void onNetworkResponse(Pair<String , String> response);

    }



}