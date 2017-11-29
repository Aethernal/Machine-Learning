package core;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class Article {

	private List<Spec> specs;

	public Article(JSONObject article) {
		String tmp;
		specs = new ArrayList<Spec>();
		for (String key : article.keySet()) {
			try {
				tmp = article.getString(key);
				
				if ( !key.equals("article_number") && !key.equals("category") && !key.equals("categorycode") && tmp != null && !tmp.equals("")) {
					this.specs.add(new Spec(key, tmp));
				}
				
			} catch (Exception e){
				
			}
		}
	}

	public List<Spec> getSpecs() {
		return specs;
	}

}
