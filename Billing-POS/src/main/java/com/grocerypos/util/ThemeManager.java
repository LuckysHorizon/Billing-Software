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
 * Enhanced theme manager with futuristic macOS-inspired styling
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
     * Apply a theme to the application with enhanced futuristic styling
     */
    public static void applyTheme(Theme theme) {
        try {
            LookAndFeel laf = getLookAndFeel(theme);
            UIManager.setLookAndFeel(laf);
            currentTheme = theme;
            
            // Enhanced futuristic styling
            applyFuturisticStyling();
            
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
     * Apply futuristic macOS-inspired styling
     */
    private static void applyFuturisticStyling() {
        // Enhanced rounded corners and modern spacing
        UIManager.put("Component.arc", 16);
        UIManager.put("Button.arc", 16);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("ProgressBar.arc", 12);
        UIManager.put("CheckBox.arc", 8);
        UIManager.put("RadioButton.arc", 8);
        
        // Modern focus indicators
        UIManager.put("Button.focusWidth", 2);
        UIManager.put("Component.focusWidth", 2);
        UIManager.put("TextComponent.focusWidth", 2);
        
        // Enhanced colors for futuristic look
        UIManager.put("Button.default.background", new Color(0, 122, 255));
        UIManager.put("Button.default.foreground", Color.WHITE);
        UIManager.put("Button.hover.background", new Color(0, 100, 220));
        UIManager.put("Button.pressed.background", new Color(0, 80, 180));
        
        // Modern table styling
        UIManager.put("Table.background", new Color(255, 255, 255, 240));
        UIManager.put("Table.alternateRowColor", new Color(248, 249, 250));
        UIManager.put("Table.selectionBackground", new Color(0, 122, 255, 100));
        UIManager.put("Table.selectionForeground", new Color(0, 122, 255));
        UIManager.put("Table.gridColor", new Color(0, 0, 0, 20));
        
        // Enhanced scrollbar styling
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("ScrollBar.thumb", new Color(0, 0, 0, 60));
        UIManager.put("ScrollBar.track", new Color(0, 0, 0, 10));
        UIManager.put("ScrollBar.hoverThumb", new Color(0, 0, 0, 80));
        
        // Modern panel backgrounds
        UIManager.put("Panel.background", new Color(240, 242, 247));
        UIManager.put("ToolBar.background", new Color(255, 255, 255, 200));
        
        // Enhanced text field styling
        UIManager.put("TextField.background", new Color(255, 255, 255, 200));
        UIManager.put("TextField.borderColor", new Color(0, 0, 0, 30));
        UIManager.put("TextField.focusedBorderColor", new Color(0, 122, 255, 150));
        
        // Modern combo box styling
        UIManager.put("ComboBox.background", new Color(255, 255, 255, 200));
        UIManager.put("ComboBox.borderColor", new Color(0, 0, 0, 30));
        UIManager.put("ComboBox.focusedBorderColor", new Color(0, 122, 255, 150));
        
        // Enhanced dialog styling
        UIManager.put("Dialog.background", new Color(240, 242, 247));
        UIManager.put("Dialog.borderColor", new Color(0, 0, 0, 20));
        
        // Modern menu styling
        UIManager.put("MenuBar.background", new Color(255, 255, 255, 200));
        UIManager.put("Menu.background", new Color(255, 255, 255, 240));
        UIManager.put("MenuItem.background", new Color(255, 255, 255, 0));
        UIManager.put("MenuItem.selectionBackground", new Color(0, 122, 255, 100));
        
        // Enhanced tooltip styling
        UIManager.put("ToolTip.background", new Color(50, 50, 50, 240));
        UIManager.put("ToolTip.foreground", Color.WHITE);
        UIManager.put("ToolTip.borderColor", new Color(0, 0, 0, 50));
        
        // Modern progress bar styling
        UIManager.put("ProgressBar.background", new Color(0, 0, 0, 20));
        UIManager.put("ProgressBar.foreground", new Color(0, 122, 255));
        
        // Enhanced tabbed pane styling
        UIManager.put("TabbedPane.selectedBackground", new Color(255, 255, 255, 0));
        UIManager.put("TabbedPane.unselectedBackground", new Color(255, 255, 255, 100));
        UIManager.put("TabbedPane.selectedForeground", new Color(0, 122, 255));
        UIManager.put("TabbedPane.unselectedForeground", new Color(100, 100, 100));
        
        // Modern slider styling
        UIManager.put("Slider.background", new Color(255, 255, 255, 200));
        UIManager.put("Slider.foreground", new Color(0, 122, 255));
        UIManager.put("Slider.thumb", new Color(0, 122, 255));
        UIManager.put("Slider.track", new Color(0, 0, 0, 20));
        
        // Enhanced spinner styling
        UIManager.put("Spinner.background", new Color(255, 255, 255, 200));
        UIManager.put("Spinner.borderColor", new Color(0, 0, 0, 30));
        UIManager.put("Spinner.focusedBorderColor", new Color(0, 122, 255, 150));
        
        // Modern checkbox and radio button styling
        UIManager.put("CheckBox.background", new Color(255, 255, 255, 0));
        UIManager.put("CheckBox.foreground", new Color(50, 50, 50));
        UIManager.put("RadioButton.background", new Color(255, 255, 255, 0));
        UIManager.put("RadioButton.foreground", new Color(50, 50, 50));
        
        // Enhanced label styling
        UIManager.put("Label.foreground", new Color(50, 50, 50));
        
        // Modern split pane styling
        UIManager.put("SplitPane.background", new Color(240, 242, 247));
        UIManager.put("SplitPane.dividerColor", new Color(0, 0, 0, 30));
        
        // Enhanced tree styling
        UIManager.put("Tree.background", new Color(255, 255, 255, 240));
        UIManager.put("Tree.selectionBackground", new Color(0, 122, 255, 100));
        UIManager.put("Tree.selectionForeground", new Color(0, 122, 255));
        
        // Modern list styling
        UIManager.put("List.background", new Color(255, 255, 255, 240));
        UIManager.put("List.selectionBackground", new Color(0, 122, 255, 100));
        UIManager.put("List.selectionForeground", new Color(0, 122, 255));
        
        // Prefer native window decorations for rounded corners
        com.formdev.flatlaf.FlatLaf.setUseNativeWindowDecorations(true);
        
        // Enhanced font selection
        String[] preferredFonts = new String[] { 
            "SF Pro Text", "SF Pro Display", "San Francisco", 
            "Segoe UI", "Helvetica Neue", "Arial" 
        };
        Font chosen = null;
        for (String name : preferredFonts) {
            Font f = new Font(name, Font.PLAIN, 13);
            if (f.getFamily().equals(name) || f.canDisplay('A')) {
                chosen = f;
                break;
            }
        }
        if (chosen != null) {
            UIManager.put("defaultFont", chosen);
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
     * Initialize with default theme and enhanced styling
     */
    public static void initialize() {
        // macOS-specific system properties
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.appearance", "system");
        
        // Apply the default theme with futuristic styling
        applyTheme(currentTheme);
    }
    
    /**
     * Get current theme colors for custom components
     */
    public static Color getPrimaryColor() {
        return new Color(0, 122, 255);
    }
    
    public static Color getSuccessColor() {
        return new Color(34, 197, 94);
    }
    
    public static Color getWarningColor() {
        return new Color(245, 158, 11);
    }
    
    public static Color getErrorColor() {
        return new Color(239, 68, 68);
    }
    
    public static Color getInfoColor() {
        return new Color(59, 130, 246);
    }
    
    public static Color getBackgroundColor() {
        return new Color(240, 242, 247);
    }
    
    public static Color getGlassBackground() {
        return new Color(255, 255, 255, 200);
    }
}