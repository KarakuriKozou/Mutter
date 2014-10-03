package util.mutter.sample;

import util.mutter.Message;
import util.mutter.Seat;
import util.mutter.ext.EventDrivenUser;
import util.mutter.ext.MessageListener;
import util.mutter.util.TestUtil;

/**
 * テストに使用する利用者サンプル。
 */
public class TestUser extends EventDrivenUser {
	private String name;
	/**
	 * コンストラクタ
	 * @param name 利用者名
	 */
	public TestUser(final String name) {
		this.name = name;
		setMessageListener(new MessageListener() {
			@Override
			public void followFrom(Message message) {
				String from = message.getFromSeatName();
				String to = message.getToSeatName();
				System.out.println("自席 " + to + " が " + from + " からフォローされた。");
			}
			@Override
			public void followTo(Message message) {
				String seatname = getSeat().getName();
				String to = message.getToSeatName();
				System.out.println("別の部屋の自席 " + seatname + " の利用者 "+ name + " が " + to + " をフォローした。");
			}
			@Override
			public void mutterFrom(Message message) {
				String from = message.getFromSeatName();
				System.out.println(from + " の利用者がつぶやいた。");
			}
			@Override
			public void mutterTo(Message message) {
				String seatname = getSeat().getName();
				System.out.println("別の部屋の自席 " + seatname + " の利用者 "+ name + " がつぶやいた。");
			}
			@Override
			public void mutterAll(Message message) {
				String from = message.getFromSeatName();
				System.out.println(from + " がブロードキャストでつぶやいた。");
			}
			@Override
			public void unfollowFrom(Message message) {
				String from = message.getFromSeatName();
				String to = message.getToSeatName();
				System.out.println("自席 " + to + " が " + from + " からフォロー解除された。");
			}
			@Override
			public void unfollowTo(Message message) {
				String to = message.getToSeatName();
				String seatname = getSeat().getName();
				System.out.println("別の部屋の自席 " + seatname + " の利用者が " + to + " をフォロー解除した。");
			}
		});
	}
	@Override
	public void received(Message message) {
		count++;
		System.out.println("--------------------");
		System.out.println(toString() + " receive " + count);
		System.out.println(message);
		super.received(message);
		TestUtil.waitMilli(this, 10);
	}
	@Override
	public String toString() {
		if (this.getSeat()==null) {
			return name + " in " + super.toString();
		} else {
			return name + " in " + this.getSeat().getRoom().getName() + "." + this.getSeat().getName() + " " + super.toString();
		}
	}
	int count = 0;
	/**
	 * メッセージの受信回数を返します。
	 * @return メッセージの受信回数
	 */
	public int getCount() {
		return count;
	}
	@Override
	protected void standUp(Seat mutter) throws Exception {
		if (standuperror) {
			throw new Exception("test error");
		}
		super.standUp(mutter);
	};
	boolean standuperror = false;
	/**
	 * 離席時に例外を発生させるかの設定を行います。
	 * @param enabled 離席時に例外を発生させるようにするにはtrue。設定解除にはfalse。
	 */
	public void setStamdupError(boolean enabled) {
		standuperror = enabled;
	}
}
