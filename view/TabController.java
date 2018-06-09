package main.view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import main.App;
import main.util.DatabaseException;
import main.util.Person;
import main.util.PersonBuilder;
import main.util.Rank;

public class TabController extends Controller{
	@FXML
	private TextField searchField;
	
	@FXML
	private TextField numberField;
	@FXML
	private TextField surnameField;
	@FXML
	private TextField nameField;
	@FXML
	private TextField cityField;
	@FXML
	private ComboBox<Rank> rankField;
	@FXML
	private DatePicker birthdayField;
	@FXML
	private  CheckBox addmissionField;
	@FXML
	private Button applyBut;
	private int oldId;
	
	@FXML
	private TextField deleteField;
	
	@FXML
	private ToggleButton editMode;
	
	
	@FXML
	private void initialize() {
		applyBut.setText("Создать");
		rankField.setItems(FXCollections.observableArrayList(Rank.values()));
	}
	
	@FXML
	private void handleSearchButton() {
		search(searchField.getText());
	}
	
	@FXML
	private void handleApplyButton() {
		try {
			if(editMode.isSelected()) {
				app.database().update(createPerson(), this.oldId);
			} else {
				app.database().insert(createPerson());
			}
			app.refreshTable();
			clearFields();
		} catch (DatabaseException e) {
			if(App.DEBUG) e.printStackTrace();
			app.showAlert("Oops!");
		}
	}
	
	@FXML
	private void handleDeleteButton() {
		String surname = deleteField.getText();
		if(check(surname, "Поле не может быть пустым")) {
			try {
				if(app.showConfirmationDialogue("Вы действительно хотите удалить все совпадения?")) {
					app.database().deleteBySurname(surname);
					app.refreshTable();
				}
			} catch (DatabaseException e) {
				if(App.DEBUG) e.printStackTrace();
				app.showAlert("Oops!");
			}
		}

	}
	
	@FXML
	private void handleShowButton() {
		search(deleteField.getText());
	}
	
	
	/* ----- Other  ----- */
	@FXML
	private void handleToggleBut() {
		clearFields();
		if(editMode.isSelected()) {
			applyBut.setText("Изменить");
		} else {
			applyBut.setText("Создать");
		}
	}
	
	private void clearFields() {
		numberField.setText("");
		surnameField.setText("");
		nameField.setText("");
		cityField.setText("");
		rankField.setValue(null);
		birthdayField.setValue(null);
		addmissionField.setSelected(false);
	}
	
	public void setFieldsValues(Person person) {
		if(editMode.isSelected()) {
			if (person == null) {
				clearFields(); 
				this.oldId = -1;
			} else {
				this.oldId = person.getNumber();
				numberField.setText(Integer.toString(this.oldId));
				surnameField.setText(person.getSurname());
				nameField.setText(person.getName());
				cityField.setText(person.getCity());
				rankField.setValue(Rank.getValue(person.getRank()));
				birthdayField.setValue(person.getDate());
				addmissionField.setSelected(person.getAdmission());
			}
		}
	}
	
	/* ----- Helper functions ----- */
	private void search(String surname) {
		if(check(surname, "Сначала введите фамилию")) {
			try {
				((MainController) rootController).setPersonsList(app.database().selectBySurname(surname));
			} catch (DatabaseException e) {
				if(App.DEBUG) e.printStackTrace();
				app.showAlert("Oops");
			}
		}
	}
	
	private Person createPerson() {
		String num = numberField.getText();
		int id = -1;
		if(num.matches("^\\d+$")) id = Integer.parseInt(num);
		Person p = new PersonBuilder(id, surnameField.getText())
				.setName(nameField.getText())
				.setCity(cityField.getText())
				.setDate(birthdayField.getValue())
				.setRank(rankField.getValue())
				.setAddmision(addmissionField.isSelected())
				.build();
		return p;
	}
	
	private boolean check(String target, String message) {
		if(target != null) {
			return true;
		} else {
			app.showAlert(message);
			return false;
		}
	}

	public void reset() {
		clearFields();
		searchField.setText("");
		deleteField.setText("");
	}
}
