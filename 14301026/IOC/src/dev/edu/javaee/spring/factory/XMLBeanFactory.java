package dev.edu.javaee.spring.factory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import dev.edu.javaee.spring.bean.BeanDefinition;
import dev.edu.javaee.spring.bean.BeanUtil;
import dev.edu.javaee.spring.bean.PropertyValue;
import dev.edu.javaee.spring.bean.PropertyValues;
import dev.edu.javaee.spring.resource.Resource;

public class XMLBeanFactory extends AbstractBeanFactory{
	
	private String xmlPath;
	
	public XMLBeanFactory(Resource resource) throws NoSuchFieldException, SecurityException
	{
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			Document document = dbBuilder.parse(resource.getInputStream());
            NodeList beanList = document.getElementsByTagName("bean");
            List <String> beanIdList = new ArrayList<String>();
            
            for(int i = 0 ; i < beanList.getLength(); i++)
            {
            	Node bean = beanList.item(i);
            	BeanDefinition beandef = new BeanDefinition();
            	String beanClassName = bean.getAttributes().getNamedItem("class").getNodeValue();
            	String beanName = bean.getAttributes().getNamedItem("id").getNodeValue();  //id
            	beanIdList.add(beanName);
            	
        		beandef.setBeanClassName(beanClassName);
        		
				try {
					Class<?> beanClass = Class.forName(beanClassName);
					beandef.setBeanClass(beanClass);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        		PropertyValues propertyValues = new PropertyValues();
        		
        		NodeList propertyList = bean.getChildNodes();
            	for(int j = 0 ; j < propertyList.getLength(); j++)
            	{
            		Node property = propertyList.item(j);
            		if (property instanceof Element) {
        				Element ele = (Element) property;
        				
        				String name = ele.getAttribute("name");
        				
        				if(ele.getAttribute("ref").equals("")){ //value
        					
        				Class<?> type;
						try {
							type = beandef.getBeanClass().getDeclaredField(name).getType();
							Object value = ele.getAttribute("value");
	        				
	        				if(type == Integer.class)
	        				{
	        					value = Integer.parseInt((String) value);
	        				}
	        				
	        				propertyValues.AddPropertyValue(new PropertyValue(
	        						name,value));
						
						} catch (NoSuchFieldException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        				
        				}else{  //ref
        					String RefBeanName = ele.getAttribute("ref"); 
        					propertyValues.AddPropertyValue(new PropertyValue(
	        						name,RefBeanName));
        					
        					
        				}
            		
            	   }
            	
               }
            	beandef.setPropertyValues(propertyValues);
            
            	this.registerBeanDefinition(beanName, beandef);  //beanName -id
            	
            }
            
        	
            
            for(int i=0;i<beanIdList.size();i++){
            BeanDefinition bean = this.beanDefinitionMap.get(beanIdList.get(i));
            List<PropertyValue> propertyValues = new ArrayList<PropertyValue>();
            
            propertyValues= bean.getPropertyValues().GetPropertyValues();
            
               for(int j=0;j<propertyValues.size();j++){
            	 PropertyValue property = propertyValues.get(j);
            	 
            	 if( property.getRef()!=""){  //有ref
            		  String refBeanName = property.getRef();
            		  	
					 Object ref = this.beanDefinitionMap.get(refBeanName).getBean();
					 BeanUtil.invokeSetterMethod(bean.getBean(), property.getName(), ref);	
            	 }
               }
           
            }
            
            
		} catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
	


	@Override
	protected BeanDefinition GetCreatedBean(BeanDefinition beanDefinition) {
		
		try {
			// set BeanClass for BeanDefinition
			
			Class<?> beanClass = beanDefinition.getBeanClass(); 
			// set Bean Instance for BeanDefinition
			Object bean = beanClass.newInstance();	
			
			List<PropertyValue> fieldDefinitionList = beanDefinition.getPropertyValues().GetPropertyValues();
			for(PropertyValue propertyValue: fieldDefinitionList)
			{
				if(propertyValue.getRef()==null ||propertyValue.getRef().equals("")){
				  BeanUtil.invokeSetterMethod(bean, propertyValue.getName(), propertyValue.getValue());
				}
				
			}
			
			beanDefinition.setBean(bean);
			
			return beanDefinition;
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
