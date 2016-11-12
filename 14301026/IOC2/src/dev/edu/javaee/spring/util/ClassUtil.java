package dev.edu.javaee.spring.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;



import Annotation.Autowired;
import Annotation.Component;



public class ClassUtil {
	 
    private static Set<Class<?>> CLASS_Component_SET;   //component
    private static Set<Class<?>> CLASS_Autowired_SET;   //autowire
  
	
    public static Set<Class<?>> getAutowiredClassSet(String packagename) {
    	System.out.println("1");
    	Set<Class<?>> beanClassSet = new HashSet<Class<?>>();
        
        return beanClassSet;
    }
	
 
    public static Set<Class<?>> getBeanClassSet(String packagename) {
        Set<Class<?>> beanClassSet = new HashSet<Class<?>>();
        //beanClassSet.addAll(getServiceClassSet());
        beanClassSet.addAll(getComponentClassSet(packagename));
        return beanClassSet;
    }
    
    public static Set<Class<?>> getComponentClassSet(String packagename) {
    	CLASS_Component_SET = getClasses(packagename);
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        for (Class<?> cls : CLASS_Component_SET) {
            if (cls.isAnnotationPresent(Component.class)) {   //有@component，加入
            	
                classSet.add(cls);
            }
        }
        return classSet;
    }


	//扫描全部class 
    public static Set<Class<?>> getClasses(String pack) {  
  
   
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();  
        // 是否循环迭代  
        boolean recursive = true;  
        
        String packageName = pack;  
        String packageDirName = packageName.replace('.', '/');   
        
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things  
        Enumeration<URL> dirs;  
        try {  
            dirs = Thread.currentThread().getContextClassLoader().getResources(  
                    packageDirName);  
            
            // 循环迭代下去  
            while (dirs.hasMoreElements()) {  
                // 获取下一个元素  
                URL url = dirs.nextElement();  
                // 得到协议的名称  
                String protocol = url.getProtocol();  
                
                // 如果是file文件 
                if ("file".equals(protocol)) {  
                    System.err.println("扫描file: in "+ packageDirName);  
                    
                    // 获取包的物理路径  
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");  
                    
                    // 以文件的方式扫描
                    ScanByFile(packageName, filePath,  
                            recursive, classes);  
                }   else{
                	System.out.println("我只会扫描文件啦！！！");
                }
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
  
        return classes;  
    }  
    
    
    // 以文件的形式来获取包下的所有Class 
       
    public static void ScanByFile(String packageName,  
            String packagePath, final boolean recursive, Set<Class<?>> classes) {  
        // 获取此包的目录 建立一个File  
        File dir = new File(packagePath);  
       
        if (!dir.exists() || !dir.isDirectory()) {              
            return;  
        }  
        //获取包下的所有文件 包括目录  
        File[] dirfiles = dir.listFiles(new FileFilter() {  
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)  
            public boolean accept(File file) {  
                return (recursive && file.isDirectory())  
                        || (file.getName().endsWith(".class"));  
            }  
        });  
        // 循环所有文件  
        for (File file : dirfiles) {  
            // 如果是目录 则继续扫描  
            if (file.isDirectory()) {    //递归调用
                ScanByFile(packageName + "."  
                        + file.getName(), file.getAbsolutePath(), recursive,  
                        classes);  
            } else {  
                // 如果是java类文件 去掉后面的.class 只留下类名  
                String className = file.getName().substring(0,  
                        file.getName().length() - 6);  
                try {                  
                     classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));    
                  } catch (ClassNotFoundException e) {  
                    
                    e.printStackTrace();  
                }  
            }  
        }  
    }  
}
