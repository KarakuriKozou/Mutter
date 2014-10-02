package util.mutter.sample;

import util.mutter.Message;
import util.mutter.Microphone;

/**
 * テストに使用する盗聴器サンプル。
 */
public class TestMicrophone implements Microphone {
	String name;
	/**
	 * コンストラクタ
	 * @param name 盗聴器名
	 */
	public TestMicrophone(String name) {
		this.name = name;
	}
	@Override
	public void pickup(Message message) throws Throwable {
		System.out.println("----------------------------------------");
		System.out.println("+++++ " + name + " pickup");
		System.out.println(message);
		if (pickuperror) {
			throw new Throwable("test error");
		}
	}
	boolean pickuperror = false;
	/**
	 * 受信時に例外を発生させるかの設定を行います。
	 * @param enabled 受信時に例外を発生させるようにするにはtrue。設定解除にはfalse。
	 */
	public void setPickupError(boolean enabled) {
		pickuperror = enabled;
	}
}
