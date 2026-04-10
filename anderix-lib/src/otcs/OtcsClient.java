/**
 * Copyright (c) MMXIX by David M. Anderson
 * All Rights Reserved
 */
package anderix.otcs;

import anderix.text.JsonReader;
import anderix.net.WebForm;
import anderix.net.MultiPartForm;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.Hashtable;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;

public class OtcsClient {
	private String token;
	private String contentServerUrl;
	private String username;
	private String password;

	public String getToken() { return token; }
	public String getContentServerUrl() { return contentServerUrl; }
	public void setContentServerUrl(String contentServerUrl) { this.contentServerUrl = contentServerUrl; }
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public void setPassword(String password) { this.password = password; }
	

	public OtcsClient() {
		
	}
	
	public OtcsClient(String contentServerUrl) {
		this.contentServerUrl = contentServerUrl;
	}
	
	public OtcsClient(String contentServerUrl, String username) {
		this.contentServerUrl = contentServerUrl;
		this.username = username;
	}

	public OtcsClient(String contentServerUrl, String username, String password) throws MalformedURLException, IOException {
		this.contentServerUrl = contentServerUrl;
		this.username = username;
		this.password = password;
		this.connect();
	}

	
	public String connect() throws MalformedURLException, IOException {
		String rval = "";
		WebForm form = new WebForm(this.contentServerUrl + "/api/v1/auth");
		form.addFormValue("username", this.username);
		form.addFormValue("password", this.password);
		rval = form.post();
		JsonReader reader = new JsonReader(rval);
		this.token = (String)reader.get("ticket");
		return rval;
	}

	public String restGet(String url) throws MalformedURLException, IOException  {
		return restGet(url, new Hashtable());
	}
	
	public String restGet(String url, Hashtable querystring) throws MalformedURLException, IOException {
		String rval = "";
		WebForm form = new WebForm(this.contentServerUrl + url);
		form.addHeader("OTCSTICKET", token);
		form.setFormValues(querystring);
		rval = form.get();
		return rval;
	}

	public String restPost(String url, Hashtable formValues) throws MalformedURLException, IOException {
		String rval = "";
		WebForm form = new WebForm(this.contentServerUrl + url);
		form.addHeader("OTCSTICKET", token);
		form.setFormValues(formValues);
		rval = form.post();
		return rval;
	}

	public String restPut(String url, Hashtable formValues) throws MalformedURLException, IOException {
		String rval = "";
		WebForm form = new WebForm(this.contentServerUrl + url);
		form.addHeader("OTCSTICKET", token);
		form.setFormValues(formValues);
		rval = form.put();
		return rval;
	}

	public String restDelete(String url) throws MalformedURLException, IOException {
		String rval = "";
		WebForm form = new WebForm(this.contentServerUrl + url);
		form.addHeader("OTCSTICKET", token);
		rval = form.delete();
		return rval;
	}
	
	public long getNodeIdFromJson(String json) {
		JsonReader reader = new JsonReader(json);
		return Long.parseLong(reader.get("results.data.properties.id"));
	}
	
	public long createNodeAndGetId(long nodeType, long parentId, String name) throws MalformedURLException, IOException {
		String json = createNode(nodeType, parentId, name);
		return getNodeIdFromJson(json);
	}
	
	public String createNode(long nodeType, long parentId, String name) throws MalformedURLException, IOException {
		String rval = "";
		WebForm form = new WebForm(this.contentServerUrl + "/api/v2/nodes");
		form.addHeader("OTCSTICKET", token);
		form.addFormValue("type", Long.toString(nodeType));
		form.addFormValue("parent_id", Long.toString(parentId));
		form.addFormValue("name", name);
		rval = form.post();
		return rval;
	}
	
	public long createFolderAndGetId(long parentId, String name) throws MalformedURLException, IOException {
		String json = createNode(0, parentId, name);
		return getNodeIdFromJson(json);
	}

	public String createFolder(long parentId, String name) throws MalformedURLException, IOException {
		return createNode(0, parentId, name);
	}
	
	public long uploadDocumentAndGetId(long folderId, String filename, String filepath) throws MalformedURLException, IOException {
		return uploadDocumentAndGetId(folderId, filename, new File(filepath));
	}

	public long uploadDocumentAndGetId(long folderId, String filename, File file) throws MalformedURLException, IOException {
		String json = uploadDocument(folderId, filename, file);
		return getNodeIdFromJson(json);	
	}
	
	public String uploadDocument(long folderId, String filename, String filepath) throws MalformedURLException, IOException {
		return uploadDocument(folderId, filename, new File(filepath));
	}
	
