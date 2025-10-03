package com.grocerypos.ui;

import com.grocerypos.ui.components.GlassCard;
import com.grocerypos.ui.components.ModernButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

public class BarcodeGeneratorWindow extends JFrame {
    private static BarcodeGeneratorWindow instance;
    private JTextField inputField;
    private JTextArea bulkArea;
    private JLabel previewLabel;
    private BufferedImage currentBarcode;

    public static BarcodeGeneratorWindow getInstance() {
        if (instance == null || !instance.isDisplayable()) {
            instance = new BarcodeGeneratorWindow(true);
        }
        return instance;
    }

    private BarcodeGeneratorWindow(boolean fromFactory) {
        System.out.println("[BarcodeGeneratorWindow] Constructing new instance " + this);
        setTitle("Barcode Generator");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        GlassCard top = new GlassCard();
        top.setLayout(new BorderLayout(8, 8));
        JPanel fields = new JPanel(new GridLayout(2, 1, 8, 8));
        JPanel singlePanel = new JPanel(new BorderLayout(8, 8));
        singlePanel.add(new JLabel("Product ID / Number:"), BorderLayout.WEST);
        inputField = new JTextField();
        singlePanel.add(inputField, BorderLayout.CENTER);
        fields.add(singlePanel);
        JPanel bulkPanel = new JPanel(new BorderLayout(8, 8));
        bulkPanel.add(new JLabel("Bulk (one per line):"), BorderLayout.NORTH);
        bulkArea = new JTextArea(4, 40);
        JScrollPane bulkScroll = new JScrollPane(bulkArea);
        bulkPanel.add(bulkScroll, BorderLayout.CENTER);
        fields.add(bulkPanel);
        top.add(fields, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        ModernButton previewBtn = new ModernButton("Preview", new Color(0, 122, 255));
        ModernButton printBtn = new ModernButton("Print", new Color(23, 162, 184));
        actions.add(previewBtn);
        actions.add(printBtn);
        top.add(actions, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        GlassCard center = new GlassCard();
        center.setLayout(new BorderLayout());
        previewLabel = new JLabel("Preview will appear here", SwingConstants.CENTER);
        center.add(new JScrollPane(previewLabel), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        previewBtn.addActionListener(e -> generatePreview());
        printBtn.addActionListener(e -> printBarcodes());
    }

    public BarcodeGeneratorWindow() {
        this(true);
    }

    private void generatePreview() {
        String value = inputField.getText().trim();
        if (value.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a value to preview.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            currentBarcode = createBarcodeImage(value, 320, 120);
            previewLabel.setIcon(new ImageIcon(currentBarcode));
            previewLabel.setText(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to generate barcode: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printBarcodes() {
        List<String> values = new ArrayList<>();
        if (!inputField.getText().trim().isEmpty()) values.add(inputField.getText().trim());
        for (String line : bulkArea.getText().split("\n")) {
            String v = line.trim();
            if (!v.isEmpty()) values.add(v);
        }
        if (values.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Provide at least one value to print.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("Barcode Print");
            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                int labelsPerPage = 8; // 2 columns x 4 rows
                int totalPages = (int) Math.ceil(values.size() / (double) labelsPerPage);
                if (pageIndex >= totalPages) return Printable.NO_SUCH_PAGE;

                Graphics2D g2 = (Graphics2D) graphics;
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                int columnWidth = (int) (pageFormat.getImageableWidth() / 2);
                int rowHeight = (int) (pageFormat.getImageableHeight() / 4);

                int start = pageIndex * labelsPerPage;
                for (int i = 0; i < labelsPerPage; i++) {
                    int idx = start + i;
                    if (idx >= values.size()) break;
                    int row = i / 2;
                    int col = i % 2;
                    int x = col * columnWidth + 20;
                    int y = row * rowHeight + 20;
                    String v = values.get(idx);
                    BufferedImage img;
                    try {
                        img = createBarcodeImage(v, columnWidth - 40, 100);
                    } catch (Exception e) {
                        // Skip invalid value
                        continue;
                    }
                    g2.drawImage(img, x, y, null);
                    g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
                    g2.drawString(v, x, y + 115);
                }
                return Printable.PAGE_EXISTS;
            });
            if (job.printDialog()) job.print();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Print failed: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BufferedImage createBarcodeImage(String value, int width, int height) throws Exception {
        BitMatrix matrix = new MultiFormatWriter().encode(value, BarcodeFormat.CODE_128, width, height);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }
}


