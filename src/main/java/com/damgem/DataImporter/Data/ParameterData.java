package com.damgem.DataImporter.Data;

import com.damgem.DataImporter.TitledError;

import java.util.Map;

public class ParameterData {
    public String values;

    public ParameterData(Map<String, String> namedParameters) throws TitledError {
        if (!namedParameters.containsKey("values")) {
            throw new TitledError("Ungültiger Aufruf", "Der Kommandozeilenparameter " +
                    "--values=\"<data>\" fehlt.");
        }

        values = namedParameters.get("values");
        if (values == null || values.isEmpty()) {
            throw new TitledError("Leere Eingabe", "Eingabe ist leer.");
        }
    }
}

