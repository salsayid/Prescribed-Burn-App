package edu.unl.cse.soft160.B2.burnplan.evaluator;

public class Supply {
	private String name;
	private int quantity;
	private double capacity;
	private String unit;
	
	public Supply (String name, int quantity, double capacity, String unit) {
		this.name = name;
		this.quantity = quantity;
		this.capacity = capacity;
		this.unit = unit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
