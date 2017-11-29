import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import core.MatchEngine;

public class Launcher {
	private MatchEngine engine;

	public static void main(int argc, String[] args) {
		MatchEngine engine;
		File instance = new File("MatchEngine.ser");
		if (instance.exists()) {
			try {
				ObjectInputStream stream = new ObjectInputStream(new FileInputStream(instance));
				engine = (MatchEngine) stream.readObject();
			} catch (IOException | ClassNotFoundException e) {
				System.err.println("Could not load MatchEngine instance");
			}
		} else {
			engine = new MatchEngine();
		}
		if (argc > 1) {
			switch (args[1]) {
			case "train":
				train(args[2]);
				break;
			case "test":
				test(args[2]);
				break;
			default:
				System.err.println("Invalid action ! should be test or train !");
				break;
			}
		} else {
			if (args[1].equals("help")) {
				help();
			} else {
				System.err.println("Missing arguments ! < test | train > <file>");
			}
		}
	}

	private static File checkFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			return file;
		}
		System.err.println("File: " + path + ", doesn't exist !");
		return null;
	}

	private static void train(String path) {
		File file;
		if ((file = checkFile(path)) != null) {
			//TODO
		}
	}

	private static void test(String path) {
		File file;
		if ((file = checkFile(path)) != null) {
			//TODO
		}
	}

	private static void help() {
		System.out.println("-- Article Category Matcher --");
		System.out.println("Action: ");
		System.out.println("help: show this help menu");
		System.out.println("test <file>: output a json with the category for the json file given as argument");
		System.out.println("\t the file need to be an array of JSON object");
		System.out.println("train <file>: add the file values to the program to train it to get good values");
		System.out.println("\t the file need to be an array of JSONObject containing a category attribute and a JSONObject containing attribute specs");
	}

}
