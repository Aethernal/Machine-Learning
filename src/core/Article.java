package core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Article implements Serializable {

	private String description;
	private List<Spec> specs;

	public Article(JSONArray article) {
		JSONObject tmp;
		specs = new ArrayList<Spec>();
		for (int i = 0; i < article.length(); i++) {
			tmp = article.getJSONObject(i);
			for (String key : tmp.keySet()) {
				if (key != "description") {
					this.specs.add(new Spec(key, tmp.getString(key)));
				} else {
					this.description = tmp.getString(key);
				}
			}
		}
	}

	public String getDescription() {
		return description;
	}

	public List<Spec> getSpecs() {
		return specs;
	}

}
