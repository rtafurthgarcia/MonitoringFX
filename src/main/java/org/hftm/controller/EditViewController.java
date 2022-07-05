package org.hftm.controller;

import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.time.Duration;

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
import org.xbill.DNS.SimpleResolver;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

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

    @FXML
    Button buttonValidate;

    @FXML
    TitledPane titledpane;

    private MonitoringFX app;  

    private Stage stage;

    private AbstractWatchDog watchDog;

    private Boolean isNewWatchDog;

    private Integer newId;

    DecimalFormat formatter = new DecimalFormat("##");

    public void setApp(MonitoringFX app) {
        this.app = app;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setNewId(Integer newId) {
        this.newId = newId;
        this.isNewWatchDog = true;
    }

    public static Duration getDurationByUnit(Integer amount, DurationType unit) {
        switch (unit) {
            case MILISECONDS:
                return Duration.ofMillis(amount);        
            case SECONDS:
                return Duration.ofSeconds(amount);
            case MINUTES:
                return Duration.ofMinutes(amount);
            case HOURS:
                return Duration.ofHours(amount);
            default:
                return null;
        }
    }

    public void setWatchDog(AbstractWatchDog watchDog) {
        this.watchDog = watchDog;
        isNewWatchDog = false;

        textfieldService.setText(this.watchDog.getService());
        textfieldPeriod.setText(formatter.format(this.watchDog.getPeriod().toMillis()));
        textfieldTimeout.setText(formatter.format(this.watchDog.getTimeout().toMillis()));
        textfieldRetries.setText(formatter.format(this.watchDog.getMaximumFailureCount()));

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

        buttonValidate.setText("Save");
        titledpane.setText("value");
        titledpane.setText("Editing watchdog #" + watchDog.getId());

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
                labelGeneric3.setText("Method");
                
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

        buttonValidate.setDisable(false);
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
        isNewWatchDog = true;

        buttonValidate.setDisable(true);

        comboboxType.setItems(FXCollections.observableArrayList(WatchDogType.values()));
        comboboxPeriod.setItems(FXCollections.observableArrayList(DurationType.values()));
        comboboxTimeout.setItems(FXCollections.observableArrayList(DurationType.values()));

        comboboxType.setPromptText("Select a watchdog type");
        comboboxTimeout.getSelectionModel().select(DurationType.SECONDS);
        comboboxPeriod.getSelectionModel().select(DurationType.SECONDS);

        textareaGeneric1.setWrapText(true);
        textareaGeneric2.setWrapText(true);

        setDefaultValues();

        comboboxType.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> this.setType(newValue));
    }

    @FXML
    public void onButtonValidateClicked() throws NumberFormatException, UnknownHostException {    
        switch (comboboxType.getSelectionModel().getSelectedItem()) {
            case DNS:
                if (isNewWatchDog) {
                    this.watchDog = new DNSRecordWatchDog(
                        newId, 
                        textfieldService.getText(), 
                        getDurationByUnit(Integer.parseInt(textfieldTimeout.getText()), comboboxTimeout.getSelectionModel().getSelectedItem()), 
                        getDurationByUnit(Integer.parseInt(textfieldPeriod.getText()), comboboxPeriod.getSelectionModel().getSelectedItem()), 
                        Integer.parseInt(textfieldRetries.getText())
                    );
                } else {
                    this.watchDog.setService(textfieldService.getText());
                    this.watchDog.setTimeout(getDurationByUnit(Integer.parseInt(textfieldTimeout.getText()), comboboxTimeout.getSelectionModel().getSelectedItem()));
                    this.watchDog.setPeriod(new javafx.util.Duration(getDurationByUnit(Integer.parseInt(textfieldPeriod.getText()), comboboxPeriod.getSelectionModel().getSelectedItem()).toMillis()));
                    this.watchDog.setMaximumFailureCount(Integer.parseInt(textfieldRetries.getText()));
                }
                
                DNSRecordWatchDog dnsRecordWatchDog = (DNSRecordWatchDog) this.watchDog;
                dnsRecordWatchDog.setRecordType((DNSRecordType)comboboxGeneric1.getSelectionModel().getSelectedItem());
                dnsRecordWatchDog.setResolver(new SimpleResolver(textareaGeneric1.getText()));

                if (isNewWatchDog) {
                    this.app.getWatchDogs().add(dnsRecordWatchDog);
                    this.watchDog.start();
                }

                break;
        
            case PING:
                if (isNewWatchDog) {
                    this.watchDog = new PingWatchDog(
                        newId, 
                        textfieldService.getText(), 
                        getDurationByUnit(Integer.valueOf(textfieldTimeout.getText()), comboboxTimeout.getSelectionModel().getSelectedItem()), 
                        getDurationByUnit(Integer.valueOf(textfieldPeriod.getText()), comboboxPeriod.getSelectionModel().getSelectedItem()), 
                        Integer.valueOf(textfieldRetries.getText())
                    );
                } else {
                        this.watchDog.setService(textfieldService.getText());
                        this.watchDog.setTimeout(getDurationByUnit(Integer.valueOf(textfieldTimeout.getText()), comboboxTimeout.getSelectionModel().getSelectedItem()));
                        this.watchDog.setPeriod(new javafx.util.Duration(getDurationByUnit(Integer.valueOf(textfieldPeriod.getText()), comboboxPeriod.getSelectionModel().getSelectedItem()).toMillis()));
                        this.watchDog.setMaximumFailureCount(Integer.valueOf(textfieldRetries.getText()));
                }

                if (isNewWatchDog) {
                    this.app.getWatchDogs().add(this.watchDog);
                    this.watchDog.start();
                }

                break;

            case HTTP:
                if (isNewWatchDog) {
                    this.watchDog = new HTTPWatchDog(
                        newId, 
                        textfieldService.getText(), 
                        getDurationByUnit(Integer.parseInt(textfieldTimeout.getText()), comboboxTimeout.getSelectionModel().getSelectedItem()), 
                        getDurationByUnit(Integer.parseInt(textfieldPeriod.getText()), comboboxPeriod.getSelectionModel().getSelectedItem()), 
                        Integer.parseInt(textfieldRetries.getText())
                    );
                } else {
                        this.watchDog.setService(textfieldService.getText());
                        this.watchDog.setTimeout(getDurationByUnit(Integer.parseInt(textfieldTimeout.getText()), comboboxTimeout.getSelectionModel().getSelectedItem()));
                        this.watchDog.setPeriod(new javafx.util.Duration(getDurationByUnit(Integer.parseInt(textfieldPeriod.getText()), comboboxPeriod.getSelectionModel().getSelectedItem()).toMillis()));
                        this.watchDog.setMaximumFailureCount(Integer.parseInt(textfieldRetries.getText()));
                }

                HTTPWatchDog httpWatchDog = (HTTPWatchDog) this.watchDog;
                httpWatchDog.setBody(textareaGeneric1.getText());
                httpWatchDog.setHeaders(textareaGeneric2.getText());
                httpWatchDog.setRequestType((RequestType)comboboxGeneric1.getSelectionModel().getSelectedItem());

                if (isNewWatchDog) {
                    this.app.getWatchDogs().add(this.watchDog);
                    this.watchDog.start();
                }

                break;
        
            case TCP:
                if (isNewWatchDog) {
                    this.watchDog = new TCPWatchDog(
                        newId, 
                        textfieldService.getText(),
                        Integer.parseInt(textareaGeneric1.getText()) 
                    );
                } else {
                        this.watchDog.setService(textfieldService.getText());
                        this.watchDog.setTimeout(getDurationByUnit(Integer.parseInt(textfieldTimeout.getText()), comboboxTimeout.getSelectionModel().getSelectedItem()));
                        this.watchDog.setPeriod(new javafx.util.Duration(getDurationByUnit(Integer.valueOf(textfieldPeriod.getText()), comboboxPeriod.getSelectionModel().getSelectedItem()).toMillis()));
                        this.watchDog.setMaximumFailureCount(Integer.parseInt(textfieldRetries.getText()));
                }

                TCPWatchDog tcpWatchDog = (TCPWatchDog) this.watchDog;
                tcpWatchDog.setPort(Integer.parseInt(textareaGeneric1.getText()));

                if (isNewWatchDog) {
                    this.app.getWatchDogs().add(this.watchDog);
                    this.watchDog.start();
                }

                break; 

            default:
                Alert alert = new Alert(AlertType.WARNING);
                alert.initOwner(app.getPrimaryStage());
                alert.setTitle("Unproper selection");
                alert.setHeaderText("No watchdog type selected");
                alert.setContentText("Please select a proper watchdog type to add.");
                alert.showAndWait();

                break;
        }

        this.stage.close();
    }

    @FXML
    public void onButtonCancelClicked() {
        this.stage.close();
    }
}
