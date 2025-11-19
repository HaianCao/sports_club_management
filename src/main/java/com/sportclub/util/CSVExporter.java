package com.sportclub.util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CSVExporter {

    public static void exportTableToCSV(JTable table, String baseFileName) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            String fileName = baseFileName + "_" + timestamp + ".csv";

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
            fileChooser.setSelectedFile(new File(fileName));

            File defaultDir = new File(System.getProperty("user.home"));
            File desktopDir = new File(defaultDir, "Desktop");
            if (desktopDir.exists() && desktopDir.isDirectory()) {
                fileChooser.setCurrentDirectory(desktopDir);
            } else {
                fileChooser.setCurrentDirectory(defaultDir);
            }

            int result = fileChooser.showSaveDialog(null);
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File csvFile = fileChooser.getSelectedFile();
            if (!csvFile.getName().toLowerCase().endsWith(".csv")) {
                csvFile = new File(csvFile.getAbsolutePath() + ".csv");
            }

            FileWriter writer = new FileWriter(csvFile, false);

            TableModel model = table.getModel();
            for (int col = 0; col < model.getColumnCount(); col++) {
                writer.write(escapeCSV(model.getColumnName(col)));
                if (col < model.getColumnCount() - 1) {
                    writer.write(",");
                }
            }
            writer.write("\n");

            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    String stringValue = (value != null) ? value.toString() : "";
                    writer.write(escapeCSV(stringValue));
                    if (col < model.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.write("\n");
            }

            writer.close();

            JOptionPane.showMessageDialog(
                    null,
                    "Xuất file thành công!\nFile được lưu tại: " + csvFile.getAbsolutePath(),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Lỗi khi xuất file: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }

        if (value.contains(",") || value.contains("\n") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }
}