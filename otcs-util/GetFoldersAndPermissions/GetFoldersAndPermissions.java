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

import anderix.io.TextFile;
import anderix.otcs.OtcsClient;
import anderix.otcs.OtcsNode;
import anderix.otcs.OtcsPermissions;
import java.io.IOException;

public class GetFoldersAndPermissions {
	static OtcsClient otcs;
	static String outputfilename = "";
	static TextFile outputfile;
	
	public static void main(String[] args) throws Exception {
		if ( args.length < 2 ) {
			System.out.println("Usage:");
			System.out.println("java -cp .;anderix.1.20.jar GetFoldersAndPermissions [content_server] [node_id] [output_file]");
			System.out.println("\t[content_server] is the server name on which OTCS is installed");
			System.out.println("\t[node_id] is the id of the node to begin from");
			System.out.println("\t[output_file] (optional) is the file to write HTML output");
		} else {
			String user = System.console().readLine("Username>");
			String passwd = new String(System.console().readPassword("Password>"));
			try {
				otcs = new OtcsClient("http://" + args[0] + "/otcs/llisapi.dll", user, passwd);
			} catch ( IOException e ) {
				System.out.println("Login failure.");
				System.exit(1);
			}
			if ( args.length > 2 ) {
				outputfilename = args[2];
				outputfile = new TextFile(outputfilename);
				outputfile.appendln("<html><body>");
			}
			printChildren(Long.parseLong(args[1]));
			if ( outputfilename!= "" ) {	
				outputfile.appendln("</body></html>");
			}
		}
	}

	public static void printChildren(long id) throws Exception {
		OtcsNode[] kids = otcs.getChildren(id);
		for ( int i = 0; i < kids.length; i++ ) {
			if ( kids[i].getType() == 0 || kids[i].getType() == 144 ) {
				System.out.println(kids[i].getPath(otcs).replaceAll(":"," | "));
				if ( outputfilename!= "" ) outputfile.appendln("<h2>" + kids[i].getPath(otcs) + "</h2>");
				writePermission(kids[i].getId());
				System.out.println();
			}
			if ( kids[i].getType() == 0 ) {
				printChildren(kids[i].getId());
			}
		}
	}
	
	public static void writePermission(long nodeId) throws Exception {
		OtcsPermissions[] perms = otcs.getPermissions(nodeId);
		for ( int i = 0; i < perms.length; i++ ) {
			System.out.println(
				perms[i].getType() + " " +
				perms[i].getName(otcs) + " " +
				perms[i].getNameFormatted()
			);
			if ( outputfilename!= "" ) outputfile.append("<p><b>(" + perms[i].getType() + ") " + perms[i].getNameFormatted() + "</b>&nbsp;<i>");
			String[] rights = perms[i].getRights();
			for ( int j = 0; j < rights.length; j++ ) {
				System.out.println(rights[j]);
				if ( outputfilename!= "" ) outputfile.append(rights[j] + "; ");
			}
			if ( outputfilename!= "" ) outputfile.append("</i></p>");
			System.out.println();
		}
	}
}