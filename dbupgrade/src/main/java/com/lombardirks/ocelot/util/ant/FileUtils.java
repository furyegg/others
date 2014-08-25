package com.lombardirks.ocelot.util.ant;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * These code fragments picked-up from org.apache.tools.ant.util.FileUtils.
 * 
 * @author Kyle Li
 */
public class FileUtils {

	/**
	 * Close a stream without throwing any exception if something went wrong.
	 * Do not attempt to close it if the argument is null.
	 * 
	 * @param device stream, can be null.
	 */
	public static void close(InputStream device) {
		if (null != device) {
			try {
				device.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
	
	/**
     * Close a Reader without throwing any exception if something went wrong.
     * Do not attempt to close it if the argument is null.
     *
     * @param device Reader, can be null.
     */
    public static void close(Reader device) {
        if (null != device) {
            try {
                device.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }

}
