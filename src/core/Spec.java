package core;

import java.io.Serializable;

public class Spec implements Serializable {
	private String name;
	private String value;
	
	public Spec(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
}
