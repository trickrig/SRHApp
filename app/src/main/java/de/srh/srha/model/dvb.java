package de.srh.srha.model;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import de.srh.srha.communication.DownloadFileFromUrl;

/***
 *  This file
 *
 *
 */
public class dvb {

	/**
	 * 
	 * @param Stationname Der Name der Station
	 * https://www.dvb.de/apps/pointfinder/index?query=$HALTESTELLE$ 
	 * erhält.
	 * @return Die ID
	 */
	 public String getIdFromName(String Stationname){
        String Source = getSource("https://www.dvb.de/apps/pointfinder/index?query=" + Stationname);

        if(Source.length() > 2)
		     return Source.substring(2, Source.indexOf("|"));
	    else

             return "ERROR STATION NOT FOUND";

    //*/
    }

    public RoutePlan getRoute(String StartID, String FinishID){
        String Source = "";
        Log.i("RoutePlan", "https://m.dvb.de/de/verbindungsauskunft/verbindungen.do?startid=" + StartID + "&zielid=" + FinishID);
        Source =getSource("https://m.dvb.de/de/verbindungsauskunft/verbindungen.do?startid=" + StartID + "&zielid=" + FinishID);
        Log.i("RoutePlan", "Founded Plan: ");
        String Url = getFirstUrlForConection(Source);
        Log.i("RoutePlan", "Founded Plan: " + Url);
        Source = getSource("https://m.dvb.de" + Url);
        return getRoutePlan(Source);
    }


	/**
	 * 
	 * @param Source Der QUelltext der beim Aufruf von 
	 * https://m.dvb.de/de/verbindungsauskunft/verbindungen.do?startid=33000312&zielid=33000112
	 * erhält
	 * @return The first URL
	 */
	 private String getFirstUrlForConection(String Source){
		String buffer = Source;
		buffer = buffer.substring(buffer.indexOf("id=\"verbindungen\""));
		buffer = buffer.substring(buffer.indexOf("rel=\"external\""));
		buffer = buffer.substring(buffer.indexOf("href"));
		buffer = buffer.substring(buffer.indexOf("\"") + 1, buffer.indexOf("\">"));
		
		return buffer;
	}
	
	 RoutePlan getRoutePlan(String Source){
		RoutePlan plan = new RoutePlan(null);

		plan.addStation(readStart(Source));
		Source = cutString(Source, "verbindungsauskunft/haltestelle");
		Source = cutString(Source, "</div>");
		
		while(Source.indexOf("start_follow") > 0){
			Source = cutString(Source, "end_follow");
// Ankunftszeit
			String Ankunftszeit = getSubstring(Source, "<strong>", "</strong>").substring(2).trim();
			Source = cutString(Source, "</strong>");
			Source = Source.substring(1);
// Ankunftsplatz
			String AnkunftsOrt = getSubstring(Source, "<strong>", "</strong>");
			
// Weiterfahrtszeit
			Source = cutString(Source, "start_follow");
			Source = Source.substring(1);
			String Linie = getSubstring(Source, "<strong>", "</strong>").trim().substring(5).trim();
			Source = cutString(Source, "</strong>");
			Source = Source.substring(1);
// Abfahrtzeit
			String Abfahrtszeit = getSubstring(Source, "<strong>", "</strong>").substring(2).trim();
			Source = cutString(Source, "</strong>");
			Source = Source.substring(1);
// Abfahrtsstation
			String AbfahrtsStation = getSubstring(Source, "<strong>", "</strong>");
			
			RouteStation test = new RouteStation(AbfahrtsStation, Ankunftszeit, Abfahrtszeit, "", Linie);
			plan.addStation(test);

			
		}
		
		Source = cutString(Source, "class=\"end");
		String Ankunftszeit = getSubstring(Source, "<strong>", "</strong>").substring(2).trim();
		Source = cutString(Source, "</strong>");
		Source = Source.substring(1);
		String Ziel = getSubstring(Source, "<strong>", "</strong>").trim();
		plan.addStation(new RouteStation(Ziel, "", Ankunftszeit, "", ""));
		return plan;
	}

	
	private RouteStation readStart(String Source){
		Source = Source.substring(Source.indexOf("class=\"start "));
		String Linie = getSubstring(Source, "<strong>", "</strong>");
		

		
		Source =cutString(Source, "</strong>");
		Source =cutString(Source, "<strong>");
		
		String StartZeit = getSubstring(Source, "<strong>", "</strong>");

		
		Source = cutString(Source, "/strong>");
		
		String StationName = getSubstring(Source, "<strong>", "</strong>");
		
		Linie = Linie.trim().substring(5).trim();
		StartZeit = StartZeit.substring(2).trim();
		
		return new RouteStation(StationName, "", StartZeit, "", Linie);
	}

/**************************************************************************************************
 * 							Helpfunctions
 * ************************************************************************************************/
	private String cutString(String Source, String subseq){
		return Source.substring(Source.indexOf(subseq));
	}
	
	private String getSubstring(String Source, String start, String End){
		return Source.substring(Source.indexOf(start) + start.length(), Source.indexOf(End));
	}

    private String getSource(String URL){
		Log.i("DVB", "StartDownloa d" + URL);
        Downloader down = new Downloader();
		Log.i("DVB", "Downloader create " + URL);
        down.execute(URL);
		Log.i("DVB", "Downloader started " + URL);

		Log.i("DVB", "Finished Download" + URL);
        return down.getSource();
    }

    class Downloader {
		private String Source;
		public void execute(String URL){
			Log.i("LOG", "in DoInBackground DownloadFileFromUrl");
			Source = "";
			int count;
			try {
				URL url = new URL(URL.replace(" ", "+"));
				URLConnection conection = url.openConnection();
				Log.i("LOG", "Download " + URL);
				conection.connect();
				Log.i("LOG", "Download COnnected" + URL);
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
				Log.i("LOG", "Readout " + URL);

			} catch (Exception e) {
				Log.i("Error: ", e.getMessage());
			}

		}

		public java.lang.String getSource() {
			return Source;
		}
	}
}
