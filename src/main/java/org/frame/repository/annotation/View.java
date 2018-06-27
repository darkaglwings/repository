package org.frame.repository.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface View {
	
	public static boolean TRUE = true;
	
	public static boolean FALSE = false;
	
	String name() default "";
	
	String scheme() default "";
	
	boolean simple() default false;
	
	String user() default "";

}
