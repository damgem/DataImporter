package com.damgem.DataImporter;

import com.damgem.DataImporter.Controller.ErrorController;
import com.damgem.DataImporter.Controller.MainController;
import com.damgem.DataImporter.Data.ParameterData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

public class Main2 extends Application {

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

    private ParameterData getParameter() throws TitledError {
        return new ParameterData(this.getParameters().getNamed());
    }

    @Override
    public void start(Stage primaryStage) {
        try { start_unsafe(primaryStage); }
        catch (TitledError error) { this.showErrorWindow(primaryStage, error.title, error.description); }
        catch (RuntimeException error) {
            this.showErrorWindow(primaryStage, "Interner Fehler", error.getMessage());
        }
    }

    private void start_unsafe() throws TitledError {

        // Read parameter and configuration data
        ParameterData parameterData = this.getParameter();

        Configuration.load(Paths.get("config.json"));

        if(Configuration.legacyMode) {
            Configuration.setCurrentProfile(Configuration.getLegacyProfile());
        } else {
            int splitIndex = parameterData.values.indexOf(';');
            Configuration.setCurrentProfile(Configuration.getProfile(parameterData.values.substring(0,splitIndex)));
            parameterData.values = parameterData.values.substring(splitIndex+1);
        }

        // Load Main Scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        Parent scene;
        try{ scene = loader.load(); }
        catch (Exception error) {
            throw new TitledError("Internal Error", "Cannot load Main.fxml: " + error.getMessage());
        }

        // Init Main Controller
        MainController controller = loader.getController();
        controller.setFields(new FieldMatcher(profile.mapping).match(parameterData.values));
        controller.setTarget(Objects.requireNonNullElse(profile.target, ""),
                Objects.requireNonNullElse(profile.subTarget, ""));

        // Prepare primary stage
        primaryStage.setTitle("DataImporter");
        primaryStage.setScene(new Scene(scene));
        primaryStage.getIcons().add(new Image("table2.png"));

        // Show primary stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
