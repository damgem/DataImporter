package com.damgem.dataImporter;

import java.util.List;

public interface DataConnector {
    List<String> readLastRow(String path) throws DataConnectorError;
    void write(String target, String subTarget, List<Field> data) throws DataConnectorError;
}
