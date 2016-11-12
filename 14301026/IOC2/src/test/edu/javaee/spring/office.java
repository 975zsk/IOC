package test.edu.javaee.spring;

import Annotation.Autowired;

public class office {
   public String officeId;

	public String getOfficeId() {
		return officeId;
	}
	
	
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	public String tostring(){
		return "office" +officeId;
	}

}
