package com.grocerypos.util;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Theme manager for switching between different FlatLaf themes
 */
public class ThemeManager {
    public enum Theme {
        LIGHT("Light", FlatLightLaf.class),
        DARK("Dark", FlatDarkLaf.class),
        MAC_LIGHT("Mac Light", FlatMacLightLaf.class),
        MAC_DARK("Mac Dark", FlatMacDarkLaf.class);
        
        private final String displayName;
        private final Class<? extends LookAndFeel> lafClass;
        
        Theme(String displayName, Class<? extends LookAndFeel> lafClass) {
            this.displayName = displayName;
            this.lafClass = lafClass;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public Class<? extends LookAndFeel> getLafClass() {
            return lafClass;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    private static Theme currentTheme = Theme.MAC_LIGHT;
    private static final Map<Theme, LookAndFeel> themeCache = new HashMap<>();
    
    /**
     * Get the current theme
     */
    public static Theme getCurrentTheme() {
        return currentTheme;
    }
    
    /**
     * Set the current theme
     */
    public static void setCurrentTheme(Theme theme) {
        currentTheme = theme;
    }
    
    /**
     * Apply a theme to the application
     */
    public static void applyTheme(Theme theme) {
        try {
            LookAndFeel laf = getLookAndFeel(theme);
            UIManager.setLookAndFeel(laf);
            currentTheme = theme;
            // Global UI defaults tuned for macOS-inspired look
            // Rounded corners and focus widths
            UIManager.put("Component.arc", 12);
            UIManager.put("Button.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("TabbedPane.selectedBackground", new java.awt.Color(0,0,0,0));
            UIManager.put("ScrollBar.showButtons", false);
            UIManager.put("Button.focusWidth", 1);
            UIManager.put("Component.focusWidth", 1);
            // Prefer native window decorations (gives rounded corners on supported OS)
            com.formdev.flatlaf.FlatLaf.setUseNativeWindowDecorations(true);
            // Font: prefer SF Pro if available, fallback to system UI
            String[] preferredFonts = new String[] { "SF Pro Text", "SF Pro Display", "San Francisco", "Segoe UI", "Helvetica Neue", "Arial" };
            java.awt.Font chosen = null;
            for (String name : preferredFonts) {
                java.awt.Font f = new java.awt.Font(name, java.awt.Font.PLAIN, 13);
                if (f.getFamily().equals(name) || f.canDisplay('A')) {
                    chosen = f;
                    break;
                }
            }
            if (chosen != null) {
                UIManager.put("defaultFont", chosen);
            }
            
            // Update all existing windows
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
            
        } catch (Exception e) {
            System.err.println("Failed to apply theme: " + theme.getDisplayName());
            e.printStackTrace();
        }
    }
    
    /**
     * Get or create a LookAndFeel instance for the theme
     */
    private static LookAndFeel getLookAndFeel(Theme theme) throws Exception {
        if (themeCache.containsKey(theme)) {
            return themeCache.get(theme);
        }
        
        LookAndFeel laf = (LookAndFeel) theme.getLafClass().getDeclaredConstructor().newInstance();
        themeCache.put(theme, laf);
        return laf;
    }
    
    /**
     * Get all available themes
     */
    public static Theme[] getAllThemes() {
        return Theme.values();
    }
    
    /**
     * Get theme by display name
     */
    public static Theme getThemeByName(String name) {
        for (Theme theme : Theme.values()) {
            if (theme.getDisplayName().equals(name)) {
                return theme;
            }
        }
        return Theme.LIGHT; // Default fallback
    }
    
    /**
     * Initialize with default theme
     */
    public static void initialize() {
        // On macOS, let menus live in the screen menu bar
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.appearance", "system");
        applyTheme(currentTheme);
    }
}
