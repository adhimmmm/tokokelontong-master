module com.example.tokokelontong {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jasperreports;
    requires itextpdf;
    requires kernel;
    requires layout;

    // INI HARUS ADA untuk FXML bisa akses field private di controller
    opens com.example.tokokelontong.controller to javafx.fxml;
    opens com.example.tokokelontong.model to javafx.fxml;



    opens com.example.tokokelontong to javafx.fxml;
    exports com.example.tokokelontong;
    exports com.example.tokokelontong.controller;
    exports com.example.tokokelontong.model;
}