package com.damgem.DataImporter.Data;

import com.damgem.DataImporter.Field.FieldBlueprint;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class FieldBlueprintListDeserializer implements JsonDeserializer<List<FieldBlueprint>>
{
    @Override
    public List<FieldBlueprint> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        if(!json.isJsonObject())
            throw new JsonParseException("<ConfigError> Expected FieldBlueprint to be a json object.");

        return json.getAsJsonObject().entrySet().stream()
                .map(e -> parseFieldBlueprint(e.getKey(), e.getValue(), context))
                .collect(Collectors.toList());
    }

    private FieldBlueprint parseFieldBlueprint(String name, JsonElement element, JsonDeserializationContext context)
            throws JsonParseException
    {
        if(element.isJsonNull()) return new FieldBlueprint(name);

        if(element.isJsonPrimitive())
        {
            JsonPrimitive prim = element.getAsJsonPrimitive();
            if(prim.isNumber()) return new FieldBlueprint(name, prim.getAsInt());
            // else fall through to error
        }
        else if(element.isJsonObject())
        {
            JsonObject obj = element.getAsJsonObject();
            obj.add("name", new JsonPrimitive(name));
            return context.deserialize(obj, FieldBlueprint.class);
        }

        // if no conversion was applicable then throw an error
        throw new JsonParseException("<ConfigError> Expected key " + name + " to be an object or integer");
    }
}
