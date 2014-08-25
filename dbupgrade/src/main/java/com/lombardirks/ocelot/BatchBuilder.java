package com.lombardirks.ocelot;

import java.io.InputStream;
import java.util.Map;

import com.lombardirks.ocelot.model.Batch;

public interface BatchBuilder {

	Map<String, Batch> build(InputStream in) throws Exception;

}
