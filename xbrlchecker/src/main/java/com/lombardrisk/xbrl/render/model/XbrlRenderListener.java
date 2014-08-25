package com.lombardrisk.xbrl.render.model;

/**
 * Created by Cesar on 17/06/2014.
 */
public interface XbrlRenderListener {

    void validatingInstance();
    void instanceValidated();

    void readingInstance();
    void instanceRead();

    void processingDataPoints(int processed, int total);
    void processedAllDataPoints();

    void rendering();
    void finishedRendering();
}
