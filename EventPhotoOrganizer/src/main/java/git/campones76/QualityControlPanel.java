package git.campones76;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for controlling thumbnail quality settings
 */
public class QualityControlPanel extends JPanel {
    private final JSlider qualitySlider;
    private final JLabel qualityLabel;
    private final JButton autoButton;
    private static final int DEFAULT_QUALITY = 85;

    public QualityControlPanel() {
        qualitySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, DEFAULT_QUALITY);
        qualityLabel = new JLabel(DEFAULT_QUALITY + "%");
        autoButton = new JButton("Default");
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMaximumSize(new Dimension(600, 80));
        setOpaque(false);

        // Title and auto button row
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setMaximumSize(new Dimension(600, 30));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Thumbnail Quality:");
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        qualityLabel.setForeground(Color.WHITE);
        titlePanel.add(qualityLabel);

        autoButton.setToolTipText("Set to recommended quality (85%)");
        autoButton.addActionListener(e -> setQuality(DEFAULT_QUALITY));
        titlePanel.add(autoButton);

        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(titlePanel);
        add(Box.createVerticalStrut(5));

        // Slider panel
        JPanel sliderPanel = new JPanel(new BorderLayout(10, 0));
        sliderPanel.setMaximumSize(new Dimension(600, 40));
        sliderPanel.setOpaque(false);

        qualitySlider.setMajorTickSpacing(25);
        qualitySlider.setMinorTickSpacing(5);
        qualitySlider.setPaintTicks(true);
        qualitySlider.setPaintLabels(true);
        qualitySlider.setOpaque(false);
        qualitySlider.setForeground(Color.WHITE);
        qualitySlider.addChangeListener(e -> {
            int value = qualitySlider.getValue();
            qualityLabel.setText(value + "%");
        });

        sliderPanel.add(qualitySlider, BorderLayout.CENTER);
        sliderPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(sliderPanel);
    }

    /**
     * Gets the current quality setting as a value between 0 and 100
     */
    public int getQuality() {
        return qualitySlider.getValue();
    }

    /**
     * Sets the quality to a specific value
     */
    public void setQuality(int quality) {
        quality = Math.max(0, Math.min(100, quality));
        qualitySlider.setValue(quality);
        qualityLabel.setText(quality + "%");
    }
}