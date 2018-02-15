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
 * <h4>
 * ScaleImageByMaxDim
 * </h4>
 * 
 * <p>
 * A container for the information needed to scale an image so that it's maximum dimension is MaxDim.
 * The other dimension will be scaled by a similar ratio.
 * </p>
 * <p>
 * Jan 30, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class ScaleImageByMaxDim extends ScaleImageInfoBase {
    private int mMaxDim;
    
    public ScaleImageByMaxDim() {}
    
    public ScaleImageByMaxDim( String awsRegionName,
                               String bucketName,
                               String s3_ID,
                               String s3_Key,
                               String imagePath,
                               String scaledImagePath,
                               String contentType,
                               int maxDim ) {
        super(awsRegionName,
              bucketName,
              s3_ID,
              s3_Key,
              imagePath,
              scaledImagePath,
              contentType);
        this.setMaxDim( maxDim );
    }

    public int getMaxDim() {
        return mMaxDim;
    }

    public void setMaxDim(int maxDim) {
        this.mMaxDim = maxDim;
    }
    
}
