package git.campones76;

/**
 * Data class to hold event metadata
 */
public class EventMetadata {
    private final String eventName;
    private final String venue;
    private final String location;
    private final String eventDate;
    private final String photographer;

    public EventMetadata(String eventName, String venue, String location,
                         String eventDate, String photographer) {
        this.eventName = eventName;
        this.venue = venue;
        this.location = location;
        this.eventDate = eventDate;
        this.photographer = photographer;
    }

    public String getEventName() {
        return eventName;
    }

    public String getVenue() {
        return venue;
    }

    public String getLocation() {
        return location;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getPhotographer() {
        return photographer;
    }

    public boolean isValid() {
        return eventName != null && !eventName.trim().isEmpty() &&
                eventDate != null && !eventDate.trim().isEmpty() &&
                photographer != null && !photographer.trim().isEmpty();
    }

    public String getFolderName() {
        String sanitizedVenue = venue.replaceAll("[^a-zA-Z0-9-_]", "-");
        return (sanitizedVenue + "-" + eventDate).toLowerCase();
    }

    public String getHeaderTitle() {
        StringBuilder title = new StringBuilder();
        if (venue != null && !venue.isEmpty()) {
            title.append(venue);
        }
        if (location != null && !location.isEmpty()) {
            if (title.length() > 0) {
                title.append(", ");
            }
            title.append(location);
        }
        if (title.length() > 0) {
            title.append(" - ");
        }
        title.append(eventName);
        return title.toString();
    }
}