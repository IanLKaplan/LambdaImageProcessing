package com.amazonaws.lambda.nderground;

/**
 * Information needed to scale an image. This includes the AWS S3 bucket, the path to the image and
 * the access ID and secret key.
 * 
 * @author Ian Kaplan
 * @date December 28, 2017
 *
 */
public abstract class ScaleImageInfoBase {
    private String mAWSRegionName;
    private String mS3Bucket;  // the S3 bucket (e.g., us-west-1, etc...)
    private String mId;   // AWS ID for S3 access
    private String mKey;  // AWS KEY for S3 access
	private String mS3Path;  // path to the original unscaled image
	private String mS3ScaledPath; // path to the scale image
    private String contentType; // Image type
    
    public ScaleImageInfoBase() {}
    
    public ScaleImageInfoBase( String awsRegionName,
                               String bucketName,
                               String s3_ID,
                               String s3_Key,
                               String imagePath,
                               String scaledImagePath,
                               String contentType ) {
        this.setAwsRegionName( awsRegionName );
        this.setS3Bucket( bucketName );
        this.setId( s3_ID);
        this.setKey(s3_Key);
        this.setS3Path(imagePath);
        this.setS3ScaledPath( scaledImagePath );
        this.setContentType(contentType);
    }
    
	public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

	public String getS3Path() {
		return mS3Path;
	}

	public void setS3Path(String s3Path) {
		this.mS3Path = s3Path;
	}

    public String getS3ScaledPath() {
        return mS3ScaledPath;
    }

    public void setS3ScaledPath(String s3ScaledPath) {
        this.mS3ScaledPath = s3ScaledPath;
    }

    public String getS3Bucket() {
        return mS3Bucket;
    }

    public void setS3Bucket(String s3Bucket) {
        this.mS3Bucket = s3Bucket;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getAwsRegionName() {
        return mAWSRegionName;
    }

    public void setAwsRegionName(String awsRegionName) {
        this.mAWSRegionName = awsRegionName;
    }
    
}
