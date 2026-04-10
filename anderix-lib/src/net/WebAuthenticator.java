/**
 * Copyright (c) 2003-2006  Anderix
 * All Rights Reserved
 */
package anderix.net;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.InetAddress;

public class WebAuthenticator extends Authenticator {
    
    private String username;
    private String password;
    
    public WebAuthenticator(String username, String password) {
    	this.username = username;
    	this.password = password;
    }
    
    // This method is called when a password-protected URL is accessed
    protected PasswordAuthentication getPasswordAuthentication() {
        // Get information about the request
        String promptString = getRequestingPrompt();
        String hostname = getRequestingHost();
        InetAddress ipaddr = getRequestingSite();
        int port = getRequestingPort();

        // Return the information
        return new PasswordAuthentication(username, password.toCharArray());
    }
}
