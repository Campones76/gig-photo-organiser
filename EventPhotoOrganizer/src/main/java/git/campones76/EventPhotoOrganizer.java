package git.campones76;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;
import java.io.InputStream;

/**
 * Main application class for the Event Photo Organizer
 */
public class EventPhotoOrganizer extends JFrame {
    private final EventDetailsPanel detailsPanel;
    private final PhotoSelectionPanel photoPanel;
    private final QualityControlPanel qualityPanel;
    private final JLabel statusLabel;
    private final List<File> selectedPhotos;
    private Image backgroundImage;

    public EventPhotoOrganizer() {
        selectedPhotos = new ArrayList<>();
        detailsPanel = new EventDetailsPanel();
        photoPanel = new PhotoSelectionPanel();
        qualityPanel = new QualityControlPanel();
        statusLabel = new JLabel(" ");
        loadBackgroundImage();
        initializeUI();
        setupMacOSIntegration();
    }

    private void loadBackgroundImage() {
        try {
            InputStream bgStream = getClass().getResourceAsStream("/assets/img/lenin.png");
            if (bgStream != null) {
                backgroundImage = ImageIO.read(bgStream);
            }
        } catch (IOException e) {
            System.err.println("Could not load background image: " + e.getMessage());
        }
    }

    private void playSound() {
        try {
            InputStream audioSource = getClass().getResourceAsStream("/assets/audio/Grouch.wav");
            if (audioSource == null) return;

            InputStream bufferedIn = new java.io.BufferedInputStream(audioSource);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip Ipod = AudioSystem.getClip();
            Ipod.open(audioStream);
            Ipod.start();
        } catch (Exception e) {
            System.err.println("D= Error playing sound: " + e.getMessage());
        }

    }


    private void initializeUI() {
        setTitle("Event Photo Organizer");
        setSize(700, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create custom content pane with background
        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    Graphics2D g2d = (Graphics2D) g.create();

                    // High-quality rendering hints
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Tile size (scaled down)
                    int tileWidth = 64;
                    int tileHeight = 64;

                    int cols = (getWidth() + tileWidth - 1) / tileWidth;
                    int rows = (getHeight() + tileHeight - 1) / tileHeight;

                    for (int row = 0; row < rows; row++) {
                        for (int col = 0; col < cols; col++) {
                            int x = col * tileWidth;
                            int y = row * tileHeight;
                            g2d.drawImage(backgroundImage, x, y, tileWidth, tileHeight, null);
                        }
                    }

                    g2d.dispose();
                }
            }
        };
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setOpaque(false);
        setContentPane(contentPane);

        add(Box.createVerticalStrut(20));

        // Title
        JLabel titleLabel = new JLabel("Event Photo Organizer");
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
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

        // Quality control panel
        qualityPanel.setAlignmentX(CENTER_ALIGNMENT);
        add(qualityPanel);
        add(Box.createVerticalStrut(15));

        // Organize button
        JButton organizeButton = new JButton("Organize Photos & Generate HTML");
        organizeButton.setAlignmentX(CENTER_ALIGNMENT);
        organizeButton.addActionListener(e -> organizePhotos());
        add(organizeButton);

        // Status label
        statusLabel.setAlignmentX(CENTER_ALIGNMENT);
        statusLabel.setForeground(Color.WHITE);
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
     * Shows the About dialog with black background and white text
     */
    private void showAboutDialog() {
        String aboutMessage = """
                Event Photo Organizer
                Version 1.1
                
                A very narrow minded tool for organizing event photos and generating
                beautiful HTML galleries with thumbnails.
                I made this as a replacement for the web export feature of Apple Aperture.
                
                I made this little program specifically to cover my needs, and to automate 
                something I had to do manually, idk if it's of any use to you guys but 
                fell free to modify it to your liking.
                
                Features:
                • Automatic photo organization
                • Thumbnail generation (WebP format)
                • Adjustable thumbnail quality
                • Responsive HTML gallery creation
                • Event metadata management
                
                © 2025 Gabe Fernando
                """;


        JTextArea aboutText = new JTextArea(aboutMessage);
        aboutText.setEditable(false);
        aboutText.setForeground(Color.WHITE);
        aboutText.setBackground(Color.BLACK);
        aboutText.setFont(new Font ("Arial", Font.PLAIN, 12));
        aboutText.setMargin(new Insets(10, 10, 10, 10));

        JLabel link = new JLabel("<html><a href=''>WEBSITE</a></html>");
        link.setForeground(Color.WHITE);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    Desktop.getDesktop().browse(new java.net.URI("https://splash.gabefernando.net"));
                } catch (Exception e) {
                    System.err.println("Could not open browser: " + e.getMessage());
                }
            }
        });

        JPanel aboutPanel = new JPanel(new BorderLayout(5, 5));
        aboutPanel.setBackground(Color.BLACK);
        aboutPanel.add(aboutText, BorderLayout.CENTER);

        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        linkPanel.setBackground(Color.BLACK);
        linkPanel.add(link);
        aboutPanel.add(linkPanel, BorderLayout.SOUTH);

        JOptionPane optionPane = new JOptionPane(aboutPanel, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = optionPane.createDialog("Credits");
        dialog.getContentPane().setBackground(Color.BLACK);
        dialog.setVisible(true);

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
        ImageIcon rubbishBinIcon = null;
        try {
            InputStream iconStream = getClass().getResourceAsStream("/assets/img/FullTrashIcon.png");
            if (iconStream != null) {
                Image rubbishBinIcon_og = ImageIO.read(iconStream);
                Image scaledImage = rubbishBinIcon_og.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                rubbishBinIcon = new ImageIcon(scaledImage);
            }
        } catch (IOException e) {
            System.err.println("Could not load trash icon: " + e.getMessage());
        }
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear all selected photos?\n(You can’t undo this action.)",
                "Clear Selection",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                rubbishBinIcon
        );

        if (result == JOptionPane.YES_OPTION) {
            selectedPhotos.clear();
            photoPanel.clearPhotoList();
            statusLabel.setText("All photos cleared");

            //Play sound
            playSound();
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
            int quality = qualityPanel.getQuality();
            PhotoOrganizer organizer = new PhotoOrganizer(selectedPhotos, metadata, quality);
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