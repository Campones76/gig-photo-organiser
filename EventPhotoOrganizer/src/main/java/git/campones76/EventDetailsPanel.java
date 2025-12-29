package git.campones76;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for entering event details
 */
public class EventDetailsPanel extends JPanel {
    private final JTextField eventNameField;
    private final JTextField venueField;
    private final JTextField locationField;
    private final JTextField eventDateField;
    private final JTextField photographerField;

    public EventDetailsPanel() {
        eventNameField = new JTextField();
        venueField = new JTextField();
        locationField = new JTextField();
        eventDateField = new JTextField();
        photographerField = new JTextField();

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new GridLayout(5, 2, 10, 10));
        setMaximumSize(new Dimension(600, 160));
        setOpaque(false);

        add(createWhiteLabel("Event Name:"));
        add(eventNameField);

        add(createWhiteLabel("Venue:"));
        add(venueField);

        add(createWhiteLabel("Location:"));
        add(locationField);

        add(createWhiteLabel("Event Date (YYYY-MM-DD):"));
        add(eventDateField);

        add(createWhiteLabel("Photographer:"));
        add(photographerField);
    }

    private JLabel createWhiteLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        return label;
    }

    public EventMetadata getEventMetadata() {
        return new EventMetadata(
                eventNameField.getText().trim(),
                venueField.getText().trim(),
                locationField.getText().trim(),
                eventDateField.getText().trim(),
                photographerField.getText().trim()
        );
    }
}