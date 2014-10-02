package util.mutter.util;

import static org.junit.Assert.*;

import org.junit.Test;

import util.mutter.util.ArrayUtil.IProcesser;

/**
 * ArrayUtil のテスト
 */
public class ArrayUtilTest {
	/**
	 * テスト
	 */
	@Test
	public void test() {
		String str;

		str = ArrayUtil.join(new Object[] {}, ", ");
		assertEquals("", str);

		str = ArrayUtil.join(new Object[] {1, 2.0, "abc"}, ", ");
		assertEquals("1, 2.0, abc", str);

		Object[] objs = ArrayUtil.convert(new IProcesser() {
			@Override
			public Object process(int index, Object obj) {
				return Math.round((Double) obj);
			}
		}, new Object[] {1.0, 2.0});
		str = ArrayUtil.join(objs, ", ");
		assertEquals("1, 2", str);
	}
}
