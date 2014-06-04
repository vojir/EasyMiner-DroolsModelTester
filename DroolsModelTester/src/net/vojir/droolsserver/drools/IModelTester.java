package net.vojir.droolsserver.drools;

import java.io.IOException;
import java.io.InputStream;

public interface IModelTester {
	public void prepareFromXml(InputStream xmlInputStream) throws IOException, Exception;
	public void testAllRows(InputStream csvInputStream,String selectionMethod) throws IOException;
}
