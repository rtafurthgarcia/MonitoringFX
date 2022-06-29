package org.hftm.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.hftm.MonitoringFX;
import org.hftm.model.AbstractWatchDog;
import org.hftm.model.HistoryRecord;
import org.hftm.model.HistoryRecord.ServiceStatus;

import org.hftm.util.ImageResources;
import org.xbill.DNS.tools.primary;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.util.Callback;

public class MainViewController {
    @FXML
    private TableView<AbstractWatchDog> watchDogsTable;

    @FXML 
    private TableColumn<AbstractWatchDog, Number> idColumn;

    @FXML
    private TableColumn<AbstractWatchDog, String> serviceColumn;

    @FXML
    private TableColumn<AbstractWatchDog, String> typeColumn;

    @FXML
    private TableColumn<AbstractWatchDog, HistoryRecord.ServiceStatus> statusColumn;

    @FXML
    private TableColumn<AbstractWatchDog, Number> heartbeatColumn;

    @FXML
    private ImageView imageStartPause;

    @FXML 
    private ImageView imageStatus;

    @FXML
    private Label labelStatus;

    @FXML
    private Label labelSelected;

    @FXML
    private Label labelTimeout;

    @FXML
    private Label labelUptime;

    @FXML
    private Label labelCreationDate;

    @FXML
    private Label labelCreationTime;

    @FXML
    private Label labelRetries;

    @FXML
    private ImageResources resources;

    private DecimalFormat uptimeFormatter;

    boolean allClear;

    private MonitoringFX app;  

    public void setApp(MonitoringFX app) {
        this.app = app;

        this.watchDogsTable.setItems(this.app.getWatchDogs());
    }

    public void startWatchDogs() {
        for(AbstractWatchDog watchDogToSchedule: this.app.getWatchDogs()) {
            watchDogToSchedule.start();
            watchDogToSchedule.currentStatusProperty().addListener((observable) -> updateServiceState());
        }
    }

    private void updateServiceState() {
        if (imageStatus != null && labelStatus != null) {
            allClear = true;

            for(AbstractWatchDog watchDogToCheck: this.app.getWatchDogs()) {
                if (watchDogToCheck.getCurrentStatus().equals(ServiceStatus.DOWN)) {
                    allClear = false;
                    break;
                }
            }

            // runLater() : Has to be run by the UI Thread 
            // Otherwise ran by the Servicescheduler one
            // https://stackoverflow.com/questions/29449297/java-lang-illegalstateexception-not-on-fx-application-thread-currentthread-t
            Platform.runLater(() -> {
                if (allClear) {
                    imageStatus.setImage(resources.get("ALL_OK"));
                    labelStatus.setText("ONLINE");
                    labelStatus.setTextFill(Paint.valueOf("#4caf50"));
                } else {
                    imageStatus.setImage(resources.get("ALL_NOK"));
                    labelStatus.setText("DEGRADED");
                    labelStatus.setTextFill(Paint.valueOf("#f44336"));
                }

                AbstractWatchDog selectedWatchDog = watchDogsTable.getSelectionModel().getSelectedItem();

                if (selectedWatchDog != null) {
                    labelUptime.setText(uptimeFormatter.format(selectedWatchDog.getUptimeSinceBeginning()) + "%");
                }
            });
        }
    }

