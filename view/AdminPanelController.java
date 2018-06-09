package main.view;

import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import main.App;
import main.util.DatabaseException;

public class AdminPanelController extends Controller{
	@FXML 
	private ListView<String> dbList;
	@FXML
	private Button deleteDbButton;
	@FXML
	private Button connectDbButton;
	
	@FXML 
	private ListView<String> tablesList;
	@FXML 
	private Label dbLabel;
	@FXML
	private Button openTableButton;
	@FXML
	private Button truncateTableButton;
	@FXML
	private Button deleteTableButton;
	
	@FXML
	private ListView<String> usersList;
	@FXML
	private Button deleteUserButton;
	
	
	private enum Lists {Database, Table, User};

	@FXML
	private void initialize() {
		dbList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tablesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		usersList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}
	
	public void initContent() {
		dbLabel.setText("");
		try {
			dbList.setItems(FXCollections.observableArrayList(app.database().getDbList()));
			tablesList.setItems(FXCollections.observableArrayList(app.database().getTablesList()));
			usersList.setItems(FXCollections.observableArrayList(app.database().getUsersList()));
		} catch (DatabaseException e) {
			if(App.DEBUG) e.printStackTrace();
			app.showAlert("Oops!");
		}
	}
	
	/* ----- Database buttons ----- */
	
	@FXML
	private void handleCreateDatabaseButton() {
		String dbName = app.showCreateDialog("Введите имя БД");
		if (check(dbName, null)) {
			try {
				app.database().createDatabase(dbName);
				refreshList(Lists.Database);
			} catch (DatabaseException e) {
				if(App.DEBUG) e.printStackTrace();
				app.showAlert("Не удалось создать БД");
			}
		}
	}
	
	@FXML
	private void handleOpenDatabaseButton() {
		String dbName = dbList.getSelectionModel().getSelectedItem();
		if(check(dbName, "Сначала выберите БД")) {
			try {
				app.database().openDatabase(dbName);
				dbLabel.setText(dbName);
				refreshList(Lists.Table);
			} catch (DatabaseException e) {
				if(App.DEBUG) e.printStackTrace();
				app.showAlert("Не удалось подключиться");
			}
		}
	}
	
	@FXML
	private void handleDeleteDatabaseButton() {
		String dbName = dbList.getSelectionModel().getSelectedItem();
		if(check(dbName, "Сначала выберите БД")) {
			try {
				if (app.showConfirmationDialogue("Вы уверены, что хотите удалить " + dbName + "?")) {
					app.database().deleteDatabase(dbName);
					refreshList(Lists.Database);
				}
			} catch (DatabaseException e) {
				if(App.DEBUG) e.printStackTrace();
				app.showAlert("Не удалось подключиться");
			}
		}
	}
	
	/*----- Tables buttons ----- */
	
	@FXML
	private void handleCreateTableButton() {
		String tableName = app.showCreateDialog("Введите имя Таблицы");
		if (check(tableName, null)) {
			try {
				app.database().createTable(tableName);
				refreshList(Lists.Table);
			} catch (DatabaseException e) {
				if(App.DEBUG) e.printStackTrace();
				app.showAlert("Не удалось создать таблицу");
			}
		}
	}
	
	@FXML
	private void handleOpenTableButton() {
		String tableName = tablesList.getSelectionModel().getSelectedItem();
		if(check(tableName, "Сначала выберите таблицу")) {
			try {
				app.database().openTable(tableName);
				app.refreshTable();
			} catch (DatabaseException e) {
				if(App.DEBUG) e.printStackTrace();
				app.showAlert("Не удалось открыть таблицу");
			}
		}
	}
	
	@FXML
	private void handleTruncateTableButton() {
		String tableName = tablesList.getSelectionModel().getSelectedItem();
		if(check(tableName, "Сначала выберите таблицу")) {
			try {
				if (app.showConfirmationDialogue("Вы уверены, что хотите отчистить " + tableName + "?"))
					app.database().truncateTable(tableName);
			} catch (DatabaseException e) {
				if(App.DEBUG) e.printStackTrace();
				app.showAlert("Не удалось отчистить таблицу");
			}
		}
	}
	
	@FXML
	private void handleDeleteTableButton() {
		String tableName = tablesList.getSelectionModel().getSelectedItem();
		if(check(tableName, "Сначала выберите таблицу")) {
			try {
				if (app.showConfirmationDialogue("Вы уверены, что хотите удалить " + tableName + "?")) {
					app.database().deleteTable(tableName);
					refreshList(Lists.Table);
				}
			} catch (DatabaseException e) {
				if(App.DEBUG) e.printStackTrace();
				app.showAlert("Не удалось удалить таблицу");
			}
		}
	}
	
	/* ----- Role buttons ----- */
	
	@FXML
	private void handleCreateUserButton() {
		String[] data = showCreateUserDialog();
		if (data != null) {
			try {
				boolean admin = app.showConfirmationDialogue("Назначить пользователя админом?");
				app.database().createUser(data[0], data[1], admin);
				refreshList(Lists.User);
			} catch (DatabaseException e) {
				if(App.DEBUG) e.printStackTrace();
				app.showAlert("Не удалось создать пользователя");
			}
		}
	}
	
	@FXML
	private void handleDeleteUserButton() {
		String userName = usersList.getSelectionModel().getSelectedItem();
		if(check(userName, "Сначала выберите пользователя")) {
			try {
				if (app.showConfirmationDialogue("Вы уверены, что хотите удалить " + userName + "?")) {
					app.database().deleteUser(userName);
					refreshList(Lists.User);
				}
			} catch (DatabaseException e) {
				if(App.DEBUG) e.printStackTrace();
				app.showAlert("Не удалось удалить пользователя");
			}
		}
	}
	
	/* ----- Helper functions ----- */
	private void refreshList(Lists type) throws DatabaseException {
		switch(type) {
		case Database:
			dbList.setItems(FXCollections.observableArrayList(app.database().getDbList()));
			break;
		case Table:
			tablesList.setItems(FXCollections.observableArrayList(app.database().getTablesList()));
			break;
		case User:
			usersList.setItems(FXCollections.observableArrayList(app.database().getUsersList()));
			break;
		}
	};
	
	private boolean check(String target, String message) {
		if(target != null) {
			return true;
		} else {
			if(message != null) app.showAlert(message);
			return false;
		}
	}
	
	private String[] showCreateUserDialog() {
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Input Dialog");
		dialog.setHeaderText(null);
		dialog.setContentText("Введите имя и пароль для нового пользователя");
		
		// Set the button types.
		ButtonType loginButtonType = new ButtonType("Ok", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField username = new TextField();
		username.setPromptText("Username");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");

		grid.add(new Label("Username:"), 0, 0);
		grid.add(username, 1, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(password, 1, 1);

		// Enable/Disable login button depending on whether a username was entered.
		Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		username.textProperty().addListener((observable, oldValue, newValue) -> {
		    loginButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);


		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == loginButtonType) {
		        return new Pair<>(username.getText(), password.getText());
		    }
		    return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();
		
		String[] res = null;
		if (result.isPresent()) {
			res = new String[2];
			res[0] = result.get().getKey();
			res[1] = result.get().getValue();
		}
		return res;
	}

	public void reset() {
		dbLabel.setText("");
		tablesList.setItems(null);
	}
	
}
