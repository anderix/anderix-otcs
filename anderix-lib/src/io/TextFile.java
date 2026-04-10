/**
 * Copyright (c) 2003-2008  Anderix
 * All Rights Reserved
 */
package anderix.io;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TextFile {
	
	private String filename = "";

	public TextFile(String filename) { 
		this.filename = filename;
		try {
        	File file = new File(filename);

	        // Create file if it does not exist
	        boolean success = file.createNewFile();
	        if (success) {
	            // File did not exist and was created
	        } else {
	            // File already exists
	        }
	     } catch ( IOException ex ) {
	     
	     }
	}

		public TextFile(File file) { 
		this.filename = file.getPath();
	}
	
	public String read() throws IOException {
		StringBuffer buf = new StringBuffer();
        BufferedReader in = new BufferedReader(new FileReader(this.filename));
        String str;
        boolean firstline = true;
        while ((str = in.readLine()) != null) {
            if ( firstline ) {
            	firstline = false;
            } else {
		buf.append("\n");
            }
            buf.append(str);
        }
        in.close();
        return buf.toString();
	}


	public String[] readln() throws IOException {
		ArrayList list = new ArrayList();
        BufferedReader in = new BufferedReader(new FileReader(this.filename));
        String str;
        while ((str = in.readLine()) != null) {
            list.add(str);
        }
        in.close();
        return (String[])list.toArray(new String[list.size()]);
	}

	public HashMap readNameValuePairs() throws IOException {
		HashMap hash = new HashMap();
        BufferedReader in = new BufferedReader(new FileReader(this.filename));
        String str;
        while ((str = in.readLine()) != null) {
        	String[] pair = str.split("=");
            hash.put(pair[0].trim(), pair[1].trim());
        }
        in.close();
        return hash;
	}

	public void write(String str) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(this.filename));
        out.write(str);
        out.close();
	}

	public void append(String str) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(this.filename, true));
        out.write(str);
        out.close();
	}

	public void appendEcho(String str) throws IOException {
		append(str);
		System.out.print(str);
	}
	
	public void appendln(String str) throws IOException {
		if ( new File(this.filename).length() > 0 ) {
			append("\n" + str);
		} else {
			append(str);
		}
	}

	public void appendlnEcho(String str) throws IOException {
		appendln(str);
		System.out.println(str);
	}

	
	public File toFile() {
		return new File(filename);
	}

	public void removeCRLF() throws IOException {
		write(read());
	}


	public void replaceAll(String regex, String replacement) throws IOException {
		String[] lines = readln();
		for ( int i = 0; i < lines.length; i++ ) {
			lines[i] = lines[i].replaceAll(regex, replacement);
		}
		write("");
		for ( int i = 0; i < lines.length; i++ ) {
			appendln(lines[i]);
		}
	}

}
