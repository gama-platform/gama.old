package msi.gama.util.graph.loader;

import static org.junit.Assert.fail;
import java.util.*;
import msi.gama.TestUtils;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test graph loading: for each graph loader, ensures it raises
 * exceptions if the file does not exists.
 * 
 * @author Samuel Thiriot
 * 
 */
@RunWith(value = Parameterized.class)
public class TestLoading {

	@Parameters
	public static Collection data() {
		LinkedList params = new LinkedList();
		for ( String format : AvailableGraphParsers.getAvailableLoaders() ) {
			String[] p = new String[1];
			p[0] = format;
			params.add(p);
		}
		return params;
	}

	String format;

	public TestLoading(final String format) {
		this.format = format;
	}

	@Test
	public void testReadFileNotExisting() {

		System.out.println("file that does not exists: a gama runtime exception should be thrown");

		System.out.println("testing format: " + format);

		try {
			GraphLoader.loadGraph(null, "/I/beg/this/file/does/not/exists/hehe", null, null, null, null, format, false);
			fail("Gama runtime exception expected.");
		} catch (GamaRuntimeException e) {
			System.out.println(e.getMessage());
		} catch (RuntimeException e) {
			fail("Gama runtime exception expected.");
		} catch (Throwable e) {
			fail("Gama runtime exception expected.");
		}

	}

	@Test
	public void testReadFormatNotExisting() {

		System.out.println("format that does not exists: a gama runtime exception should be thrown");

		try {
			GraphLoader.loadGraph(null, TestUtils.getTmpFilename("emptyFile"), null, null, null, null,
				"formatThatDoesNotExists", false);
			fail("Gama runtime exception expected.");
		} catch (GamaRuntimeException e) {
			System.out.println(e.getMessage());
		} catch (RuntimeException e) {
			fail("Gama runtime exception expected.");
		} catch (Throwable e) {
			fail("Gama runtime exception expected.");
		}

	}

}
