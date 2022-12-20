module com.example.rcwol {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires org.testng;


    opens com.example.rcwol to javafx.fxml;
    exports com.example.rcwol;
    exports com.example.rcwol.controllers;
    opens com.example.rcwol.controllers to javafx.fxml;
    exports com.example.rcwol.utils;
    opens com.example.rcwol.utils to javafx.fxml;
}