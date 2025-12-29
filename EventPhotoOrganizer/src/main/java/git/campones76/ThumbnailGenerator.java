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
 */
public class ThumbnailGenerator {
    private static final int MAX_THUMBNAIL_SIZE = 300;
    private final int quality;

    /**
     * Creates a ThumbnailGenerator with specified quality
     * @param quality Quality percentage (0-100)
     */
    public ThumbnailGenerator(int quality) {
        this.quality = Math.max(0, Math.min(100, quality));
    }

    /**
     * Creates a ThumbnailGenerator with default quality (85%)
     */
    public ThumbnailGenerator() {
        this(85);
    }

    public void createThumbnail(File sourceFile, File destFile) throws IOException {
        BufferedImage originalImage = ImageIO.read(sourceFile);
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        double scale = (width > height)
                ? (double) MAX_THUMBNAIL_SIZE / width
                : (double) MAX_THUMBNAIL_SIZE / height;

        int thumbWidth = (int) (width * scale);
        int thumbHeight = (int) (height * scale);

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

        try {
            saveAsWebP(thumbnail, destFile);
        } catch (Exception e) {
            // Fallback to JPEG if WebP fails
            File jpegFile = new File(destFile.getParentFile(),
                    destFile.getName().replace(".webp", ".jpg"));
            saveAsJPEG(thumbnail, jpegFile);
        }
    }

    private void saveAsWebP(BufferedImage image, File destFile) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");
        if (writers.hasNext()) {
            ImageWriter writer = writers.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();

            // Set compression quality if supported
            if (writeParam.canWriteCompressed()) {
                writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writeParam.setCompressionQuality(quality / 100f);
            }

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(destFile)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(image, null, null), writeParam);
                writer.dispose();
            }
        } else {
            throw new IOException("WebP writer not available");
        }
    }

    private void saveAsJPEG(BufferedImage image, File destFile) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (writers.hasNext()) {
            ImageWriter writer = writers.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();

            // Set JPEG compression quality
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionQuality(quality / 100f);

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(destFile)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(image, null, null), writeParam);
                writer.dispose();
            }
        } else {
            // Fallback without compression control
            ImageIO.write(image, "jpg", destFile);
        }
    }
}