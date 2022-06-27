package crm.services.transaction.action;

public class DeleteAction implements ABMAction {

	private String entity;
	private String ID;

	public DeleteAction(String entity, String ID){
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
	
}
