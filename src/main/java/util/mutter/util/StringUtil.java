package util.mutter.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.TreeMap;

import util.mutter.util.TravUtil.Traveler;

/**
 * 文字列操作ユーティリティ
 */
public final class StringUtil {
	static {
		new StringUtil();
	}
	private StringUtil() {}
	/**
	 * jsonで日付特例
	 */
	static final DateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSSZ");

	/**
	 * 末尾の空白を除去する。
	 * @param str 文字列
	 * @return 結果
	 */
	public static String rtrim(String str) {
		if (str==null) {
			return "";
		}
		return str.replaceAll("[ \t]+$", "");
	}
	/**
	 * strがnullか空文字列かを判定する。
	 * @param str 文字列
	 * @return nullか空文字列ならtrueを返す
	 */
	public static boolean isEmpty(String str) {
		return str==null || "".equals(str);
	}
	/**
	 * StringWriter を用いて入力をバッファリングし、toString() により書き込んだ全文を取得できる PrintWriter を取得します。
	 * @return ライター
	 */
	public static PrintWriter getStringPrintWriter() {
		final Writer writer = new StringWriter();
		return new PrintWriter(writer) {
			@Override
			public String toString() {
				return writer.toString();
			}
		};
	}
	/**
	 * 文字列末尾の改行コードだけを除去する。
	 * @param src 文字列
	 * @return EOL除去済みの文字列。
	 */
	public static String chop(String src) {
		if (src==null) {
			return "";
		}
		return src.replaceAll("[\\r\\n]+$", "");
	}

