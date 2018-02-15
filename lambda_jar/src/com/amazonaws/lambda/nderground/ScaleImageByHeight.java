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
 * <h3>
 * ScaleImageByHeight
 * </h3>
 * <p>
 * A container for the information needed by an AWS Lambda function to scale an image by height 
 * (adjusting the width by a similar ratio).
 * </p>
 * <p>
 * Jan 30, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class ScaleImageByHeight extends ScaleImageInfoBase {
    private int mHeight;
    
    public ScaleImageByHeight() {}
    
    public ScaleImageByHeight( String awsRegionName,
            String bucketName,
            String s3_ID,
            String s3_Key,
            String imagePath,
            String scaledImagePath,
            String contentType,
            int height) {
        super(awsRegionName,
              bucketName,
              s3_ID,
              s3_Key,
              imagePath,
              scaledImagePath,
              contentType);
        setHeight( height );
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }
    
}
