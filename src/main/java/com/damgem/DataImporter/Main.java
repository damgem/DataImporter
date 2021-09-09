package com.damgem.DataImporter;

import com.damgem.DataImporter.Controller.ErrorController;
import com.damgem.DataImporter.Controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class Main extends Application {

    Stage primaryStage;

    public void showErrorWindow(TitledError error) {
        // Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Error.fxml"));
        Parent root;
        try{ root = loader.load(); }
        catch (IOException e) { throw new RuntimeException(e.getMessage()); }

        // Init controller
        ((ErrorController) loader.getController()).setError(error);

        // Prepare primary stage
        this.primaryStage.setTitle("Data Importer Error");
        this.primaryStage.setScene(new Scene(root));
        this.primaryStage.getIcons().add(new Image("table2.png"));

        // Show primary stage
        this.primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try
        {
            start_unsafe();
        }
        catch (TitledError error)
        {
            this.showErrorWindow(error);
        }
        catch (RuntimeException error)
        {
            this.showErrorWindow(new TitledError("Internal Error", error.getMessage()));
        }
    }

    String getValuesParameter() throws TitledError {
        String values = getParameters().getNamed().get("values");
        if(values != null) return values;
        throw new TitledError();
    }

    private Scene initMainScene(List<UIStringField> fields) throws TitledError {
        // load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        Parent parent;
        try
        {
            parent = loader.load();
        }
        catch (Exception error)
        {
            throw new TitledError("Internal Error", "Cannot load Main.fxml: " + error.getMessage());
        }

        // pass data to controller
        MainController controller = loader.getController();
        controller.setFields(fields);
        controller.initTargets();

        return new Scene(parent);
    }


    private void start_unsafe() throws TitledError {
        // load config first
        Config.load(Paths.get("config.json"));

        // get values parameter
        String values = getValuesParameter();

        // init the profile with the first field of values
        // if initialization fails, legacy mode is in use
        int splitIndex = values.indexOf(';');
        if( Config.initProfile(values.substring(0,splitIndex)) ) {
            values = values.substring(splitIndex + 1);
        }

        // map values according to profile

        // show main scene
        Scene mainScene = initMainScene();
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("DataImporter");
        primaryStage.getIcons().add(new Image("table2.png"));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
