package git.campones76;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Handles thumbnail generation with configurable quality
 * Quality affects both the thumbnail size (as % of original) and compression quality
 */
public class ThumbnailGenerator {
    private final int qualityPercent;
    private final float compressionQuality;

    /**
     * Creates a ThumbnailGenerator with specified quality
     * @param quality Quality percentage (0-100)
     *                Controls both size (% of original image) and compression quality
     */
    public ThumbnailGenerator(int quality) {
        this.qualityPercent = Math.max(0, Math.min(100, quality));
        // Convert percentage to 0.0-1.0 range for compression
        this.compressionQuality = this.qualityPercent / 100f;
    }

    /**
     * Creates a ThumbnailGenerator with default quality (85%)
     */
    public ThumbnailGenerator() {
        this(85);
    }

    public void createThumbnail(File sourceFile, File destFile) throws IOException {
        BufferedImage originalImage = ImageIO.read(sourceFile);

        BufferedImage imageToSave;

        // At 100% quality, use original size
        if (qualityPercent == 100) {
            imageToSave = originalImage;
        } else {
            // Calculate thumbnail size as percentage of original image
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            // Apply quality percentage directly to original dimensions
            double qualityScale = qualityPercent / 100.0;

            int thumbWidth = (int) (width * qualityScale);
            int thumbHeight = (int) (height * qualityScale);

            // Ensure minimum size of 1px
            thumbWidth = Math.max(1, thumbWidth);
            thumbHeight = Math.max(1, thumbHeight);

            BufferedImage thumbnail = new BufferedImage(thumbWidth, thumbHeight,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = thumbnail.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(originalImage, 0, 0, thumbWidth, thumbHeight, null);
            g.dispose();

            imageToSave = thumbnail;
        }

        try {
            saveAsWebP(imageToSave, destFile);
        } catch (Exception e) {
            // Fallback to JPEG if WebP fails
            File jpegFile = new File(destFile.getParentFile(),
                    destFile.getName().replace(".webp", ".jpg"));
            saveAsJPEG(imageToSave, jpegFile);
        }
    }

    private void saveAsWebP(BufferedImage image, File destFile) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");
        if (!writers.hasNext()) {
            throw new IOException("WebP writer not available");
        }

        ImageWriter writer = writers.next();
        ImageWriteParam writeParam = writer.getDefaultWriteParam();

        try {
            // Set compression quality if supported
            if (writeParam.canWriteCompressed()) {
                writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

                // Get supported compression types
                String[] compressionTypes = writeParam.getCompressionTypes();
                if (compressionTypes != null && compressionTypes.length > 0) {
                    writeParam.setCompressionType(compressionTypes[0]);
                }

                // Use compression quality based on slider
                writeParam.setCompressionQuality(compressionQuality);
            }

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(destFile)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(image, null, null), writeParam);
            }
        } finally {
            writer.dispose();
        }
    }

    private void saveAsJPEG(BufferedImage image, File destFile) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            // Final fallback
            ImageIO.write(image, "jpg", destFile);
            return;
        }

        ImageWriter writer = writers.next();
        ImageWriteParam writeParam = writer.getDefaultWriteParam();

        try {
            // Set JPEG compression quality
            if (writeParam.canWriteCompressed()) {
                writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writeParam.setCompressionQuality(compressionQuality);
            }

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(destFile)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(image, null, null), writeParam);
            }
        } finally {
            writer.dispose();
        }
    }
}
