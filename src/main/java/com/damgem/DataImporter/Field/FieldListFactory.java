package com.damgem.DataImporter.Field;

import com.damgem.DataImporter.Data.ParameterData;
import com.damgem.DataImporter.DataImporterError;

import java.util.ArrayList;
import java.util.List;

public class FieldListFactory {

    private final List<FieldBlueprint> blueprints;

    public FieldListFactory(List<FieldBlueprint> blueprints)
    {
        this.blueprints = blueprints;
    }

    public List<Field> create() throws DataImporterError
    {
        String[] values = ParameterData.getInstance().values.split(";");

        List<Field> fields = new ArrayList<>(blueprints.size());
        for (FieldBlueprint blueprint : blueprints)
        {
            Integer sci = blueprint.sourceColumnIndex;

            // add field with empty value if no source column index has been specified (or null has been specified)
            if (sci == null) {
                fields.add(new Field(blueprint, ""));
                continue;
            }

            // check validity of source column index
            // sci is one base index (starting with 1 instead of 0)
            if (sci < 1 || sci > values.length) {
                throw new DataImporterError("Ungültiger sourceColumnIndex",
                        "Das Feld \"" + blueprint.name + "\" hat sourceColumnIndex \""
                                + blueprint.sourceColumnIndex + "\". Es gibt jedoch nur Einträge von 1 bis "
                                + values.length + " Datenfelder!");
            }

            // make index zero based
            sci -= 1;

            // add field
            fields.add(new Field(blueprint, values[sci]));
        }

        return fields;
    }
}
