/** \file
 * 
 * Jan 16, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package com.amazonaws.lambda.nderground;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * <h4>
 * LambdaImageScaler
 * </h4>
 * 
 * <p>
 * A wrapper class for Amazon Web Services Lambda imaging scaling functions. This class brings together the S3 code (from the S3Service object) 
 * and the image processing code (from the ImageService object).  
 * </p>
 * Jan 17, 2018
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class LambdaImageScaler {
    private final S3Service mS3Service;
    private final AbstractLoggerInterface mLogger;
    
    private LambdaImageScaler( ScaleImageInfoBase imageInfo, AbstractLoggerInterface logger ) {
        this.mS3Service = new S3Service(imageInfo.getAwsRegionName(), imageInfo.getS3Bucket(), imageInfo.getId(), imageInfo.getKey());
        this.mLogger = logger;
    }
    
    private S3Service getS3Service() {
        return this.mS3Service;
    }
    
    private AbstractLoggerInterface getLogger() {
        return this.mLogger;
    }
    
    private boolean writeImage( ScaleImageInfoBase imageInfo, BufferedImage image) {
        boolean writeOK = false;
        if (image != null) {
            String s3ScaledImagePath = imageInfo.getS3ScaledPath();
            String contentType = imageInfo.getContentType();
            writeOK = getS3Service().writeBufferedImage(s3ScaledImagePath, image, contentType, getLogger());
        }
        return writeOK;
    }
    
    private boolean scaleImageLocal( final ScaleImageByHeightWidth imageInfo) {
        boolean scaleOK = false;
        int height = imageInfo.getHeight();
        int width = imageInfo.getWidth();
        String s3ImagePath = imageInfo.getS3Path();
        if (height > 0 && width > 0) {
            InputStream imageInputStream = null;
            try {
                imageInputStream = getS3Service().s3ToInputStream(s3ImagePath, getLogger());
                if (imageInputStream != null) {
                    BufferedImage scaledImage = null;
                    try {
                        scaledImage = ImageService.scaleImage(imageInputStream, height, width, getLogger());
                        writeImage(imageInfo, scaledImage );
                        scaleOK = true;
                    }
                    finally {
                        if (scaledImage != null) {
                            scaledImage.flush();
                        }
                    }
                }
            }
            finally {
                if (imageInputStream != null) {
                    try { imageInputStream.close(); } catch(IOException e) {}
                }
            }
        } else {
            getLogger().log("LambdaImageScaler::scaleImage (height, width): bad argument");
        }
        return scaleOK;
    }
    
    private boolean scaleImageLocal( final ScaleImageByMaxDim imageInfo ) {
        boolean scaleOK = false;
        int maxDim = imageInfo.getMaxDim();
        String s3ImagePath = imageInfo.getS3Path();
        if (maxDim > 0) {
            InputStream imageInputStream = null;
            try {
                imageInputStream = getS3Service().s3ToInputStream(s3ImagePath, getLogger());
                if (imageInputStream != null) {
                    BufferedImage scaledImage = null;
                    try {
                        scaledImage = ImageService.scaleImage(imageInputStream, maxDim, getLogger());
                        writeImage(imageInfo, scaledImage );
                        scaleOK = true;
                    }
                    finally {
                        if (scaledImage != null) {
                            scaledImage.flush();
                        }
                    }
                }
            }
            finally {
                if (imageInputStream != null) {
                    try { imageInputStream.close(); } catch(IOException e) {}
                }
            }
        } else {
            getLogger().log("LambdaImageScaler::scaleImage (maxDim): bad argument");
        }
        return scaleOK;
    }
    
    private boolean scaleImageByHeightLocal( final ScaleImageByHeight imageInfo ) {
        boolean scaleOK = false;
        int maxHeight = imageInfo.getHeight();
        String s3ImagePath = imageInfo.getS3Path();
        if (maxHeight > 0) {
            InputStream imageInputStream = null;
            try {
                imageInputStream = getS3Service().s3ToInputStream(s3ImagePath, getLogger());
                if (imageInputStream != null) {
                    BufferedImage scaledImage = null;
                    try {
                        scaledImage = ImageService.scaleImageByHeight(imageInputStream, maxHeight, getLogger());
                        writeImage(imageInfo, scaledImage );
                        scaleOK = true;
                    }
                    finally {
                        if (scaledImage != null) {
                            scaledImage.flush();
                        }
                    }
                }
            }
            finally {
                if (imageInputStream != null) {
                    try { imageInputStream.close(); } catch(IOException e) {}
                }
            }
        } else {
            getLogger().log("LambdaImageScalar::scaleImageByHeight (height): bad argument");
        }
        return scaleOK;
    }
    
    
    private boolean thumbnailScaleLocal( ScaleImageByHeightWidth imageInfo ) {
        boolean scaleOK = false;
        int maxThumbHeight = imageInfo.getHeight();
        int maxThumbWidth = imageInfo.getWidth();
        String originalPath = imageInfo.getS3Path();
        String scaledPath = imageInfo.getS3ScaledPath();
        if (maxThumbHeight > 0 && maxThumbWidth > 0 && 
            originalPath != null && originalPath.length() > 0 &&
            scaledPath != null&& scaledPath.length() > 0) {
            InputStream imageInputStream = null;
            try {
                imageInputStream = getS3Service().s3ToInputStream(originalPath, getLogger());
                if (imageInputStream != null) {
                    BufferedImage scaledImage = null;
                    try {
                        scaledImage = ImageService.thumbnailScale(imageInputStream, maxThumbHeight, maxThumbWidth, getLogger());
                        writeImage(imageInfo, scaledImage );
                        scaleOK = true;
                    }
                    finally {
                        if (scaledImage != null) {
                            scaledImage.flush();
                        }
                    }
                }
            }
            finally {
                if (imageInputStream != null) {
                    try { imageInputStream.close(); } catch(IOException e) {}
                }
            }
        }
        return scaleOK;
    }
    
    /**
     * Create a thumbnail image. The maximum height and width are the height and width in the
     * ScaleImageByHeightWidth object.
     * 
     * @param imageInfo
     * @return true if the scale operation was OK, false otherwise
     */
    public static boolean thumbnailScale( final ScaleImageByHeightWidth imageInfo, AbstractLoggerInterface logger ) {
        LambdaImageScaler scaleObj = new LambdaImageScaler(imageInfo, logger);
        boolean scaleOK = scaleObj.thumbnailScaleLocal(imageInfo);
        return scaleOK;
    }
    
    public static boolean scaleImage( final ScaleImageByHeightWidth imageInfo, AbstractLoggerInterface logger ) {
        LambdaImageScaler scaleObj = new LambdaImageScaler( imageInfo, logger);
        boolean scaleOK = scaleObj.scaleImageLocal(imageInfo);
        return scaleOK;
    }
    
    public static boolean scaleImage( final ScaleImageByMaxDim imageInfo, AbstractLoggerInterface logger  ) {
        LambdaImageScaler scaleObj = new LambdaImageScaler( imageInfo, logger);
        boolean scaleOK = scaleObj.scaleImageLocal(imageInfo);
        return scaleOK;
    }
    
    public static boolean scaleImage( final ScaleImageByHeight imageInfo, AbstractLoggerInterface logger  ) {
        LambdaImageScaler scaleObj = new LambdaImageScaler( imageInfo, logger);
        boolean scaleOK = scaleObj.scaleImageByHeightLocal(imageInfo);
        return scaleOK;
    }
    

}
