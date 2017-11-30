package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class MatchEngine {
	private List<Tag> tags;

	public MatchEngine() {
		tags = new ArrayList<Tag>();
		loadData();
	}

	public void loadData() {
		File file = new File("match-engine.json");
		if (file.exists() && file.isFile() && file.canRead()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String json = "";
				String tmp;
				while((tmp = reader.readLine()) != null){
					json += tmp;
				}
				JSONArray array = new JSONArray(json);
				JSONObject obj;
				Tag t;
				for(int i = 0; i < array.length(); i++){
					obj = array.getJSONObject(i);
					t = Tag.parseJSON(obj);
					tags.add(t);
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveData(){
		File file = new File("match-engine.json");
		if((!file.exists()) || (file.isFile() && file.canWrite())){
			JSONArray array = new JSONArray();
			for(Tag t : tags){
				array.put(t.toJSON());
				
			}
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				writer.write(array.toString());
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Tag match(Article article) {
		Tag tag = null;
		double highest = -1, tmp;

		for (Tag t : tags) {
			tmp = t.getConsistency(article);
			if (tmp > highest) {
				tag = t;
				highest = tmp;
			}
		}

		return tag;
	}

	public void addTag(Tag tag) {
		tags.add(tag);
	}

	public void addDataSet(Article article, String tag) {
		Tag t;
		if( (t = getTag(tag)) == null){
			tags.add(new Tag(tag));
			t = getTag(tag);
		}
		for (Spec s : article.getSpecs()) {
			t.addSpec(s);
		}
	}

	public Tag getTag(String tag) {

		for (Tag t : tags) {
			if (t.getName().equals(tag)) {
				return t;
			}
		}
		return null;
	}

}
