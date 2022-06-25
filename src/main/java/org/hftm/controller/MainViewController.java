package org.hftm.controller;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hftm.MonitoringFX;
import org.hftm.model.AbstractWatchDog;
import org.hftm.model.HistoryRecord;
import org.hftm.model.HistoryRecord.ServiceStatus;

import javafx.beans.value.ObservableValue;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
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

    public void setScheduldedExecutionsOfWatchdogs() {
        
        /*listOfWatchdogsToSchedul.forEach((heartbeat, listOfWatchdogs) -> {
            ScheduledService<Void> ScheduledService = new ScheduledService<>() {
                @Override
                protected Task<Void> createTask() {
                    Task task = new Task<>() {
                        @Override
                        public Void call() {
                            final int toLoad = 10;
                            for (int i = 1; i <= toLoad; i++) {
                                bookNames.add(getFromDatabase(i + 1));
                                updateProgress(i, toLoad);
                            }
                            System.out.println("Got all books!");;
                            return bookNames;
                        }
                    };
                    return task;
                }
            };

            
        });

        /*
        <ScheduledService<List<String>> scheduledService = new ScheduledService<>() {
            @Override
            protected Task<List<String>> createTask() {
                Task task = new Task<>() {
                    final List<String> bookNames = new ArrayList<>();
                    @Override
                    public List<String> call() {
                        final int toLoad = 10;
                        for (int i = 1; i <= toLoad; i++) {
                            bookNames.add(getFromDatabase(i + 1));
                            updateProgress(i, toLoad);
                        }
                        System.out.println("Got all books!");;
                        return bookNames;
                    }
                    //Simulate database delay
                    private String getFromDatabase(int bookNumber) {
                        try {
                            Thread.sleep(225);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return "Book Number: " + bookNumber;
                    }
                };
                //task.setOnSucceeded(workerStateEvent -> taskLabel.setText("Waiting..."));
                return task;
            }
        }; */
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
                                    this.setStyle("-fx-background-color: green;-fx-text-fill: black;");    
                                    break;
                                case DOWN:
                                    this.setStyle("-fx-background-color: red;-fx-text-fill: black;");
                                    break;
                                case PAUSED:
                                    this.setStyle("-fx-background-color: blue;-fx-text-fill: black;");
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
