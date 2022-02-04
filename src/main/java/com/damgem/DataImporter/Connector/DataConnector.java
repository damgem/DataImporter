package com.damgem.DataImporter.Connector;

import com.damgem.DataImporter.DataImporterError;
import com.damgem.DataImporter.Field.NamedValue;

import java.util.List;

public interface DataConnector {
    void write(String target, String subTarget, List<NamedValue> data) throws DataImporterError;
}
