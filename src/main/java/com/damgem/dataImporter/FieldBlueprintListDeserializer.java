package com.damgem.dataImporter;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class FieldBlueprintListDeserializer implements JsonDeserializer<List<FieldBlueprint>> {
    @Override
    public List<FieldBlueprint> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return json.getAsJsonObject().entrySet().stream()
                .map(e -> {
                    FieldBlueprint namelessBlueprint = context.deserialize(e.getValue(), FieldBlueprint.class);
                    if(namelessBlueprint == null) namelessBlueprint = new FieldBlueprint();
                    return new FieldBlueprint(e.getKey(), namelessBlueprint);
                })
                .collect(Collectors.toList());
    }
}
