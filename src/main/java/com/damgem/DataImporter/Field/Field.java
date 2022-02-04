package com.damgem.DataImporter.Field;

import com.damgem.DataImporter.Data.ConfigurationData;
import com.damgem.DataImporter.Data.ValueMap;
import com.damgem.DataImporter.DataImporterError;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import java.util.Objects;

public class Field {

    public FieldBlueprint blueprint;

    public StringProperty value;
    public BooleanProperty isValid;

    public Field(FieldBlueprint blueprint) {
        this.blueprint = blueprint;
        this.value = new SimpleStringProperty(null);
        this.isValid = new SimpleBooleanProperty(!this.blueprint.isRequired);
        this.isValid.bind(Bindings.createBooleanBinding(
                () -> {
                    if(!this.blueprint.isRequired) return true;
                    String val = this.value.getValue();
                    return val != null && !val.isEmpty();
                },
                this.value
        ));
    }

    public Field(FieldBlueprint blueprint, String value) {
        this(blueprint);
        this.value.setValue(Objects.requireNonNullElse(value, ""));
    }

    public NamedValue extractData() throws DataImporterError
    {
        String name = blueprint.name;
        String value = Objects.requireNonNullElse(this.value.getValue(), "");
        final String valueMapName = blueprint.valueMapName;

        // check if value mapping is desired
        if(valueMapName != null)
        {
            ConfigurationData configurationData = ConfigurationData.getInstance();
            ValueMap valueMap = configurationData.valueMaps.get(blueprint.valueMapName);

            if(valueMap != null)
            {
                // check if value can be mapped
                if(!valueMap.containsKey(value)) {
                    throw new DataImporterError("Ungültiger Wert",
                            "Die ValueMap \"" + blueprint.valueMapName + "\" auf dem Feld \"" + name +
                                    "\" kann den Wert \"" + value + " \" nicht übersetzen.");
                }

                // map the value
                value = valueMap.get(value);
            }
        }

        // return the key value pair
        return new NamedValue(name, value);
    }

    @Override
    public String toString() {
        return "Field{" +
                "value=" + value +
                ", isValid=" + isValid +
                ", name='" + blueprint.name + '\'' +
                ", sourceColumnIndex=" + blueprint.sourceColumnIndex +
                ", isRequired=" + blueprint.isRequired +
                '}';
    }
}