package com.damgem.DataImporter.DataClasses;

import com.damgem.DataImporter.TitledError;

import java.util.HashMap;
import java.util.Map;

public class JsonConfiguration {

    private boolean legacyMode = false;
    private String legacyProfile = null;
    private Map<String, Profile> profiles = new HashMap<>();
    private Map<String, Map<String, Object>> valueMaps = new HashMap<>();

    public boolean isLegacyMode() {
        return legacyMode;
    }

    public Profile getProfile(String profileName) throws TitledError {
        if(!profiles.containsKey(profileName)) throw new TitledError();
        return profiles.get(profileName);
    }

    public Profile getLegacyProfile() throws TitledError {
        if(legacyProfile == null) throw new TitledError();
        return getProfile(legacyProfile);
    }

    public Map<String, Object> getValueMap(String name) throws TitledError {
        if(!valueMaps.containsKey(name)) throw new TitledError();
        return valueMaps.get(name);
    }
}
