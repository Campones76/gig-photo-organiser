package git.campones76;

/**
 * Utility methods for file operations
 */
public class FileUtils {

    /**
     * Extracts the file extension from a filename
     * @param filename the filename to process
     * @return the file extension without the dot, or "jpg" as default
     */
    public static String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return (lastDot > 0) ? filename.substring(lastDot + 1) : "jpg";
    }
}