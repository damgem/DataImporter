package com.damgem.DataImporter.Data;

import com.damgem.DataImporter.Configuration;
import com.damgem.DataImporter.TitledError;

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

    public static Profile fromConfigurationData(Configuration configurationData, ParameterData parameterData) throws TitledError {
        if(configurationData.legacyMode) {
            if(configurationData.legacyProfile == null || configurationData.legacyProfile.isEmpty()) {
                throw new TitledError("Fehler in Konfiguration", "\"legacyMode\" ist " +
                        "aktiviert, aber es ist kein legacyProfile konfiguriert.");
            }
            if(!configurationData.profiles.containsKey(configurationData.legacyProfile)) {
                throw new TitledError("Fehler in Konfiguration", "legacyMode ist " +
                        "aktiviert, aber legacyProfile gibt kein gültiges Profil an: \"" + configurationData.legacyProfile + "\"");
            }

            return configurationData.profiles.get(configurationData.legacyProfile);
        }
        else {
            int indexOfSeparator = parameterData.values.indexOf(';');
            if(indexOfSeparator == -1) {
                throw new TitledError("Fehler in Eingabe", "Eingabe enthält keine Profil " +
                        "Information und Legacy Mode ist nicht aktiviert.");
            }
            String profileName = parameterData.values.substring(0, indexOfSeparator);
            parameterData.values = parameterData.values.substring(indexOfSeparator+1);

            if(!configurationData.profiles.containsKey(profileName)) {
                throw new TitledError("Profil nicht gefunden", "legacyMode ist " +
                        "deaktiviert, und die Eingabe gibt ein nicht existierendes Profil \"" + profileName + "\" an.");
            }

            return configurationData.profiles.get(profileName);
        }
    }
}