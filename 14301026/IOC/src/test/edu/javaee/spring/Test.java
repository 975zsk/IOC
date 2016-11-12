package test.edu.javaee.spring;

import dev.edu.javaee.spring.bean.BeanDefinition;
import dev.edu.javaee.spring.bean.PropertyValue;
import dev.edu.javaee.spring.bean.PropertyValues;
import dev.edu.javaee.spring.factory.BeanFactory;
import dev.edu.javaee.spring.factory.XMLBeanFactory;
import dev.edu.javaee.spring.resource.LocalFileResource;

public class Test {

	/**
	 * @param args
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		// TODO Auto-generated method stub
		
         LocalFileResource resource = new LocalFileResource("bean.xml");
		
		BeanFactory beanFactory = new XMLBeanFactory(resource);
		// the BeanDefinition doesn`t create the real bean yet
		boss boss = (boss) beanFactory.getBean("boss");
        System.out.println(boss.tostring());
        
	}

}
