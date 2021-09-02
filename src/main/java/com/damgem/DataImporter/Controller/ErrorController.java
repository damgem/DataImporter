package com.damgem.DataImporter.Controller;

import com.damgem.DataImporter.TitledError;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ErrorController implements Initializable {
    @FXML
    public Label errorTitle;
    public Text errorDescription;

    public void setError(TitledError error) {
        this.errorTitle.setText(error.title);
        this.errorDescription.setText(error.description);
    }

    public void close(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
