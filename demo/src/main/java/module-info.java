module com.example {
    // JavaFX Modülleri
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base; // Bu da önemli olabilir

    // MongoDB ve Diğer Kütüphaneler
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires java.net.http; // Scraper için
    requires org.jsoup; // Scraper için

    // --- KRİTİK KISIMLAR (BUNLARI EKLE/KONTROL ET) ---

    // 1. Ana paketi aç
    opens com.example to javafx.fxml;

    exports com.example;

    // 2. Entity paketini MongoDB'ye aç (User, Group, CalendarEvent burada)
    opens com.example.Entity to org.mongodb.bson;

    exports com.example.Entity;

    // 3. UI paketini aç (LoginView, MainView vb.)
    opens com.example.ui to javafx.fxml;

    exports com.example.ui;

    // 4. İŞTE HATAYI ÇÖZECEK SATIRLAR:
    // Task sınıfı "com.example.ui.components" içinde olduğu için
    // bu paketi hem MongoDB'ye (veritabanı okusun diye)
    // hem de JavaFX'e (ekran çizsin diye) açmalıyız.

    opens com.example.ui.components to org.mongodb.bson, javafx.fxml;

    exports com.example.ui.components;

    // --------------------------------------------------
}