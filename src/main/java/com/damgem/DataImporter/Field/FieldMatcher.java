package com.damgem.DataImporter.Field;

import com.damgem.DataImporter.DataImporterError;

import java.util.ArrayList;
import java.util.List;

public class FieldMatcher {

    private final List<FieldBlueprint> blueprints;

    public FieldMatcher(List<FieldBlueprint> blueprints) {
        this.blueprints = blueprints;
    }

    public List<Field> match(String valuesConcatStr) throws DataImporterError {
        String[] values = valuesConcatStr.split(";");

        List<Field> fields = new ArrayList<>(blueprints.size());
        for (FieldBlueprint bp : blueprints) {
            fields.add(bp.fillFrom(values));
        }
        return fields;
    }
}
