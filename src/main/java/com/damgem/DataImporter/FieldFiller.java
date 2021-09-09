package com.damgem.DataImporter;

import com.damgem.DataImporter.DataClasses.Field;

import java.util.List;

public class FieldFiller {

    List<String> values;

    public FieldFiller(List<String> values) {
        this.values = values;
    }

    public static List<UIStringField> fill(List<Field> fields) {
        return fields.stream().map(f -> {
            UIStringField uif = new UIStringField();


        })
    }

}
