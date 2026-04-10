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
import anderix.otcs.OtcsNode;
import java.io.IOException;

public class RemovePublicAccess {
	private static OtcsClient otcs;
	public static void main(String[] args) throws Exception {
		if ( args.length < 2 ) {
			System.out.println("Usage:");
			System.out.println("java -cp .;anderix.1.20.jar RemovePublicAccess [content_server] [node_id]");
			System.out.println("\t[content_server] is the server name on which OTCS is installed");
			System.out.println("\t[node_id] is the id of the node to begin from (contents will also have public removed)");
		} else {
			String user = System.console().readLine("Username>");
			String passwd = new String(System.console().readPassword("Password>"));
			try {
				otcs = new OtcsClient("http://" + args[0] + "/otcs/llisapi.dll", user, passwd);
			} catch ( IOException e ) {
				System.out.println("Login failure.");
				System.exit(1);
			}
			long id = Long.parseLong(args[1]);
			String nodeName = new OtcsNode(id, otcs).getName();
			try {
				otcs.removePublicAccess(id);
				System.out.println("Removed Public access for " + nodeName);
			} catch ( Exception e ) {
				System.out.println("There was a problem removing Public access for " + nodeName);
			}
			removePublicAccessForChildren(otcs, id);
		}
	}

	public static void removePublicAccessForChildren(OtcsClient otcs, long id) throws Exception {
		OtcsNode[] kids = otcs.getChildren(id);
		for ( int i = 0; i < kids.length; i++ ) {
			if ( kids[i].getType() == 0 || kids[i].getType() == 144 ) {
				try {
					otcs.restDelete("/api/v2/nodes/" + kids[i].getId() + "/permissions/public");
					System.out.println("Removed Public access for " + kids[i].getPath(otcs));
				} catch ( Exception e ) {
					System.out.println("There was a problem removing Public access for " + kids[i].getPath(otcs));
				}
			}
			if ( kids[i].getType() == 0 ) {
				removePublicAccessForChildren(otcs, kids[i].getId());
			}
		}	
	}
}