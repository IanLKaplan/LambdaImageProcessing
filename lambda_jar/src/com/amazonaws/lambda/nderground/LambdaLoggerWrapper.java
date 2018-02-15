/** \file
 * 
 * Jan 10, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package com.amazonaws.lambda.nderground;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class LambdaLoggerWrapper implements AbstractLoggerInterface {
    
    private final LambdaLogger mLambdaLogger;
    
    public LambdaLoggerWrapper(LambdaLogger lambdaLogger) {
        this.mLambdaLogger = lambdaLogger;
    }

    @Override
    public void log(String msg) {
        mLambdaLogger.log(msg);
    }

}
