package com.amazonaws.lambda.nderground;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * <h3>
 * LambdaFunctionHandler
 * </h3>
 * 
 * <p>
 * An AWS Lambda function to scale an image.
 * </p>
 * <p>
 * Image scaling is memory intensive, especially when running on a web server which supports
 * many threads. By moving image scaling to an AWS Lambda function, memory usage is localized
 * and the image scaling operation becomes scalable without impacting the web server.
 * </p>  
 * <p>
 * The ScaleImageInfo data structure provides the
 * target width and height for the scaled image, along with the AWS S3 path for
 * the unscaled original image.
 * </p>
 * <p>
 * The process followed here is:
 * </p>
 * <ol>
 * <li>Read image from S3</li>
 * <li>Scale image</li>
 * <li>Write the image to the output image path on S3</li>
 * </ol>
 * 
 * Jan 3, 2018
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class ScaleImageByMaxDimFunction implements RequestHandler<ScaleImageByMaxDim, Boolean> {
    public final static String mLambdaFunctionName = "ScaleImageByMaxDim";

    @Override
    public Boolean handleRequest(ScaleImageByMaxDim scaleInfo, Context context) {
        LambdaLogger logger = context.getLogger();
        LambdaLoggerWrapper wrappedLogger = new LambdaLoggerWrapper( logger );
        Boolean scaleOK = LambdaImageScaler.scaleImage(scaleInfo, wrappedLogger);
        return scaleOK;
    }

}
