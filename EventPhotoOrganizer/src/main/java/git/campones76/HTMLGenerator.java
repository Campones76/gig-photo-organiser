package git.campones76;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Generates the HTML gallery file
 */
public class HTMLGenerator {

    public void generateGallery(File destDir, EventMetadata metadata,
                                List<String> photoFilenames) throws IOException {
        File htmlFile = new File(destDir, "index.html");
        try (PrintWriter w = new PrintWriter(new FileWriter(htmlFile))) {
            writeHeader(w, metadata);
            writeBody(w, metadata, photoFilenames);
            writeFooter(w);
        }
    }

    private void writeHeader(PrintWriter w, EventMetadata metadata) {
        w.println("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n    <meta charset=\"UTF-8\">");
        w.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        w.println("    <title>" + " - Gabe Fernando Past Gig Date Gallery Page</title>");
        w.println("    <meta name=\"description\" localizable=\"true\" content=\"Created by Apple Aperture\">\n");
        w.println("    <!-- Favicon -->");
        w.println("    <link rel=\"icon\" type=\"image/svg+xml\" href=\"assets/ico/favicon.svg\">\n");
        w.println("    <!-- Security Headers -->");
        w.println("    <meta http-equiv=\"X-Content-Type-Options\" content=\"nosniff\">");
        w.println("    <meta http-equiv=\"X-Frame-Options\" content=\"SAMEORIGIN\">");
        w.println("    <meta name=\"referrer\" content=\"no-referrer-when-downgrade\">\n    ");
        w.println("    <!-- nanogallery2 CSS -->");
        w.println("    <link href=\"https://cdnjs.cloudflare.com/ajax/libs/nanogallery2/3.0.5/css/nanogallery2.min.css\" rel=\"stylesheet\">\n    ");
        w.println("    <link rel=\"stylesheet\" type=\"text/css\" href=\"assets/css/global.css\">\n    ");
        writeStyles(w);
        w.println("</head>");
    }

    private void writeStyles(PrintWriter w) {
        w.println("    <style>");
        w.println("        body { margin: 0; padding: 0; font-family: \"HelveticaNeue-Light\", \"Helvetica Neue Light\", \"Helvetica Neue\", Helvetica, Arial, Verdana, sans-serif; }");
        w.println("        #header { margin: 0; padding: 20px 30px; color: #444; }");
        w.println("        #headerinfo1 { margin-bottom: 15px; }");
        w.println("        #headerinfo1 h2 { display: inline; margin: 0; font-size: 0.9em; font-weight: bold; }");
        w.println("        #headerinfo1 h3 { display: inline; margin: 0; font-size: 0.9em; font-weight: normal; }");
        w.println("        #header h1 { margin: 10px 0; font-size: 2.4em; font-weight: bold; }");
        w.println("        #gallery-container { padding: 0 20px; margin-bottom: 30px; }");
        w.println("        #footer { margin: 30px 0; padding: 0 30px; font-size: 0.7em; clear: both; }");
        w.println("        #footer .index a { display: inline-block; text-decoration: none; color: #666; padding-left: 15px; background: url(\"assets/img/previous.gif\") no-repeat left center; }");
        w.println("        #footer .index a:hover { color: #444; background: url(\"assets/img/previous_active.gif\") no-repeat left center; }");
        w.println("        .nGY2 { background-color: transparent !important; }");
        w.println("        .nGY2ViewerContainer { background-color: #fff !important; }");
        w.println("        .nGY2ViewerContent { background-color: #fff !important; }");
        w.println("        .nGY2ViewerAreaNext, .nGY2ViewerAreaPrevious { background-color: transparent !important; }");
        w.println("    </style>");
    }

    private void writeBody(PrintWriter w, EventMetadata metadata, List<String> photoFilenames) {
        w.println("<body>");
        w.println("    <div id=\"header\">");
        w.println("        <div id=\"headerinfo1\">");
        w.println("            <h2>Photographer:&nbsp;</h2>");
        w.println("            <h3>© " + new SimpleDateFormat("yyyy").format(new Date())
                + " " + metadata.getPhotographer() + "</h3>");
        w.println("        </div>");
        w.println("        <h1>" + metadata.getHeaderTitle() + "</h1>");
        w.println("    </div>\n");
        w.println("    <div id=\"gallery-container\">");
        w.println("        <div id=\"nanogallery2\"></div>");
        w.println("    </div>\n");
        w.println("    <div id=\"footer\">");
        w.println("        <p class=\"index\"><a href=\"https://www.gabefernando.net/previous-shows\">Go Back</a></p>");
        w.println("    </div>\n");

        writeScripts(w, photoFilenames);
        w.println("</body>\n</html>");
    }

