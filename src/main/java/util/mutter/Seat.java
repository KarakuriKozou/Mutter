package util.mutter;

import java.util.Set;

/**
 * 部屋内に生成される座席です。
 * 実際につぶやく利用者が不在でも座席さえあれば通信は成立します。<br/>
 * また、他の部屋の自分の座席との暗黙のフォロー設定がされます。<br/>
 * そのため、廊下で接続された別の部屋の同じ座席名の座席からもつぶやきが届きます。<br/>
 * ただし、全くの自分自身のつぶやきは自分には配達されませんし、フォロー設定の一覧にも現れません。<br/>
 * 自己への暗黙フォロー設定を操作しようとした場合、操作は無視されます。<br/>
 * 参照：
 * {@link util.mutter.Room#getSeat(String) Room.getSeat()}
 */
public final class Seat {
	/**
	 * 座席を生成します。
	 * @param name 座席の識別名
	 * @param room 座席の生成された部屋
	 */
	protected Seat(String name, Room room) {
		this.room = room;
		this.name = name;
	}

	private String name;
	/**
	 * 座席の識別名を返します。
	 * @return 識別名
	 */
	public String getName() {
		return name;
	}

	private Room room;
	/**
	 * 部屋を返します。
	 * @return 部屋
	 */
	public Room getRoom() {
		return room;
	}

	/**
	 * 座席に座っている利用者。
	 */
	private User user;
	/**
	 * 空席かを問い合わせます。
	 * @return 空席の場合はtrueを返します。
	 */
	public boolean isVacant() {
		return user==null;
	}
	/**
	 * 座席に座っている利用者を返します。
	 * @return 座席に座っている利用者。
	 */
	public User getUser() {
		return user;
	}
	/**
	 * 利用者を座席に座らせる際に利用します。
	 * @param user 着席させる利用者。
	 * @throws Exception 他の利用者が座っている座席に座ろうとしたなど
	 */
	public void sitDown(User user) throws Exception {
		if (user==null) {
			// 利用者としてnullが指定された
			throw new Exception("sit down ghost.(user is null)");
		}
		if (this.user!=null) {
			if (user.equals(this.user)) {
				// 二度座りは大目に見る
				return;
			} else {
				// 別の利用者が着席している
				throw new Exception("A different user sits at the seat.");
			}
		}
		try {
			this.user = user;
			this.user.sitDown(this);
		} catch (Exception ex) {
			this.user = null;
			throw new Exception("sit down user faild.", ex);
		}
	}
	/**
	 * 利用者を離席させる際に利用します。
	 * @param user 離席させる利用者
	 * @throws Exception 別の利用者が着席している。<br/>
	 * 誰も着席していない。<br/>
	 * 利用者は別の席に着席している。<br/>
	 * などの場合。
	 */
	public void standUp(User user) throws Exception {
		if (this.user==null) {
			// 誰も着席していない
			throw new Exception("Nobody sits at the seat.");
		}
		if (false == this.user.equals(user)) {
			// 別の利用者が着席している
			throw new Exception("A different user sits at the seat.");
		}
		try {
			this.user.standUp(this);
			this.user = null;
		} catch (Exception ex) {
			throw new Exception("stand up user faild.", ex);
		}
	}

	/**
	 * フォロワー（自分のつぶやきに聞き耳を立てている座席）の一覧を得ます。
	 * @return 座席一覧
	 */
	public Set<Seat> listFollowers() {
		return room.listFollowers(this.getName());
	}

	/**
	 * メッセージをフォワードしてつぶやきます。
	 * @param value データ
	 * @param cause 元となったメッセージ
	 */
	public void mutter(Object value, Message cause) {
		room.send(new Message(Message.MessageType.MUTTER
			, room.getName(), room.createSerial(), this.getName(), null
			, value, cause, false));
	}
	/**
	 * メッセージをフォワードして受取先無制限でつぶやきます。<br/>
	 * 一般的なブロードキャストなどと同様に、連鎖すればストームを引き起こすことに留意してください。
	 * @param value データ
	 * @param cause 元となったメッセージ
	 */
	public void mutterAll(Object value, Message cause) {
		room.send(new Message(Message.MessageType.MUTTERALL
			, room.getName(), room.createSerial(), this.getName(), null
			, value, cause, false));
	}
	/**
	 * 部屋限定でメッセージをフォワードしてつぶやきます。
	 * @param value データ
	 * @param cause 元となったメッセージ
	 */
	public void mutterLocal(Object value, Message cause) {
		room.send(new Message(Message.MessageType.MUTTER
			, room.getName(), room.createSerial(), this.getName(), null
			, value, cause, true));
	}
	/**
	 * 部屋限定でメッセージをフォワードして受取先無制限でつぶやきます。<br/>
	 * 一般的なブロードキャストなどと同様に、連鎖すればストームを引き起こすことに留意してください。
	 * @param value データ
	 * @param cause 元となったメッセージ
	 */
	public void mutterLocalAll(Object value, Message cause) {
		room.send(new Message(Message.MessageType.MUTTERALL
			, room.getName(), room.createSerial(), this.getName(), null
			, value, cause, true));
	}
	/**
	 * 対象のフォローを開始します。<br/>
	 * 逆に相手の座席に対して自席を指定することで無理やり送りつけることもできます。
	 * @param target 対象座席の識別名
	 */
	public void follow(String target) {
		room.send(new Message(Message.MessageType.FOLLOW
			, room.getName(), room.createSerial(), this.getName(), target
			, null, null, false));
	}
	/**
	 * 対象のフォローを解除します。
	 * 逆に相手の座席に対して自席を指定することで無理やり解除することもできます。
	 * @param target 対象座席の識別名
	 */
	public void unfollow(String target) {
		room.send(new Message(Message.MessageType.UNFOLLOW
			, room.getName(), room.createSerial(), this.getName(), target
			, null, null, false));
	}

	/**
	 * つぶやきなどのメッセージを受信した時に部屋から呼び出される処理です。<br/>
	 * フォローなどの通知も送られてきますのでメッセージ内容の確認は慎重に。<br/>
	 * 利用者に転送、利用者が居なければ読まずに捨てます。
	 * @param message メッセージ
	 */
	protected void receive(Message message) {
		if (user!=null) {
			user.push(message);
		}
	}
}