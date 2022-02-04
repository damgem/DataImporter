package com.damgem.DataImporter.Data;

import com.damgem.DataImporter.DataImporterError;
import com.damgem.DataImporter.Field.FieldBlueprint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ConfigurationData {
    public Boolean legacyMode;
    public String legacyProfile;
    public Integer windowWidth;
    public Integer windowHeight;
    public Integer keyColumnWidth;
    public Map<String, Profile> profiles;
    public Map<String, ValueMap> valueMaps;

    // Singleton Pattern
    private ConfigurationData() {}
    private static ConfigurationData instance = null;

    public static void initializeFromJsonString(String configStr)
    {
        // Create gson parser
        Type fblType = new TypeToken<List<FieldBlueprint>>() {}.getType();
        JsonDeserializer<List<FieldBlueprint>> fblDeserializer = new FieldBlueprintListDeserializer();
        Gson gson = new GsonBuilder().registerTypeAdapter(fblType, fblDeserializer).create();

        // parse config string
        instance = gson.fromJson(configStr, ConfigurationData.class);
    }

    public static void initializeFromFile(String filePathStr) throws DataImporterError
    {
        Path filePath = Paths.get(filePathStr);

        // Read json data
        String jsonString;
        try {
            jsonString = Files.readString(filePath);
        }
        catch (Exception error) {
            throw new DataImporterError("Fehlende Datei", "Die Datei "
                    + filePath.toAbsolutePath() + " kann nicht gelesen oder gefunden werden.");
        }

        // Initialize from json string
        initializeFromJsonString(jsonString);
    }

    public static ConfigurationData getInstance() throws DataImporterError {
        if(instance == null)
            throw new DataImporterError("Internal Error", "ConfigurationData is uninitialized.");

        return instance;
    }
}