	public String uploadDocument(long folderId, String filename, File file) throws MalformedURLException, IOException {
		String rval = "";
		MultiPartForm client = new MultiPartForm(this.contentServerUrl + "/api/v2/nodes");
		client.addHeader("OTCSTICKET", token);
		client.addFormValue("type", "144");
		client.addFormValue("parent_id", Long.toString(folderId));
		client.addFormValue("name", filename);
		client.addFormFile("file", file);
		rval = client.post();
		renameToFixCharacterEncoding(rval, filename);
		return rval;
	}
	
	/*
	 * This method is necessary becuase the uploadDocument method doesn't recognize UTF-8 encoded form values.
	 * There is probably a better way to do this, but for now this will work.
	 */
	private void renameToFixCharacterEncoding(String json, String name) throws MalformedURLException, IOException {
		long id = getNodeIdFromJson(json);
		renameNode(id, name);
	}
	
	public void downloadDocument(long nodeId, String localFilePath) throws MalformedURLException, IOException {
		WebForm client = new WebForm(contentServerUrl + "/api/v1/nodes/" + Long.toString(nodeId) + "/content");
		client.addHeader("OTCSTICKET", token);
		new File(localFilePath).getParentFile().mkdirs();
		client.getFile(localFilePath);		
	}

	public void downloadFolderContents(long nodeId, String localFolderPath) throws MalformedURLException, IOException {
		OtcsNode[] kids = this.getChildren(nodeId);
		for ( int i = 0; i < kids.length; i++ ) {
			if ( kids[i].getType() == 144 ) {
				kids[i].downloadDocument(this, localFolderPath+kids[i].getName());
			} else if ( kids[i].getType() == 0 ) {
				downloadFolderContents(kids[i].getId(), localFolderPath + "/" + kids[i].getName() + "/");
			}
		}
	}
	
	public String renameNode(long nodeId, String newName) throws MalformedURLException, IOException {
		String rval = "";
		WebForm form = new WebForm(this.contentServerUrl + "/api/v2/nodes/" + Long.toString(nodeId));
		form.addHeader("OTCSTICKET", token);
		form.addFormValue("name", newName);
		rval = form.put();
		return rval;
	}
	
	public String moveNode(long nodeId, long newParent) throws MalformedURLException, IOException {
		String rval = "";
		WebForm form = new WebForm(this.contentServerUrl + "/api/v2/nodes/" + Long.toString(nodeId));
		form.addHeader("OTCSTICKET", token);
		form.addFormValue("parent_id", Long.toString(newParent));
		rval = form.put();
		return rval;
	}
	
	public String deleteNode(long nodeId) throws MalformedURLException, IOException {
		String rval = "";
		WebForm form = new WebForm(this.contentServerUrl + "/api/v2/nodes/" + Long.toString(nodeId));
		form.addHeader("OTCSTICKET", token);
		rval = form.delete();
		return rval;
	}

	public String removePublicAccess(long nodeId) throws MalformedURLException, IOException {
		String rval = "";
		WebForm form = new WebForm(this.contentServerUrl + "/api/v2/nodes/" + Long.toString(nodeId) + "/permissions/public");
		form.addHeader("OTCSTICKET", token);
		rval = form.delete();
		return rval;
	}
	
	public String getEnterpriseChildrenJson() throws MalformedURLException, IOException {
		return getChildrenJson(2000);
	}
	
	public OtcsNode[] getEnterpriseChildren() throws MalformedURLException, IOException {
		return OtcsNode.parseOtcsNodesFromJson(getEnterpriseChildrenJson());
	}

	public String getChildrenJson(long nodeId) throws MalformedURLException, IOException {
		String rval = "";
		WebForm client = new WebForm(contentServerUrl + "/api/v2/nodes/" + Long.toString(nodeId) + "/nodes");
		client.addHeader("OTCSTICKET", token);
		rval = client.get();
		return rval;
	}

	public OtcsNode[] getChildren(long nodeId) throws MalformedURLException, IOException {
		return OtcsNode.parseOtcsNodesFromJson(getChildrenJson(nodeId));
	}
	
	public String getPermissionsJson(long nodeId) throws MalformedURLException, IOException {
		String rval = "";
		WebForm client = new WebForm(contentServerUrl + "/api/v2/nodes/" + Long.toString(nodeId) + "/permissions");
		client.addHeader("OTCSTICKET", token);
		rval = client.get();
		return rval;
	}	
	
	public OtcsPermissions[] getPermissions(long nodeId) throws MalformedURLException, IOException {
		return OtcsPermissions.parseOtcsPermissionsFromJson(getPermissionsJson(nodeId));
	}
	
}