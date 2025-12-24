package com.example.ui.components;

import com.example.Entity.User;
import com.example.Manager.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.List;

public class AddMemberPopup extends VBox {

    private Runnable onCancel;
    private java.util.function.Consumer<User> onAdd;  

    public AddMemberPopup() {
        this.setMaxWidth(350);
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.CENTER_LEFT);

this.setStyle(
                "-fx-background-color: #3C3C3C;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

        Label title = new Label("Add Member from Friends");
        title.setFont(Theme.getHeaderFont());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

VBox friendsContainer = new VBox(10);

List<User> friends = SessionManager.getInstance().getFriendsList();

        if (friends.isEmpty()) {
            Label empty = new Label("No friends found.\nAdd friends from the top menu first!");
            empty.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold; -fx-text-alignment: center;");
            friendsContainer.getChildren().add(empty);
        } else {
            for (User friend : friends) {
                friendsContainer.getChildren().add(createFriendItem(friend));
            }
        }

        ScrollPane scroll = new ScrollPane(friendsContainer);
        scroll.setMaxHeight(250);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

CoTaButton cancelBtn = new CoTaButton("Close", CoTaButton.StyleType.SECONDARY);
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        cancelBtn.setOnAction(e -> {
            if (onCancel != null)
                onCancel.run();
        });

        this.getChildren().addAll(title, scroll, cancelBtn);
    }

    private HBox createFriendItem(User friend) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: #555; -fx-background-radius: 10; -fx-cursor: hand;");

row.setOnMouseEntered(
                e -> row.setStyle("-fx-background-color: #666; -fx-background-radius: 10; -fx-cursor: hand;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: #555; -fx-background-radius: 10;"));

Label name = new Label(friend.getFullName());
        name.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

Label addIcon = new Label("+");
        addIcon.setStyle("-fx-text-fill: #2ECC71; -fx-font-size: 20px; -fx-font-weight: bold;");

        row.getChildren().addAll(name, spacer, addIcon);

row.setOnMouseClicked(e -> {
            if (onAdd != null)
                onAdd.accept(friend);
        });

        return row;
    }

    public void setOnCancel(Runnable r) {
        this.onCancel = r;
    }

    public void setOnAdd(java.util.function.Consumer<User> c) {
        this.onAdd = c;
    }
}