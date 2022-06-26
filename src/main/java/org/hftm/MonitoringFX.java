package org.hftm;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Paths;

import org.hftm.controller.MainViewController;
import org.hftm.model.*;
import org.xbill.DNS.*;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class MonitoringFX extends Application {

    private Stage primaryStage;

    private ObservableList<AbstractWatchDog> watchDogs = FXCollections.observableArrayList();

    public ObservableList<AbstractWatchDog> getWatchDogs() {
        return watchDogs;
    }

    public Stage getPrimaryStage() {
		return this.primaryStage;
	}

    public MonitoringFX() {
        try {
            watchDogs.add(new PingWatchDog(1, "1.1.1.1"));
            watchDogs.add(new HTTPWatchDog(2, "https://hftm.ch", HTTPWatchDog.RequestType.GET, "", ""));
            watchDogs.add(new DNSRecordWatchDog(3, "hftm.ch", Type.MX, new SimpleResolver("1.1.1.1")));   
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws UnknownHostException {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("MonitoringFX");

        this.showMainView();
    }

    public void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            //loader.setResources(resources);
            loader.setLocation(MonitoringFX.class.getResource(Paths.get("view", "MainView.fxml").toString()));

            // Show the scene containing the root layout.
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            primaryStage.show();

            MainViewController controller = loader.getController();
            controller.setApp(this);
            
            controller.startWatchDogs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}