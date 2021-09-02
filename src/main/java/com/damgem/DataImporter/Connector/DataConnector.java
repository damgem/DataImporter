package com.damgem.DataImporter.Connector;

import com.damgem.DataImporter.TitledError;
import com.damgem.DataImporter.UIField;

import java.util.List;

public interface DataConnector {
    void write(String target, String subTarget, List<UIField> data) throws TitledError;
}
