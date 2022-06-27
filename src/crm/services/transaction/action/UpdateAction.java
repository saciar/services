package crm.services.transaction.action;


public class UpdateAction implements ABMAction {
	
	private UpdateCriteria updateCriteria;
	
	public UpdateAction (UpdateCriteria updateCriteria){
		this.updateCriteria = updateCriteria;
	}

	public UpdateCriteria getUpdateCriteria() {
		return updateCriteria;
	}

	public void setUpdateCriteria(UpdateCriteria updateCriteria) {
		this.updateCriteria = updateCriteria;
	}
	
}
