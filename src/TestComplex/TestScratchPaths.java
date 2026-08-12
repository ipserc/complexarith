package TestComplex;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Portable stand-in for the original hardcoded {@code /home/ipserc/saco/...} scratch
 * folder used by several TestComplex classes to save/read signal and filter data
 * between runs. Resolves to {@code <user.home>/ipserc/saco/...} on any OS.
 */
class TestScratchPaths {

	private TestScratchPaths() {}

	static String path(String... relativeParts) {
		Path basePath = Paths.get(System.getProperty("user.home"), "ipserc", "saco");
		Path full = basePath.resolve(Paths.get("", relativeParts));
		full.getParent().toFile().mkdirs();
		return full.toString();
	}
}