    private void writeScripts(PrintWriter w, List<String> photoFilenames) {
        w.println("    <!-- jQuery -->");
        w.println("    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js\"></script>\n    ");
        w.println("    <!-- nanogallery2 JS -->");
        w.println("    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/nanogallery2/3.0.5/jquery.nanogallery2.min.js\"></script>\n");
        w.println("    <script>");
        w.println("        $(document).ready(function() {");
        w.println("            $(\"#nanogallery2\").nanogallery2({");
        w.println("                items: [");

        for (int i = 0; i < photoFilenames.size(); i++) {
            String fn = photoFilenames.get(i).replace("\\", "\\\\").replace("'", "\\'");
            String tn = fn.replaceAll("\\.(jpeg|jpg|png|gif|bmp)$", ".webp");
            w.println("                    { src: 'pictures/" + fn + "', srct: 'thumbnails/"
                    + tn + "', title: '', description: '' }"
                    + (i < photoFilenames.size() - 1 ? "," : ""));
        }

        writeGalleryConfig(w);
        w.println("        });");
        w.println("    </script>");
    }

    private void writeGalleryConfig(PrintWriter w) {
        w.println("                ],\n                thumbnailDisplayOrder: 'random',");
        w.println("                galleryMosaic : [");
        w.println("                    { w: 2, h: 2, c: 1, r: 1 }, { w: 1, h: 1, c: 3, r: 1 }, { w: 1, h: 1, c: 3, r: 2 },");
        w.println("                    { w: 1, h: 2, c: 4, r: 1 }, { w: 2, h: 1, c: 5, r: 1 }, { w: 2, h: 2, c: 5, r: 2 },");
        w.println("                    { w: 1, h: 1, c: 4, r: 3 }, { w: 2, h: 1, c: 2, r: 3 }, { w: 1, h: 2, c: 1, r: 3 },");
        w.println("                    { w: 1, h: 1, c: 2, r: 4 }, { w: 2, h: 1, c: 3, r: 4 }, { w: 1, h: 1, c: 5, r: 4 }, { w: 1, h: 1, c: 6, r: 4 }");
        w.println("                ],");
        w.println("                galleryMosaicXS : [ { w: 2, h: 2, c: 1, r: 1 }, { w: 1, h: 1, c: 3, r: 1 }, { w: 1, h: 1, c: 3, r: 2 }, { w: 1, h: 2, c: 1, r: 3 }, { w: 2, h: 1, c: 2, r: 3 }, { w: 1, h: 1, c: 2, r: 4 }, { w: 1, h: 1, c: 3, r: 4 } ],");
        w.println("                galleryMosaicSM : [ { w: 2, h: 2, c: 1, r: 1 }, { w: 1, h: 1, c: 3, r: 1 }, { w: 1, h: 1, c: 3, r: 2 }, { w: 1, h: 2, c: 1, r: 3 }, { w: 2, h: 1, c: 2, r: 3 }, { w: 1, h: 1, c: 2, r: 4 }, { w: 1, h: 1, c: 3, r: 4 } ],");
        w.println("                thumbnailHeight: 180, thumbnailWidth: 220, thumbnailAlignment: 'scaled',");
        w.println("                galleryTheme: { thumbnail: { background: '#666', borderColor: '#000', borderRadius: '0px' }, thumbnailIcon: { color: '#fff' } },");
        w.println("                galleryDisplayMode: 'rows', galleryMaxRows: 10, gallerySorting: 'random',");
        w.println("                thumbnailGutterWidth: 20, thumbnailGutterHeight: 20, thumbnailBorderVertical: 0, thumbnailBorderHorizontal: 0,");
        w.println("                thumbnailHoverEffect2: 'scale120', touchAnimation: true, touchAutoOpenDelay: 500,");
        w.println("                viewerTools: { topLeft: 'label', topRight: 'downloadButton, closeButton' },");
        w.println("                viewerToolbar: { display: true, standard: 'label, pageCounter, downloadButton', minimized: 'label, pageCounter' },");
        w.println("                viewerGallery: 'bottom', viewerGalleryTWidth: 60, viewerGalleryTHeight: 60, viewerDisplayLogo: false,");
        w.println("                breakpointSizeSM: 500, breakpointSizeME: 700, breakpointSizeLA: 1000, breakpointSizeXL: 1200");
        w.println("            });");
    }

    private void writeFooter(PrintWriter w) {
        // Footer already written in writeBody
    }
}