package com.prodactivv.excelimporter.utils;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class ObservedEntryView {

    private Circle status;
    private ImageView stop;
    private final String name;

    private HBox wrapper;

    private IStopButtonListener stopButtonListener;

    public ObservedEntryView(String name) {
        this.name = name;
        init(name);
    }

    public Node getView() {
        return wrapper;
    }

    public void setStopButtonListener(IStopButtonListener stopButtonListener) {
        this.stopButtonListener = stopButtonListener;
    }

    public String getName() {
        return name;
    }

    private void init(String labelName) {
        Label name = new Label(labelName);
        name.setMinWidth(330);
        name.setTooltip(new Tooltip(labelName));
        status = new Circle(8, Paint.valueOf("#9e9e9e"));

        initStopButton();

        wrapper = new HBox(name, status, stop);
        wrapper.setAlignment(Pos.CENTER_LEFT);
        wrapper.setSpacing(10);
    }

    @SuppressWarnings("ConstantConditions")
    private void initStopButton() {
        stop = new ImageView(new Image(getClass().getResourceAsStream("/remove.png")));
        stop.setFitHeight(20);
        stop.setFitWidth(20);
        stop.setPreserveRatio(true);
        stop.setSmooth(true);
        stop.setCache(true);
        stop.styleProperty().bind(
                Bindings.when(stop.hoverProperty())
                        .then(new SimpleStringProperty("-fx-cursor: hand"))
                        .otherwise(new SimpleStringProperty("-fx-cursor: default"))
        );

        stop.setOnMouseClicked(mouseEvent -> stopButtonListener.stop());
    }

}
