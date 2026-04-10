/**
 * Copyright (c) 2003-2006  Anderix
 * All Rights Reserved
 */
package anderix.text;

import java.util.ArrayList;

public class RssItem {

	private XmlElement item;
	
	public RssItem(XmlElement item) {
		this.item = item;
	}
	
	public String getTitle() {
		return item.getChildElementsByTagName("title")[0].getText();
	}
	
	public String getLink() {
		return item.getChildElementsByTagName("link")[0].getText();
	}
	
	public String getDescription() {
		return item.getChildElementsByTagName("description")[0].getText();
	}

	public XmlElement getXmlElement() {
		return item;
	}
	
}