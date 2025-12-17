package com.example.ui.components;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Theme {

    public static final String BG_COLOR = "#2B2B2B";
    public static final String PANEL_COLOR1 = "#3C3C3C";
    public static final String PANEL_COLOR2 = "#5d5757ff";
    public static final String PRIMARY_COLOR = "#4b1462ff";
    public static final String SECONDARY_COLOR = "#0d5f2fff";
    public static final String TEXT_WHITE = "#FFFFFF";
    public static final String TEXT_GRAY = "#CCCCCC";

    public static Font getHeaderFont() {
        return Font.font("System", FontWeight.BOLD, 24);
    }
    
    public static Font getRegularFont() {
        return Font.font("System", FontWeight.NORMAL, 14);
    }
}
