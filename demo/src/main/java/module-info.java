module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;

    opens com.example to javafx.fxml;
    exports com.example;

    exports com.cotaman.database;
    opens com.cotaman.database to org.mongodb.bson;
}
