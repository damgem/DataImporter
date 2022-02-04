package com.damgem.DataImporter.Data;

import com.damgem.DataImporter.DataImporterError;
import com.damgem.DataImporter.Field.FieldBlueprint;
import javafx.scene.chart.PieChart;

import java.util.List;

public class Profile {
    public String target;
    public String subTarget;
    public List<FieldBlueprint> mapping;

    public Profile(Profile other) {
        this.target = other.target;
        this.subTarget = other.subTarget;
        this.mapping = other.mapping;
    }

    // statically remember active profile
    static Profile activeProfile = null;

    public static Profile getActiveProfile() throws DataImporterError
    {
        if(activeProfile == null)
        {
            String profileName = extractActiveProfileName();

            ConfigurationData configurationData = ConfigurationData.getInstance();
            activeProfile = configurationData.profiles.get(profileName);

            if(activeProfile == null)
                throw new DataImporterError("Fehlendes Profil",
                        "Das Profil \"" + profileName + "\" ist nicht angelegt."
                        + " (legacyMode ist " + (configurationData.legacyMode ? "aktiviert" : "deaktiviert") + ")");
        }

        return activeProfile;
    }

    private static String extractActiveProfileName() throws DataImporterError
    {
        ConfigurationData configurationData = ConfigurationData.getInstance();
        if (configurationData.legacyMode) return configurationData.legacyProfile;

        ParameterData parameterData = ParameterData.getInstance();

        // get index of seperator
        int indexOfSeparator = parameterData.values.indexOf(';'); // first occurrence of ';'
        if (indexOfSeparator == -1) {
            throw new DataImporterError("Fehler in Eingabe",
                    "Eingabe enthält keine Profil Information und Legacy Mode ist nicht aktiviert.");
        }

        // split into 2 while skipping ';' completely
        String profileName = parameterData.values.substring(0, indexOfSeparator);
        parameterData.values = parameterData.values.substring(indexOfSeparator + 1);

        // return extracted profile name
        return profileName;
    }
}