package com.amazonaws.lambda.nderground;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class ScaleImageByHeightFunction implements RequestHandler<ScaleImageByHeight, Boolean> {
    public final static String mLambdaFunctionName = "ScaleImageByHeight";
    
    @Override
    public Boolean handleRequest(ScaleImageByHeight scaleInfo, Context context) {
        LambdaLogger logger = context.getLogger();
        AbstractLoggerInterface wrappedLogger = new LambdaLoggerWrapper( logger );
        Boolean scaleOK = LambdaImageScaler.scaleImage(scaleInfo, wrappedLogger);
        return scaleOK;
    }

}
