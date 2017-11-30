import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONObject;

import core.Article;
import core.MatchEngine;
import core.Spec;
import core.Tag;

public class Launcher {
	private static MatchEngine engine;

	public static void main(String args[]) {
		engine = new MatchEngine();
		
		if (args.length > 1) {
			switch (args[0]) {
			case "train":
				train(args[1]);
				break;
			case "test":
				test(args[1]);
				break;
			default:
				System.err.println("Invalid action ! should be test or train !");
				break;
			}
		} else {
			
			if (args.length == 1 && args[0].equals("help")) {
				help();
			} else {
				System.err.println("Missing arguments ! < test | train > <file>");
			}
		}
		
		engine.saveData();
	}

	private static File checkFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			return file;
		}
		System.err.println("File: " + path + ", doesn't exist !");
		return null;
	}

	private static JSONArray readJSON(File file){
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String json = "";
			String tmp;
			while((tmp = reader.readLine()) != null){
				json += tmp;
			}
			reader.close();
			JSONArray data = new JSONObject(json).getJSONArray("data");
			
			return data;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	private static void train(String path) {
		File file;
		if ((file = checkFile(path)) != null) {
			JSONArray data = readJSON(file);
			JSONObject article_json;
			for(int i = 0; i < data.length(); i++){
				if(i%100==99)
				System.out.println("learning from item n° " + (i + 1) +" of " + data.length());
				article_json = data.getJSONObject(i);
				Article article = new Article(article_json);
				engine.addDataSet(article, article_json.getString("category"));
			}
		}
	}

	private static void test(String path) {
		File file;
		if ((file = checkFile(path)) != null) {
			JSONArray data = readJSON(file);
			JSONObject article_json;
			double correct;
			do {
				correct = 0.0;
				for(int i = 0; i < data.length(); i++){
					article_json = data.getJSONObject(i);
					Article article = new Article(article_json);
					Tag best = engine.match(article);
					if(best.getName().equals(article_json.getString("category"))){
						correct++;
					} else {
						//show error
						Tag correct_tag = engine.getTag(article_json.getString("category"));
//						System.out.println("best tag found = " + best.getName() + " " +best.getConsistency(article)+ " correct was " + article_json.getString("category") + " " + correct_tag.getConsistency(article) +"" );
						
//						showTagConsistency(best, article);
//						showTagConsistency(correct_tag, article);
						
						correct_tag.updateTag(article);
						best.dropArticle(article);
						
					}
					
					if(i%5000==4999)
						System.out.println("matched item n° " + (i + 1) +" of " + data.length());
					
					
				}
				System.out.println("Number of correct match " + correct + " of " + data.length() +" | " + (correct / data.length() * 100.0) + "%");
			} while ((correct / data.length() * 100.0) < 90);
		}
	}

	private static void showTagConsistency(Tag t, Article article){
		DecimalFormat format = new DecimalFormat("#0.00");
		System.out.println(t.getName());
		for(Spec s : article.getSpecs()){
			if(t.getCriteria(s.getName()) != null){
				System.out.println("\t"+s.getName() + " : " + format.format(t.getCriteria(s.getName()).getWeight() / t.getWeightSum() * 100) +"%");
				System.out.println("\t\t"+s.getValue() + " : " + format.format(t.getCriteria(s.getName()).getValueConsistency(s.getValue())*100) +"%");
			}
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
