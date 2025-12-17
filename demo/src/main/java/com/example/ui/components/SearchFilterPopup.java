package com.example.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SearchFilterPopup extends VBox {

    private Runnable onSave;
    private Runnable onClear;

    public SearchFilterPopup() {

        this.setMaxWidth(300);
        this.setPadding(new Insets(20));
        this.setSpacing(15);
        this.setAlignment(Pos.TOP_LEFT);
        this.setStyle(
            "-fx-background-color: #D3D3D3;" + 
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);"
        );

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        
        CoTaTextField searchField = new CoTaTextField("Enter the file name");
        searchField.setStyle("-fx-background-radius: 20; -fx-background-color: white; -fx-text-fill: black;");
        searchField.setPrefWidth(220);

        Label searchIcon = new Label("🔍");
        searchIcon.setStyle("-fx-font-size: 18px; -fx-text-fill: #555;");

        searchBox.getChildren().addAll(searchField, searchIcon);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        Label sortLabel = new Label("Sort By:");
        sortLabel.setStyle("-fx-font-weight: bold;");
        
        ComboBox<String> sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("A-Z", "Date", "Course Code", "Upload Date");
        sortCombo.setValue("A-Z");
        sortCombo.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label typeLabel = new Label("Type:");
        typeLabel.setStyle("-fx-font-weight: bold;");
        
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("All", "Notes", "Exam", "Syllabus");
        typeCombo.setValue("All");
        typeCombo.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        grid.add(sortLabel, 0, 0);
        grid.add(sortCombo, 0, 1);
        grid.add(typeLabel, 1, 0);
        grid.add(typeCombo, 1, 1);

        Label visLabel = new Label("Visibility:");
        visLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 0 0;");

        ToggleGroup visGroup = new ToggleGroup();
        RadioButton publicRad = new RadioButton("Public");
        RadioButton groupRad = new RadioButton("Group");
        RadioButton privateRad = new RadioButton("Only Me");
        
        publicRad.setToggleGroup(visGroup);
        groupRad.setToggleGroup(visGroup);
        privateRad.setToggleGroup(visGroup);
        publicRad.setSelected(true);

        VBox visBox = new VBox(5, publicRad, groupRad, privateRad);

        CoTaButton clearBtn = new CoTaButton("Clear", CoTaButton.StyleType.DANGER);
        clearBtn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-background-radius: 15; -fx-border-color: gray; -fx-border-radius: 15;");
        
        CoTaButton saveBtn = new CoTaButton("Save", CoTaButton.StyleType.SECONDARY);
        saveBtn.setPrefWidth(80);

        HBox btnBox = new HBox(15, clearBtn, saveBtn);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        clearBtn.setOnAction(e -> {
            searchField.clear();
            sortCombo.setValue("A-Z");
            typeCombo.setValue("All");
            publicRad.setSelected(true);
            if (onClear != null) onClear.run();
        });

        saveBtn.setOnAction(e -> {
            if (onSave != null) onSave.run();
        });

        Label closeX = new Label("X");
        closeX.setStyle("-fx-font-weight: bold; -fx-cursor: hand;");
        closeX.setOnMouseClicked(e -> { if(onSave != null) onSave.run(); }); 

        HBox header = new HBox(searchBox, closeX);
        HBox.setHgrow(searchBox, javafx.scene.layout.Priority.ALWAYS);
        closeX.setAlignment(Pos.TOP_RIGHT);

        this.getChildren().addAll(header, grid, visLabel, visBox, btnBox);
    }

    public void setOnSave(Runnable action) { this.onSave = action; }
    public void setOnClear(Runnable action) { this.onClear = action; }
}