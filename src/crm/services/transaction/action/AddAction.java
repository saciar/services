package crm.services.transaction.action;

import crm.libraries.abm.entities.ABMEntity;

public class AddAction implements ABMAction {

	private ABMEntity entityToAdd;
	
	public AddAction (ABMEntity e){
		this.entityToAdd = e;
	}

	public ABMEntity getEntityToAdd() {
		return entityToAdd;
	}

	public void setEntityToAdd(ABMEntity entityToAdd) {
		this.entityToAdd = entityToAdd;
	}

}
