/**
 * Copyright (c) 2006-2019  Anderix
 * All Rights Reserved
 */
package anderix.net;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.lang.StringBuffer;
import java.util.HashMap;
import java.io.File;
import java.io.InputStream;

public class HttpServer {
	public static void main(String[] args) {
		String webroot;
		int port;
		ServerSocket server_socket;
		try {
			webroot = args[0];
		} catch (Exception e) {
			webroot = "./webroot";
		}
		try {
			port = Integer.parseInt(args[1]);
		} catch (Exception e) {
			port = 80;
		}
		try {
			server_socket = new ServerSocket(port);
			System.out.println("Anderix HTTP Server running on port " + server_socket.getLocalPort());
			System.out.println("Web Root is " + webroot);
			System.out.println("Usage options: java anderix.net.HttpServer [webroot] [port]");
		
			// server infinite loop
			while ( true ) {
			    Socket socket = server_socket.accept();
			    System.out.println("New connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
			
			    // Construct handler to process the HTTP request message.
			    try {
					HttpRequestHandler request = new HttpRequestHandler(socket, webroot);
					// Create a new thread to process the request.
					Thread thread = new Thread(request);
			
					// Start the thread.
					thread.start();
				} catch ( Exception e ) {
					System.out.println(e);
				}
			}
		} catch ( IOException e ) {
			System.out.println(e);
		}
	}
}

class HttpRequestHandler implements Runnable {
	final static String CRLF = "\r\n";
	Socket socket;
	InputStream input;
	OutputStream output;
	BufferedReader br;
	boolean fileExists = false;
	String serverLine = "Server: Anderix Http Server" + CRLF;
	String statusLine = null;
	String contentTypeLine = null;
	String entityBody = null;
	String contentLengthLine = "error";

	FileInputStream fis = null;
	HashMap queryString = null;
	String fileName = null;
	
	String webroot = "./webroot/";
	
	String[] nameValuePairs = null;
	
	public HttpRequestHandler(Socket socket, String webroot) throws Exception {
		this.socket = socket;
		this.input = socket.getInputStream();
		this.output = socket.getOutputStream();
		this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		if ( !webroot.endsWith("/") ) {
			webroot += "/";
		}
		this.webroot = webroot;
	}
	
	public HttpRequestHandler(Socket socket) throws Exception {
		this.socket = socket;
		this.input = socket.getInputStream();
		this.output = socket.getOutputStream();
		this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.webroot = "./webroot/";
	}

	public void run() {
		try {
			processRequest();
			sendResponse();
			try {
				output.close();
				br.close();
				socket.close();
			} catch ( Exception e ) {
				// no action
			}
		} catch ( Exception e ) {
		 	System.out.println(e);
		}
	}

	private void processRequest() throws Exception {
		while ( true ) {
		
			String headerLine = br.readLine();
			System.out.println(headerLine);
			
			if ( headerLine.equals(CRLF) || headerLine.equals("") ) break;
			
			StringTokenizer s = new StringTokenizer(headerLine);
			String temp = s.nextToken();
			
			if ( temp.equals("GET") ) {
				fileName = s.nextToken();
				
				fileName = webroot + fileName;

				if ( fileName.endsWith("/") ) {
					fileName += "index.html";
				}

				try {
					fis = new FileInputStream(fileName);
					statusLine = "HTTP/1.0 200 OK" + CRLF;
					contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
					contentLengthLine = "Content-Length: " + String.valueOf(fis.available()) + CRLF;
					fileExists = true;
				} catch ( FileNotFoundException e ) {
					statusLine = "HTTP/1.0 404 Not Found" + CRLF;
					contentTypeLine = "Content-type: text/html" + CRLF;
					entityBody = "<html><head><title>404 Not Found</title></head><body><p>404 Not Found</p</body></html>";
					contentLengthLine = "Content-Length: " + entityBody.getBytes().length + CRLF;
				}

			}

		}

	}
	
	private void sendResponse() throws Exception {
		// Send the status line.
		output.write(statusLine.getBytes());
		System.out.print(statusLine);
		
		// Send the server line.
		output.write(serverLine.getBytes());
		System.out.print(serverLine);
		
		// Send the content type line.
		output.write(contentTypeLine.getBytes());
		System.out.print(contentTypeLine);
		
		// Send the Content-Length
		output.write(contentLengthLine.getBytes());
		System.out.print(contentLengthLine);
		
		// Send a blank line to indicate the end of the header lines.
		output.write(CRLF.getBytes());
		System.out.print(CRLF);

		// Send the entity body.
		if ( fileExists ) {
			sendBytes(fis, output);
			fis.close();
		} else {
			output.write(entityBody.getBytes());
		}
	
	}

	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
		byte[] buffer = new byte[1024];
		int bytes = 0;
		
		while ( (bytes = fis.read(buffer)) != -1 ) {
			os.write(buffer, 0, bytes);
		}	
	}

	private static String contentType(String fileName) {
		if ( fileName.endsWith(".htm") || 
			fileName.endsWith(".html") || 
			fileName.endsWith(".txt") || 
			fileName.endsWith(".js") || 
			fileName.endsWith(".css") ) {
			
			return "text/html";
		} else if (fileName.endsWith(".xml") ) {
			return "text/xml";
		} else if ( fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ) {
			return "image/jpeg";
		} else if ( fileName.endsWith(".gif") ) {
			return "image/gif";
		} else {
			return "application/octet-stream";
		}
	}

}
