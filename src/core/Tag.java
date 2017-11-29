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

	private double getWeightSum() {
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
		int delta = 1;
		if ((crit = getCriteria(spec.getName())) != null) {
			if (crit.getWeight() <= 0.3) {
				delta = 1;
			} else if (crit.getWeight() <= 0.7) {
				delta = 2;
			} else {
				delta = 3;
			}
			crit.setWeight(crit.getWeight() + Config.weight_modifier / delta);
			crit.addValue(spec.getValue());
		} else {
			crits.add(new Criteria(spec.getName(), Config.weight_modifier));
			crit = getCriteria(spec.getName());
			crit.addValue(spec.getValue());
		}

		// drop others values
		for (SpecValue v : crit.getValues()) {
			if (!v.getValue().equals(spec.getValue())) {
				crit.dropValue(v.getValue());
			}
		}

	}

	public void dropArticle(Article article) {
		for (Spec s : article.getSpecs()) {
			dropSpec(s.getName());
		}
	}

	public void dropSpec(String name) {
		Criteria crit = getCriteria(name);
		if (crit != null) {
			int delta = 1;
			if (crit.getWeight() <= 0.3) {
				delta = 3;
			} else if (crit.getWeight() <= 0.7) {
				delta = 2;
			} else {
				delta = 1;
			}
			crit.setWeight(crit.getWeight() - Config.weight_modifier / delta);
		}
	}

	public void updateTag(Article article) {
		Criteria c;
		List<Criteria> tmp = new ArrayList<Criteria>();
		List<Criteria> unused;

		for (Spec s : article.getSpecs()) {
			if ((c = getCriteria(s.getName())) != null) {
				tmp.add(c);

			}
			addSpec(s);

		}
		unused = new ArrayList<Criteria>(crits);
		unused.removeAll(tmp);

		for (Criteria t : unused) {
			dropSpec(t.getName());
		}

	}

	public double getConsistency(Article article) {
		Criteria c;
		double valueConsistency;
		double consistency = 0;
		for (Spec s : article.getSpecs()) {

			if ((c = getCriteria(s.getName())) != null) {
				valueConsistency = c.getValueConsistency(s.getValue());
				if(valueConsistency != 0){
					consistency += c.getWeight() - (c.getWeight() / 2) * (c.getValueConsistency(s.getValue()));
				} else {
					consistency += c.getWeight() - (c.getWeight() / 2);
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
