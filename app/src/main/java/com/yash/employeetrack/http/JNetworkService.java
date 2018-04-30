package com.yash.employeetrack.http;


import android.util.Log;
import android.util.Pair;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jayesh.lahare on 4/30/2018.
 */

public class JNetworkService
{

    public static final String INTERNAL_NETWORK_ERROR = "Internal Server Error.";


    public static Pair<String , String> sendHttpPost(String urlString, HashMap<String, String> postParamaters)
    {
        StringBuffer dataReceived = new StringBuffer();

        if (postParamaters == null)
            throw new IllegalArgumentException("Post parameters can not be null");

        try
        {
            URL url = new URL(urlString);


            URLConnection connection = url.openConnection();

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "application/json");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream writer = conn.getOutputStream();
            //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            writer.write(getPostString(postParamaters).getBytes());
            writer.flush();
            writer.close();

            conn.connect();


            int responseCode = conn.getResponseCode();
            String responseMsg = conn.getResponseMessage();


            if(responseCode !=200)
            {
                if(responseMsg.length()>0)
                    return Pair.create(JNetworkConstants.NETWORK_ERROR, responseMsg);
                else
                    return Pair.create(JNetworkConstants.NETWORK_ERROR, INTERNAL_NETWORK_ERROR  );

            }


            BufferedInputStream bi = new BufferedInputStream(conn.getInputStream());

            byte[] buffer = new byte[1024];
            int ch;

            while ((ch = bi.read(buffer)) != -1)
            {
                dataReceived.append(new String(buffer, 0, ch));
            }


            bi.close();
            conn.disconnect();


            return Pair.create(JNetworkConstants.NETWORK_SUCCESS, dataReceived.toString());



        } catch (Throwable t)
        {
            Log.e("Error ", " = " + t.toString());
            t.printStackTrace();
            return Pair.create(JNetworkConstants.NETWORK_ERROR, t.toString());
        }
    }


    private static String getPostString(HashMap<String, String> paramaters) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();

        if (paramaters != null)
        {
            int i = 0;

            for (Map.Entry<String, String> entry : paramaters.entrySet())
            {
                String key = entry.getKey();
                String value = entry.getValue();

                if (i == 0)
                {
                    i++;

                } else
                {
                    result.append("&");
                }

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value, "UTF-8"));


            }
        }


        return result.toString();
    }


    private static String getQueryString(HashMap<String, String> paramaters) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();

        if (paramaters != null)
        {

            //Set keys = this.paramaters.keySet();

            int i = 0;

            for (Map.Entry<String, String> entry : paramaters.entrySet())
            {
                String key = entry.getKey();
                String value = entry.getValue();

                if (i == 0)
                {
                    result.append("?" + key + "=" + value);
                    i++;
                } else
                {
                    result.append("&" + key + "=" + value);
                }
            }
        }
        return result.toString();
    }


    /**
     * Method to call url with JSON request, with or without parameters.
     *
     * @param urlString  the url
     * @param jsonData   the JSON string as a request, can be null.
     * @return the pair,  the response in Pair format.
     * @see android.util.Pair
     */
    public static Pair<String , String> sendHttpPostJSON(String urlString, String jsonData)
    {
        StringBuffer dataReceived = new StringBuffer();

        if (jsonData == null)
            throw new IllegalArgumentException("Json data can not be null");

        //throw new Exception("Post parameters can not be null");

        try
        {

            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");

            // conn.setRequestProperty("Connection" , "keep-alive");
            //conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream writer = conn.getOutputStream();

            writer.write(jsonData.getBytes());
            writer.flush();
            writer.close();

            //conn.connect();

            int responseCode = conn.getResponseCode();
            String responseMsg = conn.getResponseMessage();


            if(responseCode !=200)
            {
                if(responseMsg.length()>0)
                    return Pair.create(JNetworkConstants.NETWORK_ERROR, responseMsg);
                else
                    return Pair.create(JNetworkConstants.NETWORK_ERROR, INTERNAL_NETWORK_ERROR  );

            }

            BufferedInputStream bi = new BufferedInputStream(conn.getInputStream());

            byte[] buffer = new byte[1024];
            int ch;

            while ((ch = bi.read(buffer)) != -1)
            {
                dataReceived.append(new String(buffer, 0, ch));
            }


            bi.close();
            conn.disconnect();

            return Pair.create(JNetworkConstants.NETWORK_SUCCESS, dataReceived.toString());

        } catch (Throwable t)
        {
            t.printStackTrace();
            return Pair.create(JNetworkConstants.NETWORK_ERROR, t.toString()  );
        }
    }


    /**
     * Method to call DELETE method, with or without parameters.
     *
     * @param url  the url
     * @param deleteParamaters   the parameters need to send, can be null.
     * @return the pair,  the response in Pair format.
     * @see android.util.Pair
     */
    public static Pair<String, String> sendHttpDelete(String url, HashMap<String, String> deleteParamaters)
    {
        StringBuffer dataReceived = new StringBuffer();
        try
        {
            String finalUrl = url;

            if (deleteParamaters != null)
                finalUrl = finalUrl + getQueryString(deleteParamaters);

            URL urlLink = new URL(finalUrl);

            HttpURLConnection connection = (HttpURLConnection) urlLink.openConnection();

            //connection.setUseCaches(true);
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("DELETE");

            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");

            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);


            int responseCode = connection.getResponseCode();
            String responseMsg = connection.getResponseMessage();


            if(responseCode !=200)
            {
                if(responseMsg.length()>0)
                    return Pair.create(JNetworkConstants.NETWORK_ERROR, responseMsg);
                else
                    return Pair.create(JNetworkConstants.NETWORK_ERROR, INTERNAL_NETWORK_ERROR  );

            }

            BufferedInputStream bi = new BufferedInputStream(connection.getInputStream());

            byte[] buffer = new byte[1024];
            int ch;

            while ((ch = bi.read(buffer)) != -1)
                dataReceived.append(new String(buffer, 0, ch));

            bi.close();
            connection.disconnect();

            return Pair.create(JNetworkConstants.NETWORK_SUCCESS, dataReceived.toString());

        } catch (Throwable t)
        {
            t.printStackTrace();
            return Pair.create(JNetworkConstants.NETWORK_ERROR, t.toString());

        }
    }

}
