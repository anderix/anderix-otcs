/**
 * Copyright (c) 2003-2006  Anderix
 * All Rights Reserved
 */
package anderix.text;

import java.util.ArrayList;

public class RssImage {

	private XmlElement img;
	
	public RssImage(XmlElement img) {
		this.img = img;
	}
	
	public String getUrl() {
		return img.getChildElementsByTagName("url")[0].getText();
	}
	
	public String getTitle() {
		return img.getChildElementsByTagName("title")[0].getText();
	}
	
	public String getLink() {
		return img.getChildElementsByTagName("link")[0].getText();
	}
	
	public String getDescription() {
		return img.getChildElementsByTagName("description")[0].getText();
	}
	
	public String getWidth() {
		return img.getChildElementsByTagName("width")[0].getText();
	}
	
	public String getHeight() {
		return img.getChildElementsByTagName("height")[0].getText();
	}

	public XmlElement getXmlElement() {
		return img;
	}
	
}