	/**
	 * json を編集するための Traveler
	 */
	static class JsonTraveler extends TravUtil.Traveler {
		StringBuilder sb = new StringBuilder();
		@Override
		public String toString() {
			return sb.toString();
		}
		@Override
		public void detectNull() {
			sb.append("null");
		}
		@Override
		public void detectString(String string) {
			sb.append("\"" + toJsonEncoding(string) + "\"");
		}
		@Override
		public void detectNumber(Number number) {
			sb.append(number.toString());
		}
		@Override
		public void detectBoolean(Boolean bool) {
			sb.append(bool.toString());
		}
		@Override
		public void detectMapEntryKey(Object key, Object value) {
			traversal(key.toString());
			sb.append(":");
			traversal(value);
		}
		@Override
		public void detectObject(Object value) {
			if (Date.class.isInstance(value)) {
				synchronized (dateformat) {
					sb.append("\"" + dateformat.format(value) + "\"");
				}
			} else {
				sb.append("\"" + value.toString() + "\"");
			}
		}
		@Override
		public void beginArray(Object object) {
			sb.append("[");
		}
		@Override
		public void continueArray(Object object) {
			sb.append(",");
		}
		@Override
		public void endArray(Object object) {
			sb.append("]");
		}
		@Override
		public void beginMap(Object object) {
			sb.append("{");
		}
		@Override
		public void continueMap(Object object) {
			sb.append(",");
		}
		@Override
		public void endMap(Object object) {
			sb.append("}");
		}
	}
	/**
	 * オブジェクトをJSONに変換します。
	 * けど、json-simpleとかjaksonとか使うほうが楽。
	 * 実行ファイルサイズ削減とかのニッチ用途。
	 * @param source オブジェクト
	 * @return JSON文字列
	 */
	public static String jsonEncode(Object source) {
		Traveler traveler = new JsonTraveler();
		TravUtil.traversal(traveler, source);
		return traveler.toString();
	}
	/**
	 * 通常の文字列をjsonエンコーディングする。
	 * @param string 通常の文字列
	 * @return jsonエンコーディング文字列
	 */
	public static String toJsonEncoding(String string) {
		String inner = (String)string;
		inner = inner.replace("\\", "\\\\");
		inner = inner.replace("\"", "\\\"");
		inner = inner.replace("\b", "\\b");
		inner = inner.replace("\f", "\\f");
		inner = inner.replace("\n", "\\n");
		inner = inner.replace("\r", "\\r");
		inner = inner.replace("\t", "\\t");
		return inner;
	}
	/**
	 * jsonエンコーディングされた文字列を通常の文字列にする。
	 * @param string jsonエンコーディング文字列
	 * @return 通常の文字列
	 */
	public static String fromJsonEncoding(String string) {
		String inner = (String)string;
		inner = inner.replace("\\\"", "\"");
		inner = inner.replace("\\b", "\b");
		inner = inner.replace("\\f", "\f");
		inner = inner.replace("\\n", "\n");
		inner = inner.replace("\\r", "\r");
		inner = inner.replace("\\t", "\t");
		inner = inner.replace("\\\\", "\\");
		return inner;
	}
	/**
	 * \エスケープを飛び越えて次の"の位置を探します。
	 * @param string 文字列
	 * @param fromIndex 開始位置。開始位置が"ならfromIndexが発見位置となります。
	 * @return "の位置。発見できなければ-1
	 */
	public static int indexOfStringEnd(String string, int fromIndex) {
		return indexOfStringEnd(string.toCharArray(), fromIndex);
	}
	/**
	 * \エスケープを飛び越えて次の"の位置を探します。
	 * @param chars 文字配列
	 * @param fromIndex 開始位置。開始位置が"ならfromIndexが発見位置となります。
	 * @return "の位置。発見できなければ-1
	 */
	public static int indexOfStringEnd(char[] chars, int fromIndex) {
		for (int i=fromIndex; i<chars.length; i++) {
			switch (chars[i]) {
			case '\"':
				return i;
			case '\\':
				// エスケープされた文字はスキップ
				i++;
			}
		}
		return -1;
	}
	/**
	 * 自前でjsonをオブジェクトにする。<br/>
	 * 間に合わせなので、どうしてもjacksonとかjson-simpleを使いたくない場合のみ使用のこと。
	 * @param string json
	 * @return プリミティブなとリストとマップで構成されたオブジェクト
	 * @throws ParseException 解析エラー
	 */
	public static Object jsonDecode(String string) throws ParseException {
		JsonParser tokenizer = new JsonParser(string);
		String token = tokenizer.next();
		if ("[".equals(token)) {
			return jsonDecodeArray(tokenizer);
		} else if ("{".equals(token)) {
			return jsonDecodeMap(tokenizer);
		}
		return jsonValueOf(token);
	}
	/**
	 * トークン化クラス
	 */
	private static class JsonParser {
		String string;
		char[] chars;
		int pos = 0;
		public JsonParser(String string) {
			this.string = string;
			this.chars = this.string.toCharArray();
		}
		public int getPos() {
			return pos;
		}
		public String next() throws ParseException {
			if (pos>=chars.length) {
				return null;
			}
			String res;

			// 先頭のチェック
			char ch = chars[pos];
			switch (ch) {
			case ' ':
			case '\t':
			case '\r':
			case '\n':
				pos++;
				return next();
			case ':':
			case '[':
			case ']':
			case '{':
			case '}':
			case ',':
				// 記号
				pos++;
				return "" + ch;
			case '\"':
				// 文字列の開始を検出したので終わりまでをトークンとして返す
				int last = indexOfStringEnd(chars, pos+1);
				if (last<0) {
					// "...<EOF>
					throw new ParseException("invalid end of text", chars.length);
				}
				res = string.substring(pos, last+1);
				pos = last+1;
				return res;
			}

			// 続きのチェック
			for (int i = pos+1; i<chars.length; i++) {
				ch = chars[i];
				switch (ch) {
				case ' ':
				case '\t':
				case '\r':
				case '\n':
				case ':':
				case '[':
				case ']':
				case '{':
				case '}':
				case ',':
				case '\"':
					// i 以前の文字列をトークンとして返す
					res = string.substring(pos, i);
					pos = i;
					return res;
				}
			}

			// 終わりに達した
			res = string.substring(pos, chars.length);
			pos = chars.length;
			return res;
		}
	}
	/**
	 * リスト構築
	 * @param tokenizer トークン化クラス
	 * @return 結果
	 * @throws ParseException 解析失敗
	 */
	private static LinkedList<Object> jsonDecodeArray(JsonParser tokenizer) throws ParseException {
		LinkedList<Object> list = new LinkedList<Object>();
		String token;
		while ((token=tokenizer.next())!=null) {
			if (token.equals("[")) {
				list.add(jsonDecodeArray(tokenizer));

			} else if (token.equals("]")) {
				return list;

			} else if (token.equals("{")) {
				list.add(jsonDecodeMap(tokenizer));

			} else if (token.equals("}")) {
				// [...}
				throw new ParseException("invalid map end", tokenizer.getPos());

			} else if (token.equals(",")) {
				// noop
				// ,,,, とか続いていた場合のチェック・・・は省略
				// 一個の , と判断することで簡略化
				// また、最後の , は無視

			} else {
				list.add(jsonValueOf(token));

			}
		}
		// [...<EOF>
		throw new ParseException("invalid end of text", tokenizer.getPos());
	}
	/**
	 * マップ構築
	 * @param tokenizer トークン化クラス
	 * @return 結果
	 * @throws ParseException 解析失敗
	 */
	private static TreeMap<String, Object> jsonDecodeMap(JsonParser tokenizer) throws ParseException {
		TreeMap<String, Object> map = new TreeMap<String, Object>();
		String token;
		String key = null;
		while ((token=tokenizer.next())!=null) {
			if (token.equals("[")) {
				if (key==null) {
					// {[
					throw new ParseException("key must string", tokenizer.getPos());
				}
				map.put(key, jsonDecodeArray(tokenizer));
				key = null;

			} else if (token.equals("]")) {
				// {...]
				throw new ParseException("invalid array end", tokenizer.getPos());

			} else if (token.equals("{")) {
				if (key==null) {
					// {{
					throw new ParseException("key must string", tokenizer.getPos());
				}
				map.put(key, jsonDecodeMap(tokenizer));
				key = null;

			} else if (token.equals(":")) {
				if (key==null) {
					// ,:xxx,
					throw new ParseException("empty key", tokenizer.getPos());
				}
				// :::: とか続いていた場合のチェック・・・は省略
				// 一個の : と判断することで簡略化

			} else if (token.equals(",")) {
				if (key!=null) {
					// ,xxx:,
					throw new ParseException("empty value", tokenizer.getPos());
				}
				// ,,,, とか続いていた場合のチェック・・・は省略
				// 一個の , と判断することで簡略化
				// また、最後の , は無視

			} else if (token.equals("}")) {
				if (key!=null) {
					// ,xxx:,
					throw new ParseException("empty value", tokenizer.getPos());
				}
				return map;

			} else {
				if (key==null) {
					key = jsonValueOf(token).toString();
				} else {
					map.put(key, jsonValueOf(token));
					key = null;
				}
			}
		}
		// {...<EOF>
		throw new ParseException("invalid end of text", tokenizer.getPos());
	}
	/**
	 * json要素のオブジェクト変換
	 * @param string json要素
	 * @return オブジェクト
	 */
	static Object jsonValueOf(String string) throws ParseException {
		// null
		if (string.equals("null")) {
			return null;
		}
		// Boolean
		if (string.equals("true") || string.equals("false")) {
			return Boolean.valueOf(string);
		}
		// String
		if (string.startsWith("\"")) {
			// 中身を切り出した文字列として返す
			String work = fromJsonEncoding(string);
			return work.substring(1, work.length()-1);
		}
		// Number
		try {
			return Long.valueOf(string);
		} catch (NumberFormatException ex) {
			// noop
		}
		try {
			return Double.valueOf(string);
		} catch (NumberFormatException ex) {
			// noop
		}
		// 解析不能
		throw new ParseException(string, 0);
	}
}
