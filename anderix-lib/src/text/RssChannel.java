/**
 * Copyright (c) 2003-2006  Anderix
 * All Rights Reserved
 */
package anderix.text;

import java.util.ArrayList;

public class RssChannel {

	private XmlElement channel;
	
	public RssChannel(XmlElement channel) {
		this.channel = channel;
	}
	
	public String getTitle() {
		return channel.getChildElementsByTagName("title")[0].getText();
	}
	
	public String getCopyright() {
		return channel.getChildElementsByTagName("copyright")[0].getText();
	}

	public String getLink() {
		return channel.getChildElementsByTagName("link")[0].getText();
	}
	
	public String getDescription() {
		return channel.getChildElementsByTagName("description")[0].getText();
	}

	public String getLanguage() {
		return channel.getChildElementsByTagName("language")[0].getText();
	}
	
	public String getLastBuildDate() {
		return channel.getChildElementsByTagName("lastBuildDate")[0].getText();
	}

	public String getTtl() {
		return channel.getChildElementsByTagName("ttl")[0].getText();
	}

	public RssImage getImage() {
		XmlElement image = channel.getChildElementsByTagName("image")[0];
		return new RssImage(image);
	}

	public RssItem[] getItems() {
		XmlElement[] items = channel.getChildElementsByTagName("item");
		ArrayList list = new ArrayList();
		for ( int i = 0; i < items.length; i++ ) {
			list.add(new RssItem(items[i]));
		}
		return (RssItem[])list.toArray(new RssItem[0]);
	}

	public XmlElement getXmlElement() {
		return channel;
	}
	
}