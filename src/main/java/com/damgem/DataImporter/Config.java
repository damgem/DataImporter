package com.damgem.DataImporter;

import com.damgem.DataImporter.DataClasses.Field;
import com.damgem.DataImporter.DataClasses.JsonConfiguration;
import com.damgem.DataImporter.DataClasses.Profile;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Config {

    private static JsonConfiguration jsonConfig;
    private static Profile currentProfile;


    public static boolean initProfile(String name) throws TitledError {
        if(jsonConfig.isLegacyMode()) {
            currentProfile = jsonConfig.getLegacyProfile();
            return false;
        }
        currentProfile = jsonConfig.getProfile(name);
        return true;
    }

    public static Profile getProfile() {
        return currentProfile;
    }

    public static Map<String, Object> getValueMap(String name) throws TitledError {
        return jsonConfig.getValueMap(name);
    }

    public static void load(Path filePath) throws TitledError {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Field.class, (JsonDeserializer<Field>)(JsonElement json, Type type, JsonDeserializationContext context) -> {
                        if(json.isJsonObject()) return new Gson().fromJson(json, type);
                        if(json.isJsonNull()) return new Field();
                        return new Field(json.getAsInt());
                })
                .registerTypeAdapter(new TypeToken<List<Field>>() {}.getType(), (JsonDeserializer<List<Field>>)(JsonElement json, Type type, JsonDeserializationContext context) ->
                        json.getAsJsonObject().entrySet().stream().map(e -> {
                                Field field = context.deserialize(e.getValue(), Field.class);
                                field.key = e.getKey();
                                return field;
                        }).collect(Collectors.toList())
                )
                .create();

        try {
            String jsonString = Files.readString(filePath);
            jsonConfig = gson.fromJson(jsonString, JsonConfiguration.class);
        } catch (Exception e) {
            throw new TitledError();
        }
    }
}

