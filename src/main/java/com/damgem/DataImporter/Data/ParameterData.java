package com.damgem.DataImporter.Data;

import com.damgem.DataImporter.DataImporterError;

import java.util.Map;

public class ParameterData {
    public String values;
    public String configFile;

    private ParameterData(Map<String, String> namedParameters) throws DataImporterError {
        if (!namedParameters.containsKey("values")) {
            throw new DataImporterError("Ungültiger Aufruf", "Der Kommandozeilenparameter " +
                    "--values=\"<data>\" fehlt.");
        }

        values = namedParameters.get("values");
        if (values == null || values.isEmpty()) {
            throw new DataImporterError("Leere Eingabe", "Eingabe ist leer.");
        }

        configFile = namedParameters.get("config");
    }

    // Singleton Pattern
    static ParameterData instance = null;

    static public void initialize(Map<String, String> namedParameters) throws DataImporterError {
        instance = new ParameterData(namedParameters);
    }

    static public ParameterData getInstance() throws DataImporterError {
        if(instance == null)
            throw new DataImporterError("Internal Error", "ParameterData uninitialized!");

        return instance;
    }
}

