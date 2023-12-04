package edu.unl.cse.soft160.B2.burnplan.evaluator;

public class Supply {
	private String name;
	private Double quantity;
	private Double capacity;
	private String unit;
	
	public Supply (String name, Double quantity, Double capacity, String unit) {
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

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public Double getCapacity() {
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
