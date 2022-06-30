package org.hftm.controller;

import java.util.HashMap;

import org.hftm.MonitoringFX;
import org.hftm.model.AbstractWatchDog;
import org.xbill.DNS.tools.primary;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class EditViewController {

    private enum DurationTypes {
        MILISECONDS, 
        SECONDS,
        MINUTES,
        HOURS
    }

    private enum WatchDogTypes {
        DNS,
        HTTP,
        PING,
        TCP
    }

    @FXML
    ComboBox<WatchDogTypes> comboboxType;

    @FXML
    ComboBox<DurationTypes> comboboxPeriod;

    @FXML
    ComboBox<DurationTypes> comboboxTimeout;

    @FXML
    TextField textfieldService;

    @FXML
    TextField textfieldPeriod;

    @FXML
    TextField textfieldTimeout;

    @FXML
    TextField textfieldRetries;

    @FXML
    TextField textfieldGeneric1;

    @FXML
    TextField textfieldGeneric2;

    @FXML
    Label labelGeneric1;

    @FXML
    Label labelGeneric2;

    private MonitoringFX app;  

    public void setApp(MonitoringFX app) {
        this.app = app;
    }

    public void setWatchDog(AbstractWatchDog watchDog) {

    }

    /**
     * 
     */
    @FXML
    public void initialize() {
        comboboxType.setItems(FXCollections.observableArrayList(WatchDogTypes.DNS, WatchDogTypes.HTTP, WatchDogTypes.PING, WatchDogTypes.TCP));
        comboboxPeriod.setItems(FXCollections.observableArrayList(DurationTypes.MILISECONDS, DurationTypes.SECONDS, DurationTypes.MINUTES, DurationTypes.HOURS));
        comboboxTimeout.setItems(FXCollections.observableArrayList(DurationTypes.MILISECONDS, DurationTypes.SECONDS, DurationTypes.MINUTES, DurationTypes.HOURS));
    }

    @FXML
    public void onButtonValidateClicked() {

    }

    @FXML
    public void onButtonCancelClicked() {
        
    }
}
