package core;

import org.json.JSONObject;

public class SpecValue {
	private String value;
	private double weight;

	public SpecValue(String value, double weight) {
		this.value = value;
		this.weight = weight;
	}

	public String getValue() {
		return value;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = Math.max(Config.weight_modifier,Math.min(1, weight));
	}

	public JSONObject toJSON(){
		JSONObject obj = new JSONObject();
		obj.put("value", value);
		obj.put("weight", weight);
		return obj;
	}

	public static SpecValue parseJSON(JSONObject json) {
		String value = json.getString("value");
		double weight = json.getDouble("weight");
		
		SpecValue c = new SpecValue(value, weight);

		return c;
	}
	
}
