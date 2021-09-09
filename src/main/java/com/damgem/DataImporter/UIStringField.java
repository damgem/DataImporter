package com.damgem.DataImporter;

import com.damgem.DataImporter.DataClasses.Field;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UIStringField extends Field {

    public Property<String> value = new SimpleObjectProperty<>(null);;
    public BooleanProperty isValid = new SimpleBooleanProperty(true);
    public BooleanProperty isDisabled = new SimpleBooleanProperty(false);

    public UIStringField(String value, Field field) throws TitledError {
        super(field);
        isValid.bind(Bindings.createBooleanBinding(() -> {
            boolean isEmpty = value == null || !value.isEmpty();
            return !isRequired || isEmpty;
        }));
    }

    public Parent render() {
        Label label = new Label(key);
        label.setTooltip(new Tooltip(key));
        if(isRequired) label.setStyle("-fx-font-weight: bold");

        TextField field = new TextField(value.getValue());
        value.bindBidirectional(field.textProperty());

        field.styleProperty().bind(Bindings.createStringBinding(() -> {
            if (isValid.getValue()) return "";
            return "-fx-background-color: #fa8072,linear-gradient(to bottom, derive(#fa8072,60%) 5%,derive(#fa8072,90%) 40%);";
        }));

        Parent parent = new HBox(label, field);
        parent.disableProperty().bindBidirectional(isDisabled);
        parent.setVisible(!isHidden);
        return parent;
    }
}