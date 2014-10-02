package util.mutter.sample;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import util.mutter.Message;
import util.mutter.ext.InternalPassageWay;

/**
 * テストに使用する廊下サンプル。
 */
public class TestInternalPassageWay extends InternalPassageWay {
	/**
	 * テスト用にnullを送るスイッチ
	 * @throws Throwable
	 */
	public void sendnull() throws Throwable {
		super.send(null);
	}
	@Override
	public void pickup(Message message) throws Throwable {
		if (false==processerror) {
			super.pickup(message);
			return;
		}
		throw new Throwable();
	}
	@Override
	protected byte[] encodeMessage(Message message) {
		if (false==decodeerror) {
			return super.encodeMessage(message);
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
			// invalid object
			oos.writeObject(new byte[] {-1,-2,-3,-4});
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}
		return bos.toByteArray();
	}
	boolean decodeerror = false;
	/**
	 * デコード時に例外を発生させます。
	 * @param enabled 例外を起こしたい場合にはtrue。
	 */
	public void setDecodeError(boolean enabled) {
		decodeerror = enabled;
	}
	boolean processerror = false;
	/**
	 * process()時に例外を発生させます。
	 * @param enabled 例外を起こしたい場合にはtrue。
	 */
	public void setProcessError(boolean enabled) {
		processerror = enabled;
	}
}
