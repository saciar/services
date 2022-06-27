package crm.services.transaction.action;

import java.util.StringTokenizer;
import java.util.Vector;

public class ListCriteria {
	
	private Vector fields;
	private Vector values;
	private String[] entities;
	private boolean startsWith;
	private Vector selectFields;
	
	public ListCriteria(String e, boolean startsWith){
		if (e.indexOf("|")!= -1){
			StringTokenizer st = new StringTokenizer(e,"|");
			entities = new String[st.countTokens()];
			int i=0;
			while(st.hasMoreElements()){
				entities[i++] = st.nextToken();
			}
		}
		else {
			entities = new String[]{e};
		}

		this.startsWith = startsWith;
		fields = null;
		values = null;
	}
	
	public ListCriteria(String e){
		this(e,false);
	}
	
	public void addFieldAndValue(String field, String value){
		
		if (fields == null && values == null){
			fields = new Vector();
			values = new Vector();
		}
		
		fields.add(field);
		values.add(value);
		
	}

	public void addSelectField(String fieldname){
		if (selectFields == null){
			selectFields = new Vector();
		}
		selectFields.addElement(fieldname);
	}
	public boolean hasSelectFields(){
		return selectFields != null && !selectFields.isEmpty();
	}
	public boolean hasFields(){
		return fields != null && !fields.isEmpty();
	}
	public Vector getSelectFields(){
		return selectFields;
	}
	
	public Vector getFields() {
		return fields;
	}

	public Vector getValues() {
		return values;
	}

	public String getEntity() {
		return entities[0];
	}
	
	public String[] getEntities() {
		return entities;
	}
	
	public boolean hasJoin(){
		return entities.length > 1;
	}

	public boolean getStartsWith() {
		return startsWith;
	}

	public void setStartsWith(boolean startsWith) {
		this.startsWith = startsWith;
	}	

	
	
}
