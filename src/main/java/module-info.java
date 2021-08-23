module com.damgem.dataImporter {
    requires javafx.controls;
    requires org.apache.poi.poi;
    // requires org.apache.poi.ooxml;
    requires com.healthmarketscience.jackcess;
    requires com.google.gson;
    requires javafx.fxml;

    opens com.damgem.dataImporter to com.google.gson;
    exports com.damgem.dataImporter;
}