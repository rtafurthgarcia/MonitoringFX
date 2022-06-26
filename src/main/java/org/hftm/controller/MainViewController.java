package org.hftm.controller;

import org.hftm.MonitoringFX;
import org.hftm.model.AbstractWatchDog;
import org.hftm.model.HistoryRecord;
import org.hftm.model.HistoryRecord.ServiceStatus;

import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

    private MonitoringFX app;  

    private String currentStyle;

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
                                    break;
                                case DOWN:
                                    this.setStyle("-fx-background-color: red;-fx-text-fill: white;");
                                    break;
                                case FAILED:
                                    this.setStyle("-fx-background-color: red;-fx-text-fill: white;");
                                    break;
                                case PAUSED:
                                    this.setStyle("-fx-background-color: blue;-fx-text-fill: white;");
                                    break;
                                default:
                                    this.setStyle("-fx-background-color: yellow;-fx-text-fill: black;");
                            }

                            currentStyle = this.getStyle();
                        }
                    }
        
                    @Override
                    public void updateSelected(boolean selected) {
                        // TODO Auto-generated method stub
                        super.updateSelected(selected);

                        this.setStyle(currentStyle + "-fx-text-fill: black;");
                        currentStyle = this.getStyle();
                    }
                };
            }
        
        };

        this.statusColumn.setCellFactory(factory);
    }
}
