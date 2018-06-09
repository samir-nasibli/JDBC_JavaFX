package main.view;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import main.util.Person;

public class MainController extends Controller{
	@FXML
	private AnchorPane tablePane;
	@FXML
	private AnchorPane tabPane;
	
	private View tableView;
	private View tabView;
	
	private ObservableList<Person> personsList;
	
	private void setAnchorView(AnchorPane pane, Pane viewPane) {
		AnchorPane.setTopAnchor(viewPane, 0.0);
		AnchorPane.setLeftAnchor(viewPane, 0.0);
		AnchorPane.setRightAnchor(viewPane, 0.0);
		AnchorPane.setBottomAnchor(viewPane, 0.0);
		pane.getChildren().add(viewPane);
	}
	
	@Override
	public void initContent() {		
		
		tableView = new View("view/TableView.fxml", app);
		tableView.getController().setRootController(this);
		setAnchorView(tablePane, tableView.getView());
		((TableController) tableView.getController()).setPersons(personsList);

		
		tabView = new View("view/TabView.fxml", app);
		tabView.getController().setRootController(this);
		setAnchorView(tabPane, tabView.getView());

	}
	
	public void fillEditForm(Person selectedPerson) {
		((TabController) tabView.getController()).setFieldsValues(selectedPerson);
	}
	
	public void setPersonsList(List<Person> personList) {
		if(personList == null) {
			this.personsList = null;
		} else {
			this.personsList = FXCollections.observableArrayList(personList);
		}
		((TableController) tableView.getController()).setPersons(this.personsList);
	}
	
	public void reset() {
		setPersonsList(null);
		((TabController) tabView.getController()).reset();
	}
}
