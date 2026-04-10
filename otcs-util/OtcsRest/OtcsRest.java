/*
Copyright (C) MMXIX by Anderix. Written by David M. Anderson. All rights reserved.

This software is provided AS-IS, WITHOUT ANY EXPRESS OR IMPLIED WARRANTY. In no 
event will the author be held liable for any damages arising from the use of this 
software, or from the inability of anyone to use this software.

Permission is granted to anyone to use this software for any purpose, including 
commercial applications, and to alter it and redistribute it freely, subject to 
the following restrictions:  

1. The origin of this software must not be misrepresented; you must not claim 
   that you wrote the original software. 
   
   If you use this software in a product, an acknowledgment in the product 
   documentation would be appreciated but is not required.

2. Altered versions must be plainly marked as such, and must not be 
   misrepresented as being the original software.

3. This notice may not be removed or altered from any distribution.
*/
import anderix.otcs.OtcsClient;
import anderix.io.TextFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

public class OtcsRest {
	private static OtcsClient otcs;
	public static void main(String[] args) throws Exception {
		if ( args.length < 2 ) {
			System.out.println("Usage:");
			System.out.println("java -cp .;anderix.1.20.jar OtcsRest [method] [API call]");
			System.out.println("\t[method] is GET, POST, PUT or DELETE");
			System.out.println("\t[API call] is the API call to be invoked, including querystring");
			System.out.println("\tNOTE: if the API call includes & or spaces in querystring, it must be quoted");
		} else {
			HashMap properties = new TextFile("OtcsRest.properties").readNameValuePairs();
			try {
				otcs = new OtcsClient("http://" + properties.get("server") + "/otcs/llisapi.dll", (String)properties.get("username"), (String)properties.get("password"));
			} catch ( IOException e ) {
				System.out.println("Login failure.");
				System.exit(1);
			}
			
			String apiAndQuerystring = args[1];
			boolean hasQuerystring = false;
			String api = "";
			Hashtable values = new Hashtable();
			if ( apiAndQuerystring.indexOf('?') != -1 ) {
				hasQuerystring = true;
				String[] tmp = apiAndQuerystring.split("\\?");
				api = tmp[0];
				String[] nameValuePairs = tmp[1].split("&");
				for ( int i = 0; i < nameValuePairs.length; i++ ) {
					String[] separatedNameValuePairs = nameValuePairs[i].split("=");
					values.put(separatedNameValuePairs[0], separatedNameValuePairs[1]);
				}
			} else {
				api = apiAndQuerystring;
			}
			if ( "GET".equalsIgnoreCase(args[0]) ) {
				if ( hasQuerystring ) {
					System.out.println( otcs.restGet(api, values) );
				} else {
					System.out.println( otcs.restGet(api) );
				}
			} else if ( "POST".equalsIgnoreCase(args[0]) ) {
				if ( hasQuerystring ) {
					System.out.println( otcs.restPost(api, values) );
				} else {
					System.out.println( "Querystring is required for POST." );
				}				
			} else if ( "PUT".equalsIgnoreCase(args[0]) ) {
				if ( hasQuerystring ) {
					System.out.println( otcs.restPut(api, values) );
				} else {
					System.out.println( "Querystring is required for PUT." );
				}					
			} else if ( "DELETE".equalsIgnoreCase(args[0]) ) {
				System.out.println( otcs.restDelete(api) );
			} else {
				System.out.println("Invalid method");
			}
		}
	}
}