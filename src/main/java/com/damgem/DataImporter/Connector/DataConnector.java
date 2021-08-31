package com.damgem.DataImporter.Connector;

import com.damgem.DataImporter.DataImporterError;
import com.damgem.DataImporter.Field.Field;

import java.util.List;

public interface DataConnector {
    void write(String target, String subTarget, List<Field> data) throws DataImporterError;
}
