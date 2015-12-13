package de.srh.srha.model;

public class dvb {
	
	/**
	 * 
	 * @param Source Der Html QUelltext den man beim aufrufen der URL
	 * https://www.dvb.de/apps/pointfinder/index?query=$HALTESTELLE$ 
	 * erhält.
	 * @return Die ID
	 */
	static String getId(String Source){

		return Source.substring(2, Source.indexOf("|"));
	}
	
	/**
	 * 
	 * @param Source Der QUelltext der beim Aufruf von 
	 * https://m.dvb.de/de/verbindungsauskunft/verbindungen.do?startid=33000312&zielid=33000112
	 * erhält
	 * @return The first URL
	 */
	static String getFirstUrlForConection(String Source){
		String buffer = Source;
		buffer = buffer.substring(buffer.indexOf("id=\"verbindungen\""));
		buffer = buffer.substring(buffer.indexOf("rel=\"external\""));
		buffer = buffer.substring(buffer.indexOf("href"));
		buffer = buffer.substring(buffer.indexOf("\"")+1, buffer.indexOf("\">"));
		
		return buffer;
	}
	
	static RoutePlan getRoutePlan(String Source){
		RoutePlan plan = new RoutePlan(null);

		plan.addStation(readStart(Source));
		Source = cutString(Source, "https://m.dvb.de/de/verbindungsauskunft/haltestelle");
		Source = cutString(Source, "</div><spa");
		
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
			String Linie = getSubstring(Source, "<strong>", "</strong>").trim().substring(5).trim();;
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
		
		Source = cutString(Source , "class=\"end");
		String Ankunftszeit = getSubstring(Source, "<strong>", "</strong>").substring(2).trim();
		Source = cutString(Source, "</strong>");
		Source = Source.substring(1);
		String Ziel = getSubstring(Source, "<strong>", "</strong>").trim();
		plan.addStation(new RouteStation(Ziel, Ankunftszeit, "", "", ""));
		return plan;
	}
	
	private static RouteStation readNext(String Source){
		
		Source = cutString(Source, "end_followthru");
		
		
		return new RouteStation(" ", " ", " ", " ", " ");
		
	}
	
	private static RouteStation readStart(String Source){
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
	private static String cutString(String Source, String subseq){
		return Source.substring(Source.indexOf(subseq));
	}
	
	private static String getSubstring(String Source, String start, String End){
		return Source.substring(Source.indexOf(start) + start.length(), Source.indexOf(End));
	}
}
