module ui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.annotation;
    requires java.sql;
    requires com.fasterxml.jackson.databind;
    requires jdk.compiler;

    opens ui to javafx.fxml;
    opens controller to javafx.fxml;
    opens controller.mainUiPages to javafx.fxml;
    exports controller;
    exports controller.mainUiPages;
    exports ui;
    exports domain;
    exports domain.dto;
    exports repository;
    exports service;

    exports config;
    opens config to javafx.fxml;
}