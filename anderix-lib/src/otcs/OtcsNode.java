/**
 * Copyright (c) MMXIX by David M. Anderson
 * All Rights Reserved
 */
package anderix.otcs;

import anderix.text.JsonReader;
import anderix.net.WebForm;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.File;

public class OtcsNode {
	private String token;
	
	protected boolean container;
	protected long container_size;
	protected String create_date;
	protected long create_user_id;
	protected String description;
	protected String guid;
	protected long id;
	protected String modify_date;
	protected long modify_user_id;
	protected String name;
	protected String path = "";
	protected long owner_group_id;
	protected long owner_user_id;
	protected long parent_id;
	protected boolean reserved;
	protected String reserved_date;
	protected long reserved_user_id;
	protected long type;
	protected String type_name;

	public boolean isContainer() { return this.container; }
	public long getContainerSize() { return this.container_size; }
	public String getCreateDate() { return this.create_date; }
	public long getCreateUserId() { return this.create_user_id; }
	public String getDescription() { return this.description; }
	public String getGuid() { return this.guid; }
	public long getId() { return this.id; }
	public String getModifyDate() { return this.modify_date; }
	public long getModifyUserId() { return this.modify_user_id; }
	public String getName() { return this.name; }
	public String getPath() { return this.path; }
	public String getPath(OtcsClient otcs) throws MalformedURLException, IOException { 
		rebuildOtcsPath(otcs);
		return this.path; 
	}
	public long getOwnerGroupId() { return this.owner_group_id; }
	public long getOwnerUserId() { return this.owner_user_id; }
	public long getParentId() { return this.parent_id; }
	public boolean isReserved() { return this.reserved; }
	public String getReservedDate() { return this.reserved_date; }
	public long getReservedUserId() { return this.reserved_user_id; }
	public long getType() { return this.type; }
	public String getTypeName() { return this.type_name; }

	public OtcsNode() {
		
	}
	
	public OtcsNode(long nodeId, OtcsClient otcs) throws MalformedURLException, IOException {
		WebForm client = new WebForm(otcs.getContentServerUrl() + "/api/v2/nodes/" + Long.toString(nodeId) + "/properties");
		client.addHeader("OTCSTICKET", otcs.getToken());
		JsonReader reader = new JsonReader(client.get());
		String jsonPath = "results.data.properties.";
		this.container = (Boolean)reader.getBoolean(jsonPath + "container");
		this.container_size = (Long)reader.getLong(jsonPath + "container_size");
		this.create_date = (String)reader.get(jsonPath + "create_date");
		this.create_user_id = (Long)reader.getLong(jsonPath + "create_user_id");
		this.description = (String)reader.get(jsonPath + "description");
		this.guid = (String)reader.get(jsonPath + "guid");
		this.id = (Long)reader.getLong(jsonPath + "id");
		this.modify_date = (String)reader.get(jsonPath + "modify_date");
		this.modify_user_id = (Long)reader.getLong(jsonPath + "modify_user_id");
		this.name = (String)reader.get(jsonPath + "name");
		this.owner_group_id = (Long)reader.getLong(jsonPath + "owner_group_id");
		this.owner_user_id = (Long)reader.getLong(jsonPath + "owner_user_id");
		this.parent_id = (Long)reader.getLong(jsonPath + "parent_id");
		this.reserved = (Boolean)reader.getBoolean(jsonPath + "reserved");
		this.reserved_date = (String)reader.get(jsonPath + "reserved_date");
		this.reserved_user_id = (Long)reader.getLong(jsonPath + "reserved_user_id");
		this.type = (Long)reader.getLong(jsonPath + "type");
		this.type_name = (String)reader.get(jsonPath + "type_name");
	}
	
	public void rebuildOtcsPath(OtcsClient otcs) throws MalformedURLException, IOException {
		String path = "";
		WebForm form = new WebForm(otcs.getContentServerUrl() + "/api/v1/nodes/" + Long.toString(this.id) + "/ancestors");
		form.addHeader("OTCSTICKET", otcs.getToken());
		String json = form.get();
		JsonReader reader = new JsonReader(json);
		for ( int i = 0; i < reader.getInt("ancestors.length"); i++ ) {
			if ( path != "" ) path += ":";
			path += OtcsNode.unescapeJavaString((String)reader.get("ancestors[" + Long.toString(i) + "].name"));
		}
		this.path = path;
	} 

	public void downloadDocument(OtcsClient otcs, String localFilePath) throws MalformedURLException, IOException {
		WebForm form = new WebForm(otcs.getContentServerUrl() + "/api/v1/nodes/" + Long.toString(this.id) + "/content");
		form.addHeader("OTCSTICKET", otcs.getToken());
		new File(localFilePath).getParentFile().mkdirs();
		form.getFile(localFilePath);
	}

