package com.sc.hm.jvm.agent.util;

import java.beans.Beans;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Utility to instantiate a class.
 * It heavily uses reflection for any instantiation and/or initialization.
 * 
 * @author Sudiptasish Chanda
 */
public class _ObjectCreator {
    
	/**
     * Create and return the instance of the class designated by this className.
     * It uses the no-argument constructor (default one) while instantiating
     * the object (provided the no-argument constructor is defined).
     * 
     * @param   <T>
     * @param   className           Class name
     * 
     * @return  T                   New Instance
     * @throws  Exception           If any error occurs
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(String className) throws Exception {    	
    	Class<T> clazz = (Class<T>)Class.forName(className.trim());
    	Object obj = Beans.instantiate(_ObjectCreator.class.getClassLoader(), className);
    	return clazz.cast(obj);
    }
    
    /**
     * Create and return the instance of the class designated by this className.
     * It uses the specific parameterized constructor while instantiating the class
     * instance.
     * 
     * @param   <T>
     * @param   className           Class name
     * @param   paramTypes          Constructor Parameter types
     * @param   params              Constructor Parameter names
     * 
     * @return  T                   New Instance
     * @throws  Exception           If any error occurs
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(String className
                               , Class[] paramTypes
                               , Object[] params) throws Exception {
        Class<?> clazz;
        
        clazz = Class.forName(className.trim());
        Constructor<T> constructor = (Constructor<T>)clazz.getDeclaredConstructor(paramTypes);
        T obj = constructor.newInstance(params);
        return obj;
    }
    
    /**
     * Create and return the instance of the class designated by this className.
     * It uses a static accessor or factory method for instantiating.
     * 
     * @param   <T>
     * @param   className           Class name
     * @param   factoryMethod       Factory/accessor method name (static)
     * @param   paramTypes          Parameter types
     * @param   params              Parameter values
     * 
     * @return  T                   New Instance
     * @throws  Exception           If any error occurs
     */
    @SuppressWarnings("unchecked")
    public static <T> T createStatic(String className, String factoryMethod
            , Class[] paramTypes, Object[] params) throws Exception {
        
        Class<?> clazz;
        clazz = Class.forName(className.trim());
        Method method = clazz.getDeclaredMethod(factoryMethod, paramTypes);
        return (T)method.invoke(null, params);
    }
}
