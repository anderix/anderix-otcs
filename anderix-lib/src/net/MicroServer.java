/**
 * Copyright (c) 2022  Anderix
 * All Rights Reserved
 */
package anderix.net;

import java.net.*;
import java.io.*;
import java.util.*;

public class MicroServer {
	protected int port = 80;
	private final String newLine = "\r\n";
	protected String contentType = "text/plain";

	protected String get(String request) {
		String response = request;
		return response;
	}
	
	protected String post(String request) {
		String response = request;
		return response;
	}
	
	protected String put(String request) {
		String response = request;
		return response;
	}
	
	protected String delete(String request) {
		String response = request;
		return response;
	}
	
	protected String process(String request) {
		String response = request;
		return response;
	}

	public MicroServer() {
	
	}

	public MicroServer(int port) {
		this.port = port;
	}

	public MicroServer(int port, String contentType) {
		this.port = port;
		this.contentType = contentType;
	}

	public void start() {
		try {
			ServerSocket socket = new ServerSocket(port);

			while ( true ) {
				Socket connection = socket.accept();

				try {
					BufferedReader in = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
					OutputStream out = new BufferedOutputStream(connection.getOutputStream());
					PrintStream pout = new PrintStream(out);

					String request = in.readLine();
					if ( request == null ) continue;

					while ( true ) {
						String ignore = in.readLine();
						if ( ignore == null || ignore.length() == 0 ) break;
					}

					if ( !( request.endsWith(" HTTP/1.0") || request.endsWith(" HTTP/1.1") ) ) {
						pout.print("HTTP/1.0 400 Bad Request"+newLine+newLine);
					} else {
						String response = "";
						if ( request.startsWith("GET ") ) {
							response = get( request.substring(4, request.length()-9) );
						} else if ( request.startsWith("POST ") ) {
							response = post( request.substring(5, request.length()-9) );
						} else if ( request.startsWith("PUT ") ) {
							response = put( request.substring(4, request.length()-9) );
						} else if ( request.startsWith("DELETE ") ) {
							response = delete( request.substring(7, request.length()-9) );
						} else {
							response = process(request);
						}

						pout.print("HTTP/1.0 200 OK" + newLine +
							"Content-Type: " + contentType + newLine +
							"Date: " + new Date() + newLine +
							"Content-length: " + response.length() + newLine + 
							"Access-Control-Allow-Origin: *" + newLine +
							newLine + response
						);
					}

					pout.close();
				} catch ( Throwable tri ) {
						System.err.println("Error handling request: "+tri);
				}
			}
		} catch ( Throwable tr ) {
			System.err.println("Could not start server: "+tr);
		}
	}
	
	public static void main( String[] args ) {
		MicroServer server = new MicroServer(80, "application/json");
		server.start();
	}
}