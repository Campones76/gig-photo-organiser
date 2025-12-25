package git.campones76;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Handles thumbnail generation
 */
public class ThumbnailGenerator {
    private static final int MAX_THUMBNAIL_SIZE = 300;

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
            ImageIO.write(thumbnail, "jpg", jpegFile);
        }
    }

    private void saveAsWebP(BufferedImage image, File destFile) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");
        if (writers.hasNext()) {
            ImageWriter writer = writers.next();
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(destFile)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(image, null, null),
                        writer.getDefaultWriteParam());
                writer.dispose();
            }
        } else {
            throw new IOException("WebP writer not available");
        }
    }
}