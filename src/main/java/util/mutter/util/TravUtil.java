package util.mutter.util;

import java.util.Collection;
import java.util.Map;

/**
 * マップなどで構成されたデータを横断します。
 */
public class TravUtil {
	/**
	 * TravUtil.traversal() で横断するエージェントです。
	 * 構造を解析した結果をイベントとして受け取ります。
	 */
	public abstract static class Traveler {
		/**
		 * null を発見した時に呼ばれる。
		 */
		public abstract void detectNull();

		/**
		 * String を発見した時に呼ばれる。
		 * @param string 発見した値
		 */
		public abstract void detectString(String string);

		/**
		 * Number を発見した時に呼ばれる。
		 * @param number 発見した値
		 */
		public abstract void detectNumber(Number number);

		/**
		 * Boolean を発見した時に呼ばれる。
		 * @param bool 発見した値
		 */
		public abstract void detectBoolean(Boolean bool);

		/**
		 * マップエントリを発見した時に呼ばれる。
		 * valueも横断したい場合には traversal(value) を内部で呼ぶこと。
		 * @param key 発見したキー
		 * @param value 発見した値
		 */
		public abstract void detectMapEntryKey(Object key, Object value);

		/**
		 * Object を発見した時に呼ばれる。<br/>
		 * というより、判断不能なインスタンスを発見した時に呼ばれる。
		 * @param value 発見した値
		 */
		public abstract void detectObject(Object value);

		/**
		 * 配列の先頭を発見した時に呼ばれる。
		 * @param object 発見した値
		 */
		public abstract void beginArray(Object object);

		/**
		 * 配列の継続を発見した時に呼ばれる。
		 * @param object 発見した値
		 */
		public abstract void continueArray(Object object);

		/**
		 * 配列の末尾を発見した時に呼ばれる。
		 * @param object 発見した値
		 */
		public abstract void endArray(Object object);

		/**
		 * マップデータの先頭を発見した時に呼ばれる。
		 * @param object 発見した値
		 */
		public abstract void beginMap(Object object);

		/**
		 * マップデータの継続を発見した時に呼ばれる。
		 * @param object 発見した値
		 */
		public abstract void continueMap(Object object);

		/**
		 * マップデータの末尾を発見した時に呼ばれる。
		 * @param object 発見した値
		 */
		public abstract void endMap(Object object);

		/**
		 * マップエントリのvalueを処理するためのもの。
		 * @param source マップエントリの値
		 */
		protected void traversal(Object source) {
			TravUtil.traversal(this, source);
		}
	}

	/**
	 * マップなどで構成されたデータを横断します。<br/>
	 * Collection, Iterable は全て配列としてエージェントに通知します。
	 * @param traveler 横断するエージェント
	 * @param source データ
	 */
	public static void traversal(Traveler traveler, Object source) {
		String result = null;
		boolean cont = false;
		if (source == null) {
			traveler.detectNull();

		} else if (source instanceof String) {
			traveler.detectString((String) source);

		} else if (source instanceof Number) {
			traveler.detectNumber((Number) source);

		} else if (source instanceof Boolean) {
			result = source.toString();
			traveler.detectBoolean((Boolean) source);

		} else if (source.getClass().isArray()) {
			traveler.beginArray(source);
			for (Object object : (Object[]) source) {
				if (cont) {
					traveler.continueArray(source);
				}
				cont = true;
				traversal(traveler, object);
			}
			traveler.endArray(source);

		} else if (source instanceof Collection) {
			traveler.beginArray(source);
			for (Object element : (Collection<?>) source) {
				if (cont) {
					traveler.continueArray(source);
				}
				cont = true;
				traversal(traveler, element);
			}
			traveler.endArray(source);

		} else if (source instanceof Iterable) {
			traveler.beginArray(source);
			for (Object element : (Iterable<?>) source) {
				if (cont) {
					traveler.continueArray(source);
				}
				cont = true;
				traversal(traveler, element);
			}
			traveler.endArray(source);

		} else if (source instanceof Map) {
			traveler.beginMap(source);
			for (Map.Entry<?, ?> entry : ((Map<?, ?>) source).entrySet()) {
				if (cont) {
					traveler.continueMap(source);
				}
				cont = true;
				traveler.detectMapEntryKey(entry.getKey(), entry.getValue());
			}
			traveler.endMap(source);

		} else {
			traveler.detectObject(source);

		}
	}
}
