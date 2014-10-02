package util.mutter.ext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import util.mutter.Message;
import util.mutter.Message.MessageType;
import util.mutter.PassageWay;

/**
 * Java の直列化機能を使用した、廊下の基底クラスです。<br/>
 * あくまで json など直列化を実装する場合の参考実装です。<br/>
 * オブジェクトのバージョン機構の利用はして「いないはず」です。<br/>
 * 適合するバージョンを選択するのは使う方の責務であり権利だと考えます。
 */
public abstract class SerializePassageWay extends PassageWay {
	@Override
	public void pickup(Message message) throws Throwable {
		if (message!=null) {
			sender(encodeMessage(message));
		}
	}

	/**
	 * リモートに転送する処理を実装するためのメソッドです。
	 * @param bytes バイトデータ
	 * @throws Throwable
	 */
	protected abstract void sender(byte[] bytes) throws Throwable;

	/**
	 * リモートからのバイトデータをローカルのメッセージとして送り出すためのメソッドです。<br/>
	 * 受信スレッドなどから呼び出されるためのメソッドです。
	 * @param bytes
	 * @throws Throwable
	 */
	protected void receive(byte[] bytes) throws Throwable {
		if (bytes.length>0) {
			send(decodeMessage(bytes));
		}
	}

	/**
	 * メッセージのエンコード
	 * @param message メッセージ
	 * @return バイトデータ
	 */
	protected byte[] encodeMessage(Message message) {
		if (message==null) {
			return new byte[0];
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeUTF(message.getMessageType().name());
			oos.writeUTF(message.getRoomName());
			oos.writeUTF(message.getSerial());
			oos.writeUTF(message.getFromSeatName());
			oos.writeUTF(message.getToSeatName());
//			oos.writeObject(message.getDate());
			oos.writeObject(message.getValue());
			oos.writeObject(encodeMessage(message.getCause()));
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}
		return bos.toByteArray();
	}
	/**
	 * メッセージのデコード
	 * @param bytes バイトデータ
	 * @return メッセージ
	 */
	protected Message decodeMessage(byte[] bytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		try {
			ObjectInputStream ois = new ObjectInputStream(bis);
			String messageTypeName = ois.readUTF();
			MessageType messageType = MessageType.valueOf(MessageType.class, messageTypeName);
			String roomName = ois.readUTF();
			String serial = ois.readUTF();
			String fromSeatName = ois.readUTF();
			String toSeatName = ois.readUTF();
//			Date date = (Date)ois.readObject();
			Object value = ois.readObject();
			byte[] causebytes = (byte[]) ois.readObject();
			Message cause = null;
			if (causebytes!=null && causebytes.length!=0) {
				cause = decodeMessage(causebytes);
			}
			return buildMessage(messageType, roomName, serial, fromSeatName, toSeatName, value, cause);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
