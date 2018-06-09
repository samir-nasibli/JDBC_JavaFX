package main;

import java.io.IOException;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import main.util.Config;
import main.util.DatabaseException;
import main.util.DatabaseManager;
import main.view.AdminPanelController;
import main.view.LoginController;
import main.view.MainController;
import main.view.View;


public class App extends Application {
	
	public final static boolean DEBUG = true;
	
	private Stage prStage;
	private BorderPane rootLayout;
	private View loginView;
	private View mainView;
	private View menuBarView;
	private View adminView;
	
	private DatabaseManager db;	

	
	public App() {
		try {			
			db = new DatabaseManager();
		} catch (ClassNotFoundException e) {
			if(App.DEBUG) e.printStackTrace();
			this.showAlert("Database driver not found.");
			Platform.exit();
		}
	}
	

	@Override
	public void start(Stage prStage) throws Exception {
		Config.load();
		this.prStage = prStage;
		this.prStage.setTitle("Database worker");
		
		initRootLayout();
		drawLoginView();
		
	}
	
	private void initRootLayout() {
        try {

            FXMLLoader loader = setLoader("view/RootLayout.fxml");
            rootLayout = (BorderPane)loader.load();

            Scene scene = new Scene(rootLayout);
            prStage.setScene(scene);
            prStage.setResizable(false);
            prStage.show();
            
        } catch (IOException e) {
        	if(App.DEBUG) e.printStackTrace();
        }
	}
	
	public void drawLoginView() {
        if(loginView == null) loginView = new View("view/LoginView.fxml", this);
        ((LoginController) loginView.getController()).clearPass();
        rootLayout.setTop(null);
       	rootLayout.setCenter(loginView.getView());
	}
	
	public void drawMenuBar() {
		if(menuBarView == null) menuBarView = new View("view/MenuBarView.fxml", this);
		rootLayout.setTop(menuBarView.getView());
	}

	
	public void drawMainView() {
		if(mainView == null) mainView = new View("view/MainView.fxml", this);
		rootLayout.setCenter(mainView.getView());
	}
	
	public void drawAdminView() {
		if(adminView == null) adminView = new View("view/AdminPanelView.fxml", this);
		rootLayout.setCenter(adminView.getView());
	}
	
	
	/* ----- Popup dialogues ------ */
	
	public String showCreateDialog(String message) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Input Dialog");
		dialog.setHeaderText(null);
		dialog.setContentText(message);
		Optional<String> result = dialog.showAndWait();
		String value = null;
		if (result.isPresent()) value = result.get();
		return value;
	}
	
	public boolean showConfirmationDialogue(String message) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText(null);
		alert.setContentText(message);
		Optional<ButtonType> result = alert.showAndWait();
		return result.get() == ButtonType.OK;
	}
	
	/** Default error message */
	public void showAlert(String message) {
		showAlert("Error", "Invalid action", message, AlertType.ERROR);
	}
	/** Alert message */
	public void showAlert(String title, String header, String message, AlertType at) {
		Alert alert = new Alert(at);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
	}
	
	/* ----- Other methods ----- */
	
	public void refreshTable() {
		try {
			((MainController) mainView.getController()).setPersonsList(
					db.selectAll());
		} catch (DatabaseException e) {
			if(App.DEBUG) e.printStackTrace();
			this.showAlert("Не удалось обновить данные таблицы");
		}
	}
	
	public DatabaseManager database() {
		return this.db;
	}
	
	public static FXMLLoader setLoader(String path) throws IOException {
		FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource(path));
        return loader;
	}
	
	@Override
	public void stop() {
		try {
			((MainController) mainView.getController()).reset();
			((AdminPanelController) adminView.getController()).reset();
			db.reset();
			db.closeConnetion();
		} catch (DatabaseException e) {
			if(App.DEBUG) e.printStackTrace();
//			this.showAlert("Cannot close database connection");
		}
	}
	
	
	public static void main(String[] args) {
		launch(args);
		
//		LocalDate l = LocalDate.now();
//		System.out.println(l.toString());
	}
}
