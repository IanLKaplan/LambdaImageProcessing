/** \file
 * 
 * Jan 16, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package com.amazonaws.lambda.nderground;

/**
 * 
 * ScaleImageByHeightWidth
 * Jan 30, 2018
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class ScaleImageByHeightWidth extends ScaleImageInfoBase {
    private int mHeight;
    private int mWidth;
    
    public ScaleImageByHeightWidth() {}
    
    public ScaleImageByHeightWidth( String awsRegionName,
                                    String bucketName,
                                    String s3_ID,
                                    String s3_Key,
                                    String imagePath,
                                    String scaledImagePath,
                                    String contentType,
                                    int height,
                                    int width) {
        super(awsRegionName,
              bucketName,
              s3_ID,
              s3_Key,
              imagePath,
              scaledImagePath,
              contentType);
        setHeight( height );
        setWidth( width );
    }
    
    public int getHeight() {
        return mHeight;
    }
    public void setHeight(int height) {
        this.mHeight = height;
    }
    public int getWidth() {
        return mWidth;
    }
    public void setWidth(int width) {
        this.mWidth = width;
    }
    
}
