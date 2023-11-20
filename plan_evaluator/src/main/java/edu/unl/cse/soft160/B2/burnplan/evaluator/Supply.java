package edu.unl.cse.soft160.B2.burnplan.evaluator;

public class Supply {
	private String name;
	private double quantity;
	private double capacity;
	private String unit;
	
	public Supply (String name, double quantity, double capacity, String unit, FuelType fuelType) {
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

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
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
