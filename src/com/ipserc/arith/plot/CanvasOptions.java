package com.ipserc.arith.plot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic passthrough bag of extra gnuplot configuration ({@code yrange}/{@code zrange}, legend
 * position, {@code pm3d}/palette, {@code set label}/{@code set arrow} annotations, output file or
 * terminal override...) for the higher plotting layers ({@code MatrixComplexPlot}, {@code
 * PolynomPlot}/{@code Polynom}) whose {@code SimpleGnuplot} instance is a local variable inside a
 * {@code static} method, unreachable from outside.
 * <p>
 * Deliberately NOT a pile of typed setters: {@link SimpleGnuplot#set(String, String)} and {@link
 * SimpleGnuplot#getPostInit()} are already a generic raw escape hatch straight to gnuplot -- ANY
 * {@code set <key> <value>} directive or raw command already works today, the only real gap was
 * that domain-layer callers had no way to reach it. This class just carries that same raw-passthrough
 * pattern one layer up, so it doesn't need to grow a new typed method for every gnuplot directive
 * (current or future) the way {@code SimpleGnuplot} itself deliberately avoids.
 */
public final class CanvasOptions {

	/** No extra configuration -- a no-op {@link #apply(SimpleGnuplot)}. Used as the implicit
	 * default by every {@code xxxSync}/{@code xxxAsync} overload that doesn't take a {@code
	 * CanvasOptions} parameter. */
	public static final CanvasOptions NONE = new CanvasOptions();

	private final Map<String, String> extraSettings = new LinkedHashMap<>();
	private final List<String> extraPostInit = new ArrayList<>();
	private String outputFile;
	private String terminal;

	/** Adds a raw {@code set <key> <value>} directive (e.g. {@code withSetting("yrange", "[-1:1]")},
	 * {@code withSetting("key", "top left")}, {@code withSetting("pm3d", "")}). */
	public CanvasOptions withSetting(String key, String value) {
		extraSettings.put(key, value);
		return this;
	}

	/** Adds a raw gnuplot command verbatim (e.g. {@code "set label 'foo' at 1,2"}, {@code "set
	 * palette rgbformulae 33,13,10"}, {@code "set arrow from 0,0 to 1,1"}). */
	public CanvasOptions withPostInit(String rawCommand) {
		extraPostInit.add(rawCommand);
		return this;
	}

	/** See {@link SimpleGnuplot#setOutputFile(String)}. */
	public CanvasOptions withOutputFile(String path) {
		this.outputFile = path;
		return this;
	}

	/** See {@link SimpleGnuplot#setTerminal(String)}. */
	public CanvasOptions withTerminal(String rawSpec) {
		this.terminal = rawSpec;
		return this;
	}

	/** Applies every option accumulated here onto {@code p}. Called by the domain-layer generic
	 * entry points right before {@code p.plot(mode)}. */
	public void apply(SimpleGnuplot p) {
		for (Map.Entry<String, String> e : extraSettings.entrySet()) p.set(e.getKey(), e.getValue());
		for (String cmd : extraPostInit) p.getPostInit().add(cmd);
		if (outputFile != null) p.setOutputFile(outputFile);
		if (terminal != null) p.setTerminal(terminal);
	}
}
