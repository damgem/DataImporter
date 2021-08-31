package com.damgem.DataImporter.Field;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import java.util.Objects;

public class Field extends FieldBlueprint {

    public StringProperty value;
    public BooleanProperty isValid;

    public Field(FieldBlueprint blueprint) {
        super(blueprint);
        this.value = new SimpleStringProperty(null);
        this.isValid = new SimpleBooleanProperty(!this.isRequired);
        this.isValid.bind(Bindings.createBooleanBinding(
                () -> {
                    if(!this.isRequired) return true;
                    String val = this.value.getValue();
                    return val != null && !val.isEmpty();
                },
                this.value
        ));
    }

    public Field(String value, FieldBlueprint other) {
        this(other);
        this.value.setValue(Objects.requireNonNullElse(value, ""));
    }

    @Override
    public String toString() {
        return "Field{" +
                "value=" + value +
                ", isValid=" + isValid +
                ", name='" + name + '\'' +
                ", sourceColumnIndex=" + sourceColumnIndex +
                ", isRequired=" + isRequired +
                '}';
    }
}