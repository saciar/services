package crm.services.transaction.action;

import java.util.Vector;

public class UpdateCriteria {
	
	private String entity;
	private String ID;
	private Vector values;
	private Vector fields;
	
	public UpdateCriteria (String entity, String ID){
		this.entity = entity;
		this.ID = ID;
	}
	
	public String getEntity() {
		return entity;
	}
	
	public void setEntity(String entity) {
		this.entity = entity;
	}
	
	public String getID() {
		return ID;
	}
	
	public void setID(String id) {
		ID = id;
	}
	
	public Vector getFields() {
		return fields;
	}
	
	public Vector getValues() {
		return values;
	}
	
	public void addFieldAndValue(String field, String value){
		
		if (fields == null && values == null){
			fields = new Vector();
			values = new Vector();
		}
		
		fields.add(field);
		values.add(value);
		
	}
	
}
