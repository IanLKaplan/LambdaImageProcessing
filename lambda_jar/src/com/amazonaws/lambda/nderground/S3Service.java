/** \file
 * 
 * Jan 3, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package com.amazonaws.lambda.nderground;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.Base64;

/**
 * <h3>
 * S3Service
 * </h3>
 * <p>
 * Read and write image files to AWS S3. This code supports AWS Lambda functions for image scaling.
 * </p>
 * 
 * <p>
 * Jan 3, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class S3Service {
    private final String mAWS_ID;
    private final String mAWS_KEY;
    private final String mS3Bucket;
    private final String mAWSRegionName;
    private AmazonS3 s3Client = null;
    
    public S3Service(String awsRegionName, String s3Bucket, String AWS_ID, String AWS_KEY) {
        this.mAWSRegionName = awsRegionName;
        this.mS3Bucket = s3Bucket;
        this.mAWS_ID = AWS_ID;
        this.mAWS_KEY = AWS_KEY;
    }
    
    private String getS3Bucket() { return this.mS3Bucket; }
    private String getAWS_ID() { return this.mAWS_ID; }
    private String getAWS_KEY() { return this.mAWS_KEY; }
    private String getRegionName() { return this.mAWSRegionName; }

    /**
     * 
     * Build an AmazonS3 client. This method uses the new Amazon interface, which, interestingly, requires a region
     * name.
     * 
     * @return An Amazon S3 client
     */
    protected AmazonS3 getS3Client(String AWS_ID, String AWS_KEY) {
        BasicAWSCredentials credentials = new BasicAWSCredentials( AWS_ID, AWS_KEY );
        AWSStaticCredentialsProvider credentialProvider = new AWSStaticCredentialsProvider( credentials );
        ClientConfiguration config = new ClientConfiguration();
        config.setProtocol(Protocol.HTTP);
        AmazonS3ClientBuilder clientBuilder = AmazonS3ClientBuilder.standard();
        clientBuilder.setCredentials(credentialProvider);
        clientBuilder.setClientConfiguration(config);
        clientBuilder.setRegion(getRegionName());
        AmazonS3 s3client = clientBuilder.build();
        return s3client;
    }
    
    
    protected AmazonS3 getS3Client() {
        if (s3Client == null) {
            s3Client = getS3Client( getAWS_ID(), getAWS_KEY() );
        }
        return s3Client;
    }
    
    
    /**
     * Delete an S3 object.
     * 
     * @param path the path and file name for the object that will be removed
     * @throws AmazonClientException this exception will be thrown if something goes wrong with
     *         the delete.
     */
    public void deleteFile(String path) throws AmazonClientException {
        getS3Client().deleteObject(getS3Bucket(), path);
    }

    
    /**
     * <h3>
     * s3ToInputStream
     * </h3>
     * 
     * <p>
     * Given and S3 path to a file, return an InputStream or null if there's an error.
     * </p>
     * 
     * @param s3Bucket the S3 bucket that contains the S3 path
     * @param path the S3 path to the image
     * @param credentials AWS credentials
     * @param logger for logging errors
     * @param logger a logger for errors
     * @return an InputStream object, or null if the reference failed.
     */
    public InputStream s3ToInputStream(String s3Path, 
                                       AbstractLoggerInterface logger) {
        InputStream istream = null;
        try {
            S3Object s3Obj = getS3Client().getObject( getS3Bucket(), s3Path );
            istream = s3Obj.getObjectContent();
        } catch (SdkClientException e) {
            String msg = "Error reading S3 path " + s3Path + ": " + e.getLocalizedMessage();
            logger.log( msg );
        }
        return istream;
    }
    
    
    /**
     * Write an InputStream to an S3 bucket.
     * 
     * @param s3Key the "path" and file name for the object (e.g., /foo/bar/mySelfie.jpg)
     * @param istream an InputStream for the object to be written to S3
     * @param numBytes the size of the object, in bytes
     * @param contentType the HTML content type.
     * @return true if the hash of the S3 object is the same as the hash calculated from the input stream. False if there
     *         was a write failure or if the hash does not match.
     * @throws AmazonClientException 
     */
    private boolean writeStream(String s3Key, 
                                InputStream istream, 
                                long numBytes, 
                                AbstractLoggerInterface logger) throws AmazonClientException {
        boolean hashOK = false;
        // Calculate the MD5 hash as the data is written
        try {
        MessageDigest md = MessageDigest.getInstance("MD5");
        DigestInputStream distream = new DigestInputStream(istream, md);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength( numBytes );
        PutObjectRequest putRequest = new PutObjectRequest( getS3Bucket(), s3Key, distream, metadata );
        PutObjectResult rslt = getS3Client().putObject( putRequest );
        String md5Hash = rslt.getContentMd5();
        byte[] s3Digest = md.digest();
        String s3MD5 = Base64.encodeAsString( s3Digest );
        hashOK = md5Hash.equals(s3MD5);
        if (! hashOK) {
            logger.log("ImageDisplayController::writeStream: error writing to S3 storage for " + s3Key);
        }
        }
        catch (NoSuchAlgorithmException e) {
            
        }
        return hashOK;
    }
    
    
    private String getImageType(String contentType) {
        final String prefix = "image/";
        String imageType = "jpeg";
        if (contentType != null) {
            int ix = contentType.indexOf(prefix);
            if (ix > 0) {
                int end = ix + prefix.length();
                if (end < contentType.length()) {
                    imageType = contentType.substring(end);
                }
            }
        }
        return imageType;
    }
    
    
    /**
     * Write a BufferedImage object to Amazon S3 storage
     *
     <h3>
     A note on the code
     </h3>
     <p>
     The hack to ByteArrayOutputStream was suggested in a StackOverflow post: http://stackoverflow.com/a/12253050/2341077
     The ByteArrayOutputStream toByteArray() method makes a copy of of the data.  By overriding the method, the 
     copy is eliminated. 
     </p>
     <p>
     The constructor for ByteArrayInputStream(byte[] buf, int offset, int length) does not copy:
     </p>
     <blockquote>
     Creates ByteArrayInputStream that uses buf as its buffer array. The initial value of pos is offset and the 
     initial value of count is the minimum of offset+length and buf.length. The buffer array is not copied. 
     The buffer's mark is set to the specified offset.
     </blockquote>
     <p>
     The result is that the only data movement takes place in the ImageIO.write operation that writes to the
     ByteArrayOutputStream.
     </p>
     * 
     * @param s3Key the path where the image should be stored
     * @param image the image, in a BufferedImage object
     * @param numBytes number of bytes for the file
     * @param contentType - the MIME content type
     * @return true if the hash from S3 matches the hash of the file.
     */
    public boolean writeBufferedImage(String s3Key, 
                                      BufferedImage image, 
                                      String contentType,
                                      AbstractLoggerInterface logger) {
        boolean imageOK = false;
        if (image != null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream() {
                        @Override
                        public synchronized byte[] toByteArray() {
                            return this.buf;
                        }
                    }; // overridden class
            String imageType = getImageType( contentType );
            try {
                ImageIO.write(image, imageType, outStream);
                int imageSize = outStream.size();
                InputStream inStream = new ByteArrayInputStream(outStream.toByteArray(), 0, imageSize);
                imageOK = writeStream(s3Key, inStream, imageSize, logger);
            }
            catch(IllegalArgumentException e) {
                logger.log( "Bad argument to ImageIO.write: " + e.getLocalizedMessage() );
            }
            catch(IOException e) {
                logger.log( "Error writing to S3. Path = " + s3Key );
            }
        } else {
            logger.log( "Image is null" );
        }
        return imageOK;
    }
    

}
