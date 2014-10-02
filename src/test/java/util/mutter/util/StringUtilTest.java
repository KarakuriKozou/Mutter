package util.mutter.util;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

/**
 * StringUtil のテスト
 *
 */
public class StringUtilTest {

	/**
	 * テスト
	 */
	@Test
	public void testRtrim() {
		assertEquals("abc", StringUtil.rtrim("abc "));
		assertEquals(" abc", StringUtil.rtrim(" abc "));
		assertEquals("", StringUtil.rtrim(" "));
	}

	/**
	 * テスト
	 */
	@Test
	public void testIsEmpty() {
		assertEquals(true, StringUtil.isEmpty(""));
		assertEquals(true, StringUtil.isEmpty(null));
		assertEquals(false, StringUtil.isEmpty(" "));
	}

	/**
	 * テスト
	 */
	@Test
	public void testGetStringPrintWriter() {
		PrintWriter out = StringUtil.getStringPrintWriter();
		out.append("abc");
		out.append("def");
		assertEquals("abcdef", out.toString());
	}

	/**
	 * テスト
	 */
	@Test
	public void testChop() {
		PrintWriter out = StringUtil.getStringPrintWriter();
		out.print("abc");
		out.println("def");
		assertEquals("abcdef", StringUtil.chop(out.toString()));
	}

	/**
	 * テスト
	 */
	@Test
	public void testIndexOfStringEnd() {
		assertEquals(1, StringUtil.indexOfStringEnd("\"\"", 1));
		assertEquals(3, StringUtil.indexOfStringEnd("\"\\\"\"", 1));
		assertEquals(-1, StringUtil.indexOfStringEnd("\"\\\"", 1));
	}

	/**
	 * テスト
	 * @throws ParseException パース失敗時
	 */
	@Test
	public void testJsonValueOf() throws ParseException {
		assertNull(StringUtil.jsonValueOf("null"));
		assertTrue("", StringUtil.jsonValueOf("true").equals(true));
		assertTrue("", StringUtil.jsonValueOf("false").equals(false));
		assertEquals("ab\"c", StringUtil.jsonValueOf("\"ab\\\"c\""));
		assertEquals(4L, StringUtil.jsonValueOf("4"));
		assertEquals(3.14D, StringUtil.jsonValueOf("3.14"));
		try {
			StringUtil.jsonValueOf("3.14.1");
			fail("fail");
		} catch (ParseException e) {
			// OK
		}
	}

	/**
	 * テスト
	 * @throws ParseException パース失敗時
	 */
	@Test
	public void testJsonDecode() throws ParseException {
		assertNull(StringUtil.jsonDecode("null"));
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) StringUtil.jsonDecode("{ \t\r\n\"abc\":1234,,\"\":[,,]}");
		assertEquals(1234L, map.get("abc"));

		try {
			StringUtil.jsonDecode("\"abc");
			fail("fail");
		} catch (ParseException e) {
			// OK
		}

		try {
			StringUtil.jsonDecode("[\"<EOF>\"");
			fail("fail");
		} catch (ParseException e) {
			// OK
		}

		try {
			StringUtil.jsonDecode("[\"a\"}");
			fail("fail");
		} catch (ParseException e) {
			// OK
		}

		try {
			StringUtil.jsonDecode("{\"a\":\"b\"]");
			fail("fail");
		} catch (ParseException e) {
			// OK
		}

		try {
			StringUtil.jsonDecode("{\"a\":}");
			fail("fail");
		} catch (ParseException e) {
			// OK
		}

		try {
			StringUtil.jsonDecode("{\"a\":,}");
			fail("fail");
		} catch (ParseException e) {
			// OK
		}

		try {
			StringUtil.jsonDecode("[[]");
			fail("fail");
		} catch (ParseException e) {
			// OK
		}

		try {
			StringUtil.jsonDecode("[{]");
			fail("fail");
		} catch (ParseException e) {
			// OK
		}

