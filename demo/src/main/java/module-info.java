module com.example {

    requires java.desktop;
     
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;  

requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires java.net.http;  
    requires org.jsoup;  

opens com.example to javafx.fxml;

    exports com.example;

opens com.example.Entity to org.mongodb.bson;

    exports com.example.Entity;

opens com.example.ui to javafx.fxml;

    exports com.example.ui;

opens com.example.ui.components to org.mongodb.bson, javafx.fxml;

    exports com.example.ui.components;

}