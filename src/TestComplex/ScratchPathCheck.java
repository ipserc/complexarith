package TestComplex;

public class ScratchPathCheck {
	public static void main(String[] args) {
		System.out.println(TestScratchPaths.path("signal_samples.txt"));
		System.out.println(TestScratchPaths.path("fourier_20201023_2013", "signal_samples.txt"));
		java.io.File f = new java.io.File(TestScratchPaths.path("fourier_20201023_2013", "signal_samples.txt"));
		System.out.println("Parent exists: " + f.getParentFile().exists());
	}
}
