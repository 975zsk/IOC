package test.edu.javaee.spring;

import Annotation.Component;

@Component("car")
public class car {
	public String carId="001";
	public String carColor="red";
	
    public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public String getCarColor() {
		return carColor;
	}

	public void setCarColor(String carColor) {
		this.carColor = carColor;
	}

	public String tostring(){
    	return "the car is " + carColor+" with " +carId;
    }
}
