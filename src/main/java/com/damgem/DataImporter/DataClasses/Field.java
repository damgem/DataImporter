package com.damgem.DataImporter.DataClasses;

import com.google.gson.annotations.Expose;

public class Field {
    @Expose(deserialize = false)
    public String key;

    public Integer sourceColumnIndex = null;
    public boolean isRequired = false;
    public boolean isHidden = false;
    public boolean isDisabled = false;
    public String valueMapName = null;

    public Field() {}
    public Field(int sourceColumnIndex) { this.sourceColumnIndex = sourceColumnIndex; }

    public Field(Field other) {
        this.key = other.key;
        this.sourceColumnIndex = other.sourceColumnIndex;
        this.isRequired = other.isRequired;
        this.isHidden = other.isHidden;
        this.isDisabled = other.isDisabled;
    }
}
