package git.campones76;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Generates CSS and metadata files
 */
public class AssetGenerator {

    public void copyAssets(File cssDir, File imgDir, File icoDir) throws IOException {
        copyCSSAssets(cssDir);
        copyImageAssets(imgDir);
        copyFaviconAssets(icoDir);
    }

    /**
     * Copies CSS assets from resources to the destination directory
     * @param cssDir destination directory for CSS files
     * @throws IOException if copying fails
     */
    private void copyCSSAssets(File cssDir) throws IOException {
        copyResourceToFile("/assets/css/global.css", new File(cssDir, "global.css"));
    }

    private void createImageReadme(File imgDir) throws IOException {
        File readmeFile = new File(imgDir, "README.txt");
        try (PrintWriter w = new PrintWriter(new FileWriter(readmeFile))) {
            w.println("Please place arrow image files here:");
            w.println("- previous.gif");
            w.println("- previous_active.gif");
        }
    }

    /**
     * Copies image assets from resources to the destination directory
     * @param imgDir destination directory for images
     * @throws IOException if copying fails
     */
    private void copyImageAssets(File imgDir) throws IOException {
        String[] imageFiles = {
                "previous.gif",
                "previous_active.gif",
                "404-panda.png",
                "header1.png"
        };

        for (String imageFile : imageFiles) {
            copyResourceToFile("/assets/img/" + imageFile, new File(imgDir, imageFile));
        }
    }

    /**
     * Copies favicon assets from resources to the destination directory
     * @param icoDir destination directory for favicon files
     * @throws IOException if copying fails
     */
    private void copyFaviconAssets(File icoDir) throws IOException {
        copyResourceToFile("/assets/ico/favicon.svg", new File(icoDir, "favicon.svg"));
    }

    /**
     * Copies a resource from the classpath to a file
     * @param resourcePath path to resource (e.g., "/assets/img/previous.gif")
     * @param destFile destination file
     * @throws IOException if resource not found or copying fails
     */
    private void copyResourceToFile(String resourcePath, File destFile) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath +
                        ". Please ensure the file exists in src/main/resources" + resourcePath);
            }
            Files.copy(is, destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void createMetadataFile(File destDir, EventMetadata metadata, int photoCount) {
        try {
            File metadataFile = new File(destDir, "event-info.txt");
            try (PrintWriter w = new PrintWriter(new FileWriter(metadataFile))) {
                w.println("Event Name: " + metadata.getEventName());
                w.println("Venue: " + (metadata.getVenue().isEmpty() ? "Not specified" : metadata.getVenue()));
                w.println("Location: " + (metadata.getLocation().isEmpty() ? "Not specified" : metadata.getLocation()));
                w.println("Event Date: " + metadata.getEventDate());
                w.println("Photographer: " + metadata.getPhotographer());
                w.println("Number of Photos: " + photoCount);
                w.println("Organized on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }
        } catch (IOException e) {
            System.err.println("Could not create metadata file: " + e.getMessage());
        }
    }
}