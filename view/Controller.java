package main.view;

import main.App;

public abstract class Controller {
	protected App app;
	protected Controller rootController;
	
	
	public void setMainApp(App app) {
		this.app = app;
	}
	
	public void setRootController(Controller controller) {
		this.rootController = controller;
	}
	
	public void initContent() {
		
	}
}
