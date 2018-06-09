package main.view;

import java.time.LocalDate;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import main.App;
import main.util.DatabaseException;
import main.util.Person;

public class TableController extends Controller {
	@FXML
    private TableView<Person> personTable;
    @FXML
    private TableColumn<Person, Number> numberColumn;
    @FXML
    private TableColumn<Person, String> surameColumn;
    @FXML
    private TableColumn<Person, String> nameColumn;
    @FXML
    private TableColumn<Person, String> cityColumn;
    @FXML
    private TableColumn<Person, String> rankColumn;
    @FXML
    private TableColumn<Person, LocalDate> dateColumn;
    @FXML
    private TableColumn<Person, Boolean> admissionColumn;

    
    @FXML
    private void initialize() {
        numberColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
        surameColumn.setCellValueFactory(cellData -> cellData.getValue().surnameProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        cityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        rankColumn.setCellValueFactory(cellData -> cellData.getValue().rankProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        admissionColumn.setCellValueFactory(cellData -> cellData.getValue().admissionProperty());
        
        personTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> ((MainController) rootController).fillEditForm(newValue));
        
    }
    
	@FXML
	private void handleRefreshButton() {
		try {
			((MainController) rootController).setPersonsList(app.database().selectAll());
		} catch (DatabaseException e) {
			if(App.DEBUG) e.printStackTrace();
			app.showAlert("Oops");
		}
	}
	
    public void setPersons(ObservableList<Person> personsList) {
    	personTable.setItems(personsList);
    }
}
