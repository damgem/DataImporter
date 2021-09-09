package com.damgem.DataImporter.Controller;

import com.damgem.DataImporter.Config;
import com.damgem.DataImporter.Connector.AccessConnector;
import com.damgem.DataImporter.Connector.DataConnector;
import com.damgem.DataImporter.Connector.ExcelConnector;
import com.damgem.DataImporter.TitledError;
import com.damgem.DataImporter.UIStringField;
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

    private List<UIStringField> fields;
    private final Property<Boolean> disabled = new SimpleBooleanProperty(false);

    DataConnector dataConnector;

    String target;
    String subTarget;

    public void setFields(List<UIStringField> fields) {
        this.fields = fields;
        boolean customSelection = false;
        for (int fi = 0; fi < fields.size(); fi++) {
            UIStringField f = fields.get(fi);
            this.grid.add(new FieldName(f), 0, fi);
            FieldValue fv = new FieldValue(f, fi);
            this.grid.add(fv, 1, fi);
            if(!customSelection && f.value.getValue().isEmpty()){
                customSelection = true;
                fv.requestFocus();
            }
        }
    }

    public void initTargets() throws TitledError {
        // set target label
        target = Config.getProfile().target;
        targetLabel.setText("Target: " + target);

        // set sub-target label (leave empty if null)
        subTarget = Config.getProfile().subTarget;
        subTargetLabel.setText(subTarget == null ? "" : "Sub-Target: " + subTarget);

        // determine and initialize connector type
        if(target.endsWith(".mdb"))
        {
            dataConnector = new AccessConnector(target, subTarget);
        }
        else if(target.endsWith(".xls"))
        {
            dataConnector = new ExcelConnector(target, subTarget);
        }
        else
        {
            throw new TitledError("Fehlerhafte Konfiguration", "Format des Targets " + target +
                    " nicht erkannt.");
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
        } catch (TitledError error) {
            this.errorDialog(error.title, error.description);
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
}
