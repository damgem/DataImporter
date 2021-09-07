package com.damgem.DataImporter.Field;

import com.damgem.DataImporter.DataImporterError;

import java.util.Objects;

public class FieldBlueprint {

    public final String name;
    public final Integer sourceColumnIndex;
    public final boolean isRequired;

    public FieldBlueprint(String name, Integer sourceColumnIndex, Boolean isRequired) {
        this.name = Objects.requireNonNullElse(name, "");
        this.sourceColumnIndex = sourceColumnIndex;
        this.isRequired = Objects.requireNonNullElse(isRequired, false);
    }

    public FieldBlueprint() {
        this(null, null, null);
    }

    public FieldBlueprint(FieldBlueprint other) {
        this(other.name, other.sourceColumnIndex, other.isRequired);
    }

    public FieldBlueprint(String name, FieldBlueprint namelessBlueprint) {
        this(name, namelessBlueprint.sourceColumnIndex, namelessBlueprint.isRequired);
    }

    private Field fill(String value) { return new Field(value, this); }

    public Field fillFrom(String[] values) throws DataImporterError {
        if(this.sourceColumnIndex == null) return this.fill("");
        if(this.sourceColumnIndex > values.length) {
            throw new DataImporterError("Eingabe Fehler", "Das Feld \"" + this.name + "\" soll " +
                    "aus dem " + this.sourceColumnIndex + ". Feld gelesen werden. Die " +
                    "Eingabe hat jedoch nur " + values.length + " Datenfelder!");
        }
        if(this.sourceColumnIndex <= 0) {
            throw new DataImporterError("Konfigurations Fehler", "Das Feld \"" + this.name +
                    "\" soll aus dem Feld mit negativem Index gelesen werden: " + this.sourceColumnIndex);
        }
        // subtract 1 to index as source column index is an 1-based index
        return this.fill(values[this.sourceColumnIndex - 1]);
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