		try {
			StringUtil.jsonDecode("{\"<EOF>\"");
			fail("fail");
		} catch (ParseException e) {
			// OK
		}
		try {
			StringUtil.jsonDecode("{a:}");
			fail("fail");
		} catch (ParseException e) {
			// OK
		}

		try {
			StringUtil.jsonDecode("{:a}");
			fail("fail");
		} catch (ParseException e) {
			// OK
		}

		try {
			StringUtil.jsonDecode("{[]:a}");
			fail("fail");
		} catch (ParseException e) {
			// OK
		}

		try {
			StringUtil.jsonDecode("{{}:a}");
			fail("fail");
		} catch (ParseException e) {
			// OK
		}
	}

	/**
	 * テスト
	 * @throws Exception 例外発生時
	 */
	@Test
	public void testJsonEncode() throws Exception {

		assertJsonEncode(null);
		assertJsonEncode("");
		assertJsonEncode(1);
		assertJsonEncode(1.1);
//		assertJsonEncode(new BigInteger("12345678901234567890"));
//		assertJsonEncode(new BigDecimal("12345678901234567890.123456789"));

		assertJsonEncode(new LinkedList<Object>(){{
		}});

		assertJsonEncode(new LinkedList<Object>(){{
			add(true);
		}});

		assertJsonEncode(new LinkedList<Object>(){{
			add(true);
			add(false);
			add(null);
		}});

		assertJsonEncode(new Object[]{
			true
			, new TreeMap<String, Object>()
			, new Date()
		});

		assertJsonEncode(new Object[]{
			true
			, new TreeMap<String, Object>(){{
				put("int", 2);
			}}
		});

		assertJsonEncode(new Object[]{
			true
			, new TreeMap<String, Object>(){{
				put("int", 2);
				put("string", "text");
			}}
		});

		assertJsonEncode(new Object[]{
			true
			, new TreeMap<String, Object>(){{
				put("int", 2);
				put("string", "text");
				put("array", new Object[]{1,"1"});
				put("map", new TreeMap<String, Object>(){{
					put("string", "val");
				}});
			}}
		});

		assertJsonEncode(new TreeMap<String, Object>(){{
			put("int", 2);
			put("string", "text");
			put("array", new Object[]{1,"1"});
			put("map", new TreeMap<String, Object>(){{
				put("string", "val");
			}});
			put("iterable0", new Iterable<String>() {
				ArrayList<String> list = new ArrayList<String>();
				@Override
				public Iterator<String> iterator() {
					return list.iterator();
				}
			});
			put("iterable1", new Iterable<String>() {
				ArrayList<String> list = new ArrayList<String>();
				{
					list.add("item1");
				}
				@Override
				public Iterator<String> iterator() {
					return list.iterator();
				}
			});
			put("iterable2", new Iterable<String>() {
				ArrayList<String> list = new ArrayList<String>();
				{
					list.add("item1");
					list.add("item2");
				}
				@Override
				public Iterator<String> iterator() {
					return list.iterator();
				}
			});
			put("object", new Object());
		}});
	}
	/**
	 * json変換の確認
	 * @param obj
	 * @throws Exception
	 */
	public static void assertJsonEncode(Object obj) throws Exception {
		String pass1, pass2;
		Object parsed;
		pass1 = StringUtil.jsonEncode(obj);
		System.out.println(pass1);

		// パースする
//		parsed = perser.parse(pass1);
//		assertNotEquals(obj, parsed);
//		pass2 = StringUtil.jsonEncode(parsed);
//		// キーのハッシュ値が一致するのでMapの順番も合致しているはず
////		assertEquals(pass1, pass2);
//		assertEquals(obj, parsed);

		// パースする
		parsed = StringUtil.jsonDecode(pass1);
//		assertNotEquals(obj, parsed);
		pass2 = StringUtil.jsonEncode(parsed);
		System.out.println(pass2);
		// キーのハッシュ値が一致するのでMapの順番も合致しているはず
		assertEquals(pass1, pass2);
//		assertEquals(obj, parsed);
	}
}
