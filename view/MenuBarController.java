package main.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenuBarController extends Controller{
	
	@FXML
	private Button tableButton;
	@FXML
	private Button adminButton;
	@FXML
	private Button logoutButton;

	
	/* ----- Button handlers ----- */
	@FXML
	private void handleTableButton() {
		app.drawMainView();
	}
	
	@FXML
	private void handleAdminButton() {
		app.drawAdminView();
	}	
	
	@FXML
	private void handleLogoutButton() {
		app.stop();
		app.drawLoginView();
	}	
}
