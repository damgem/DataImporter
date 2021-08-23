package com.damgem.dataImporter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Objects;

public class ErrorController {
    @FXML
    public Label errorTitle;
    public Text errorDescription;

    public void setErrorMessage(String title, String description) {
        this.errorTitle.setText(title);
        this.errorDescription.setText(description);
        System.out.println("[" + title + "]" + description);
    }

    public void close(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

}
