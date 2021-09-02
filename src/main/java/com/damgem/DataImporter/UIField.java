package com.damgem.DataImporter;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class UIField<T> extends Configuration.Field {

    public Property<T> value = new SimpleObjectProperty<>(null);;
    public Property<Predicate<T>> validator;
    public BooleanProperty isValid = new SimpleBooleanProperty(true);

    public UIField(Configuration.Field field) {
        super(field);
        this.validator = new SimpleObjectProperty<>(x -> true);
        this.isValid.bind(Bindings.createBooleanBinding(
                () -> this.validator.getValue().test(this.value.getValue()),
                this.value,
                this.validator
        ));
    }

    public UIField(T value, Configuration.Field field) throws TitledError {
        this(field);
        if(this.valueMapName != null) {
            value = (T) Configuration.getValueMap(this.valueMapName).get(value);
        }
        this.value.setValue(value);
    }

    public UIField(String value) throws TitledError {
    }

    public UIField fillFrom(String[] values) throws TitledError {
        if(this.sourceColumnIndex == null) return this.fill("");
        if(this.sourceColumnIndex >= values.length) {
            throw new TitledError("Eingabe Fehler", "Das Feld \"" + this.name + "\" soll " +
                    "aus dem " + this.sourceColumnIndex + ". Feld (Indizes starten bei 0) gelesen werden. Die " +
                    "Eingabe hat jedoch nur " + values.length + " Datenfelder!");
        }
        if(this.sourceColumnIndex < 0) {
            throw new TitledError("Konfigurations Fehler", "Das Feld \"" + this.name +
                    "\" soll aus dem Feld mit negativem Index " + this.sourceColumnIndex + " gelesen werden!");
        }
        return this.fill(values[this.sourceColumnIndex]);
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