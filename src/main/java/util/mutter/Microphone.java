package util.mutter;

/**
 * 部屋のメッセージを拾う盗聴器です。<br/>
 * 盗聴器は部屋に仕掛けます。<br/>
 * 参照：
 * {@link util.mutter.Room#addMicrophone(Microphone) Room.addMicrophone()}
 */
public interface Microphone {
	/**
	 * メッセージが発生すると呼ばれます。
	 * @param message メッセージ
	 * @throws Throwable
	 */
	void pickup(Message message) throws Throwable;
}
