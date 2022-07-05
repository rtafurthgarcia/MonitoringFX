package org.hftm;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Paths;

import org.hftm.controller.EditViewController;
import org.hftm.controller.MainViewController;
import org.hftm.model.*;
import org.hftm.util.DNSRecordType;
import org.hftm.util.RequestType;
import org.xbill.DNS.*;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class MonitoringFX extends Application {

    private Stage primaryStage;
    private Stage editViewStage;

    EditViewController editViewController;

    private ObservableList<AbstractWatchDog> watchDogs = FXCollections.observableArrayList();

    public ObservableList<AbstractWatchDog> getWatchDogs() {
        return watchDogs;
    }

    public Stage getPrimaryStage() {
		return this.primaryStage;
	}

    public Stage getEditViewStage() {
        return this.editViewStage;
    }

    public MonitoringFX() {
        try {
            watchDogs.add(new PingWatchDog(1, "1.1.1.1"));
            watchDogs.add(new HTTPWatchDog(2, "https://hftm.ch", RequestType.GET, "", ""));
            watchDogs.add(new DNSRecordWatchDog(3, "hftm.ch", DNSRecordType.A, new SimpleResolver("1.1.1.1")));   
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
            loader.setLocation(MonitoringFX.class.getResource("view/MainView.fxml"));

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

    public void showEditView(AbstractWatchDog watchDog) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MonitoringFX.class.getResource("view/EditView.fxml"));
            Scene scene = new Scene(loader.load(), 600, 270);
            
            editViewStage = new Stage();
            editViewStage.setResizable(false);
            
            editViewStage.setTitle("MonitoringFX: watchdog");
            editViewStage.setScene(scene);
            editViewStage.show();
            
            editViewController = loader.getController();
            editViewController.setStage(editViewStage);
            editViewController.setApp(this);

            if (watchDog != null) {
                editViewController.setWatchDog(watchDog);
            } else {
                editViewController.setNewId(getWatchDogs().size() + 1);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}