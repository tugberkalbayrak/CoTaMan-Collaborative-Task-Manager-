module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;
    requires java.net.http;
    requires transitive org.mongodb.driver.sync.client;
    requires transitive org.mongodb.bson;
    requires transitive org.mongodb.driver.core;
    requires org.slf4j;

    opens com.example to javafx.fxml;
    exports com.example;

    exports com.example.database;
    opens com.example.database to org.mongodb.bson;
}
