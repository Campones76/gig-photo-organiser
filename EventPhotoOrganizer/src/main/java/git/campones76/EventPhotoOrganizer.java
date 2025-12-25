package git.campones76;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

/**
 * Main application class for the Event Photo Organizer
 */
public class EventPhotoOrganizer extends JFrame {
    private final EventDetailsPanel detailsPanel;
    private final PhotoSelectionPanel photoPanel;
    private final JLabel statusLabel;
    private final List<File> selectedPhotos;

    public EventPhotoOrganizer() {
        selectedPhotos = new ArrayList<>();
        detailsPanel = new EventDetailsPanel();
        photoPanel = new PhotoSelectionPanel();
        statusLabel = new JLabel(" ");
        initializeUI();
        setupMacOSIntegration();
    }

    private void initializeUI() {
        setTitle("Event Photo Organizer");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(Box.createVerticalStrut(20));

        // Title
        JLabel titleLabel = new JLabel("Event Photo Organizer");
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(20));

        // Details panel
        detailsPanel.setAlignmentX(CENTER_ALIGNMENT);
        add(detailsPanel);
        add(Box.createVerticalStrut(20));

        // Photo selection panel
        photoPanel.setAlignmentX(CENTER_ALIGNMENT);
        photoPanel.setImportListener(() -> importPhotos());
        photoPanel.setClearListener(() -> clearPhotos());
        add(photoPanel);
        add(Box.createVerticalStrut(15));

        // Organize button
        JButton organizeButton = new JButton("Organize Photos & Generate HTML");
        organizeButton.setAlignmentX(CENTER_ALIGNMENT);
        organizeButton.addActionListener(e -> organizePhotos());
        add(organizeButton);

        // Status label
        statusLabel.setAlignmentX(CENTER_ALIGNMENT);
        statusLabel.setForeground(new java.awt.Color(0, 128, 0));
        add(Box.createVerticalStrut(10));
        add(statusLabel);

        setLocationRelativeTo(null);
    }

    /**
     * Sets up macOS-specific integration (menu bar, dock icon, about handler)
     */
    private void setupMacOSIntegration() {
        // Set application icon
        try {
            InputStream iconStream = getClass().getResourceAsStream("/assets/ico/app-icon.png");
            if (iconStream != null) {
                Image icon = ImageIO.read(iconStream);
                setIconImage(icon);

                // Set dock icon on macOS
                if (Taskbar.isTaskbarSupported()) {
                    Taskbar taskbar = Taskbar.getTaskbar();
                    if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                        taskbar.setIconImage(icon);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }

        // Set up macOS About handler
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.APP_ABOUT)) {
                desktop.setAboutHandler(e -> showAboutDialog());
            }
        }

        // For non-macOS systems, add a Help menu
        if (!System.getProperty("os.name").toLowerCase().contains("mac")) {
            createMenuBar();
        }
    }

    /**
     * Creates a menu bar with Help > About option (for non-macOS systems)
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    /**
     * Shows the About dialog
     */
    private void showAboutDialog() {
        String aboutMessage = """
                Event Photo Organizer
                Version 1.0
                
                A very narrow minded tool for organizing event photos and generating
                beautiful HTML galleries with thumbnails.
                I made this as a replacement for the web export feature of Apple Aperture.
                
                I made this little program specifically to cover my needs, and to automate 
                something I had to do manually, idk if it's of any use to you guys but 
                fell free to modify it to your liking.
                
                Features:
                • Automatic photo organization
                • Thumbnail generation (WebP format)
                • Responsive HTML gallery creation
                • Event metadata management
                
                © 2025 Gabe Fernando
                """;

        JOptionPane.showMessageDialog(
                this,
                aboutMessage,
                "About Event Photo Organizer",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void importPhotos() {
        List<File> files = photoPanel.selectPhotos(this);
        if (files != null && !files.isEmpty()) {
            // Add to existing photos instead of replacing
            selectedPhotos.addAll(files);
            photoPanel.updatePhotoList(selectedPhotos);
            statusLabel.setText(selectedPhotos.size() + " photo(s) selected");
        }
    }

    private void clearPhotos() {
        if (selectedPhotos.isEmpty()) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear all selected photos?",
                "Clear Photos",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            selectedPhotos.clear();
            photoPanel.clearPhotoList();
            statusLabel.setText("All photos cleared");
        }
    }

    private void organizePhotos() {
        if (selectedPhotos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please import photos first!",
                    "No Photos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        EventMetadata metadata = detailsPanel.getEventMetadata();
        if (!metadata.isValid()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in event name, date, and photographer!",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        File destinationDir = selectDestinationDirectory();
        if (destinationDir != null) {
            PhotoOrganizer organizer = new PhotoOrganizer(selectedPhotos, metadata);
            organizer.organize(this, destinationDir, (success, message) -> {
                if (success) {
                    statusLabel.setText("Successfully organized " + selectedPhotos.size() +
                            " photos and generated HTML gallery!");
                    JOptionPane.showMessageDialog(this,
                            message,
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            message,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

    private File selectDestinationDirectory() {
        // Use native directory picker
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        FileDialog dirDialog = new FileDialog(this, "Select destination folder", FileDialog.LOAD);
        dirDialog.setVisible(true);
        System.setProperty("apple.awt.fileDialogForDirectories", "false");

        String directory = dirDialog.getDirectory();
        String file = dirDialog.getFile();

        if (directory != null && file != null) {
            return new File(directory + file);
        } else if (directory != null) {
            return new File(directory);
        }

        return null;
    }

    public static void main(String[] args) {
        // Set macOS system properties before creating UI
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", "Event Photo Organizer");
        }

        SwingUtilities.invokeLater(() -> {
            EventPhotoOrganizer app = new EventPhotoOrganizer();
            app.setVisible(true);
        });
    }
}