package util.mutter;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import util.mutter.util.StringUtil;

/**
 * メッセージです。
 * 座席で、もしくは連絡通路を介しての転送により生成されます。
 */
public final class Message {
	/**
	 * メッセージの種別。<br/>
	 * 一般的知識として、列挙と文字列の相互変換は
	 * <pre>
	 * MessageType messageType = xxxx;
	 * String messageTypeName = messageType.name();
	 * messageType = MessageType.valueOf(MessageType.class, messageTypeName);
	 * </pre>
	 * とする。
	 */
	public static enum MessageType {
		/**
		 * つぶやき
		 */
		MUTTER,
		/**
		 * 全エンドポイントにつぶやきをプッシュ
		 */
		MUTTERALL,
		/**
		 * フォロー登録要請
		 */
		FOLLOW,
		/**
		 * フォロー解除要請
		 */
		UNFOLLOW;
	}
	/**
	 * メッセージを生成します
	 * @param messageType メッセージの種別
	 * @param roomName 領域名
	 * @param serial ユニークID。ユニークIDはメッセージを作成した部屋のインスタンスが供給します。 {@link util.mutter.Room#createSerial() createSerial()}
	 * @param fromSeatName 発信元の座席名
	 * @param toSeatName 受信先の座席名。フォロー要請・解除以外では空文字列です。
	 * @param value データ
	 * @param cause 元となったメッセージ
	 * @param local 部屋に閉じたメッセージの場合にはtrue
	 */
	protected Message(MessageType messageType, String roomName, String serial
		, String fromSeatName, String toSeatName
		, Object value, Message cause
		, boolean local) {
		this.messageType = messageType;
		this.roomName = StringUtil.rtrim(roomName);
		this.serial = StringUtil.rtrim(serial);
		this.fromSeatName = StringUtil.rtrim(fromSeatName);
		this.toSeatName = StringUtil.rtrim(toSeatName);
		this.value = value;
		this.cause = cause;
		this.local = local;
	}

	private MessageType messageType;
	private String roomName;
	private String serial;
	private String fromSeatName;
	private String toSeatName;
	private Object value;
	private Message cause;
	private boolean local;
	/**
	 * メッセージの種別を返します。
	 * @return メッセージ種別
	 */
	public MessageType getMessageType() {
		return messageType;
	}
	/**
	 * 領域IDを返します。
	 * @return 領域ID
	 */
	public String getRoomName() {
		return roomName;
	}
	/**
	 * メッセージに割り当てられたユニークIDを返します。
	 * @return ユニークID
	 */
	public String getSerial() {
		return serial;
	}
	/**
	 * 発信元の座席の識別名を返します。
	 * @return 発信元の座席の識別名
	 */
	public String getFromSeatName() {
		return fromSeatName;
	}
	/**
	 * 送信先として指定された座席の識別名を返します。フォロー要請・解除以外ではnullです。
	 * @return to 送信先の座席の識別名
	 */
	public String getToSeatName() {
		return toSeatName;
	}
	/**
	 * タイムスタンプを返します。
	 * @return 生成日時
	 */
	public Date getDate() {
		return new Date(Long.valueOf(serial, 16));
	}
	/**
	 * データを返します。
	 * @return データ
	 */
	public Object getValue() {
		return value;
	}
	/**
	 * メッセージの元となったメッセージを返します。
	 * @return 元となったメッセージ
	 */
	public Message getCause() {
		return cause;
	}
	/**
	 * メッセージの通知範囲を判定します。
	 * @return 部屋内限定のメッセージの場合にはtrueを返す
	 */
	public boolean isLocalOnly() {
		return local;
	}
	@Override
	public String toString() {
		DateFormat datetime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSSZ", Locale.JAPAN);
		PrintWriter out = StringUtil.getStringPrintWriter();
		Message msg = this;
		String pref = "";
		while (msg!=null) {
			out.println(pref + msg.getMessageType() + (local?" (local)": "")
				+ " [" + msg.getRoomName() + "@" + msg.getSerial() + "] from:" + msg.getFromSeatName()
				+ " to:" + msg.getToSeatName() + ", date:" + datetime.format(msg.getDate()) );
			out.println("\tvalue:" + msg.value );
			pref = "caused by ";
			msg = msg.cause;
		}
		return out.toString();
	}
}