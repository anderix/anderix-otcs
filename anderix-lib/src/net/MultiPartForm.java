/**
 * Copyright (c) 2003-2019  Anderix
 * All Rights Reserved
 */
package anderix.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Iterator;
import java.net.Authenticator;

public class MultiPartForm extends WebForm {

    private Hashtable fileValues = new Hashtable();

	public MultiPartForm(String actionUrl) throws MalformedURLException {
		super(actionUrl);
	}

	public MultiPartForm(String actionUrl, Authenticator auth) throws MalformedURLException {
		super(actionUrl);
		Authenticator.setDefault(auth);
	}

	public MultiPartForm(URL actionUrl) {
		super(actionUrl);
	}

	public MultiPartForm(URL actionUrl, Authenticator auth) {
		super(actionUrl);
		Authenticator.setDefault(auth);
	}

	public MultiPartForm(String actionUrl, Hashtable formValues) throws MalformedURLException {
		super(actionUrl, formValues);
	}

	public MultiPartForm(String actionUrl, Hashtable formValues, Authenticator auth) throws MalformedURLException {
		super(actionUrl, formValues);
		Authenticator.setDefault(auth);
	}

	public MultiPartForm(URL actionUrl, Hashtable formValues) {
		super(actionUrl, formValues);
	}

	public MultiPartForm(URL actionUrl, Hashtable formValues, Authenticator auth) {
		super(actionUrl, formValues);
		Authenticator.setDefault(auth);
	}

	public MultiPartForm(String actionUrl, Hashtable formValues, Hashtable fileValues) throws MalformedURLException {
		super(actionUrl, formValues);
		this.fileValues = fileValues;
	}
	
	public MultiPartForm(String actionUrl, Hashtable formValues, Hashtable fileValues, Authenticator auth) throws MalformedURLException {
		super(actionUrl, formValues);
		Authenticator.setDefault(auth);
		this.fileValues = fileValues;
	}

	public MultiPartForm(URL actionUrl, Hashtable formValues, Hashtable fileValues) {
		super(actionUrl, formValues);
		this.fileValues = fileValues;
	}

	public MultiPartForm(URL actionUrl, Hashtable formValues, Hashtable fileValues, Authenticator auth) {
		super(actionUrl, formValues);
		Authenticator.setDefault(auth);
		this.fileValues = fileValues;
	}

	public void addFormFile(String name, File value) {
	    fileValues.put(name, value);
	}
	
	public void setFormFiles(Hashtable fileValues) {
	    this.fileValues = fileValues;
	}

    
    public String post() throws IOException {
		String boundary = MultiPartFormOutputStream.createBoundary();
		URLConnection con = MultiPartFormOutputStream.createConnection(url);
		con.setRequestProperty("Accept", "*/*");
		con.setRequestProperty("Content-Type", MultiPartFormOutputStream.getContentType(boundary));
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Cache-Control", "no-cache");
		String hkey;
		String hval;
		Iterator it = headers.keySet().iterator(); 
		while ( it.hasNext() ) {
			hkey = (String)it.next();
			hval = (String)headers.get(hkey);
			con.setRequestProperty(hkey, hval);	
		}
		MultiPartFormOutputStream out = new MultiPartFormOutputStream (con.getOutputStream(), boundary);

    	String key;
    	String value;
    	
	    it = formValues.keySet().iterator(); 
	    while ( it.hasNext() ) {
	        key = (String)it.next();
	    	value = (String)formValues.get(key);
	    	out.writeField(key, value);
	    }

		File file;
		String mimetype;

	    it = fileValues.keySet().iterator(); 
	    while ( it.hasNext() ) {
	        key = (String)it.next();
	    	file = (File)fileValues.get(key);
			mimetype = URLConnection.guessContentTypeFromName(file.getName());
			out.writeFile(key, mimetype, file);
	    }
        
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


}