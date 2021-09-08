package com.damgem.DataImporter;

import com.damgem.DataImporter.DataClasses.Field;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import java.util.function.Predicate;

public class UIField<T> extends Field {

    public Property<T> value = new SimpleObjectProperty<>(null);;
    public BooleanProperty isValid = new SimpleBooleanProperty(true);


    public UIField(String value, Field field) throws TitledError {
        super(field);
        isValid.bind(Bindings.createBooleanBinding(() -> {
            boolean isRequiredOK = isRequired ? : true;
        }));
    }
}