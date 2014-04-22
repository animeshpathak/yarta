package fr.inria.arles.yarta;

import java.io.File;
import java.io.PrintStream;
import java.util.Stack;

public class Profiler {

	public static class Info {

		public Info(String methodName, long startTime) {
			this.methodName = methodName;
			this.startTime = startTime;
		}

		String methodName;
		long startTime;
	}

	public Profiler(String folderPath) {
		this.folderPath = folderPath;
	}

	public void init(String name) {
		new File(folderPath + "/yarta").mkdirs();
		if (out == null) {
			try {
				out = new PrintStream(getProfileFilename(name));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void uninit() {
		if (out != null) {
			out.close();
			out = null;
		}
	}

	public void startMethod(String method) {
		callStack.push(new Info(method, System.currentTimeMillis()));
	}

	public void stopMethod() {
		Info info = callStack.pop();
		deltaTime = System.currentTimeMillis() - info.startTime;

		out.println(String.format("%s;%d", info.methodName, deltaTime));
	}

	private String getProfileFilename(String name) {
		return folderPath + "/yarta/" + name + ".csv";
	}

	private Stack<Profiler.Info> callStack = new Stack<Profiler.Info>();

	private long deltaTime;

	private PrintStream out;
	private String folderPath;
}
