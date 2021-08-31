package com.damgem.DataImporter.Data;

import com.damgem.DataImporter.DataImporterError;

import java.util.Map;

public class ParameterData {
    public String values;

    public ParameterData(Map<String, String> namedParameters) throws DataImporterError {
        if (!namedParameters.containsKey("values")) {
            throw new DataImporterError("Ungültiger Aufruf", "Der Kommandozeilenparameter " +
                    "--values=\"<data>\" fehlt.");
        }

        values = namedParameters.get("values");
        if (values == null || values.isEmpty()) {
            throw new DataImporterError("Leere Eingabe", "Eingabe ist leer.");
        }
    }
}

