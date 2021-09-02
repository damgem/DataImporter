package com.damgem.DataImporter;

import com.damgem.DataImporter.Data.Profile;

import java.util.ArrayList;
import java.util.List;

public class FieldMatcher {

    static public List<UIField> match(Profile profile, String valuesConcatStr) throws TitledError {
        String[] values = valuesConcatStr.split(";");

        List<UIField> fields = new ArrayList<>(blueprints.size());
        for (FieldBlueprint bp : blueprints) {
            fields.add(bp.fillFrom(values));
        }
        return fields;
    }
}
