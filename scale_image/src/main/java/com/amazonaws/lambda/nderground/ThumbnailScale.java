package com.amazonaws.lambda.nderground;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class ThumbnailScale implements RequestHandler<ScaleImageByHeightWidth, Boolean> {
    public final static String mLambdaFunctionName = "ThumbnailScale";
    
    @Override
    public Boolean handleRequest(ScaleImageByHeightWidth scaleInfo, Context context) {
        LambdaLogger logger = context.getLogger();
        AbstractLoggerInterface wrappedLogger = new LambdaLoggerWrapper( logger );
        Boolean scaleOK = LambdaImageScaler.thumbnailScale(scaleInfo, wrappedLogger);
        return scaleOK;
    }

}
