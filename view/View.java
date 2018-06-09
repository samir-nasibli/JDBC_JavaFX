package main.view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import main.App;

public class View {
	
	private Pane view;
	private Controller controller;
	
	public View(String fxmlPath, App app) {
		try {
			
			FXMLLoader loader = App.setLoader(fxmlPath); // Don't work with app method! But works, if it's static.
			view = loader.load();
			
			controller = loader.getController();
			controller.setMainApp(app);
			
			controller.initContent();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Pane getView() {
		return view;
	}

	public Controller getController() {
		return controller;
	}

}
