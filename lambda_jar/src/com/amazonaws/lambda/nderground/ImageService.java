/** \file
 * 
 * Jan 6, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package com.amazonaws.lambda.nderground;

import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

/**
 * <h3>
 * ImageService
 * </h3>
 * 
 * <p>
 * This class is a container for a set of static functions for scaling images. These images are read via an
 * InputStream object. The functions return a BufferedImage object with the scaled image result.
 * </p>
 * 
 * <p>
 * Jan 30, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class ImageService {

    
    /**
     * Read an image from an input stream and scale it so that the maximum image dimension is maxDim.
     * If the scaling operation was successful, return the image as a BufferedImage object. Otherwise
     * the function returns null.
     * 
     * @param inputImageStream an InputStream for the image to be read.
     * @param maxDim the maximum dimension (in either width or height) for the resulting image
     * @param logger An implementation of the AbstractLoggerInterface for logging errors.
     * @return a BufferedImage object or null if the operation failed.
     */
    public static BufferedImage scaleImage(InputStream inputImageStream, 
                                           int maxDim,
                                           AbstractLoggerInterface logger) {
        BufferedImage scaledImage = null;
        BufferedImage original = null;
        try {
            original = ImageIO.read( inputImageStream );
            try {
                if (original != null) {
                    scaledImage = Scalr.resize(original, maxDim);
                } else {
                    logger.log("ImageService::scaleImage (MaxDim): scaled image is null");
                }
            }
            catch (ImagingOpException e) {
                logger.log("ImageService::scaleImage (MaxDim): Error scaling image: " + e.getLocalizedMessage());
            }
        } catch (IOException e) {
            logger.log("ImageService::scaleImage (MaxDim): Error reading image stream: " + e.getLocalizedMessage());
        }
        finally {
            if (original != null) {
                // free up the memory consumed by the original image
                original.flush();
            }
        }
        return scaledImage;
    } // scaleImage
    

    /**
     * Read an image from the InputStream and scale it to height x width pixels.
     * 
     * @param inputImageStream the input stream to read the image from.
     * @param height the height, in pixels, for the scaled image.
     * @param width the width, in pixels, for the scaled image.
     * @param logger an implementation of the AbstractLogger interface for logging errors.
     * @return a BufferedImage object or null if the scale operation failed.
     */
    public static BufferedImage scaleImage(InputStream inputImageStream, 
                                           int height, 
                                           int width,
                                           AbstractLoggerInterface logger) {
        BufferedImage original = null;
        BufferedImage scaledImage = null;
        try {
            original = ImageIO.read( inputImageStream );
            try {
                if (original != null) {
                    scaledImage = Scalr.resize(original, width, height);
                } else {
                    logger.log("ImageService::scaleImage: scaled image is null");
                }
            }
            catch (ImagingOpException e) {
                logger.log("ImageService::scaleImage: Error scaling image: " + e.getLocalizedMessage());
            }
        } catch (IOException e) {
            logger.log("ImageService::scaleImage: Error reading image stream: " + e.getLocalizedMessage());
        }
        finally {
            if (original != null) {
                // free up the memory consumed by the original image
                original.flush();
            }
        }
        return scaledImage;
    }  // scaleImage
    

    /**
     *<p> 
     * Scale an image by height. Often it is the image height that is an issue in image display, so
     * scale the image by a maximum height, adjusting the width by the same ratio.
     * </p>
     * 
     * @param inputImageStream InputStream to read the image from
     * @param maxHeight the maxium height for the scaled image
     * @param logger an implementation of the AbstractLoggerInterfacer for logging errors
     * @return a BufferedImage or null if the image could not be read or scaled.
     */
    public static BufferedImage scaleImageByHeight(InputStream inputImageStream, 
                                                   int maxHeight, 
                                                   AbstractLoggerInterface logger) {
        BufferedImage original = null;
        BufferedImage scaledImage = null;
        try {
            original = ImageIO.read( inputImageStream );
            if (original != null) {
                int originalWidth = original.getWidth();
                int originalHeight = original.getHeight();
                int adjWidth = originalWidth;
                int adjHeight = originalHeight;
                if (maxHeight < originalHeight) {
                    adjHeight = maxHeight;
                    double ratio = (double)maxHeight / (double)originalHeight;
                    adjWidth = (int)Math.round( adjWidth * ratio );
                    try {
                        scaledImage = Scalr.resize(original, adjWidth, adjHeight);
                    }
                    catch (ImagingOpException e) {
                        logger.log("ImageService::scaleImageByHeight: error scaling image: " + e.getLocalizedMessage());
                    }
                } else {
                    // the height of the original image is less than or equal to maxHeight so the image 
                    // should not be scaled.
                    scaledImage = original;
                }
            } else {
                logger.log("ImageService::scaleImageByHeight: scaled image is null");
            }
        }
        catch (IOException e) {
            logger.log("ImageService::scaleImageByHeight: error reading image: " + e.getLocalizedMessage());
        }
        finally {
            if (original != null && original != scaledImage) {
                original.flush();
            }
        }
        return scaledImage;
    } // scaleImageByHeight
    
    
    /**
     * <p>
     * Calculater the height and width for a thumbnail image, based on the maximum thumbnail height and width and the 
     * image height and width.
     * </p>
     * 
     * @param maxThumbHeight
     * @param maxThumbWidth
     * @param imageHeight
     * @param imageWidth
     * @return a hash map where the strings "height" and "width" return the thumbnail height and width
     */
    public static HashMap<String, Integer> calculateThumbDimensions( int maxThumbHeight, int maxThumbWidth,
                                                                     int imageHeight, int imageWidth) {
        HashMap<String, Integer> thumbInfo = new HashMap<String, Integer>();
        int adjHeight = imageHeight;
        int adjWidth = imageWidth;
        if (imageHeight > maxThumbHeight && imageWidth >= imageHeight) { // a square or landscape image
            adjHeight = maxThumbHeight;
            double ratio = imageHeight / maxThumbHeight;
            adjWidth = (int) Math.round( imageWidth / ratio );
        } else if (imageWidth > maxThumbWidth) {  // portrait
            adjWidth = maxThumbWidth;
            double ratio = imageWidth / maxThumbWidth;
            adjHeight = (int) Math.round(imageHeight / ratio );
        }
        thumbInfo.put("height", adjHeight);
        thumbInfo.put("width", adjWidth);
        return thumbInfo;
    }


    public static BufferedImage thumbnailScale(InputStream imageInputStream, int maxThumbHeight, int maxThumbWidth,
                                               AbstractLoggerInterface logger) {
        BufferedImage original = null;
        BufferedImage thumbnailImage = null;
        try {
            original = ImageIO.read( imageInputStream );
            if (original != null) {
                int originalWidth = original.getWidth();
                int originalHeight = original.getHeight();
                HashMap<String, Integer> thumbInfo = calculateThumbDimensions( maxThumbHeight, maxThumbWidth, originalHeight, originalWidth);
                int adjHeight = thumbInfo.get("height");
                int adjWidth = thumbInfo.get("width");
                if (originalWidth > adjWidth || originalHeight > adjHeight) {
                    thumbnailImage = Scalr.resize(original, adjWidth, adjHeight);
                } else {
                    thumbnailImage = original;
                }
            }
        }
        catch (IOException e) {
            logger.log("ImageService::thumbnailScale: error reading image: " + e.getLocalizedMessage());
        }
        finally {
            if (original != null && original != thumbnailImage) {
                original.flush();
            }
        }
        return thumbnailImage;
    }

}
