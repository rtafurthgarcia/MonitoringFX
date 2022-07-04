package org.hftm.controller;

import org.hftm.MonitoringFX;
import org.hftm.model.AbstractWatchDog;
import org.hftm.model.DNSRecordWatchDog;
import org.hftm.model.HTTPWatchDog;
import org.hftm.model.PingWatchDog;
import org.hftm.model.TCPWatchDog;
import org.hftm.util.DNSRecordType;
import org.hftm.util.DurationType;
import org.hftm.util.RequestType;
import org.hftm.util.WatchDogType;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

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
    TextArea textareaGeneric1;

    @FXML
    TextArea textareaGeneric2;

    @FXML
    ComboBox comboboxGeneric1;

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

    private Stage stage;

    private AbstractWatchDog watchDog;

    public void setApp(MonitoringFX app) {
        this.app = app;
    }

    public void setStage(Stage stage) {
        this.stage = stage;

        /**this.stage.widthProperty().addListener((o, oldValue, newValue) -> {
            if(newValue.intValue() < 600.0) {
                this.stage.setResizable(false);
                this.stage.setWidth(600);
                this.stage.setResizable(true);
            }
        });

        this.stage.heightProperty().addListener((o, oldValue, newValue) -> {
            if(newValue.intValue() < 600.0) {
                this.stage.setResizable(false);
                this.stage.setHeight(600);
                this.stage.setResizable(true);
            }
        });**/
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
            setType(WatchDogType.DNS);
        } else if (watchDog instanceof HTTPWatchDog) {
            setType(WatchDogType.HTTP);
        } else if (watchDog instanceof PingWatchDog) {
            setType(WatchDogType.PING);
        } else if (watchDog instanceof TCPWatchDog) {
            setType(WatchDogType.TCP);
        }

        comboboxType.setDisable(true);
    }

    private void setType(WatchDogType type) {
        setDefaultValues();

        switch (type) {
            case DNS:
                comboboxType.getSelectionModel().select(WatchDogType.DNS);
                
                comboboxGeneric1.setItems(FXCollections.observableArrayList(DNSRecordType.values()));
                comboboxGeneric1.getSelectionModel().select(DNSRecordType.ANY);

                labelGeneric1.setText("Resolver");
                labelGeneric3.setText("DNS Record");

                textareaGeneric1.setPromptText((DNSRecordWatchDog.DEFAULT_RESOLVER));
                
                labelGeneric1.setVisible(true);
                labelGeneric3.setVisible(true);
                textareaGeneric1.setVisible(true);
                comboboxGeneric1.setVisible(true);
                rowconstraints1.setMinHeight(54);
                rowconstraints3.setMinHeight(27);
                rowconstraints1.setMaxHeight(54);
                rowconstraints3.setMaxHeight(27);

                
                if (this.watchDog != null) {
                    DNSRecordWatchDog dnsRecordWatchDog = (DNSRecordWatchDog) this.watchDog;
                    
                    textareaGeneric1.setText(dnsRecordWatchDog.getResolver().getAddress().getHostString());
                    comboboxGeneric1.getSelectionModel().select(dnsRecordWatchDog.getRecordType());
                }

                break;
        
            case HTTP:
            
                comboboxType.getSelectionModel().select(WatchDogType.HTTP);

                labelGeneric1.setText("Body");
                labelGeneric2.setText("Headers");
                labelGeneric3.setText("Request type");
                
                labelGeneric1.setVisible(true);
                labelGeneric2.setVisible(true);
                labelGeneric3.setVisible(true);
                textareaGeneric1.setVisible(true);
                textareaGeneric2.setVisible(true);
                comboboxGeneric1.setVisible(true);
                
                rowconstraints1.setMinHeight(100);
                rowconstraints2.setMinHeight(100);
                rowconstraints3.setMinHeight(27);
                rowconstraints1.setMaxHeight(100);
                rowconstraints2.setMaxHeight(100);
                rowconstraints3.setMaxHeight(27);

                comboboxGeneric1.setItems(FXCollections.observableArrayList(RequestType.values()));
                comboboxGeneric1.getSelectionModel().select(RequestType.GET);
                
                if (this.watchDog != null) {
                    HTTPWatchDog httpWatchDog = (HTTPWatchDog) this.watchDog;
                    
                    textareaGeneric1.setText(httpWatchDog.getBody());
                    textareaGeneric2.setText(httpWatchDog.getHeaders());
                    
                    comboboxGeneric1.getSelectionModel().select(httpWatchDog.getRequestType());
                }
                
                stage.setHeight(505);
                stage.setWidth(600);

                break;
            case PING:
                comboboxType.getSelectionModel().select(WatchDogType.PING);

                stage.setHeight(305);

                break;
            default:
                comboboxType.getSelectionModel().select(WatchDogType.TCP);

                labelGeneric1.setText("Port");
                textareaGeneric1.setText(String.valueOf(TCPWatchDog.DEFAULT_PORT));
                
                labelGeneric1.setVisible(true);
                textareaGeneric1.setVisible(true);
                rowconstraints1.setMinHeight(54);
                rowconstraints1.setMaxHeight(54);

                if (this.watchDog != null) {
                    TCPWatchDog tcpWatchDog = (TCPWatchDog) this.watchDog;

                    textareaGeneric1.setText(String.valueOf(tcpWatchDog.getPort()));
                }

                break;
        }
    }

    private void setDefaultValues() {

        if (stage != null) {
            stage.setHeight(360);
            stage.setWidth(600);
        }

        labelGeneric1.setVisible(false);
        labelGeneric2.setVisible(false);
        labelGeneric3.setVisible(false);
        textareaGeneric1.setVisible(false);
        textareaGeneric2.setVisible(false);
        comboboxGeneric1.setVisible(false);
        rowconstraints1.setMaxHeight(0);
        rowconstraints2.setMaxHeight(0);
        rowconstraints2.setMaxHeight(0);
        rowconstraints1.setMinHeight(0);
        rowconstraints2.setMinHeight(0);
        rowconstraints2.setMinHeight(0);
        textareaGeneric1.setText("");
        textareaGeneric2.setText("");
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

        textareaGeneric1.setWrapText(true);
        textareaGeneric2.setWrapText(true);

        setDefaultValues();

        comboboxType.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> this.setType(newValue));
    }

    @FXML
    public void onButtonValidateClicked() {

    }

    @FXML
    public void onButtonCancelClicked() {
        this.stage.close();
    }
}
