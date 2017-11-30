package core;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Criteria {
	private String name;
	private double weight;
	protected List<SpecValue> values;

	public Criteria(String name, double weight) {
		this.name = name;
		this.weight = weight;
		values = new ArrayList<SpecValue>();
	}

	public SpecValue getSpecValue(String value) {

		for (SpecValue v : values) {
			if (v.getValue().equals(value)) {
				return v;
			}
		}

		return null;
	}

	public double getValueConsistency(String value) {
		double consistency = 0;
		if(getSpecValue(value) != null){
			consistency += getSpecValue(value).getWeight();
		}
		
		consistency = consistency / sumValuesWeight();
		return consistency;
	}

	public double sumValuesWeight() {
		double value = 0;

		for (SpecValue v : values) {
			value += v.getWeight();
		}

		return value;
	}

	public void addValue(String value) {
		SpecValue v;
		double delta = 1;
		if ((v = getSpecValue(value)) != null) {
			if(v.getWeight() / sumValuesWeight() <= 0.3){
				delta = Config.weight_modifier / 10 * 5; 
			} else if (v.getWeight() / sumValuesWeight() <= 0.7){
				delta = Config.weight_modifier / 10 * 2.5; 
			} else {
				delta = Config.weight_modifier / 10; 
			}
			v.setWeight(v.getWeight() + delta);
		} else {
			values.add(new SpecValue(value, Config.weight_modifier));
		}
	}

	public void errodeValue(String value){
		SpecValue v = getSpecValue(value);
		if(v != null){
			double delta = 1;
			if(v.getWeight() / sumValuesWeight() <= 0.3){
				delta = Config.weight_modifier / 100; 
			} else if (v.getWeight() / sumValuesWeight() <= 0.7){
				delta = Config.weight_modifier / 100 * 25; 
			} else {
				delta = Config.weight_modifier / 100 * 50; 
			}
			v.setWeight(v.getWeight() - delta);
		}
	}
	
	public String getName() {
		return name;
	}

	public double getWeight() {
		return weight;
	}

	public List<SpecValue> getValues() {
		return values;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWeight(double weight) {
		this.weight = Math.max(Config.weight_modifier,weight);
	}

	public JSONObject toJSON(){
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("weight", weight);
		
		for(SpecValue v : values){
			obj.append("values", v.toJSON());
		}
		
		return obj;
	}
	
	public static Criteria parseJSON(JSONObject json){
		String name = json.getString("name");
		double weight = json.getDouble("weight");
		Criteria c = new Criteria(name, weight);
		JSONArray values = json.getJSONArray("values");
		JSONObject obj;
		for(int i = 0; i < values.length(); i++){
			obj = values.getJSONObject(i);
			c.values.add(SpecValue.parseJSON(obj));
		}
		return c;
	}
	
}
