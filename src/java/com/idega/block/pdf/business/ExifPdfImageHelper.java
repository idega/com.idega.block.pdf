package com.idega.block.pdf.business;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

/**
 * Java 8 / iText 5 helper for JPEG EXIF orientation.
 * */
public final class ExifPdfImageHelper {

    private static final Logger LOGGER = Logger.getLogger(ExifPdfImageHelper.class.getName());

    private static final ExifPdfImageHelper instance = new ExifPdfImageHelper();

    private ExifPdfImageHelper() {
    }

    public static final ExifPdfImageHelper getInstance() {
    	return instance;
    }

    public Image create(byte[] bytes) throws IOException, BadElementException {
        Image image = Image.getInstance(bytes);

        // JPEG EXIF correction only. Other formats keep iText's own handling,
        // including TIFF's built-in orientation handling.
        if (!image.isJpeg()) {
            return image;
        }

        int orientation = readOrientation(bytes);
        switch (orientation) {
            case 3:
                image.setRotationDegrees(180f);
                break;
            case 6:
                image.setRotationDegrees(-90f); // Clockwise
                break;
            case 8:
                image.setRotationDegrees(90f); // Counterclockwise
                break;
            case 2:
            case 4:
            case 5:
            case 7:
                return createMirroredImage(bytes, orientation);
            default:
                // Orientation 1, or missing/invalid orientation: keep as stored.
                break;
        }

        // For ordinary rotations, keep the original JPEG without re-encoding.
        return image;
    }

    private int readOrientation(byte[] bytes) {
        try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
            Metadata metadata = JpegMetadataReader.readMetadata(input);
            ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            Integer orientation = directory == null ? null : directory.getInteger(ExifIFD0Directory.TAG_ORIENTATION);

            if (orientation != null && orientation >= 1 && orientation <= 8) {
                return orientation;
            }
        } catch (JpegProcessingException | IOException e) {
            // Keep an otherwise readable photo when its metadata cannot be read.
            LOGGER.log(Level.WARNING, "Cannot read JPEG EXIF orientation; using stored orientation.", e);
        }
        return 1;
    }

    private Image createMirroredImage(byte[] bytes, int orientation) throws IOException, BadElementException {
        BufferedImage source;
        try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
            source = ImageIO.read(input);
        }
        if (source == null) {
            throw new IOException("Cannot decode JPEG for EXIF mirroring.");
        }

        int width = source.getWidth();
        int height = source.getHeight();
        boolean swapDimensions = orientation == 5 || orientation == 7;

        AffineTransform transform;
        switch (orientation) {
            case 2: // Horizontal reflection
                transform = new AffineTransform(-1, 0, 0, 1, width, 0);
                break;
            case 4: // Vertical reflection
                transform = new AffineTransform(1, 0, 0, -1, 0, height);
                break;
            case 5: // Transpose
                transform = new AffineTransform(0, 1, 1, 0, 0, 0);
                break;
            case 7: // Transverse
                transform = new AffineTransform(0, -1, -1, 0, height, width);
                break;
            default:
                throw new IllegalArgumentException("Not a mirrored orientation: " + orientation);
        }

        BufferedImage corrected = new BufferedImage(
                swapDimensions ? height : width,
                swapDimensions ? width : height,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D graphics = corrected.createGraphics();
        try {
            graphics.drawImage(source, transform, null);
        } finally {
            graphics.dispose();
        }

        return Image.getInstance(corrected, null);
    }

}