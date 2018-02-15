/** \file
 * 
 * Jan 10, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package com.amazonaws.lambda.nderground;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaLoggerWrapper implements AbstractLoggerInterface {
    private final Logger mLogger; 
    
    public JavaLoggerWrapper(String loggerName ) {
        mLogger = Logger.getLogger( loggerName );
    }

    @Override
    public void log(String msg) {
        mLogger.log(Level.SEVERE, msg );
    }

}
