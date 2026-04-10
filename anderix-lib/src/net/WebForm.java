/**
 * Copyright (c) 2003-2019  Anderix
 * All Rights Reserved
 */
package anderix.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Iterator;
import java.net.Authenticator;
import java.net.HttpURLConnection;

public class WebForm {

	protected URL url;
	protected Hashtable formValues = new Hashtable();
	protected Hashtable headers = new Hashtable();
	protected String rawBody = "";
	protected String response = "";

	public WebForm(String actionUrl) throws MalformedURLException {
		this.url = new URL(actionUrl);
	}

	public WebForm(URL actionUrl) {
		this.url = actionUrl;
	}

	public WebForm(String actionUrl, Authenticator auth) throws MalformedURLException {
		Authenticator.setDefault(auth);
		this.url = new URL(actionUrl);
	}
	
	public WebForm(URL actionUrl, Authenticator auth) {
		Authenticator.setDefault(auth);
		this.url = actionUrl;
	}

	public WebForm(String actionUrl, String rawBody) throws MalformedURLException {
		this.url = new URL(actionUrl);
		this.rawBody = rawBody;
	}
	
	public WebForm(String actionUrl, String rawBody, Authenticator auth) throws MalformedURLException {
		Authenticator.setDefault(auth);
		this.url = new URL(actionUrl);
		this.rawBody = rawBody;
	}

	public WebForm(String actionUrl, Hashtable formValues) throws MalformedURLException {
		this.url = new URL(actionUrl);
		this.formValues = formValues;
	}
	
	public WebForm(String actionUrl, Hashtable formValues, Authenticator auth) throws MalformedURLException {
		Authenticator.setDefault(auth);
		this.url = new URL(actionUrl);
		this.formValues = formValues;
	}

	public WebForm(URL actionUrl, Hashtable formValues) {
		this.url = actionUrl;
		this.formValues = formValues;
	}

	public WebForm(URL actionUrl, Hashtable formValues, Authenticator auth) {
		Authenticator.setDefault(auth);
		this.url = actionUrl;
		this.formValues = formValues;
	}

	public WebForm(URL actionUrl, String rawBody) {
		this.url = actionUrl;
		this.rawBody = rawBody;
	}

	public WebForm(URL actionUrl, String rawBody, Authenticator auth) {
		Authenticator.setDefault(auth);
		this.url = actionUrl;
		this.rawBody = rawBody;
	}

	public void setAuthenticator(Authenticator auth) {
	    Authenticator.setDefault(auth);
	}

	public void setAction(String url) throws MalformedURLException {
	    this.url = new URL(url);
	}

	public void addFormValue(String name, String value) {
	    formValues.put(name, value);
	}
	
	public void setFormValues(Hashtable formValues) {
	    this.formValues = formValues;
	}

	public void addHeader(String name, String value) {
	    headers.put(name, value);
	}
	
	public void setHeaders(Hashtable headers) {
	    this.headers = headers;
	}
	
	//TODO: I made this change to allow passing raw body, but didn't fully regression test the formData.
	private String requestBody() {
		String rval = "";
	    if ( rawBody.length() > 0 ) {
			rval = rawBody;
	    } else {
			rval = formData();
		}
		return rval;
	}
	
	private String formData() {
		StringBuffer data = new StringBuffer();
		try {
			String key;
			String value;
			boolean first = true;
			Iterator it = formValues.keySet().iterator(); 
			while ( it.hasNext() ) {
				if ( !first ) {
					data.append("&"); 
				} else {
					first = false;
				}
				key = (String)it.next();
				data.append(URLEncoder.encode(key, "UTF-8"));
				data.append("=");
				value = (String)formValues.get(key);
				data.append(URLEncoder.encode(value, "UTF-8"));	
			}
		} catch ( UnsupportedEncodingException ueex ) {
			throw new RuntimeException(ueex.getMessage());
		}
		return data.toString().trim();	
	}

