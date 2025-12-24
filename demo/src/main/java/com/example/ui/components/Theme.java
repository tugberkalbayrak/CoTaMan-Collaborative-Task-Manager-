package com.example.ui.components;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Theme {

    public static String BG_COLOR = "#2B2B2B";
    public static String PANEL_COLOR1 = "#3C3C3C";
    public static String PANEL_COLOR2 = "#5d5757ff";
    public static String PRIMARY_COLOR = "#4b1462ff";
    public static String SECONDARY_COLOR = "#0d5f2fff";
    public static String TEXT_WHITE = "#FFFFFF";
    public static String TEXT_GRAY = "#CCCCCC";

    public enum ThemeType {
        DARK, LIGHT, FOREST, OCEAN, SUNSET, LAVENDER, MIDNIGHT, COFFEE
    }

    public static void applyTheme(ThemeType type) {
        switch (type) {
            case LIGHT:
                BG_COLOR = "#F5F6FA";
                PANEL_COLOR1 = "#FFFFFF";
                PANEL_COLOR2 = "#E1E1E1";
                PRIMARY_COLOR = "#3498DB";
                SECONDARY_COLOR = "#2ECC71";
                TEXT_WHITE = "#2C3E50";
                TEXT_GRAY = "#7F8C8D";
                break;
            case FOREST:
                BG_COLOR = "#1B261D"; // Dark Green-Black
                PANEL_COLOR1 = "#2C3E2E"; // Deep Green
                PANEL_COLOR2 = "#405944";
                PRIMARY_COLOR = "#27AE60"; // Emerald
                SECONDARY_COLOR = "#E67E22"; // Wood/Orange
                TEXT_WHITE = "#ECF0F1";
                TEXT_GRAY = "#BDC3C7";
                break;
            case OCEAN:
                BG_COLOR = "#1A252F"; // Deep Navy
                PANEL_COLOR1 = "#2C3E50"; // Navy
                PANEL_COLOR2 = "#34495E";
                PRIMARY_COLOR = "#3498DB"; // Blue
                SECONDARY_COLOR = "#1ABC9C"; // Teal
                TEXT_WHITE = "#ECF0F1";
                TEXT_GRAY = "#BDC3C7";
                break;
            case SUNSET:
                BG_COLOR = "#2C1B18"; // Dark Red-Brown
                PANEL_COLOR1 = "#4E2C22";
                PANEL_COLOR2 = "#6E3D32";
                PRIMARY_COLOR = "#E74C3C"; // Red-Orange
                SECONDARY_COLOR = "#F39C12"; // Gold
                TEXT_WHITE = "#ECF0F1";
                TEXT_GRAY = "#BDC3C7";
                break;
            case LAVENDER:
                BG_COLOR = "#F3E5F5"; // Light Purple White
                PANEL_COLOR1 = "#FFFFFF";
                PANEL_COLOR2 = "#E1BEE7";
                PRIMARY_COLOR = "#9B59B6"; // Purple
                SECONDARY_COLOR = "#8E44AD"; // Darker Purple
                TEXT_WHITE = "#4A235A";
                TEXT_GRAY = "#7D3C98";
                break;
            case MIDNIGHT:
                BG_COLOR = "#000000"; // Pitch Black
                PANEL_COLOR1 = "#111111"; // Almost Black
                PANEL_COLOR2 = "#222222";
                PRIMARY_COLOR = "#0000FF"; // Pure Blue
                SECONDARY_COLOR = "#FFFF00"; // Yellow Contrast
                TEXT_WHITE = "#FFFFFF";
                TEXT_GRAY = "#AAAAAA";
                break;
            case COFFEE:
                BG_COLOR = "#3E2723"; // Dark Brown
                PANEL_COLOR1 = "#4E342E";
                PANEL_COLOR2 = "#5D4037";
                PRIMARY_COLOR = "#D7CCC8"; // Cream
                SECONDARY_COLOR = "#A1887F"; // Light Brown
                TEXT_WHITE = "#EFEBE9";
                TEXT_GRAY = "#BCAAA4";
                break;
            case DARK:
            default:
                BG_COLOR = "#2B2B2B";
                PANEL_COLOR1 = "#3C3C3C";
                PANEL_COLOR2 = "#5d5757ff";
                PRIMARY_COLOR = "#4b1462ff"; // Original Purple
                SECONDARY_COLOR = "#0d5f2fff"; // Original Green
                TEXT_WHITE = "#FFFFFF";
                TEXT_GRAY = "#CCCCCC";
                break;
        }
    }

    public static Font getHeaderFont() {
        return Font.font("System", FontWeight.BOLD, 24);
    }

    public static Font getRegularFont() {
        return Font.font("System", FontWeight.NORMAL, 14);
    }
}
