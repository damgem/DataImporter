package com.damgem.DataImporter;

import com.damgem.DataImporter.Controller.ErrorController;
import com.damgem.DataImporter.Controller.MainController;
import com.damgem.DataImporter.Data.ConfigurationData;
import com.damgem.DataImporter.Data.ParameterData;
import com.damgem.DataImporter.Data.Profile;
import com.damgem.DataImporter.Field.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

    private ConfigurationData getConfiguration(Path path) throws DataImporterError {
        // Create gson parser
        Type FieldBlueprintList = new TypeToken<List<FieldBlueprint>>() {}.getType();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(FieldBlueprint.class, new FieldBlueprintDeserializer())
                .registerTypeAdapter(FieldBlueprintList, new FieldBlueprintListDeserializer())
                .create();

        // Parse Json / Load Profiles
        String jsonString;
        try { jsonString = Files.readString(path); }
        catch (Exception error) {
            throw new DataImporterError("Fehlende Datei", "Die Datei " + path.toAbsolutePath() +
                    " kann nicht gelesen oder gefunden werden.");
        }
        return gson.fromJson(jsonString, ConfigurationData.class);
    }

    private ParameterData getParameter() throws DataImporterError {
        return new ParameterData(this.getParameters().getNamed());
    }

    @Override
    public void start(Stage primaryStage) {
        try { start_unsafe(primaryStage); }
        catch (DataImporterError error) { this.showErrorWindow(primaryStage, error.errorTitle, error.errorDescription); }
        catch (RuntimeException error) {
           this.showErrorWindow(primaryStage, "Interner Fehler", error.getMessage());
        }
    }

    private void start_unsafe(Stage primaryStage) throws DataImporterError {

        // Read parameter and configuration data
        ParameterData parameterData = this.getParameter();
        ConfigurationData configurationData = this.getConfiguration(Paths.get("config.json"));

        // Retrieve profile data
        Profile profile = Profile.fromConfigurationData(configurationData, parameterData);

        // Load Main Scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        Parent scene;
        try{ scene = loader.load(); }
        catch (Exception error) {
            throw new DataImporterError("Internal Error", "Cannot load Main.fxml: " + error.getMessage());
        }

        // Init Main Controller
        MainController controller = loader.getController();
        controller.setDimensions(configurationData.windowWidth, configurationData.windowHeight, configurationData.keyColumnWidth);
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