    @FXML
    public void initialize() {
        resources = new ImageResources();
        allClear = true;
        uptimeFormatter = new DecimalFormat("##.##");

        this.idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        this.serviceColumn.setCellValueFactory(cellData -> cellData.getValue().serviceProperty());
        this.typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        this.statusColumn.setCellValueFactory(cellData -> cellData.getValue().currentStatusProperty());
        this.heartbeatColumn.setCellValueFactory(cellData -> cellData.getValue().periodProperty());

        labelSelected.setText("");
        labelTimeout.setText("");
        labelCreationDate.setText("");
        labelCreationTime.setText("");
        labelRetries.setText("");
        labelUptime.setText("??%");

        /*label.textProperty().bind(integerProperty.asString());        
        labelSelected.textProperty().bind(currentlySelectedWatchDog.idProperty().asString());
        labelRetries.textProperty().bind(currentlySelectedWatchDog.maximumFailureCountProperty().asString());
        labelTimeout.textProperty().bind(currentlySelectedWatchDog.timeoutProperty().asString());
        labelCreationDate.textProperty().bind(currentlySelectedWatchDog.getCreationDateTime().pro);**/

        Callback factory = new Callback<TableColumn<AbstractWatchDog, ServiceStatus>, TableCell<AbstractWatchDog, ServiceStatus>>() {
            @Override
            public TableCell<AbstractWatchDog, ServiceStatus> call(TableColumn<AbstractWatchDog, ServiceStatus> param) {
                return new TableCell<AbstractWatchDog, ServiceStatus>() {
                    @Override
                    public void updateItem(ServiceStatus item, boolean empty) {
                        super.updateItem(item, empty);
        
                        //labelUptime.setText(String.valueOf(selectedWatchDog.getUptimeSinceBeginning()) + "%");

                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.name());
        
                            switch (item) {
                                case UP:
                                    this.setStyle("-fx-background-color: green;-fx-text-fill: white;");    
                                    //this.getParent().setDisable(false);
                                    break;
                                case DOWN:
                                    this.setStyle("-fx-background-color: red;-fx-text-fill: white;");
                                    //this.getParent().setDisable(false);
                                    break;
                                case FAILED:
                                    this.setStyle("-fx-background-color: red;-fx-text-fill: white;");
                                    //this.getParent().setDisable(false);
                                    break;
                                case PAUSED:
                                    this.setStyle("-fx-background-color: blue;-fx-text-fill: white;");
                                    //this.getParent().setDisable(true);
                                    break;
                                default:
                                    this.setStyle("-fx-background-color: yellow;-fx-text-fill: black;");
                                    //this.getParent().setDisable(false);
                            }

                        }
                    }
                };
            }
        
        };

        this.statusColumn.setCellFactory(factory);

        this.watchDogsTable.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> this.onSelectedWatchDog(newValue));
    }

    @FXML
    void onButtonStartPauseClicked() {
        AbstractWatchDog selectedWatchDog = watchDogsTable.getSelectionModel().getSelectedItem();
        if (selectedWatchDog != null) {
            if (selectedWatchDog.getCurrentStatus().equals(ServiceStatus.PAUSED)) {
                selectedWatchDog.reset();
                selectedWatchDog.start();
            } else {
                selectedWatchDog.cancel();
            }

        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(app.getPrimaryStage());
            alert.setTitle("Unproper selection");
            alert.setHeaderText("No watchdog selected");
            alert.setContentText("Please select a proper watchdog to pause or start again.");
            alert.showAndWait();
        }
    }

    void onSelectedWatchDog(AbstractWatchDog selectedWatchDog) {
        if (selectedWatchDog.getCurrentStatus().equals(ServiceStatus.PAUSED)) {
            imageStartPause.setImage(resources.get("START_LOGO"));
        } else {
            imageStartPause.setImage(resources.get("PAUSE_LOGO"));
        }

        labelSelected.setText("Selected: #" + selectedWatchDog.getId().toString());
        labelTimeout.setText(selectedWatchDog.getTimeout().toString() + "ms");
        labelCreationDate.setText(selectedWatchDog.getCreationDateTime().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
        labelCreationTime.setText(selectedWatchDog.getCreationDateTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
        labelRetries.setText(String.valueOf(selectedWatchDog.getMaximumFailureCount()));
        labelUptime.setText(uptimeFormatter.format(selectedWatchDog.getUptimeSinceBeginning()) + "%");
    }

    @FXML
    void onButtonAddClicked() {

    }

    @FXML
    void onButtonDeleteClicked() {

    }

    @FXML 
    void onButtonEditClicked() {

    }
}
