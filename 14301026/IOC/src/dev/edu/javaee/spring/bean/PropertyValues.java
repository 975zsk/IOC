package dev.edu.javaee.spring.bean;

import java.util.ArrayList;
import java.util.List;

public class PropertyValues {
	private List<PropertyValue> propertyValues = new ArrayList<PropertyValue>();
	
	public void AddPropertyValue(PropertyValue propertyValue){
		this.propertyValues.add(propertyValue);
	}
	public List<PropertyValue> GetPropertyValues()
	{
		return this.propertyValues;
	}
}
