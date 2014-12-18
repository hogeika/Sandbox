package org.hogeika.lightweight_template;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

import nu.validator.htmlparser.dom.HtmlDocumentBuilder;
import nu.validator.htmlparser.sax.HtmlParser;

import org.apache.commons.io.IOUtils;
import org.hogeika.lightweight_template.dummy.Dummy;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TestTest {

	public static final String INPUT = "<html><head><title>ƒ^ƒCƒgƒ‹</title></head><body><div class='main'><span class='test'>HogeHoge</span><span>FugaFuga</span></div></body></html>";
	@Test
	public void testHtmlDocumentBuilder() throws SAXException, IOException {
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
		Reader reader = new StringReader(INPUT);
		Document doc = builder.parse(new InputSource(reader));
		System.out.println(doc.getChildNodes().item(0).getNodeName());
	}
	public static final String SOURCE = "function f(x){return x+1} f(value.b);";

	public class Hoge {
		public int a = 8;
	}
	@Test
	public void testRhino() throws Exception {
		Context context = Context.enter();
		context.setWrapFactory(new MyWrapFactory());
		try {
			Scriptable scriptable = context.initStandardObjects();
	//		Object value = Context.javaToJS(new Object(){public static final int a = 7;}, scriptable);
	//		Object value = Context.javaToJS(new Value(), scriptable);
	//		Object value = Context.javaToJS(new Hoge(){public int b = 5;}, scriptable);
	//		Object value = Context.javaToJS(Value.getObj(), scriptable);
	//		Object value = new MyNativeJavaObject(scriptable, Value.getObj());
			Object value = Context.javaToJS(new Object(){public int a = 12; public int b = 13;}, scriptable);
			ScriptableObject.putProperty(scriptable, "value", value);
			Object result = context.evaluateString(scriptable, SOURCE, "<embed>",1,null);
			System.out.println(result);
		}finally{
			Context.exit();
		}
	}

	public static final String FUNCTION = "function (x){return this.a + x;}";
	@Test
	public void testRhinoScope() throws Exception {
		Context context = Context.enter();
		context.setWrapFactory(new MyWrapFactory());
		try {
			Scriptable scope1 = context.initStandardObjects();
			Scriptable value = (Scriptable) Context.javaToJS(new Object(){public int a = 12; public int b = 13;}, scope1);
			Function func = context.compileFunction(scope1, FUNCTION, "<FUNCTION>", 1, null);
			Object result = func.call(context, scope1, value, new Object[]{1});
			System.out.println(result);
		}finally{
			Context.exit();
		}
	}

//	public static final String FUNCTION2 = "function(){return this.getElementsByTagName('body').item(0).getNodeName();}";
	public static final String FUNCTION2 = "function(){return this.createElement('div').getNodeName();}";
	@Test
	public void testDOMandJS() throws Exception {
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
		Reader reader = new StringReader(INPUT);
		Document doc = builder.parse(new InputSource(reader));

		Context context = Context.enter();
		context.setWrapFactory(new MyWrapFactory());
		try {
			Scriptable scope1 = context.initStandardObjects();
			Scriptable document = (Scriptable) Context.javaToJS(doc, scope1);
			Function func = context.compileFunction(scope1, FUNCTION2, "<FUNCTION2>", 1, null);
			Object result = func.call(context, scope1, document, new Object[]{});
			Object javaObj = Context.jsToJava(result, String.class);
			System.out.println(javaObj);
		}finally{
			Context.exit();
		}
		
	}

	@Test
	public void testJQuery() throws Exception {
		URL env_url = ClassLoader.getSystemResource("env.js");
		URL jquery_url = ClassLoader.getSystemResource("jquery-2.1.1.js");

//		HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
		Reader reader = new StringReader(INPUT);
//		Document doc = builder.parse(new InputSource(reader));

		Context context = Context.enter();
		context.setWrapFactory(new MyWrapFactory());
		try {
			Scriptable scope1 = context.initStandardObjects();
			Object result;
			result = context.evaluateReader(scope1, new InputStreamReader(env_url.openStream()), "env.js", 1, null);

			Function func = (Function)scope1.get("DOMDocument",scope1);
			Object document = func.construct(context, scope1, new Object[]{Context.javaToJS(new InputSource(reader), scope1)});
			System.out.println(document);
			ScriptableObject.putProperty(scope1, "document", document);

//			result = context.evaluateString(scope1, "window.document", "<embed>", 1, null);
//			System.out.println(result);

//			Function func = (Function)scope1.get("_loadDOM", scope1);
//			result = func.call(context, scope1, scope1, new Object[]{Context.javaToJS(doc, scope1)});

			result = context.evaluateString(scope1, "window.document", "<embed>", 1, null);
			System.out.println(Context.jsToJava(result, Object.class));
			
//			result = context.evaluateString(scope1, "window.location='" + test_url.toString() + "'", "<embed>", 1, null);
//			result = context.evaluateString(scope1, "Object.defineProperty", "<embed>", 1, null);
//			System.out.println(result);
			
			result = context.evaluateReader(scope1, new InputStreamReader(jquery_url.openStream()), "jquery-2.1.1.js", 1, null);
			result = context.evaluateString(scope1, "$('body').length", "<embed>", 1, null);
			System.out.println(result);
			result = context.evaluateString(scope1, "$('span').length", "<embed>", 1, null);
			System.out.println(result);
			result = context.evaluateString(scope1, "$('div').html()", "<embed>", 1, null);
			System.out.println(result);
			result = context.evaluateString(scope1, "$('span.test').addClass('fuga')", "<embed>", 1, null);
			System.out.println(result);
			result = context.evaluateString(scope1, "$('div').html()", "<embed>", 1, null);
			System.out.println(result);
		}finally{
			Context.exit();
		}
		
	}

	@Test
	public void testReflection() throws Exception {
		Object testObject = Value.getObj();
		Method m = testObject.getClass().getDeclaredMethod("testMe");
		//		m.setAccessible(true);
		m.invoke(testObject); // prints out "testme"
		Field f = testObject.getClass().getField("a");
		System.out.println(f.get(testObject));
		new Dummy().func(testObject);
	}

}
