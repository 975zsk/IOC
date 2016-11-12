package test.edu.javaee.spring;

public class boss {
  private office office;

  
  //@Autowired
 // public boss(office office){
     
 //     this.office = office ;
  //}

  public void setOffice(office office){
	   this.office=office;
  }
  public String tostring(){
	  return "this boss has "+"and in "+office.tostring();
  }
}
