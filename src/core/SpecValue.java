package core;

import java.io.Serializable;

public class SpecValue implements Serializable {
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

}
