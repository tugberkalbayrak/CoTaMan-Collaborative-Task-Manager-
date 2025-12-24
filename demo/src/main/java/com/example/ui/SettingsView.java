package com.example.ui;

import com.example.Entity.User;
import com.example.Manager.SessionManager;
import com.example.ui.components.CoTaButton;
import com.example.ui.components.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import java.io.File;

public class SettingsView extends VBox {

  private javafx.scene.control.ComboBox<Theme.ThemeType> themeComboBox;
  private TextField nameField;
  private TextField emailField;
  private TextField bilkentIdField;
  private TextField photoPathField;
  private ImageView profileImageView;
  private Label statusLabel;

  public SettingsView() {
    this.setPadding(new Insets(40));
    this.setSpacing(20);
    this.setAlignment(Pos.CENTER);
    this.setStyle("-fx-background-color: " + Theme.BG_COLOR + ";");

    Label header = new Label("Settings");
    header.setFont(Theme.getHeaderFont());
    header.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");

    User user = SessionManager.getInstance().getCurrentUser();
    if (user == null) {
      this.getChildren().add(new Label("User not logged in"));
      return;
    }

    VBox form = new VBox(15);
    form.setMaxWidth(400);
    form.setAlignment(Pos.CENTER);

    profileImageView = new ImageView();
    profileImageView.setFitWidth(100);
    profileImageView.setFitHeight(100);
    profileImageView.setPreserveRatio(false);

    Circle clip = new Circle(50, 50, 50);
    profileImageView.setClip(clip);

    updateProfileImage(user.getProfilePhotoPath());

    nameField = createStyledTextField("Full Name", user.getFullName());
    emailField = createStyledTextField("Email", user.getEmail());
    bilkentIdField = createStyledTextField("Bilkent ID", user.getBilkentId());

    Label photoLabel = new Label("Profile Photo Path");
    photoLabel.setStyle("-fx-text-fill: #BDC3C7; -fx-font-size: 12px;");

    HBox photoBox = new HBox(10);
    photoPathField = createStyledTextField("Select profile photo...",
        user.getProfilePhotoPath() != null ? user.getProfilePhotoPath() : "");
    HBox.setHgrow(photoPathField, Priority.ALWAYS);

    CoTaButton browseBtn = new CoTaButton("Browse", CoTaButton.StyleType.SECONDARY);
    browseBtn.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Select Profile Photo");
      fileChooser.getExtensionFilters().addAll(
          new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
      File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());
      if (selectedFile != null) {
        String path = selectedFile.toURI().toString();
        photoPathField.setText(path);
        updateProfileImage(path);
      }
    });

    photoBox.getChildren().addAll(photoPathField, browseBtn);

    // Theme Selector
    Label themeLabel = new Label("Application Theme");
    themeLabel.setStyle("-fx-text-fill: #BDC3C7; -fx-font-size: 12px;");

    themeComboBox = new javafx.scene.control.ComboBox<>();
    themeComboBox.getItems().addAll(Theme.ThemeType.values());
    themeComboBox.setValue(Theme.ThemeType.DARK); // Default
    themeComboBox.setMaxWidth(400);
    themeComboBox.setStyle("-fx-background-color: " + Theme.PANEL_COLOR1 + "; -fx-text-fill: white;");

    CoTaButton saveBtn = new CoTaButton("Save Changes", CoTaButton.StyleType.PRIMARY);
    saveBtn.setOnAction(e -> saveChanges());

    statusLabel = new Label();
    statusLabel.setStyle("-fx-text-fill: #2ECC71; -fx-font-size: 14px;");

    form.getChildren().addAll(profileImageView, nameField, emailField, bilkentIdField,
        photoLabel, photoBox,
        themeLabel, themeComboBox,
        saveBtn, statusLabel);

    this.getChildren().addAll(header, form);
  }

  private TextField createStyledTextField(String prompt, String value) {
    TextField tf = new TextField(value);
    tf.setPromptText(prompt);
    tf.setStyle("-fx-background-color: " + Theme.PANEL_COLOR1
        + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 5;");
    return tf;
  }

  private void updateProfileImage(String path) {
    if (path != null && !path.isEmpty()) {
      try {

        if (!path.startsWith("file:") && !path.startsWith("http")) {
          path = new File(path).toURI().toString();
        }
        Image image = new Image(path, 100, 100, false, false);
        if (!image.isError()) {
          profileImageView.setImage(image);
        }
      } catch (Exception e) {
        System.out.println("Failed to load image: " + e.getMessage());
      }
    }
  }

  private void saveChanges() {
    User user = SessionManager.getInstance().getCurrentUser();
    if (user != null) {
      user.setFullName(nameField.getText());
      user.setEmail(emailField.getText());
      user.setBilkentId(bilkentIdField.getText());

      user.setProfilePhotoPath(photoPathField.getText());

      SessionManager.getInstance().getRepository().updateUser(user);

      // Apply Theme
      Theme.ThemeType selectedTheme = themeComboBox.getValue();
      if (selectedTheme != null) {
        Theme.applyTheme(selectedTheme);
        com.example.MainApp.getInstance().reloadUI();
      } else {
        statusLabel.setText("Changes saved successfully!");
      }
    }
  }
}
