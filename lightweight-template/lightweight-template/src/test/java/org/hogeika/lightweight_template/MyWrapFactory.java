package org.hogeika.lightweight_template;

import java.lang.reflect.Field;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

public class MyWrapFactory extends WrapFactory {
	public static class MyNativeJavaObject extends NativeJavaObject {
		/**
		 * 
		 */
		private static final long serialVersionUID = 999252668287311879L;

		public MyNativeJavaObject(Scriptable scope, Object javaObject, Class<?> staticType) {
			super(scope, javaObject, staticType);
		}

		public MyNativeJavaObject(Scriptable scope, Object javaObject) {
			super(scope, javaObject, null);
		}

		@Override
		protected void initMembers() {
			super.initMembers();
			Object o  = members;
			try {
				Class c = o.getClass();
				Field f = c.getDeclaredField("members");
				f.setAccessible(true);
				Map<String,Object> members = (Map<String, Object>) f.get(o);
				for (Object m : members.values()){
					if (m instanceof Field){
						((Field) m).setAccessible(true);
					}
				}
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public Object wrap(Context cx, Scriptable scope, Object obj,
			Class<?> staticType)
	{
		if (obj instanceof String || obj instanceof Number ||
				obj instanceof Boolean)
		{
			return obj;
		} else if (obj instanceof Character) {
			char[] a = { ((Character)obj).charValue() };
			return new String(a);
		}
		return super.wrap(cx, scope, obj, staticType);
	}
	  
	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope,
			Object javaObject, Class<?> staticType) {
        Scriptable wrap;
        wrap = new MyNativeJavaObject(scope, javaObject, staticType);
        return wrap;
	}

}
