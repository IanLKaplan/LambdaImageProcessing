package com.amazonaws.lambda.nderground;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.protocol.json.SdkJsonGenerator.JsonGenerationException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.s3.model.Region;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class LambdaFunctionHandlerTest {
    private final static String aws_access_key_id = "Your AWS ID goes here";
    private final static String aws_secret_access_key = "Your AWS secret Key goes here";
    private static String className = "LambdaFunctionHandlerTest";
    private static Logger logger = Logger.getLogger(className);
    private static JavaLoggerWrapper wrappedLogger = new JavaLoggerWrapper( className );
    

    /**
     * See http://www.baeldung.com/jackson-inheritance
     * 
     * @param obj
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */    
    private static String objectToJSON( Object obj) {
        String json = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(obj);
        } catch (JsonGenerationException | IOException e) {
            logger.severe("Object to JSON failed: " + e.getLocalizedMessage());
        }
        return json;
    }

    private ScaleImageByMaxDim createMaxDimInput(String scaledImagePath) {
        ScaleImageByMaxDim scaleInfo = new ScaleImageByMaxDim();
        LambdaImageScalerTest.initializeImageInfoBase(scaleInfo, scaledImagePath);
        int maxOriginalDim = Math.max(LambdaImageScalerTest.originalHeight, LambdaImageScalerTest.originalWidth);
        int newMaxDim = maxOriginalDim >> 2;
        scaleInfo.setMaxDim( newMaxDim );
        return scaleInfo;
    }
    
    
    private ScaleImageByHeight createHeightInput(String scaledImagePath) {
        ScaleImageByHeight scaleInfo = new ScaleImageByHeight();
        LambdaImageScalerTest.initializeImageInfoBase(scaleInfo, scaledImagePath );
        int newHeight = LambdaImageScalerTest.originalHeight >> 1;
        scaleInfo.setHeight(newHeight);
        return scaleInfo;
    }
    
    private ScaleImageByHeightWidth createHeightWidthInput(String scaledImagePath) {
        ScaleImageByHeightWidth scaleInfo = new ScaleImageByHeightWidth();
        LambdaImageScalerTest.initializeImageInfoBase(scaleInfo, scaledImagePath );
        int newHeight = LambdaImageScalerTest.originalHeight >> 1;
        int newWidth = LambdaImageScalerTest.originalWidth >> 1;
        scaleInfo.setHeight( newHeight );
        scaleInfo.setWidth(newWidth);
        return scaleInfo;
    }
    
    private ScaleImageByHeightWidth createThumbnailInput(String scaledImagePath) {
        final int THUMBNAIL_HEIGHT = 140;
        final int THUMBNAIL_WIDTH = 190;
        ScaleImageByHeightWidth scaleInfo = new ScaleImageByHeightWidth();
        LambdaImageScalerTest.initializeImageInfoBase(scaleInfo, scaledImagePath );
        scaleInfo.setHeight(THUMBNAIL_HEIGHT);
        scaleInfo.setWidth(THUMBNAIL_WIDTH);
        return scaleInfo;
    }
    
    
    private AWSLambda buildClient() {
        // replace the US-West reference for your Region if it is different
        Regions region = Regions.fromName(Region.US_West.toString());
        BasicAWSCredentials credentials = new BasicAWSCredentials(aws_access_key_id, aws_secret_access_key);
        AWSLambdaClientBuilder builder = AWSLambdaClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region);
        AWSLambda client = builder.build();
        return client;
    }

    
    @Test
    public void testScaleImageByHeightWidthFunction() {
        final String scaledImagePath = "image_scale_test_by_height_width.jpg";
        AWSLambda client = buildClient();
        ScaleImageByHeightWidth scaleInfo = createHeightWidthInput( scaledImagePath );
        String scaleInfoStr = objectToJSON( scaleInfo );
        InvokeRequest req = new InvokeRequest().withFunctionName(ScaleImageByHeightWidthFunction.mLambdaFunctionName).withPayload( scaleInfoStr );
        InvokeResult requestResult = client.invoke(req);
        ByteBuffer byteBuf = requestResult.getPayload();
        if (byteBuf != null) {
            String result = StandardCharsets.UTF_8.decode(byteBuf).toString();
            logger.info("testScaleImageByHeightWidthFunction::Lambda result: " + result);
            if (LambdaImageScalerTest.checkImageHeightWidth(LambdaImageScalerTest.s3Service, scaledImagePath, scaleInfo.getHeight(), scaleInfo.getWidth(), wrappedLogger)) {
                LambdaImageScalerTest.s3Service.deleteFile( scaledImagePath );
                logger.info("testScaleImageByHeightWidth: test passed");
            } else {
                fail("testScaleImageByHeightWidth: result image either not present or not correct dimensions");
            }
        } else {
            logger.severe("testScaleImageByHeightWidth::handleRequest: result payload is null");
        }
    }
    
    
    @Test
    public void testThumbnailScale() {
        final String thumbnailPath = "image_test_thumbnail.jpg";
        AWSLambda client = buildClient();
        ScaleImageByHeightWidth scaleInfo = createThumbnailInput( thumbnailPath );
        String scaleInfoStr = objectToJSON( scaleInfo );
        InvokeRequest req = new InvokeRequest().withFunctionName(ThumbnailScale.mLambdaFunctionName).withPayload( scaleInfoStr );
        InvokeResult requestResult = client.invoke(req);
        ByteBuffer byteBuf = requestResult.getPayload();
        if (byteBuf != null) {
            String result = StandardCharsets.UTF_8.decode(byteBuf).toString();
            logger.info("testThumbnailScale::Lambda result: " + result);
            if (LambdaImageScalerTest.checkImageThumbnail(LambdaImageScalerTest.s3Service, scaleInfo.getS3Path(), thumbnailPath, scaleInfo.getHeight(), scaleInfo.getWidth(), wrappedLogger)) {
                LambdaImageScalerTest.s3Service.deleteFile( thumbnailPath );
                logger.info("testThumbnailScale: test passed");
            } else {
                fail("testThumbnailScale: result image either not present or not correct dimensions");
            }
        } else {
            logger.severe("testThumbnailScale::handleRequest: result payload is null");
        }
    }
    

    @Test
    public void testScaleImageByHeightFunction() {
        final String scaledImagePath = "image_scale_test_by_height.jpg";
        AWSLambda client = buildClient();
        ScaleImageByHeight scaleInfo = createHeightInput( scaledImagePath );
        String scaleInfoStr = objectToJSON( scaleInfo );
        InvokeRequest req = new InvokeRequest().withFunctionName(ScaleImageByHeightFunction.mLambdaFunctionName).withPayload( scaleInfoStr );
        InvokeResult requestResult = client.invoke(req);
        ByteBuffer byteBuf = requestResult.getPayload();
        if (byteBuf != null) {
            String result = StandardCharsets.UTF_8.decode(byteBuf).toString();
            logger.info("testScaleImageByHeightFunction::Lambda result: " + result);
            if (LambdaImageScalerTest.checkImageHeight(LambdaImageScalerTest.s3Service, scaledImagePath, scaleInfo.getHeight(), wrappedLogger)) {
                LambdaImageScalerTest.s3Service.deleteFile( scaledImagePath );
                logger.info("testScaleImageByHeight: test passed");
            } else {
                fail("testScaleImageByHeight: result image either not present or not correct dimensions");
            }
        } else {
            logger.severe("testScaleImageByHeight::handleRequest: result payload is null");
        }
    }

    @Test
    public void testScaleImageByMaxDimFunction() {
        final String scaledImagePath = "image_scale_test_max_dim.jpg";
        AWSLambda client = buildClient();
        ScaleImageByMaxDim scaleInfo = createMaxDimInput( scaledImagePath );
        String scaleInfoStr = objectToJSON( scaleInfo );
        InvokeRequest req = new InvokeRequest().withFunctionName(ScaleImageByMaxDimFunction.mLambdaFunctionName).withPayload( scaleInfoStr );
        InvokeResult requestResult = client.invoke(req);
        ByteBuffer byteBuf = requestResult.getPayload();
        if (byteBuf != null) {
            String result = StandardCharsets.UTF_8.decode(byteBuf).toString();
            logger.info("testScaleImageByMaxDimFunction::Lambda result: " + result);
            if (LambdaImageScalerTest.checkImageMaxDim(LambdaImageScalerTest.s3Service, scaledImagePath, scaleInfo.getMaxDim(), wrappedLogger)) {
                LambdaImageScalerTest.s3Service.deleteFile( scaledImagePath );
                logger.info("testScaleImageByMaxDim: test passed");
            } else {
                fail("testScaleImageByMaxDim: result image either not present or not correct dimensions");
            }
        } else {
            logger.severe("testScaleImageBuyMaxDim::handleRequest: result payload is null");
        }
    }
}
