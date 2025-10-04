package com.grocerypos.ui.components;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebcamScannerDialog extends JDialog {
    private Webcam webcam;
    private WebcamPanel panel;
    private JButton startButton;
    private JButton stopButton;
    private JButton closeButton;
    private JLabel statusLabel;
    private JComboBox<String> modeCombo; // Product | Customer
    private volatile String decodedText;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public WebcamScannerDialog(Window owner) {
        super(owner, "Webcam Scanner", ModalityType.APPLICATION_MODAL);
        setSize(640, 520);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8, 8));

        statusLabel = new JLabel("Idle");
        add(statusLabel, BorderLayout.SOUTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        closeButton = new JButton("Close");
        stopButton.setEnabled(false);
        modeCombo = new JComboBox<>(new String[]{"Product", "Customer"});
        controls.add(new JLabel("Mode:"));
        controls.add(modeCombo);
        controls.add(startButton);
        controls.add(stopButton);
        controls.add(closeButton);
        add(controls, BorderLayout.NORTH);

        startButton.addActionListener(e -> start());
        stopButton.addActionListener(e -> stop());
        closeButton.addActionListener(e -> {
            stop();
            dispose();
        });
    }

    public String getDecodedText() { return decodedText; }
    public boolean isCustomerMode() { return "Customer".equals(modeCombo.getSelectedItem()); }

    private void start() {
        if (running.get()) return;
        try {
            if (webcam == null) {
                webcam = Webcam.getDefault();
                if (webcam == null) {
                    JOptionPane.showMessageDialog(this, "No webcam detected", "Webcam", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                webcam.setViewSize(new java.awt.Dimension(640, 480));
                panel = new WebcamPanel(webcam);
                panel.setMirrored(true);
                add(panel, BorderLayout.CENTER);
                revalidate();
            }
            webcam.open();
            running.set(true);
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            statusLabel.setText("Scanning...");
            new Thread(this::loopScan, "webcam-scan").start();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to start webcam: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stop() {
        running.set(false);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        statusLabel.setText("Stopped");
        try {
            if (webcam != null && webcam.isOpen()) webcam.close();
        } catch (Exception ignore) {}
    }

    private void loopScan() {
        MultiFormatReader reader = new MultiFormatReader();
        while (running.get()) {
            try {
                BufferedImage image = webcam.getImage();
                if (image == null) continue;
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                Result result = reader.decodeWithState(bitmap);
                if (result != null) {
                    decodedText = result.getText();
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Detected: " + decodedText);
                        stop();
                        dispose();
                    });
                    break;
                }
            } catch (NotFoundException nf) {
                // keep scanning
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> statusLabel.setText("Error: " + ex.getMessage()));
            }
            try { Thread.sleep(150); } catch (InterruptedException ignored) {}
        }
    }
}


