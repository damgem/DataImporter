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
        if(this.sourceColumnIndex >= values.length) {
            throw new DataImporterError("Eingabe Fehler", "Das Feld \"" + this.name + "\" soll " +
                    "aus dem " + this.sourceColumnIndex + ". Feld (Indizes starten bei 0) gelesen werden. Die " +
                    "Eingabe hat jedoch nur " + values.length + " Datenfelder!");
        }
        if(this.sourceColumnIndex < 0) {
            throw new DataImporterError("Konfigurations Fehler", "Das Feld \"" + this.name +
                    "\" soll aus dem Feld mit negativem Index " + this.sourceColumnIndex + " gelesen werden!");
        }
        return this.fill(values[this.sourceColumnIndex]);
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