	public String delete() throws IOException {
	    HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("DELETE");
		String hkey;
		String hval;
		Iterator it = headers.keySet().iterator(); 
		while ( it.hasNext() ) {
			hkey = (String)it.next();
			hval = (String)headers.get(hkey);
			con.setRequestProperty(hkey, hval);	
		}
		
		//BUG? .delete() currently does nothing with formData(). I'm not sure if it should be able to.
		
	    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())); // Should I use "UTF-8"?
	    StringBuffer resp = new StringBuffer();
	    String line;
	    while ( (line = in.readLine()) != null ) {
	        resp.append(line);
	        resp.append("\r\n");
	    }
	    in.close();
	    this.response = resp.toString().trim();
	    return response;
	}
	
	public String put() throws IOException {
	    HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("PUT");
		String hkey;
		String hval;
		Iterator it = headers.keySet().iterator(); 
		while ( it.hasNext() ) {
			hkey = (String)it.next();
			hval = (String)headers.get(hkey);
			con.setRequestProperty(hkey, hval);	
		}
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	    con.setDoOutput(true);
	    OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
	    out.write(requestBody());
	    out.flush();
		out.close();
		
	    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())); // Should I use "UTF-8"?
	    StringBuffer resp = new StringBuffer();
	    String line;
	    while ( (line = in.readLine()) != null ) {
	        resp.append(line);
	        resp.append("\r\n");
	    }
	    in.close();
	    this.response = resp.toString().trim();
	    return response;
	}
	
	public String post() throws IOException {
	    URLConnection con = url.openConnection();
		String hkey;
		String hval;
		Iterator it = headers.keySet().iterator(); 
		while ( it.hasNext() ) {
			hkey = (String)it.next();
			hval = (String)headers.get(hkey);
			con.setRequestProperty(hkey, hval);	
		}
	    con.setDoOutput(true);
	    OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
	    out.write(requestBody());
	    out.flush();
		out.close();
		
	    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())); // Should I use "UTF-8"?
	    StringBuffer resp = new StringBuffer();
	    String line;
	    while ( (line = in.readLine()) != null ) {
	        resp.append(line);
	        resp.append("\r\n");
	    }
	    in.close();
	    this.response = resp.toString().trim();
	    return response;
	}

	public String get() throws IOException {
		URL urlGet = new URL(url.toString() + "?" + formData());
	    URLConnection  con = urlGet.openConnection();
		String hkey;
		String hval;
		Iterator it = headers.keySet().iterator(); 
		while ( it.hasNext() ) {
			hkey = (String)it.next();
			hval = (String)headers.get(hkey);
			con.setRequestProperty(hkey, hval);	
		}
		
	    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())); // Should I use "UTF-8"?
	    StringBuffer resp = new StringBuffer();
	    String line;
	    while ( (line = in.readLine()) != null ) {
	        resp.append(line);
	        resp.append("\r\n");
	    }
	    in.close();
	    this.response = resp.toString().trim();
	    return response;
	}

	public void getFile(String filepath) throws IOException {
		URL urlGet = new URL(url.toString() + "?" + formData());
	    URLConnection  con = urlGet.openConnection();
		String hkey;
		String hval;
		Iterator it = headers.keySet().iterator(); 
		while ( it.hasNext() ) {
			hkey = (String)it.next();
			hval = (String)headers.get(hkey);
			con.setRequestProperty(hkey, hval);	
		}
		InputStream input = con.getInputStream();
		byte[] buffer = new byte[4096];
		int n;

		OutputStream output = new FileOutputStream(filepath);
		while ( (n = input.read(buffer)) != -1 ) {
			output.write(buffer, 0, n);
		}
		output.close();		
	}	
	
	/*
	public String get() throws IOException {
        URL urlGet = new URL(url.toString() + "?" + formData());
        BufferedReader in = new BufferedReader(new InputStreamReader(urlGet.openStream()));
        String line = "";
        StringBuffer resp = new StringBuffer();
        while ((line = in.readLine()) != null) {
			resp.append(line);
			resp.append("\r\n");
        }
        in.close();
        this.response = resp.toString().trim();
        return response;
	}
	*/
	public String getResponse() {
	    return response;
	}
}