	public static OtcsNode[] parseOtcsNodesFromJson(String json) throws MalformedURLException, IOException {
		JsonReader reader = new JsonReader(json);
		int resultsLength = reader.getInt("results.length");

		OtcsNode[] nodes = new OtcsNode[resultsLength];
		for ( int i = 0; i < resultsLength; i++ ) {
			String jsonPath = "results[" + Integer.toString(i) + "].data.properties.";
			OtcsNode node = new OtcsNode();
			node.container = (Boolean)reader.getBoolean(jsonPath + "container");
			node.container_size = (Long)reader.getLong(jsonPath + "container_size");
			node.create_date = (String)reader.get(jsonPath + "create_date");
			node.create_user_id = (Long)reader.getLong(jsonPath + "create_user_id");
			node.description = (String)reader.get(jsonPath + "description");
			node.guid = (String)reader.get(jsonPath + "guid");
			node.id = (Long)reader.getLong(jsonPath + "id");
			node.modify_date = (String)reader.get(jsonPath + "modify_date");
			node.modify_user_id = (Long)reader.getLong(jsonPath + "modify_user_id");
			node.name = unescapeJavaString((String)reader.get(jsonPath + "name"));
			node.owner_group_id = (Long)reader.getLong(jsonPath + "owner_group_id");
			node.owner_user_id = (Long)reader.getLong(jsonPath + "owner_user_id");
			node.parent_id = (Long)reader.getLong(jsonPath + "parent_id");
			node.reserved = (Boolean)reader.getBoolean(jsonPath + "reserved");
			node.reserved_date = (String)reader.get(jsonPath + "reserved_date");
			node.reserved_user_id = (Long)reader.getLong(jsonPath + "reserved_user_id");
			node.type = (Long)reader.getLong(jsonPath + "type");
			node.type_name = (String)reader.get(jsonPath + "type_name");

			nodes[i] = node;
		}
		return nodes;
	}
	
	/*
	 * Unescapes a string that contains standard Java escape sequences.
	 * <ul>
	 * <li><strong>&#92;b &#92;f &#92;n &#92;r &#92;t &#92;" &#92;'</strong> :
	 * BS, FF, NL, CR, TAB, double and single quote.</li>
	 * <li><strong>&#92;X &#92;XX &#92;XXX</strong> : Octal character
	 * specification (0 - 377, 0x00 - 0xFF).</li>
	 * <li><strong>&#92;uXXXX</strong> : Hexadecimal based Unicode character.</li>
	 * </ul>
	 * 
	 * @param st
	 *            A string optionally containing standard java escape sequences.
	 * @return The translated string.
	 */
	 
	// CREDIT: https://gist.github.com/uklimaschewski/6741769
	// found on: // CREDIT: https://stackoverflow.com/questions/3537706/how-to-unescape-a-java-string-literal-in-java
	private static String unescapeJavaString(String st) {

		StringBuilder sb = new StringBuilder(st.length());

		for (int i = 0; i < st.length(); i++) {
			char ch = st.charAt(i);
			if (ch == '\\') {
				char nextChar = (i == st.length() - 1) ? '\\' : st
						.charAt(i + 1);
				// Octal escape?
				if (nextChar >= '0' && nextChar <= '7') {
					String code = "" + nextChar;
					i++;
					if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
							&& st.charAt(i + 1) <= '7') {
						code += st.charAt(i + 1);
						i++;
						if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
								&& st.charAt(i + 1) <= '7') {
							code += st.charAt(i + 1);
							i++;
						}
					}
					sb.append((char) Integer.parseInt(code, 8));
					continue;
				}
				switch (nextChar) {
				case '\\':
					ch = '\\';
					break;
				case 'b':
					ch = '\b';
					break;
				case 'f':
					ch = '\f';
					break;
				case 'n':
					ch = '\n';
					break;
				case 'r':
					ch = '\r';
					break;
				case 't':
					ch = '\t';
					break;
				case '\"':
					ch = '\"';
					break;
				case '\'':
					ch = '\'';
					break;
				// Hex Unicode: u????
				case 'u':
					if (i >= st.length() - 5) {
						ch = 'u';
						break;
					}
					int code = Integer.parseInt(
							"" + st.charAt(i + 2) + st.charAt(i + 3)
									+ st.charAt(i + 4) + st.charAt(i + 5), 16);
					sb.append(Character.toChars(code));
					i += 5;
					continue;
				}
				i++;
			}
			sb.append(ch);
		}
		return sb.toString();
	}
	
}