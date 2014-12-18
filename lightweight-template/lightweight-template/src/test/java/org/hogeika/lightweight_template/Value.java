package org.hogeika.lightweight_template;

public class Value {
	public static  Object getObj(){
		Object testObject = new Object() {
			public int a = 10;
			public int b = 11;
		    public void testMe() { 
		        System.out.println("testme");
		    }
		};
		return testObject;
	}
}
