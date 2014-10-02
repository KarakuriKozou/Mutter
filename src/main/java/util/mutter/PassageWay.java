package util.mutter;

import util.mutter.Message.MessageType;


/**
 * 部屋と部屋の間でつぶやきを共有するための廊下の雛型
 * （遠隔システム間でつぶやきを共有するためのブリッジ機構の基底クラス）
 * です。<br/>
 * 部屋の中でのメッセージは逐一send()に渡されます。<br/>
 * 単にブリッジする目的だけではなくフィルタしたり加工したり一対多に接続させることも可能です。<br/>
 * <br/>
 * ただし、場合によっては利用者から別の利用者に廊下を介さずにチクる方が簡単な場合もあります。<br/>
 * 参照：
 * {@link util.mutter.Room#addPassageWay(PassageWay) Room.registerPassageWay()}
 */
public abstract class PassageWay {
	/**
	 * 設置されている部屋。
	 */
	protected Room room;
	/**
	 * 設置されている部屋を返します。
	 * @return 部屋
	 */
	public Room getRoom() {
		return room;
	}
	/**
	 * 部屋を設定します。<br/>
	 * {@link util.mutter.Room#addPassageWay(PassageWay) Room.addPassageWay()}
	 * 、
	 * {@link util.mutter.Room#removePassageWay(PassageWay) Room.removePassageWay()}
	 * を実行した時に自動的に呼ばれます。
	 * @param room 設置する部屋
	 */
	protected void setRoom(Room room) {
		this.room = room;
	}

	/**
	 * 部屋に流れるメッセージを処理するために実装するメソッドです。
	 * @param message メッセージ
	 * @throws Throwable 廊下の処理例外
	 */
	public abstract void pickup(Message message) throws Throwable;
	/**
	 * リモートから送られてきたデータに基いてメッセージを構築する場合に使用します。<br/>
	 * cause を構築する場合にも使用しますので send() と分離してあります。
	 * @param messageType メッセージの種別
	 * @param roomName 領域名
	 * @param serial ユニークID
	 * @param fromSeatName 発信元のエンドポイント
	 * @param toSeatName 受信先のエンドポイント
	 * @param value データ
	 * @param cause 元となったメッセージ
	 * @return 構築されたメッセージ
	 */
	protected Message buildMessage(MessageType messageType, String roomName, String serial, String fromSeatName, String toSeatName, Object value, Message cause) {
		return new Message(messageType, roomName, serial, fromSeatName, toSeatName, value, cause, false);
	}
	/**
	 * 部屋に新たにメッセージを送るためのメソッドです。<br/>
	 * {@link Room#send(Message)}がアクセスを限定しているため、廊下の実装ではこれを使用します。
	 * @param message メッセージ
	 * @throws Throwable 例外が発生
	 */
	protected void send(Message message) throws Throwable {
		if (message!=null) {
			room.send(message);
		}
	}
}