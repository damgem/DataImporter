package com.damgem.DataImporter.Controller;

import com.damgem.DataImporter.Connector.AccessConnector;
import com.damgem.DataImporter.Connector.DataConnector;
import com.damgem.DataImporter.Connector.ExcelConnector;
import com.damgem.DataImporter.DataImporterError;
import com.damgem.DataImporter.Field.Field;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    // FXML Stuff
    public GridPane grid;
    public Button buttonCancel;
    public Button buttonConfirm;
    public Label targetLabel;
    public Label subTargetLabel;
    public AnchorPane anchor;
    public ColumnConstraints keyColumn;

    private List<Field> fields;
    private final Property<Boolean> disabled = new SimpleBooleanProperty(false);

    DataConnector dataConnector;

    String target;
    String subTarget;

    public void setFields(List<Field> fields) {
        this.fields = fields;
        boolean customSelection = false;
        for (int fi = 0; fi < fields.size(); fi++) {
            Field f = fields.get(fi);
            this.grid.add(new FieldName(f), 0, fi);
            FieldValue fv = new FieldValue(f, fi);
            this.grid.add(fv, 1, fi);
            if(!customSelection && f.value.getValue().isEmpty()){
                customSelection = true;
                fv.requestFocus();
            }
        }
    }

    public void setTarget(String target, String subTarget) throws DataImporterError {
        target = Objects.requireNonNullElse(target, "");
        this.targetLabel.setText("Target: " + target);
        this.target = target;

        subTarget = Objects.requireNonNullElse(subTarget, "");
        String subTargetPrefix = subTarget.isEmpty() ? "" : "Sub-Target: ";
        this.subTargetLabel.setText(subTargetPrefix + subTarget);
        this.subTarget = subTarget;

        if(target.endsWith(".mdb")) {
            this.dataConnector = new AccessConnector(target, subTarget);
        } else if(target.endsWith(".xls")) {
            this.dataConnector = new ExcelConnector(target, subTarget);
        } else {
            throw new DataImporterError("Fehlerhafte Konfiguration", "Format des Targets "
                    + target + " nicht erkannt.");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.buttonCancel.disableProperty().bind(this.disabled);
        this.buttonConfirm.disableProperty().bind(this.disabled);
        this.subTargetLabel.visibleProperty().bind(this.subTargetLabel.textProperty().isEmpty().not());
    }

    public void closeWindow() {
        Platform.exit();
    }

    public void importData() {
        this.disabled.setValue(true);

        boolean successful = false;
        try {
            dataConnector.write(this.target, this.subTarget, this.fields);
            successful = true;
        } catch (DataImporterError error) {
            this.errorDialog(error.errorTitle, error.errorDescription);
        } catch (Throwable error) {
            this.errorDialog("Internal Error", error.getMessage());
        }

        if(!successful) {
            PauseTransition pause = new PauseTransition(Duration.millis(250));
            pause.setOnFinished(event -> this.disabled.setValue(false));
            pause.play();
        } else {
            ColorInput effect = new ColorInput(0, 0, 1000, 1000, Color.rgb(0,0,0, 0));
            Timeline fade = new Timeline(
                    new KeyFrame(Duration.millis(100), new KeyValue(effect.paintProperty(), Paint.valueOf("green")))
            );
            this.grid.getScene().getRoot().setEffect(effect);
            fade.setOnFinished(e -> this.closeWindow());
            fade.play();
        }
    }

    public void errorDialog(String errorTitle, String errorDescription) {
        // Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Error.fxml"));
        Parent root;

        try{ root = loader.load(); }
        catch (IOException error) { throw new RuntimeException(error.getMessage()); }

        // Init controller
        ErrorController controller = loader.getController();
        controller.setErrorMessage(errorTitle, errorDescription);

        // Create stage
        Stage stage = new Stage();

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Data Importer Error");
        stage.getIcons().add(new Image("table2.png"));
        stage.setScene(new Scene(root));

        // Show stage
        stage.showAndWait();
    }

    public void setDimensions(Integer width, Integer height, Integer keyColumnWidth) {
        if(width != null) this.anchor.setPrefWidth(width);
        if(height != null) this.anchor.setPrefHeight(height);
        if(keyColumnWidth != null) this.keyColumn.setPrefWidth(keyColumnWidth);
    }

    private class FieldName extends Label {
        FieldName(Field field) {
            super(field.name);
            if(field.isRequired) this.setStyle("-fx-font-weight: bold");
            this.disableProperty().bind(disabled);
            this.setTooltip(new Tooltip(field.name));
        }
    }

   private class FieldValue extends TextField {
        FieldValue(Field field, int row){
            super(field.value.getValue());
            this.textProperty().bindBidirectional(fields.get(row).value);
            this.styleProperty().bind(Bindings.createStringBinding(
                    () -> field.isValid.getValue() ? "" : "-fx-background-color: #fa8072,linear-gradient(to bottom, derive(#fa8072,60%) 5%,derive(#fa8072,90%) 40%);",
                    field.isValid
            ));
            this.disableProperty().bind(disabled);
        }
   }
}
