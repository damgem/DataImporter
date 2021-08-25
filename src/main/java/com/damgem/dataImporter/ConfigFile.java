package com.damgem.dataImporter;

import java.util.Map;


public class ConfigFile {
    Boolean legacyMode;
    String legacyProfile;
    Map<String, ConnectorConfig> profiles;
}
