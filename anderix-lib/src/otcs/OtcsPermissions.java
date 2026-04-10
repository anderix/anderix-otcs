/**
 * Copyright (c) MMXIX by David M. Anderson
 * All Rights Reserved
 */
package anderix.otcs;

import anderix.text.JsonReader;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.ArrayList;

public class OtcsPermissions {
	protected ArrayList<String> rights = new ArrayList<String>();
	private boolean rightsPopulated = false;
	private boolean canSee = false;
	private boolean canSeeContents = false;
	private boolean canModify = false;
	private boolean canEditAttributes = false;
	private boolean canAddItems = false;
	private boolean canReserve = false;
	private boolean canDeleteVersions = false;
	private boolean canDelete = false;
	private boolean canEditPermissions = false;
	protected long right_id;
	protected String member_name = "";
	protected String member_name_formatted = "";
	protected String type;
	
	public String[] getRights() { return rights.toArray(new String[rights.size()]); }
	public boolean canSee() { 
		if ( !rightsPopulated ) populateRights();
		return this.canSee;
	}
	public boolean canSeeContents() { 
		if ( !rightsPopulated ) populateRights();
		return this.canSeeContents;
	}
	public boolean canModify() { 
		if ( !rightsPopulated ) populateRights();
		return this.canModify;
	}
	public boolean canEditAttributes() { 
		if ( !rightsPopulated ) populateRights();
		return this.canEditAttributes;
	}
	public boolean canAddItems() { 
		if ( !rightsPopulated ) populateRights();
		return this.canAddItems;
	}
	public boolean canReserve() { 
		if ( !rightsPopulated ) populateRights();
		return this.canReserve;
	}
	public boolean canDeleteVersions() { 
		if ( !rightsPopulated ) populateRights();
		return this.canDeleteVersions;
	}
	public boolean canDelete() { 
		if ( !rightsPopulated ) populateRights();
		return this.canDelete;
	}
	public boolean canEditPermissions() { 
		if ( !rightsPopulated ) populateRights();
		return this.canEditPermissions;
	}
	public long getRightId() { return this.right_id; }
	public String getName() { return this.member_name; }
	public String getName(OtcsClient otcs) throws MalformedURLException, IOException, Exception { 
		if ( this.type.equals("public") ) {
			return "Public";
		} else {
			lookupNameFromRightId(otcs, this.right_id);
			return this.member_name; 
		}
	}
	public String getNameFormatted() { 
		if ( this.type.equals("public") ) {
			return "Public";
		} else {
			return this.member_name_formatted; 
		}
	}
	public String getNameFormatted(OtcsClient otcs) throws MalformedURLException, IOException, Exception  { 
		if ( this.type.equals("public") ) {
			return "Public";
		} else {
			lookupNameFromRightId(otcs, this.right_id);
			return this.member_name_formatted;
		}
	}
	public String getType() { return this.type; }

	private void populateRights() {
		for ( int i = 0; i < this.rights.size(); i++ ) {
			if ( rights.get(i).equals("see") ) {
				this.canSee = true;
			} else if ( rights.get(i).equals("see_contents") ) {
				this.canSeeContents = true;
			} else if ( rights.get(i).equals("modify") ) {
				this.canModify = true;
			} else if ( rights.get(i).equals("edit_attributes") ) {
				this.canEditAttributes = true;
			} else if ( rights.get(i).equals("add_items") ) {
				this.canAddItems = true;
			} else if ( rights.get(i).equals("reserve") ) {
				this.canReserve = true;
			} else if ( rights.get(i).equals("delete_versions") ) {
				this.canDeleteVersions = true;
			} else if ( rights.get(i).equals("delete") ) {
				this.canDelete = true;
			} else if ( rights.get(i).equals("edit_permissions") ) {
				this.canEditPermissions = true;
			}
		}			
		rightsPopulated = true;
	}
	
	public void lookupNameFromRightId(OtcsClient otcs, long rightId) throws MalformedURLException, IOException {
		String json = otcs.restGet("/api/v2/members/" + Long.toString(rightId));
		JsonReader reader = new JsonReader(json);
		this.member_name = (String)reader.get("results.data.properties.name");
		this.member_name_formatted = (String)reader.get("results.data.properties.name_formatted");
	}

	public static OtcsPermissions[] parseOtcsPermissionsFromJson(String json) throws MalformedURLException, IOException {
		JsonReader reader = new JsonReader(json);
		int resultsSize = reader.getInt("results.length");
		OtcsPermissions[] rval = new OtcsPermissions[resultsSize];
		for ( int i = 0; i < resultsSize; i++ ) {
			OtcsPermissions perm = new OtcsPermissions();
			perm.type = (String)reader.get("results[" + Integer.toString(i) + "].data.permissions.type");
			if ( !"public".equals((String)reader.get("results[" + Integer.toString(i) + "].data.permissions.type")) ) {
				perm.right_id = Long.parseLong(reader.get("results[" + Integer.toString(i) + "].data.permissions.right_id"));
			}
			int permissionsSize = 0;
			if ( reader.get("results[" + Integer.toString(i) + "].data.permissions.permissions.length") != null ) {
				permissionsSize = Integer.parseInt(reader.get("results[" + Integer.toString(i) + "].data.permissions.permissions.length"));
			}
			for ( int j = 0; j < permissionsSize; j++ ) {
				perm.rights.add( reader.get("results[" + Integer.toString(i) + "].data.permissions.permissions[" + Integer.toString(j) + "]") );
			}
			rval[i] = perm;
		}
		return rval;
	}
}