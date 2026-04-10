/**
 * Copyright (c) 2003-2006  Anderix
 * All Rights Reserved
 */
package anderix.text;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;

/*
 * <code>XmlElement</code> is the only node type for
 * simplified DOM model.
 */
public class XmlElement {
	private String tagName;
	private String text;
	private HashMap attributes;
	private LinkedList childElements;

	public XmlElement(String tagName) {
		this.tagName = tagName;
		attributes = new HashMap();
		childElements = new LinkedList();
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAttribute(String name) {
		return (String)attributes.get(name);
	}

	public void setAttribute(String name, String value) {
		attributes.put(name, value);
	}

	public void addChildElement(XmlElement element) {
		childElements.add(element);
	}

	public XmlElement[] getChildElements() {
		return (XmlElement[])childElements.toArray(new XmlElement[0]);
	}

	public XmlElement[] getChildElementsByTagName(String tagName) {
		XmlElement[] children = getChildElements();
		ArrayList list = new ArrayList();
		for ( int i = 0; i < children.length; i++ ) {
			if ( children[i].getTagName().equalsIgnoreCase(tagName) ) {
				list.add(children[i]);
			}
		}
		return (XmlElement[])list.toArray(new XmlElement[0]);
	}

}
