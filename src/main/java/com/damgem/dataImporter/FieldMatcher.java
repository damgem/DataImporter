package com.damgem.dataImporter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FieldMatcher {

    private final List<FieldBlueprint> blueprints;

    public FieldMatcher(List<FieldBlueprint> blueprints) {
        this.blueprints = blueprints;
    }

    public List<Field> match(String valuesConcatStr) throws FieldMatcher.Error{
        String[] values = valuesConcatStr.split(";");

        List<Field> fields = new ArrayList<>(blueprints.size());
        for (FieldBlueprint bp : blueprints) {
            fields.add(bp.fillFrom(values));
        }
        return fields;
    }

    public static class Error extends Exception {
        String errorTitle, errorDescription;

        public Error(String errorTitle, String errorDescription) {
            super(errorTitle + ": " + errorDescription);
            this.errorTitle = errorTitle;
            this.errorDescription = errorDescription;
        }
    }
}
