/**
 * Copyright (c) 2003-2006  Anderix
 * All Rights Reserved
 */
package anderix.text;

import java.io.IOException;
import java.io.Reader;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class RssFeed extends XmlDocument {

	public RssFeed(Reader reader) {
		super(reader);
	}
	
	public RssFeed(String xml) {
		super(xml);
	}
	
	public RssFeed(File file) throws IOException {
		super(file);
	}
	
	public RssFeed(URL url) throws IOException {
		super(url);
	}
	
	public RssChannel[] getChannels() throws IOException {
		XmlElement[] channels = getRootElement().getChildElementsByTagName("channel");
		ArrayList list = new ArrayList();
		for ( int i = 0; i < channels.length; i++ ) {
			list.add(new RssChannel(channels[i]));
		}
		return (RssChannel[])list.toArray(new RssChannel[0]);
	}
}