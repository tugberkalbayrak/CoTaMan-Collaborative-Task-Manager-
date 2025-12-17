package com.example.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

public class UploadFilePopup extends VBox {

    private Runnable onCancel;
    private Runnable onSave;

    private CoTaTextField fileNameField;
    private ComboBox<String> fileTypeCombo;
    private ComboBox<String> courseCombo;
    private ComboBox<String> visibilityCombo;
    private Label dragDropLabel;

    private File selectedFile;

    public UploadFilePopup() {

        this.setMaxWidth(450);
        this.setSpacing(15);
        this.setPadding(new Insets(30));
        this.setAlignment(Pos.TOP_LEFT);
        
        this.setStyle(
            "-fx-background-color: #D3D3D3;" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);"
        );

        Label title = new Label("Add New File");
        title.setFont(Theme.getHeaderFont());
        title.setStyle("-fx-text-fill: #333; -fx-font-size: 20px;");

        VBox dragDropArea = new VBox(10);
        dragDropArea.setAlignment(Pos.CENTER);
        dragDropArea.setPrefHeight(100);
        dragDropArea.setStyle(
            "-fx-background-color: #E0E0E0;" + 
            "-fx-border-color: #888;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-border-style: dashed;" + 
            "-fx-border-width: 2;" +
            "-fx-cursor: hand;"
        );

        Label iconLbl = new Label("☁️");
        iconLbl.setStyle("-fx-font-size: 30px;");
        
        dragDropLabel = new Label("Drag & Drop your file here or Click to Upload");
        dragDropLabel.setStyle("-fx-text-fill: #555; -fx-font-weight: bold;");

        dragDropArea.getChildren().addAll(iconLbl, dragDropLabel);

        dragDropArea.setOnMouseClicked(e -> chooseFile());

        fileNameField = new CoTaTextField("File Name");
        fileNameField.setStyle("-fx-background-radius: 10; -fx-background-color: white; -fx-text-fill: black;");

        fileTypeCombo = createStyledComboBox("File Type (e.g. Notes, Exam)");
        fileTypeCombo.getItems().addAll("Syllabus", "Lecture Notes", "Old Exam", "Homework", "Other");

        courseCombo = createStyledComboBox("Related Course Code (e.g. CS102)");
        courseCombo.getItems().addAll("CS101", "CS102", "MATH101", "MATH102", "ENG101", "TURK101");

        visibilityCombo = createStyledComboBox("Visibility");
        visibilityCombo.getItems().addAll("Public", "Group Only", "Private (Only Me)");

        CoTaButton saveBtn = new CoTaButton("Save", CoTaButton.StyleType.SECONDARY);
        CoTaButton cancelBtn = new CoTaButton("Cancel", CoTaButton.StyleType.DANGER);
        
        saveBtn.setPrefWidth(100);
        cancelBtn.setPrefWidth(100);

        HBox actionBox = new HBox(15, saveBtn, cancelBtn);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        cancelBtn.setOnAction(e -> { if (onCancel != null) onCancel.run(); });
        saveBtn.setOnAction(e -> { 

            if (onSave != null) onSave.run(); 
        });

        this.getChildren().addAll(
            title, 
            dragDropArea, 
            fileNameField, 
            fileTypeCombo, 
            courseCombo, 
            visibilityCombo, 
            new Region(),
            actionBox
        );
        VBox.setVgrow(this.getChildren().get(6), Priority.ALWAYS);
    }

    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Resource File");

        File file = fileChooser.showOpenDialog(this.getScene().getWindow());
        
        if (file != null) {
            this.selectedFile = file;
            this.dragDropLabel.setText("Selected: " + file.getName());
            this.fileNameField.setText(file.getName());
        }
    }

    private ComboBox<String> createStyledComboBox(String prompt) {
        ComboBox<String> combo = new ComboBox<>();
        combo.setPromptText(prompt);
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #AAA; " +
            "-fx-border-radius: 10;"
        );
        return combo;
    }

    public String getFileName() { return fileNameField.getText(); }
    public String getFileType() { return fileTypeCombo.getValue(); }
    public String getCourse() { return courseCombo.getValue(); }
    public String getVisibility() { return visibilityCombo.getValue(); }
    public File getSelectedFile() { return selectedFile; }

    public void setOnCancel(Runnable action) { this.onCancel = action; }
    public void setOnSave(Runnable action) { this.onSave = action; }
}