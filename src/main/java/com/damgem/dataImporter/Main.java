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
        Map<String, String> params = this.getParameters().getNamed();
        if(!params.containsKey("values")) {
            this.showErrorWindow(primaryStage, "Ung\u00FCltiger Aufruf", "Der Kommandozeilenparameter --values fehlt.");
            return;
        }

        // Get Parameter
        String paramString = params.get("values");
        if(paramString.isEmpty()) {
            this.showErrorWindow(primaryStage, "Leere Eingabe", "Eingabe ist leer.");
            return;
        }

        // Create gson parser
        Type FieldBlueprintList = new TypeToken<List<FieldBlueprint>>() {}.getType();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(FieldBlueprint.class, new FieldBlueprintDeserializer())
                .registerTypeAdapter(FieldBlueprintList, new FieldBlueprintListDeserializer())
                .create();

        // Parse Json / Load Profiles
        String jsonString;
        try { jsonString = Files.readString(Paths.get("config.json")); }
        catch (Exception error) {
            this.showErrorWindow(primaryStage, "Fehlende Konfiguration", "Die Datei config.json " +
                    "kann nicht gelesen oder gefunden werden.\n\n" + error.getMessage());
            return;
        }
        ConfigFile cfg = gson.fromJson(jsonString, ConfigFile.class);

        // Select Profile
        ConnectorConfig profile;
        if(cfg.legacyMode) {
            if(cfg.legacyProfile == null || cfg.legacyProfile.isEmpty()) {
                this.showErrorWindow(primaryStage, "Fehler in Konfiguration", "\"legacyMode\" ist " +
                        "aktiviert, aber es ist kein legacyProfile konfiguriert.");
                return;
            }
            if(!cfg.profiles.containsKey(cfg.legacyProfile)) {
                this.showErrorWindow(primaryStage, "Fehler in Konfiguration", "legacyMode ist " +
                        "aktiviert, aber legacyProfile gibt kein g\u00FCltiges Profil an: \"" + cfg.legacyProfile + "\"");
                return;
            }
            profile = cfg.profiles.get(cfg.legacyProfile);
        }
        else {
            int indexOfSeperator = paramString.indexOf(';');
            if(indexOfSeperator == -1) {
                this.showErrorWindow(primaryStage, "Fehler in Eingabe", "Eingabe \"" + paramString
                        + "\" enth\u00E4lt keine Profil Information und Legacy Mode ist nicht aktiviert.");
                return;
            }
            String profileName = paramString.substring(0, indexOfSeperator);
            paramString = paramString.substring(indexOfSeperator);

            if(!cfg.profiles.containsKey(profileName)) {
                this.showErrorWindow(primaryStage, "Profil nicht gefunden", "legacyMode ist " +
                        "deaktiviert, und die Eingabe gibt ein nicht existierendes Profil \"" + profileName + "\" an.");
                return;
            }

            profile = cfg.profiles.get(profileName);
        }

        // Load Main Scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        Parent scene;
        try{ scene = loader.load(); }
        catch (Exception error) {
            this.showErrorWindow(primaryStage, "Internal Error", "Cannot load Main.fxml: " +
                    error.getMessage());
            return;
        }

        // Init Main Controller
        MainController controller = loader.getController();
        try {
            controller.setFields(new FieldMatcher(profile.mapping).match(paramString));
        } catch (FieldMatcher.Error error) {
            this.showErrorWindow(primaryStage, error.errorTitle, error.errorDescription);
            return;
        }
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
