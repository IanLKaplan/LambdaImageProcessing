/** \file
 * 
 * Jan 10, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package com.amazonaws.lambda.nderground;

/**
 * To support testing, the AbstractLoggerInterface allows the classes that implement the interface 
 * to write either the java.util.Logger or the LambdaLogger. This allows the code to be run outside
 * of the Amazon Lambda environment for testing.
 * 
 * AbstractLoggerInterface
 * Jan 10, 2018
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public interface AbstractLoggerInterface {
    
    public void log(final String msg);

}
