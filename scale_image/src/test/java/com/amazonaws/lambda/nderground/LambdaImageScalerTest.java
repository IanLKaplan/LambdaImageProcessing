/** \file
 * 
 * Jan 17, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package com.amazonaws.lambda.nderground;

import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.amazonaws.regions.Regions;

public class LambdaImageScalerTest {
    private final static String S3_and_DynamoDB_ID = "Your AWS ID goes here";
    private final static String S3_and_DynamoDB_KEY = "Your AWS secret key goes here";
    private final static String bucketName = "name_of_your_S3_bucket";
    private final static String path = "image_scale_test_original.jpg";
    // Replace this with your region if it is not US_WEST_1
    private final static String awsRegionName = Regions.US_WEST_1.getName();
    
    public final static int originalWidth = 5472;
    public final static int originalHeight = 3648;
    
    public final static S3Service s3Service = new S3Service(awsRegionName, bucketName, S3_and_DynamoDB_ID, S3_and_DynamoDB_KEY );
    
    
    /**
    * <p>
    * S3ContentType
    * </p>
    * <p>
    * The media content for the file written to Amazon S3
    * </p>
    * <p>
    * Mar 14, 2015
    * </p>
    * 
    * @author Ian Kaplan, iank@bearcave.com
    */
   private enum S3ContentType {
       TEXT("Content-Type: text/html"),
       GIF("Content-Type: image/gif"),   // GIF image; Defined in RFC 2045 and RFC 2046
       JPEG("Content-Type: image/jpeg"), // JPEG JFIF image; Defined in RFC 2045 and RFC 2046
       PNG("Content-Type: image/png"),   // Portable Network Graphics; Defined in RFC 2083
       TIFF("Content-Type: image/tiff"); // TIF image;
       
       private final String contentType;
       private S3ContentType( final String type ) {
           contentType = type;
       }
       public String getType() { return contentType; }
   }
    
    
    public static boolean checkImageHeightWidth( S3Service s3Service, String imagePath, int height, int width, AbstractLoggerInterface logger ) {
        boolean imageOK = false;
        InputStream istream = null;
        try {
            istream = s3Service.s3ToInputStream(imagePath, logger);
            if (istream != null) {
                BufferedImage image = null;
                try {
                    image = ImageIO.read( istream );
                    int imageHeight = image.getHeight();
                    int imageWidth = image.getWidth();
                    if (imageHeight == height && imageWidth == width) {
                        imageOK = true;
                    } else {
                        logger.log("checkImageHeightWidth: imageHeight = " + imageHeight +
                                ", imageWidth = " + imageWidth + " expected (" + height + "," + width + ")");
                    }
                } catch (IOException e) {
                    logger.log("checkImageHeightWidth: error reading image - " + e.getLocalizedMessage());
                }
                finally {
                    if (image != null) {
                        image.flush();
                    }
                }
            } else {
                logger.log("checkImageHeightWidth: error creating InputStream");
            }
        }
        finally {
            if (istream != null) {
                try { istream.close(); } catch (IOException e) {}
            }
        }
        return imageOK;
    }
    
    
    
    public static boolean checkImageThumbnail( S3Service s3Service, String originalPath, String thumbnailPath, int maxThumbHeight, int maxThumbWidth, AbstractLoggerInterface logger ) {
        boolean imageOK = false;
        InputStream thumbnailStream = null;
        InputStream originalStream = null;
        try {
            originalStream = s3Service.s3ToInputStream(originalPath, logger);
            thumbnailStream = s3Service.s3ToInputStream(thumbnailPath, logger);
            if (thumbnailStream != null && originalStream != null) {
                BufferedImage originalImage = null;
                BufferedImage thumbnailImage = null;
                try {
                    originalImage = ImageIO.read(originalStream);
                    thumbnailImage = ImageIO.read( thumbnailStream );
                    int originalHeight = originalImage.getHeight();
                    int originalWidth = originalImage.getWidth();
                    int thumbnailHeight = thumbnailImage.getHeight();
                    int thumbnailWidth = thumbnailImage.getWidth();
                    // calculaterThumbDimensions( int maxThumbHeight, int maxThumbWidth, int imageHeight, int imageWidth)
                    HashMap<String, Integer> thumbInfo = ImageService.calculateThumbDimensions(maxThumbHeight, maxThumbWidth, originalHeight, originalWidth);
                    int adjHeight = thumbInfo.get("height");
                    int adjWidth = thumbInfo.get("width");
                    if (thumbnailHeight == adjHeight && thumbnailWidth == adjWidth) {
                        imageOK = true;
                    } else {
                        logger.log("checkImageThumbnail: imageHeight = " + thumbnailHeight +
                                ", imageWidth = " + thumbnailWidth + " expected (" + adjHeight + "," + adjWidth + ")");
                    }
                } catch (IOException e) {
                    logger.log("checkImageThumbnail: error reading image - " + e.getLocalizedMessage());
                }
                finally {
                    if (thumbnailImage != null) {
                        thumbnailImage.flush();
                    }
                    if (originalImage != null) {
                        originalImage.flush();
                    }
                }
            } else {
                logger.log("checkImageThumbnail: error creating InputStream");
            }
        }
        finally {
            if (thumbnailStream != null) {
                try { thumbnailStream.close(); } catch (IOException e) {}
            }
            if (originalStream != null) {
                try { originalStream.close(); } catch (IOException e) {}
            }
        }
        return imageOK;
    }
    
    
    
    public static boolean checkImageMaxDim( S3Service s3Service, String imagePath, int maxDim, AbstractLoggerInterface logger ) {
        boolean imageOK = false;
        InputStream istream = null;
        try {
            istream = s3Service.s3ToInputStream(imagePath, logger);
            if (istream != null) {
                BufferedImage image = null;
                try {
                    image = ImageIO.read( istream );
                    int imageHeight = image.getHeight();
                    int imageWidth = image.getWidth();
                    int imageMaxDim = Math.max(imageHeight, imageWidth);
                    if (maxDim <= imageMaxDim) {
                        imageOK = true;
                    } else {
                        logger.log("checkImageMaxDim: imageMaxDim = " + imageMaxDim +
                                   ", which should be <= " + maxDim);
                    }
                } catch (IOException e) {
                    logger.log("checkImageMaxDim: error reading image - " + e.getLocalizedMessage());
                }
                finally {
                    if (image != null) {
                        image.flush();
                    }
                }
            } else {
                logger.log("checkImageMaxDim: error creating InputStream");
            }
        }
        finally {
            if (istream != null) {
                try { istream.close(); } catch (IOException e) {}
            }
        }
        return imageOK;
    }
    
    
    public static boolean checkImageHeight( S3Service s3Service, String imagePath, int height, AbstractLoggerInterface logger ) {
        boolean imageOK = false;
        InputStream istream = null;
        try {
            istream = s3Service.s3ToInputStream(imagePath, logger);
            if (istream != null) {
                BufferedImage image = null;
                try {
                    image = ImageIO.read( istream );
                    int imageHeight = image.getHeight();
                    if (imageHeight == height) {
                        imageOK = true;
                    } else {
                        logger.log("checkImageHeight: imageHeight = " + imageHeight +
                                   ", which should be " + height);
                    }
                } catch (IOException e) {
                    logger.log("checkImageHeight: error reading image - " + e.getLocalizedMessage());
                }
                finally {
                    if (image != null) {
                        image.flush();
                    }
                }
            } else {
                logger.log("checkImageHeight: error creating InputStream");
            }
        }
        finally {
            if (istream != null) {
                try { istream.close(); } catch (IOException e) {}
            }
        }
        return imageOK;
    }
    
    /**
     * <p>
     * Initialize the following fields:
     * </p>
     * <ul>
     * <li> String mS3Path;  // path to the original unscaled image </li>
     * <li>String mS3ScaledPath; // path to the scale image</li>
     * <li>private String mS3Bucket;  // the S3 bucket (e.g., nderground-us-norcal, nderground-test)</li>
     * <li>String mId;   // AWS ID for S3 access</li>
     * <li>String mKey;  // AWS KEY for S3 access</li>
     * <li>private String contentType; // Image type</li>
     * </ul>
     * 
     * @param baseObj
     * @param scaledImagePath
     */
    public static void initializeImageInfoBase( ScaleImageInfoBase baseObj, String scaledImagePath ) {
        baseObj.setAwsRegionName(awsRegionName);
        baseObj.setS3Path( path );
        baseObj.setS3ScaledPath( scaledImagePath);
        baseObj.setS3Bucket( bucketName );
        baseObj.setId(S3_and_DynamoDB_ID);
        baseObj.setKey(S3_and_DynamoDB_KEY);
        baseObj.setContentType( S3ContentType.JPEG.getType() );
    }

    @Test
    public void testScaleImageByHeightWidth() {
        // divide by two
        final int newWidth = originalWidth >> 1;
        final int newHeight = originalHeight >> 1;
        final String scaledPath = "image_scale_test_width_height.jpg";
        AbstractLoggerInterface logger = new JavaLoggerWrapper( "testScaleImageByHeightWidth" );
        ScaleImageByHeightWidth imageInfo = new ScaleImageByHeightWidth();
        initializeImageInfoBase( imageInfo, scaledPath);
        imageInfo.setHeight( newHeight );
        imageInfo.setWidth(newWidth);
        LambdaImageScaler.scaleImage(imageInfo, logger);
        if (checkImageHeightWidth(s3Service, scaledPath, newHeight, newWidth, logger )) {
            s3Service.deleteFile(scaledPath);
            logger.log("testScaleImageByHeightWidth: test passed");
        } else {
            fail("testScaleImageByHeightWidth: result image either not present or not correct dimensions");
        }
    }

    @Test
    public void testScaleImageByMaxDim() {
        int maxDim = Math.max(originalWidth, originalHeight);
        int newMaxDim = maxDim >> 2;  // divide by 4
        final String scaledPath = "image_scale_test_max_dim.jpg";
        AbstractLoggerInterface logger = new JavaLoggerWrapper("testScaleImageByMaxDim");
        ScaleImageByMaxDim imageInfo = new ScaleImageByMaxDim();
        initializeImageInfoBase(imageInfo, scaledPath );
        imageInfo.setMaxDim(newMaxDim);
        LambdaImageScaler.scaleImage(imageInfo, logger);
        if (checkImageMaxDim(s3Service, scaledPath, newMaxDim, logger)) {
            s3Service.deleteFile(scaledPath);
            logger.log("testScaleImageByMaxDim: test passed");
        } else {
            fail("testScaleImageByMaxDim: result image either not present or not correct dimensions");
        }
    }

    @Test
    public void testScaleImageByHeight() {
        int newHeight = originalHeight >> 1; // divide by two
        final String scaledPath = "image_scale_test_by_height.jpg";
        AbstractLoggerInterface logger = new JavaLoggerWrapper("testScaleImageByHeight");
        ScaleImageByHeight imageInfo = new ScaleImageByHeight();
        initializeImageInfoBase(imageInfo, scaledPath );
        imageInfo.setHeight(newHeight);
        LambdaImageScaler.scaleImage(imageInfo, logger);
        if (checkImageHeight(s3Service, scaledPath, newHeight, logger)) {
            s3Service.deleteFile(scaledPath);
            logger.log("testScaleImageByHeight: test passed");
        } else {
            fail("testScaleImageByHeight: result image either not present or not correct dimensions");
        }
    }

}
