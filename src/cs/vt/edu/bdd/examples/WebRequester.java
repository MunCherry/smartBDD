package cs.vt.edu.bdd.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WebRequester {
	
	private String url = null;
	private  String response = null;
	
	public WebRequester() {
		super();
	}
	
	public WebRequester(String url) {
		this.url = url;
	}
	
	public void sendRequest() throws IOException {
		sendRequest(url);
	}
	
	public void sendRequest(String urlString) throws IOException {
		
		response = null;
		
		URLConnection urlConnection = null;
		BufferedReader bufferedReader = null;
		
		try {
			//Send the http request here!
			URL url = new URL(urlString);
			urlConnection = url.openConnection();
			bufferedReader = new BufferedReader(
	                                new InputStreamReader(urlConnection.getInputStream()));
	        String inputLine;
			StringBuffer responseBuffer = new StringBuffer();
				 
			while ((inputLine = bufferedReader.readLine()) != null) 
				responseBuffer.append(inputLine);
			
			response = responseBuffer.toString();
			
		} finally {
			bufferedReader.close();
		}
		
	}
	
	public boolean responseContains(String matchValue) {
		return response.contains(matchValue);
	}
	
}
