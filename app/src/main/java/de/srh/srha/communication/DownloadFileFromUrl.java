package de.srh.srha.communication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by fettpet on 13.12.15.
 */
public class DownloadFileFromUrl extends AsyncTask<String, String, String> {
    protected String Source;
    protected boolean finished;
    public String getSource(){

        waitUntilFinished();
        return Source;
    }


    public void waitUntilFinished(){
        while(!finished && !isCancelled()){

        }
    }


    /**
     * Before starting background thread Show Progress Bar Dialog
     * */
    @Override
    protected void onPreExecute() {
        Log.i("LOG", "Download Pre");
       // super.onPreExecute();
        finished = false;
        Log.i("LOG", "Download Pre finished");
    }

    /**
     * Downloading file in background thread
     * http://stackoverflow.com/questions/15758856/android-how-to-download-file-from-webserver
     * */
    @Override
    protected String doInBackground(String... f_url) {
        Log.i("LOG", "in DoInBackground DownloadFileFromUrl");
        Source = "";
        int count;
        try {
            URL url = new URL(f_url[0].replace(" ", "+"));
            URLConnection conection = url.openConnection();
            Log.i("LOG", "Download " + f_url[0]);
            conection.connect();
            Log.i("LOG", "Download COnnected" + f_url[0]);
            // this will be useful so that you can show a tipical 0-100%
            // progress bar
            int lenghtOfFile = conection.getContentLength();
            Log.i("LOG", "Download Size " + Integer.toString(lenghtOfFile));
            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);
            byte[] data = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                // writing data to file
                Source = Source.concat(new String(data).trim());
                data = new byte[1024];
            }
            Log.i("LOG", "Readout " + f_url[0]);
            finished = true;

        } catch (Exception e) {
            Log.i("Error: ", e.getMessage());
        }
        

        return null;
    }
    /**
     * Updating progress bar
     * */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
    }

    public boolean isFinished(){
        return finished;
    }
    /**
     * After completing background task Dismiss the progress dialog
     * **/
    @Override
    protected void onPostExecute(String file_url) {
        Log.i("LOG", "Download finished");
        finished = true;
    }

    protected  void onChanceled(String url){
        Log.i("LOG", "Download cannceled " + url);
    }

}