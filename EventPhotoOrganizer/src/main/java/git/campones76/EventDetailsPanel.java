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

        add(new JLabel("Event Name:"));
        add(eventNameField);

        add(new JLabel("Venue:"));
        add(venueField);

        add(new JLabel("Location:"));
        add(locationField);

        add(new JLabel("Event Date (YYYY-MM-DD):"));
        add(eventDateField);

        add(new JLabel("Photographer:"));
        add(photographerField);
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