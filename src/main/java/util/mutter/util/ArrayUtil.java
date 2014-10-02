package util.mutter.util;


/**
 * 配列ユーティリティ
 */
public final class ArrayUtil {
	static {
		new ArrayUtil();	// カバレッジ対策
	}
	/**
	 * オブジェクト操作インタフェース
	 */
	public static interface IProcesser {
		/**
		 * オブジェクトを操作するコールバックです。
		 * @param index リストの0からのインデックス
		 * @param obj オブジェクト
		 * @return 操作結果
		 */
		Object process(int index, Object obj);
	}
	/**
	 * 要素を文字列結合する
	 * @param ad 要素操作インタフェース
	 * @param arr 配列
	 * @param separator 区切り文字列
	 * @return 結果
	 */
	public static String join(IProcesser ad, Object[] arr, String separator) {
		StringBuilder sb = new StringBuilder();
		if (arr.length>0) {
			sb.append(ad.process(0, arr[0]));
		}
		for (int i=1; i<arr.length; i++) {
			sb.append(separator);
			sb.append(ad.process(i, arr[i]));
		}
		return sb.toString();
	}
	/**
	 * 要素をtoString()で文字列化して結合する
	 * @param arr 配列
	 * @param terminater 区切り文字列
	 * @return 結果
	 */
	public static String join(Object[] arr, String terminater) {
		return join(new IProcesser() {
			@Override
			public String process(int index, Object obj) {
				return obj.toString();
			}
		}, arr, terminater);
	}
	/**
	 * IProcesserを介して、配列要素を変換
	 * @param ad 要素操作インタフェース
	 * @param arr 配列
	 * @return 結果
	 */
	public static Object[] convert(IProcesser ad, Object[] arr) {
		Object[] res = new Object[arr.length];
		for (int i=0; i<arr.length; i++) {
			res[i] = ad.process(i, arr[i]);
		}
		return res;
	}
}
