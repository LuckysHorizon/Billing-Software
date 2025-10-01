package com.grocerypos.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Utility class for creating smooth animations and transitions
 */
public class AnimationUtils {
    
    /**
     * Create a smooth fade-in animation for a component
     */
    public static void fadeIn(Component component, int durationMs) {
        fadeIn(component, durationMs, null);
    }
    
    /**
     * Create a smooth fade-in animation for a component with callback
     */
    public static void fadeIn(Component component, int durationMs, ActionListener onComplete) {
        if (!(component instanceof Window)) {
            return; // Only works with windows
        }
        
        Window window = (Window) component;
        Timer timer = new Timer(16, null);
        final long startTime = System.currentTimeMillis();
        
        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float) elapsed / durationMs);
            
            // Easing function for smooth animation
            float easedProgress = easeInOutCubic(progress);
            
            if (window instanceof JWindow) {
                ((JWindow) window).setOpacity(easedProgress);
            } else if (window instanceof JDialog) {
                ((JDialog) window).setOpacity(easedProgress);
            } else if (window instanceof JFrame) {
                ((JFrame) window).setOpacity(easedProgress);
            }
            
            if (progress >= 1.0f) {
                timer.stop();
                if (onComplete != null) {
                    onComplete.actionPerformed(e);
                }
            }
        });
        
        window.setOpacity(0.0f);
        timer.start();
    }
    
    /**
     * Create a smooth fade-out animation for a component
     */
    public static void fadeOut(Component component, int durationMs) {
        fadeOut(component, durationMs, null);
    }
    
    /**
     * Create a smooth fade-out animation for a component with callback
     */
    public static void fadeOut(Component component, int durationMs, ActionListener onComplete) {
        if (!(component instanceof Window)) {
            return; // Only works with windows
        }
        
        Window window = (Window) component;
        Timer timer = new Timer(16, null);
        final long startTime = System.currentTimeMillis();
        
        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float) elapsed / durationMs);
            
            // Easing function for smooth animation
            float easedProgress = easeInOutCubic(progress);
            
            if (window instanceof JWindow) {
                ((JWindow) window).setOpacity(1.0f - easedProgress);
            } else if (window instanceof JDialog) {
                ((JDialog) window).setOpacity(1.0f - easedProgress);
            } else if (window instanceof JFrame) {
                ((JFrame) window).setOpacity(1.0f - easedProgress);
            }
            
            if (progress >= 1.0f) {
                timer.stop();
                if (onComplete != null) {
                    onComplete.actionPerformed(e);
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * Create a smooth slide-in animation for a component
     */
    public static void slideIn(Component component, int durationMs, SlideDirection direction) {
        slideIn(component, durationMs, direction, null);
    }
    
    /**
     * Create a smooth slide-in animation for a component with callback
     */
    public static void slideIn(Component component, int durationMs, SlideDirection direction, ActionListener onComplete) {
        Timer timer = new Timer(16, null);
        final long startTime = System.currentTimeMillis();
        final Point originalLocation = component.getLocation();
        final Dimension size = component.getSize();
        
        // Set initial position based on direction
        Point startPosition = new Point(originalLocation);
        switch (direction) {
            case FROM_LEFT:
                startPosition.x = -size.width;
                break;
            case FROM_RIGHT:
                startPosition.x = component.getParent().getWidth();
                break;
            case FROM_TOP:
                startPosition.y = -size.height;
                break;
            case FROM_BOTTOM:
                startPosition.y = component.getParent().getHeight();
                break;
        }
        
        component.setLocation(startPosition);
        
        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float) elapsed / durationMs);
            
            // Easing function for smooth animation
            float easedProgress = easeOutCubic(progress);
            
            Point currentPosition = new Point();
            currentPosition.x = (int) (startPosition.x + (originalLocation.x - startPosition.x) * easedProgress);
            currentPosition.y = (int) (startPosition.y + (originalLocation.y - startPosition.y) * easedProgress);
            
            component.setLocation(currentPosition);
            
            if (progress >= 1.0f) {
                timer.stop();
                component.setLocation(originalLocation);
                if (onComplete != null) {
                    onComplete.actionPerformed(e);
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * Create a smooth scale animation for a component
     */
    public static void scaleIn(Component component, int durationMs) {
        scaleIn(component, durationMs, null);
    }
    
    /**
     * Create a smooth scale animation for a component with callback
     */
    public static void scaleIn(Component component, int durationMs, ActionListener onComplete) {
        Timer timer = new Timer(16, null);
        final long startTime = System.currentTimeMillis();
        final Dimension originalSize = component.getSize();
        final Point originalLocation = component.getLocation();
        
        // Set initial scale
        component.setSize(0, 0);
        component.setLocation(
            originalLocation.x + originalSize.width / 2,
            originalLocation.y + originalSize.height / 2
        );
        
        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float) elapsed / durationMs);
            
            // Easing function for smooth animation
            float easedProgress = easeOutBack(progress);
            
            int currentWidth = (int) (originalSize.width * easedProgress);
            int currentHeight = (int) (originalSize.height * easedProgress);
            
            component.setSize(currentWidth, currentHeight);
            component.setLocation(
                originalLocation.x + (originalSize.width - currentWidth) / 2,
                originalLocation.y + (originalSize.height - currentHeight) / 2
            );
            
            if (progress >= 1.0f) {
                timer.stop();
                component.setSize(originalSize);
                component.setLocation(originalLocation);
                if (onComplete != null) {
                    onComplete.actionPerformed(e);
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * Create a smooth color transition animation
     */
    public static void colorTransition(Component component, Color fromColor, Color toColor, int durationMs) {
        colorTransition(component, fromColor, toColor, durationMs, null);
    }
    
    /**
     * Create a smooth color transition animation with callback
     */
    public static void colorTransition(Component component, Color fromColor, Color toColor, int durationMs, ActionListener onComplete) {
        Timer timer = new Timer(16, null);
        final long startTime = System.currentTimeMillis();
        
        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float) elapsed / durationMs);
            
            // Easing function for smooth animation
            float easedProgress = easeInOutCubic(progress);
            
            int r = (int) (fromColor.getRed() + (toColor.getRed() - fromColor.getRed()) * easedProgress);
            int g = (int) (fromColor.getGreen() + (toColor.getGreen() - fromColor.getGreen()) * easedProgress);
            int b = (int) (fromColor.getBlue() + (toColor.getBlue() - fromColor.getBlue()) * easedProgress);
            int a = (int) (fromColor.getAlpha() + (toColor.getAlpha() - fromColor.getAlpha()) * easedProgress);
            
            component.setForeground(new Color(r, g, b, a));
            
            if (progress >= 1.0f) {
                timer.stop();
                component.setForeground(toColor);
                if (onComplete != null) {
                    onComplete.actionPerformed(e);
                }
            }
        });
        
        timer.start();
    }
    
    // Easing functions for smooth animations
    private static float easeInOutCubic(float t) {
        return t < 0.5f ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
    }
    
    private static float easeOutCubic(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }
    
    private static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return 1 + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
    }
    
    /**
     * Slide direction enum
     */
    public enum SlideDirection {
        FROM_LEFT, FROM_RIGHT, FROM_TOP, FROM_BOTTOM
    }
}
