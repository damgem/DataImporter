package com.damgem.DataImporter;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import org.apache.poi.ss.formula.functions.T;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Configuration {
    public static boolean legacyMode = false;

    private static String legacyProfile = null;
    private static Map<String, Profile> profiles = new HashMap<>();
    private static Map<String, Map<String, Object>> valueMaps = new HashMap<>();

    public static Profile getProfile(String profileName) throws TitledError {
        if(!profiles.containsKey(profileName)) throw new TitledError();
        return profiles.get(profileName);
    }

    public static Profile getLegacyProfile() throws TitledError {
        if(legacyProfile == null) throw new TitledError();
        return getProfile(legacyProfile);
    }

    public static class Profile {
        public String target;
        public String subTarget;
        public List<Field> mapping;
    }

    public static Map<String, Object> getValueMap(String name) throws TitledError {
        if(!valueMaps.containsKey(name)) throw new TitledError();
        return valueMaps.get(name);
    }

    @Expose(deserialize = false)
    private static Profile currentProfile;

    public static Profile getCurrentProfile() {
        return currentProfile;
    }

    public static void setCurrentProfile(Profile profile) {
        currentProfile = profile;
    }

    public static class Field {
        @Expose(deserialize = false)
        public String key;

        public Integer sourceColumnIndex = null;
        public boolean isRequired = false;
        public boolean isHidden = false;
        public boolean isDisabled = false;
        public String valueMapName = null;

        public Field() {}
        public Field(int sourceColumnIndex) { this.sourceColumnIndex = sourceColumnIndex; }

        public Field(Field other) {
            this.key = other.key;
            this.sourceColumnIndex = other.sourceColumnIndex;
            this.isRequired = other.isRequired;
            this.isHidden = other.isHidden;
            this.isDisabled = other.isDisabled;
        }
    }

    public static class ConvenienceFieldDeserializer implements JsonDeserializer<Configuration.Field> {
        @Override
        public Configuration.Field deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            if(json.isJsonObject()) return new Gson().fromJson(json, type);
            if(json.isJsonNull()) return new Configuration.Field();
            return new Configuration.Field(json.getAsInt());
        }
    }

    public static class FieldListDeserializer implements JsonDeserializer<List<Configuration.Field>> {
        @Override
        public List<Field> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return json.getAsJsonObject().entrySet().stream().map(e -> {
                Field field = context.deserialize(e.getValue(), Field.class);
                field.key = e.getKey();
                return field;
            }).collect(Collectors.toList());
        }
    }

    /**
     * Loads the values of the specified configuration file into the static fields of the {@link Configuration} class.
     * @param filePath path to the configuration file
     */
    public static void load(Path filePath) throws TitledError {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .registerTypeAdapter(Field.class, new ConvenienceFieldDeserializer())
                .registerTypeAdapter(new TypeToken<List<Field>>() {}.getType(), new FieldListDeserializer())
                .create();

        try {
            String jsonString = Files.readString(filePath);
            gson.fromJson(jsonString, Configuration.class);
        } catch (Exception e) {
            throw new TitledError();
        }
    }
}

