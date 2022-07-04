package org.hftm.controller;

import org.hftm.MonitoringFX;
import org.hftm.model.AbstractWatchDog;
import org.hftm.model.DNSRecordWatchDog;
import org.hftm.model.HTTPWatchDog;
import org.hftm.model.PingWatchDog;
import org.hftm.model.TCPWatchDog;
import org.hftm.util.DNSRecordType;
import org.hftm.util.DurationType;
import org.hftm.util.WatchDogType;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

//import javax.jmdns.impl.constants.DNSRecordType;

public class EditViewController {

    @FXML
    ComboBox<WatchDogType> comboboxType;

    @FXML
    ComboBox<DurationType> comboboxPeriod;

    @FXML
    ComboBox<DurationType> comboboxTimeout;

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
    ComboBox<DNSRecordType> comboboxGeneric1;

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

    @FXML
    GridPane gridpane;

    private MonitoringFX app;  

    private AbstractWatchDog watchDog;

    public void setApp(MonitoringFX app) {
        this.app = app;
    }

    // couldnt find how to remove a row so I had to invoke the Gods of SO
    // https://stackoverflow.com/questions/40516514/remove-a-row-from-a-gridpane
    private void deleteRow(final int row) {
        Set<Node> deleteNodes = new HashSet<>();
        for (Node child : gridpane.getChildren()) {
            // get index from child
            Integer rowIndex = GridPane.getRowIndex(child);
    
            // handle null values for index=0
            int r = rowIndex == null ? 0 : rowIndex;
    
            if (r > row) {
                // decrement rows for rows after the deleted row
                GridPane.setRowIndex(child, r-1);
            } else if (r == row) {
                // collect matching rows for deletion
                deleteNodes.add(child);
            }
        }
    
        // remove nodes from row
        gridpane.getChildren().removeAll(deleteNodes);
    }

    public void setWatchDog(AbstractWatchDog watchDog) {
        this.watchDog = watchDog;

        textfieldService.setText(this.watchDog.getService());
        textfieldPeriod.setText(String.valueOf(this.watchDog.getPeriod().toMillis()));
        textfieldTimeout.setText(String.valueOf(this.watchDog.getTimeout().toMillisPart()));
        textfieldRetries.setText(String.valueOf(this.watchDog.getMaximumFailureCount()));

        comboboxTimeout.getSelectionModel().select(DurationType.MILISECONDS);
        comboboxPeriod.getSelectionModel().select(DurationType.MILISECONDS);

        if (watchDog instanceof DNSRecordWatchDog) {
            DNSRecordWatchDog dnsRecordWatchDog = (DNSRecordWatchDog) this.watchDog;

            comboboxType.getSelectionModel().select(WatchDogType.DNS);
            
            comboboxGeneric1.setItems(FXCollections.observableArrayList(DNSRecordType.values()));
            comboboxGeneric1.getSelectionModel().select(dnsRecordWatchDog.getRecordType());

            labelGeneric1.setText("Resolver");
            labelGeneric3.setText("DNS Record");

            textfieldGeneric1.setText(dnsRecordWatchDog.getResolver().getAddress().getHostString());

            deleteRow(6);

        } else if (watchDog instanceof HTTPWatchDog) {
            comboboxType.getSelectionModel().select(WatchDogType.HTTP);
        } else if (watchDog instanceof PingWatchDog) {
            comboboxType.getSelectionModel().select(WatchDogType.PING);
        } else {
            comboboxType.getSelectionModel().select(WatchDogType.TCP);
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
        comboboxType.setItems(FXCollections.observableArrayList(WatchDogType.values()));
        comboboxPeriod.setItems(FXCollections.observableArrayList(DurationType.values()));
        comboboxTimeout.setItems(FXCollections.observableArrayList(DurationType.values()));

        comboboxType.setPromptText("Select a watchdog type");
        comboboxTimeout.getSelectionModel().select(DurationType.MILISECONDS);
        comboboxPeriod.getSelectionModel().select(DurationType.MILISECONDS);
    }

    @FXML
    public void onButtonValidateClicked() {

    }

    @FXML
    public void onButtonCancelClicked() {
        
    }
}
