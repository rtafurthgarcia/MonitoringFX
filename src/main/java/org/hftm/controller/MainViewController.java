package org.hftm.controller;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.hftm.MonitoringFX;
import org.hftm.model.AbstractWatchDog;
import org.hftm.model.HistoryRecord;
import org.hftm.model.HistoryRecord.ServiceStatus;

import org.hftm.util.ImageResources;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private ImageResources resources;

    private MonitoringFX app;  


    public void setApp(MonitoringFX app) {
        this.app = app;

        this.watchDogsTable.setItems(this.app.getWatchDogs());
    }

    public void startWatchDogs() {
        for(AbstractWatchDog watchDogToSchedule: this.app.getWatchDogs()) {
            watchDogToSchedule.start();
        }
    }

    @FXML
    public void initialize() {
        resources = new ImageResources();
        //imageStartPause = new ImageView(resources.get("PAUSE_LOGO"));

        this.idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        this.serviceColumn.setCellValueFactory(cellData -> cellData.getValue().serviceProperty());
        this.typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        this.statusColumn.setCellValueFactory(cellData -> cellData.getValue().currentStatusProperty());
        this.heartbeatColumn.setCellValueFactory(cellData -> cellData.getValue().periodProperty());

        Callback factory = new Callback<TableColumn<AbstractWatchDog, ServiceStatus>, TableCell<AbstractWatchDog, ServiceStatus>>() {
            @Override
            public TableCell<AbstractWatchDog, ServiceStatus> call(TableColumn<AbstractWatchDog, ServiceStatus> param) {
                return new TableCell<AbstractWatchDog, ServiceStatus>() {
                    @Override
                    public void updateItem(ServiceStatus item, boolean empty) {
                        super.updateItem(item, empty);
        
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
            if (selectedWatchDog.isRunning()) {
                selectedWatchDog.cancel();
            } else {
                selectedWatchDog.reset();
                selectedWatchDog.start();
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
