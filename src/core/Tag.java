package core;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Tag {

	private String name;
	private List<Criteria> crits;

	public Tag(String name) {
		this.name = name;
		this.crits = new ArrayList<Criteria>();
	}

	public Criteria getCriteria(String name) {
		for (Criteria c : crits) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}

	public double getWeightSum() {
		double sum = 0;
		for (Criteria c : crits) {
			sum += c.getWeight();
		}
		return sum;
	}

	public void addCriteria(Criteria criteria) {
		crits.add(criteria);
	}

	public void addSpec(Spec spec) {
		Criteria crit;
		double delta = 1;
		if ((crit = getCriteria(spec.getName())) != null) {
			if (crit.getWeight() / getWeightSum() <= 0.3) {
				delta = Config.weight_modifier / 10 * 5;
			} else if (crit.getWeight() / getWeightSum() <= 0.7) {
				delta = Config.weight_modifier / 10 * 2.5;
			} else {
				delta = Config.weight_modifier / 10;
			}
			crit.setWeight(crit.getWeight() + delta);
			crit.addValue(spec.getValue());
		} else {
			crits.add(new Criteria(spec.getName(), Config.weight_modifier));
			crit = getCriteria(spec.getName());
			crit.addValue(spec.getValue());
		}

	}

	public void dropArticle(Article article) {
		for (Spec s : article.getSpecs()) {
			dropSpec(s);
		}
	}

	public void dropSpec(Spec s) {
		Criteria crit = getCriteria(s.getName());
		if (crit != null) {
			double delta = 1;
			if (crit.getWeight() / getWeightSum() <= 0.3) {
				delta = Config.weight_modifier / 10; 
			} else if (crit.getWeight() / getWeightSum() <= 0.7) {
				delta = Config.weight_modifier / 10 * 2.5;
			} else {
				delta = Config.weight_modifier / 10 * 5; 
			}
			crit.setWeight(crit.getWeight() - delta);
			crit.dropValue(s.getValue());
		}
	}
	
	public void dropSpec(Criteria crit) {
		if (crit != null) {
			double delta = 1;
			if (crit.getWeight() / getWeightSum() <= 0.3) {
				delta = Config.weight_modifier / 10; 
			} else if (crit.getWeight() / getWeightSum() <= 0.7) {
				delta = Config.weight_modifier / 10 * 2.5; 
			} else {
				delta = Config.weight_modifier / 10 * 5; 
			}
			crit.setWeight(crit.getWeight() - delta);
		}
	}

	public void updateTag(Article article) {
		Criteria c;
		List<Criteria> tmp = new ArrayList<Criteria>();
		List<Criteria> unused;

		for (Spec s : article.getSpecs()) {
			if ((c = getCriteria(s.getName())) != null) {
				
//				//erroding other values
//				for(SpecValue value : c.getValues()){
//					if(!value.getValue().equals(s.getValue())){
//						c.dropValue(value.getValue());
//					}
//				}
				
				tmp.add(c);
			}
			addSpec(s);
			
			
		}
		unused = new ArrayList<Criteria>(crits);
		unused.removeAll(tmp);
		
		//erroding unused criteria
		for (Criteria t : unused) {
			dropSpec(t);
		}

	}

	public double getConsistency(Article article) {
		Criteria c;
		double valueConsistency = 0;
		double consistency = 0;
		for (Spec s : article.getSpecs()) {

			if ((c = getCriteria(s.getName())) != null) {
				valueConsistency += c.getValueConsistency(s.getValue());
				if(valueConsistency != 0){
					consistency += c.getWeight()  * (valueConsistency);
				}
			}
			
		}
		consistency = (consistency) / (getWeightSum());

		return consistency;
	}

	public List<Criteria> getCrits() {
		return crits;
	}

	public String getName() {
		return name;
	}

	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		obj.put("name", name);

		for (Criteria c : crits) {
			obj.append("criteria", c.toJSON());
		}

		return obj;
	}

	public static Tag parseJSON(JSONObject json) {
		String name = json.getString("name");
		Tag c = new Tag(name);
		JSONArray values = json.getJSONArray("criteria");
		JSONObject obj;
		for (int i = 0; i < values.length(); i++) {
			obj = values.getJSONObject(i);
			c.crits.add(Criteria.parseJSON(obj));
		}
		return c;
	}

}
