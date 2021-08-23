package com.damgem.dataImporter;

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
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Main extends Application {

    public void errorScene(Stage primaryStage, String errorTitle, String errorDescription) {
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
        Map<String, String> params = this.getParameters().getNamed();
        if(!params.containsKey("values")) {
            this.errorScene(primaryStage, "Ung\u00FCltiger Aufruf", "Der Kommandozeilenparameter --values fehlt.");
            return;
        }

        // Create gson parser
        Type FieldBlueprintList = new TypeToken<List<FieldBlueprint>>() {}.getType();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(FieldBlueprint.class, new FieldBlueprintDeserializer())
                .registerTypeAdapter(FieldBlueprintList, new FieldBlueprintListDeserializer())
                .create();

        // Parse Json / Load Profiles
        Type profilesType = new TypeToken<Map<String, ConnectorConfig>>() {}.getType();
        String jsonString;
        try { jsonString = Files.readString(Paths.get("config.json")); }
        catch (Exception error) {
            this.errorScene(primaryStage, "Fehlende Konfiguration", "Die Datei config.json kann nicht gelesen oder gefunden werden.\n\n" + error.getLocalizedMessage());
            return;
        }
        Map<String, ConnectorConfig> profiles = gson.fromJson(jsonString, profilesType);

        // Select Profile
        ConnectorConfig profile = profiles.get("FB");

        // Load Main Scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        Parent scene;
        try{ scene = loader.load(); }
        catch (Exception error) {
            this.errorScene(primaryStage, "Internal Error", "Cannot load Main.fxml: " + error.getLocalizedMessage());
            return;
        }

        // Init Main Controller
        MainController controller = loader.getController();
        try {
            controller.setFields(new FieldMatcher(profile.mapping).match(params.get("values")));
        } catch (FieldMatcher.Error error) {
            this.errorScene(primaryStage, error.errorTitle, error.errorDescription);
            return;
        }
        controller.setTarget(Objects.requireNonNullElse(profile.target, ""), Objects.requireNonNullElse(profile.subTarget, ""));

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
