package test.edu.javaee.spring;

import Annotation.Autowired;

public class boss {
	
 @Autowired
  public office office;
 
 @Autowired
  public car car;
  
  
  

  public String tostring(){
	  return "this boss has "+car.tostring()+" and in "+office.tostring();
  }

  public office getOffice() {
	return office;
  }
  

  public car getCar() {
	 return car;
  }
  

}
