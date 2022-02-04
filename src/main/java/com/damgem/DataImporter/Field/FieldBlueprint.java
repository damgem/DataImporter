package com.damgem.DataImporter.Field;

import com.damgem.DataImporter.DataImporterError;

import java.util.Objects;

public class FieldBlueprint {

    public final String name;
    public final Integer sourceColumnIndex;
    public final boolean isRequired;
    public final String valueMapName;

    public FieldBlueprint(String name, Integer sourceColumnIndex, Boolean isRequired, String valueMapName)
    {
        this.name = Objects.requireNonNullElse(name, "");
        this.sourceColumnIndex = sourceColumnIndex;
        this.isRequired = Objects.requireNonNullElse(isRequired, false);
        this.valueMapName = valueMapName;
    }

    public FieldBlueprint(String name, Integer sourceColumnIndex) {
        this(name, sourceColumnIndex, null, null);
    }

    public FieldBlueprint(String name) {
        this(name, null);
    }

    public FieldBlueprint() {
        this(null);
    }

    @Override
    public String toString() {
        return "FieldBlueprint{" +
                "name='" + name + '\'' +
                ", sourceColumnIndex=" + sourceColumnIndex +
                ", isRequired=" + isRequired +
                '}';
    }
}
