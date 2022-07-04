package org.hftm.controller;

import org.hftm.MonitoringFX;
import org.hftm.model.AbstractWatchDog;
import org.hftm.model.DNSRecordWatchDog;
import org.hftm.model.HTTPWatchDog;
import org.hftm.model.PingWatchDog;
import org.hftm.model.TCPWatchDog;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.RowConstraints;
import java.time.Duration;

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
    ComboBox<String> comboboxGeneric1;

    @FXML
    Label labelGeneric1;

    @FXML
    Label labelGeneric2;

    @FXML
    Label labelGeneric3;

    @FXML
    RowConstraints rowconstraints1;

    @FXML
    RowConstraints rowconstraints2;

    @FXML
    RowConstraints rowconstraints3;

    private MonitoringFX app;  

    private AbstractWatchDog watchDog;

    public void setApp(MonitoringFX app) {
        this.app = app;
    }

    public void setWatchDog(AbstractWatchDog watchDog) {
        this.watchDog = watchDog;

        textfieldService.setText(this.watchDog.getService());
        textfieldPeriod.setText(this.watchDog.getPeriod().toString());
        textfieldTimeout.setText(String.valueOf(this.watchDog.getTimeout().toMillisPart()));
        textfieldRetries.setText(String.valueOf(this.watchDog.getMaximumFailureCount()));

        if (watchDog instanceof DNSRecordWatchDog) {
            comboboxType.getSelectionModel().select(WatchDogTypes.DNS);
        } else if (watchDog instanceof HTTPWatchDog) {
            comboboxType.getSelectionModel().select(WatchDogTypes.HTTP);
        } else if (watchDog instanceof PingWatchDog) {
            comboboxType.getSelectionModel().select(WatchDogTypes.PING);
        } else {
            comboboxType.getSelectionModel().select(WatchDogTypes.TCP);
        }

        /*if (watchDogClass.equals(DNSRecordWatchDog)) {
            labelGeneric1.setText("Resolver");
            labelGeneric3.setText("Record type");

            rowconstraints2.maxHeightProperty().set(0);


        }*/
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
