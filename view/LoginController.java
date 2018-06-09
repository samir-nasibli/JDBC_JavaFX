package main.view;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.util.DatabaseException;

public class LoginController extends Controller{
	@FXML
	private TextField loginField;
	@FXML
	private PasswordField passwordField;
	
	
	@FXML
	private void handleLogInButton() {
		String login = loginField.getText();
		String password = passwordField.getText();
		if(check(login) && check(password)) {
			app.database().setUser(login, password);
			try {
				app.database().connect();				
				app.drawMenuBar();
				app.drawMainView();
			} catch (DatabaseException e) {
				app.showAlert("Cannot connect to database server");
				e.printStackTrace();
			}
			
		} else {
			app.showAlert("Access deniened!");
		}
	}
	
	private boolean check(String value) {
		return value != null && !value.equals(""); 
	}
	
	public void clearPass() {
		this.passwordField.setText("");
	}
}
