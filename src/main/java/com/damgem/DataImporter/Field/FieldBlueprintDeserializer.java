package com.damgem.DataImporter.Field;

import com.google.gson.*;

import java.lang.reflect.Type;

public class FieldBlueprintDeserializer implements JsonDeserializer<FieldBlueprint> {
    @Override
    public FieldBlueprint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json.isJsonNull()) return new FieldBlueprint(null, null, false);
        if(json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) return new FieldBlueprint(null, json.getAsInt(), false);

        if(!json.isJsonObject()) throw new RuntimeException("Cannot parse json config");
        return new Gson().fromJson(json.getAsJsonObject(), FieldBlueprint.class);
    }
}
