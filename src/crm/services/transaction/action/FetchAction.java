package crm.services.transaction.action;


public class FetchAction implements ABMAction {
	
	private ListCriteria listCriteria;

	public FetchAction (ListCriteria listCriteria) {
		this.listCriteria = listCriteria;
	}
	
	public ListCriteria getListCriteria() {
		return listCriteria;
	}

	public void setListCriteria(ListCriteria listCriteria) {
		this.listCriteria = listCriteria;
	}
	
}
