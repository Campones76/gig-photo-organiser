package git.campones76;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for selecting and displaying photos
 */
public class PhotoSelectionPanel extends JPanel {
    private final JTextArea photoListArea;
    private final JButton importButton;
    private final JButton clearButton;
    private Runnable importListener;
    private Runnable clearListener;

    public PhotoSelectionPanel() {
        photoListArea = new JTextArea(8, 50);
        importButton = new JButton("Import Photos");
        clearButton = new JButton("Clear All");
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMaximumSize(new Dimension(600, 320));
        setOpaque(false);

        // Button panel with import and clear buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setMaximumSize(new Dimension(600, 40));
        buttonPanel.setOpaque(false);

        importButton.addActionListener(e -> {
            if (importListener != null) {
                importListener.run();
            }
        });
        buttonPanel.add(importButton);

        clearButton.addActionListener(e -> {
            if (clearListener != null) {
                clearListener.run();
            }
        });
        buttonPanel.add(clearButton);

        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(buttonPanel);
        add(Box.createVerticalStrut(15));

        JLabel photoListLabel = new JLabel("Selected Photos:");
        photoListLabel.setForeground(Color.WHITE);
        photoListLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(photoListLabel);
        add(Box.createVerticalStrut(5));

        photoListArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(photoListArea);
        scrollPane.setMaximumSize(new Dimension(600, 180));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane);
    }

    public void setImportListener(Runnable listener) {
        this.importListener = listener;
    }

    public void setClearListener(Runnable listener) {
        this.clearListener = listener;
    }

    public List<File> selectPhotos(Component parent) {
        // Use native file dialog
        FileDialog fileDialog = new FileDialog((Frame) SwingUtilities.getWindowAncestor(parent),
                "Select Photos", FileDialog.LOAD);

        // Enable multi-selection
        fileDialog.setMultipleMode(true);

        // Set file filter for images
        fileDialog.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String lower = name.toLowerCase();
                return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                        lower.endsWith(".png") || lower.endsWith(".gif") ||
                        lower.endsWith(".bmp") || lower.endsWith(".webp");
            }
        });

        fileDialog.setVisible(true);

        // Get selected files
        File[] files = fileDialog.getFiles();
        if (files != null && files.length > 0) {
            return Arrays.asList(files);
        }

        return null;
    }

    public void updatePhotoList(List<File> photos) {
        StringBuilder sb = new StringBuilder();
        for (File file : photos) {
            sb.append(file.getName()).append("\n");
        }
        photoListArea.setText(sb.toString());
    }

    public void clearPhotoList() {
        photoListArea.setText("");
    }
}