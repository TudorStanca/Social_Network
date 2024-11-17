module ui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.annotation;
    requires java.sql;
    requires com.fasterxml.jackson.databind;

    opens ui to javafx.fxml;
    opens controller to javafx.fxml;
    exports controller;
    exports ui;
    exports domain;
    exports repository;
    exports service;
}