module org.hftm {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires java.net.http;
    requires org.dnsjava;
    //requires org.jmdns;

    exports org.hftm;

    opens org.hftm.controller to javafx.fxml;
    opens org.hftm.model to javafx.fxml;
}