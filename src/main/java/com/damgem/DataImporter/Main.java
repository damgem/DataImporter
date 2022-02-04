package com.damgem.DataImporter;

import com.damgem.DataImporter.Controller.ErrorController;
import com.damgem.DataImporter.Controller.MainController;
import com.damgem.DataImporter.Data.ConfigurationData;
import com.damgem.DataImporter.Data.ParameterData;
import com.damgem.DataImporter.Data.Profile;
import com.damgem.DataImporter.Field.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public void showErrorWindow(Stage primaryStage, String errorTitle, String errorDescription) {
        // Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Error.fxml"));
        Parent root;

        try{ root = loader.load(); }
        catch (IOException error) { throw new RuntimeException(error.getMessage()); }

        // Init controller
        ErrorController controller = loader.getController();
        controller.setErrorMessage(errorTitle, errorDescription);

        // Prepare primary stage
        primaryStage.setTitle("Data Importer Error");
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image("table2.png"));

        // Show primary stage
        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            initialize_config();
            start_unsafe(primaryStage);
        }
        catch (DataImporterError error) { this.showErrorWindow(primaryStage, error.errorTitle, error.errorDescription); }
        catch (RuntimeException error) {
           this.showErrorWindow(primaryStage, "Interner Fehler", error.getMessage());
        }
    }

    private void initialize_config() throws DataImporterError
    {
        // Initialize Parameter data
        ParameterData.initialize(this.getParameters().getNamed());

        // Initialize Configuration data
        ParameterData parameterData = ParameterData.getInstance();
        String configFilePath = Objects.requireNonNullElse(parameterData.configFile, "config.json");
        ConfigurationData.initializeFromFile(configFilePath);
    }

    private void start_unsafe(Stage primaryStage) throws DataImporterError
    {
        // Get configuration
        ConfigurationData configurationData = ConfigurationData.getInstance();
        ParameterData parameterData = ParameterData.getInstance();
        Profile profile = Profile.getActiveProfile();

        // Load Main Scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        Parent scene;
        try{ scene = loader.load(); }
        catch (Exception error) {
            throw new DataImporterError("Internal Error", "Cannot load Main.fxml: " + error.getMessage());
        }

        // Initialize main controller
        MainController controller = loader.getController();

        controller.setFields(new FieldListFactory(profile.mapping).create());

        controller.setTarget(
                Objects.requireNonNullElse(profile.target, ""),
                Objects.requireNonNullElse(profile.subTarget, "")
        );

        controller.setDimensions(
                configurationData.windowWidth,
                configurationData.windowHeight,
                configurationData.keyColumnWidth
        );

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
