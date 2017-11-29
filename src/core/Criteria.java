package core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Criteria implements Serializable {
	private String name;
	private double weight;
	private List<SpecValue> values;

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
		consistency += getSpecValue(value).getWeight();

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
		int delta = 1;
		if ((v = getSpecValue(value)) != null) {
			if(v.getWeight() <= 0.3){
				delta = 1;
			} else if (v.getWeight() <= 0.7){
				delta = 2;
			} else {
				delta = 3;
			}
			v.setWeight(v.getWeight() + Config.weight_modifier / delta);
		} else {
			values.add(new SpecValue(value, Config.weight_modifier));
		}
	}

	public void dropValue(String value){
		SpecValue v = getSpecValue(value);
		int delta = 1;
		if(v.getWeight() <= 0.3){
			delta = 3;
		} else if (v.getWeight() <= 0.7){
			delta = 2;
		} else {
			delta = 1;
		}
		v.setWeight(v.getWeight() - Config.weight_modifier / delta);
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
		this.weight = Math.max(Config.weight_modifier,Math.min(1, weight));
	}

}
