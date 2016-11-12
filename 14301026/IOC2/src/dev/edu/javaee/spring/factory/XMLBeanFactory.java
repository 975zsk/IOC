package dev.edu.javaee.spring.factory;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import Annotation.Autowired;





import dev.edu.javaee.spring.bean.BeanDefinition;
import dev.edu.javaee.spring.bean.BeanUtil;
import dev.edu.javaee.spring.bean.PropertyValue;
import dev.edu.javaee.spring.bean.PropertyValues;
import dev.edu.javaee.spring.resource.Resource;
import dev.edu.javaee.spring.util.ClassUtil;

public class XMLBeanFactory extends AbstractBeanFactory{
	
	private String xmlPath;
	private String package_path;  //要扫描的包
	List<String> beanNameList = new ArrayList<String>(); //用来存放bean的名字
	
	public XMLBeanFactory(Resource resource,String package_path)
	{
		this.package_path=package_path;
		
		//先扫描xml文件里的
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			Document document = dbBuilder.parse(resource.getInputStream());
            NodeList beanList = document.getElementsByTagName("bean");
            for(int i = 0 ; i < beanList.getLength(); i++)
            {
            	Node bean = beanList.item(i);
            	BeanDefinition beandef = new BeanDefinition();
            	String beanClassName = bean.getAttributes().getNamedItem("class").getNodeValue();
            	String beanName = bean.getAttributes().getNamedItem("id").getNodeValue();
            	beanNameList.add(beanName);
            	
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
            	
              	this.registerBeanDefinition(beanName, beandef);  //xml 文件里的先实例化了 和setValue
            	
            }
            
           // 把XML文件里有的ref 先注入了
//            for(int i=0;i<beanNameList.size();i++){
//               BeanDefinition bean = this.beanDefinitionMap.get(beanNameList.get(i));
//                List<PropertyValue> propertyValues = new ArrayList<PropertyValue>();
//                
//                propertyValues= bean.getPropertyValues().GetPropertyValues();
//                
//                   for(int j=0;j<propertyValues.size();j++){
//                	 PropertyValue property = propertyValues.get(j);
//                	 
//                	 if( (property.getRef()!="") & (beanNameList.contains(property.getRef()))){  //有ref且是xml里的ref bean 
//                		  String refBeanName = property.getRef();
//                		  	
//    					 Object ref = this.beanDefinitionMap.get(refBeanName).getBean();
//    					 BeanUtil.invokeSetterMethod(bean.getBean(), property.getName(), ref);	
//                	 }
//                   }
//               
//                }
            
		} catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		this.readComponent(package_path); //扫描包，实现@component,将其实例化 放入Map
		this.injectAurowiredObject();    //实现ref的注入
	
		
	}

	
	@Override
	protected BeanDefinition GetCreatedBean(BeanDefinition beanDefinition) {
		
		try {
			// set BeanClass for BeanDefinition
			
			Class<?> beanClass = beanDefinition.getBeanClass(); 
			// set Bean Instance for BeanDefinition
			Object bean = beanClass.newInstance();	
			
			//有value  放value,没有就是ref 空着等注入
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
	/**
	 * 读取注解@component
	 */
	public void readComponent(String packagename){
		Set<Class<?>> classSet = new HashSet<Class<?>>();
		classSet=ClassUtil.getBeanClassSet(packagename);
		for (Iterator<Class<?>> it = classSet.iterator();it.hasNext();){
			Class<?> beanClass = it.next();
			System.out.println(beanClass.getName());
			System.out.println();
			//BeanDefinition beanDefine = new BeanDefinition(classname.getSimpleName(),classname.getName());
			BeanDefinition beanDefine = new BeanDefinition();
			beanDefine.setBeanClass(beanClass);  //放入class
			beanDefine.setBeanClassName(beanClass.getName()); //放入class地址			

			try {
			beanDefine.setBean(Class.forName(beanClass.getName().toString()).newInstance());  //放入bean的实例
			
			this.beanDefinitionMap.put(beanClass.getSimpleName(), beanDefine); //放入Map
			this.beanNameList.add(beanClass.getSimpleName());
								
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	
	/**
	 * Aurowired set ref
	 */
	public void injectAurowiredObject()
	{


            for (int i=0;i<this.beanNameList.size();i++) {
            	String beanName = beanNameList.get(i);
            	BeanDefinition bean = this.beanDefinitionMap.get(beanName); 
            	
                Object beanInstance = bean.getBean(); //得到实例
                
                Field[] beanFields = beanInstance.getClass().getDeclaredFields();
                
                if (beanFields.length>0) {
                    for (Field beanField : beanFields) {
                        if (beanField.isAnnotationPresent(Autowired.class)) {  //是autowired注解
                        	
                            Class<?> beanFieldClass = beanField.getType();
                            Object beanFieldInstance = this.beanDefinitionMap.get(beanFieldClass.getSimpleName()).getBean();
                            
                            System.out.println(beanFieldInstance);
                       
                            if (beanFieldInstance != null) {                
                                beanField.setAccessible(true);
                                try {
									beanField.set(beanInstance, beanFieldInstance); //字段的注入方法
								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
                            }
                        }
                    }
                }
            }
            }
        
	
	


}
