package git.campones76;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Handles the photo organization process
 */
public class PhotoOrganizer {
    private final List<File> photos;
    private final EventMetadata metadata;

    public PhotoOrganizer(List<File> photos, EventMetadata metadata) {
        this.photos = photos;
        this.metadata = metadata;
    }

    public void organize(Component parent, File baseDir, BiConsumer<Boolean, String> callback) {
        String folderName = metadata.getFolderName();
        File destDir = new File(baseDir, folderName);
        File picturesDir = new File(destDir, "pictures");
        File thumbnailsDir = new File(destDir, "thumbnails");
        File assetsDir = new File(destDir, "assets");
        File cssDir = new File(assetsDir, "css");
        File imgDir = new File(assetsDir, "img");
        File icoDir = new File(assetsDir, "ico");

        // Create directories
        picturesDir.mkdirs();
        thumbnailsDir.mkdirs();
        cssDir.mkdirs();
        imgDir.mkdirs();
        icoDir.mkdirs();

        // Show progress dialog
        JProgressBar progressBar = new JProgressBar(0, photos.size() * 2);
        progressBar.setStringPainted(true);
        JDialog progressDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent),
                "Processing Photos", true);
        progressDialog.setLayout(new BorderLayout(10, 10));
        JLabel progressLabel = new JLabel("Copying and creating thumbnails...");
        progressDialog.add(progressLabel, BorderLayout.NORTH);
        progressDialog.add(progressBar, BorderLayout.CENTER);
        progressDialog.setSize(400, 100);
        progressDialog.setLocationRelativeTo(parent);

        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            List<String> photoFilenames = new ArrayList<>();

            @Override
            protected Void doInBackground() throws Exception {
                int progress = 0;
                int count = 1;
                ThumbnailGenerator thumbnailGen = new ThumbnailGenerator();

                for (File photo : photos) {
                    String extension = FileUtils.getFileExtension(photo.getName());
                    String newFileName = String.format("Credit %s - %d.%s",
                            metadata.getPhotographer(), count, extension);
                    File destFile = new File(picturesDir, newFileName);
                    Files.copy(photo.toPath(), destFile.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                    publish(++progress);

                    String thumbnailName = newFileName.substring(0,
                            newFileName.lastIndexOf('.')) + ".webp";
                    File thumbnailFile = new File(thumbnailsDir, thumbnailName);
                    thumbnailGen.createThumbnail(photo, thumbnailFile);
                    photoFilenames.add(newFileName);
                    publish(++progress);
                    count++;
                }

                AssetGenerator assetGen = new AssetGenerator();
                assetGen.copyAssets(cssDir, imgDir, icoDir);
                assetGen.createMetadataFile(destDir, metadata, photos.size());

                HTMLGenerator htmlGen = new HTMLGenerator();
                htmlGen.generateGallery(destDir, metadata, photoFilenames);

                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                int latest = chunks.get(chunks.size() - 1);
                progressBar.setValue(latest);
                progressLabel.setText(String.format("Processing photo %d of %d...",
                        (latest + 1) / 2, photos.size()));
            }

            @Override
            protected void done() {
                progressDialog.dispose();
                try {
                    get();
                    callback.accept(true,
                            "Photos organized, thumbnails created, and HTML generated successfully!\n\n" +
                                    "Location: " + destDir.getAbsolutePath() + "\nHTML file: index.html");
                } catch (Exception ex) {
                    callback.accept(false, "Error processing photos: " + ex.getMessage());
                }
            }
        };

        worker.execute();
        progressDialog.setVisible(true);
    }
}