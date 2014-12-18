package org.hogeika.lightweight_template.dummy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Dummy {
	public void func(Object testObject) throws Exception {
		Method m = testObject.getClass().getDeclaredMethod("testMe");
		m.setAccessible(true);
		m.invoke(testObject); // prints out "testme"
		Field f = testObject.getClass().getField("a");
		f.setAccessible(true);
		System.out.println(f.get(testObject));
	}
}
