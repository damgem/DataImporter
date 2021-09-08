module com.damgem.dataImporter {
    requires javafx.controls;
    requires org.apache.poi.poi;
    // requires org.apache.poi.ooxml;
    requires com.healthmarketscience.jackcess;
    requires com.google.gson;
    requires javafx.fxml;

    opens com.damgem.DataImporter to com.google.gson;
    exports com.damgem.DataImporter;
    exports com.damgem.DataImporter.Connector;
    opens com.damgem.DataImporter.Connector to com.google.gson;
    exports com.damgem.DataImporter.DataClasses;
    opens com.damgem.DataImporter.Field to com.google.gson;
    exports com.damgem.DataImporter.Data;
    opens com.damgem.DataImporter.Data to com.google.gson;
    exports com.damgem.DataImporter.Controller;
    opens com.damgem.DataImporter.Controller to com.google.gson;
}