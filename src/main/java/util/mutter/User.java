package util.mutter;

import java.util.LinkedList;

/**
 * 利用者の基底クラスです。<br/>
 * つぶやき通信はこれを継承したインスタンスに処理を委譲することで実行します。<br/>
 * receive() は送信側のスレッドにおいて実行されることに注意してください。<br/>
 * 具体的には、必要に応じてですが更に別のキューに繋ぎ、処理用のスレッドで処理を行うなどの対策です。
 * 参照：
 * {@link util.mutter.Seat#sitDown(User) Seat.sitDown()}, {@link util.mutter.Seat#standUp(User) Seat.standUp()}
 */
public abstract class User {
//	/**
//	 * 例外を送出したくはないが、記録に残したいためのロガー
//	 */
//	protected Logger logger = Logger.getLogger(this.getClass().toString());
	/**
	 * 受信したつぶやきを一件づつ処理するメソッドを実装します。<br/>
	 * 呼び出される順序は到着順で、スレッドで同時に呼び出されることはありません。<br/>
	 * フォロー登録、フォロー解除の要請も受信しますのでメッセージ種別でのハンドリングが必要です。
	 * @param message 受信したメッセージ
	 */
	public abstract void received(Message message);

	/**
	 * つぶやきをバッファリングするキューです。
	 */
	protected LinkedList<Message> queue = new LinkedList<Message>();
	/**
	 * 再帰的に同じつぶやきを受信した時に排除するチェッカーです。
	 */
//	private LinkedHashSet<Message> checker = new LinkedHashSet<Message>();

	/**
	 * つぶやきを受信した時に座席から自動的に呼び出されます。<br/>
	 * このメソッドでは受信したつぶやきを同期化のためバッファリングします。<br/>
	 * そして、abstractであるreceived()を順番に呼び出します。<br/>
	 * 利用者のサブクラスを実装する時にはreceive()の方をオーバーライドしなければなりません。
	 * @param message 受信したメッセージ
	 */
	protected void push(Message message) {
		synchronized (queue) {
//			if (checker.contains(message)) {
//				// 何かの原因で再帰的に呼び出されている
//				PrintWriter out = StringUtil.getStringPrintWriter();
//				out.println("message do not reentrant : " + toString());
//				out.println("\tmessage:" + message.toString());
//				String msg = out.toString();
//				// 異常とみなしたいけど
//				logger.severe(msg);
//				return;
//			} else {
//				checker.add(message);
//			}
			queue.add(message);
			if (queue.size()>1) {
				// 処理中
				return;
			}
		}
		while (message!=null) {
			received(message);
			synchronized (queue) {
//				checker.remove(message);
				queue.removeFirst();
				message = queue.peekFirst();
			}
		}
	}

	/**
	 * つぶやきます。<br/>
	 * このメッセージは自分自身には届きませんが、別の部屋の同一席には届きます。
	 * @param value データ
	 * @param cause 元となったメッセージ。自発的なメッセージの場合にはnullを指定。
	 * @throws Exception 着席していない利用者でつぶやこうとした場合
	 */
	public void mutter(Object value, Message cause) throws Exception {
		if (seat==null) {
			throw new Exception("missing endpoint.");
		}
		seat.mutter(value, cause);
	}
	/**
	 * 部屋限定でつぶやきます。<br/>
	 * このメッセージは自分自身には届きません。
	 * @param value データ
	 * @param cause 元となったメッセージ。自発的なメッセージの場合にはnullを指定。
	 * @throws Exception 着席していない利用者でつぶやこうとした場合
	 */
	public void mutterLocal(Object value, Message cause) throws Exception {
		if (seat==null) {
			throw new Exception("missing endpoint.");
		}
		seat.mutterLocal(value, cause);
	}
	/**
	 * 受取先無制限（フォローされていない座席にも届く）でつぶやきます。<br/>
	 * このメッセージは自分にも届きます。<br/>
	 * 一般的なブロードキャストなどと同様に、連鎖すればストームを引き起こすことに留意してください。
	 * @param value データ
	 * @param cause 元となったメッセージ。自発的なメッセージの場合にはnullを指定。
	 * @throws Exception 着席していない利用者でつぶやこうとした場合
	 */
	public void mutterAll(Object value, Message cause) throws Exception {
		if (seat==null) {
			throw new Exception("missing endpoint.");
		}
		seat.mutterAll(value, cause);
	}
	/**
	 * 部屋限定で受取先無制限（フォローされていない座席にも届く）でつぶやきます。<br/>
	 * このメッセージは自分にも届きます。<br/>
	 * 一般的なブロードキャストなどと同様に、連鎖すればストームを引き起こすことに留意してください。
	 * @param value データ
	 * @param cause 元となったメッセージ。自発的なメッセージの場合にはnullを指定。
	 * @throws Exception 着席していない利用者でつぶやこうとした場合
	 */
	public void mutterLocalAll(Object value, Message cause) throws Exception {
		if (seat==null) {
			throw new Exception("missing endpoint.");
		}
		seat.mutterLocalAll(value, cause);
	}
	/**
	 * 対象のフォロー（つぶやきに聞き耳を立てる）を開始します
	 * @param target 対象エンドポイント識別名
	 * @throws Exception 着席していない利用者でつぶやこうとした場合
	 */
	public void follow(String target) throws Exception {
		if (seat==null) {
			throw new Exception("missing endpoint.");
		}
		seat.follow(target);
	}
	/**
	 * 対象のフォローを解除します。
	 * @param target 対象座席識別名
	 * @throws Exception 着席していない利用者でつぶやこうとした場合
	 */
	public void unfollow(String target) throws Exception {
		if (seat==null) {
			throw new Exception("missing endpoint.");
		}
		seat.unfollow(target);
	}

	/**
	 * 座っている座席を保持します。
	 */
	protected Seat seat;
	/**
	 * 座っている座席を返します。
	 * @return 座席
	 */
	public Seat getSeat() {
		return seat;
	}
	/**
	 * 実際に着席します。<br/>
	 * このメソッドは座席にこの利用者が登録された際に自動的に呼ばれます。<br/>
	 * このメソッドをオーバーライドすることで、着席のタイミングを取得できます。
	 * @param seat 登録した座席
	 * @throws Exception 別の座席に着席している場合
	 */
	protected void sitDown(Seat seat) throws Exception {
		if (this.seat!=null) {
			throw new Exception("already sit down other seat.");
		}
		this.seat = seat;
	}
	/**
	 * 実際に離席します。<br/>
	 * このメソッドは座席からこの利用者が登録解除された際に自動的に呼ばれます。<br/>
	 * このメソッドをオーバーライドすることで、離席のタイミングを取得できます。
	 * @param mutter 登録解除された座席
	 * @throws Exception オーバーライドしたメソッド用
	 */
	protected void standUp(Seat mutter) throws Exception {
		this.seat = null;
	}

	@Override
	public String toString() {
		if (this.seat==null) {
			return "[]";
		} else {
			return "[" + this.seat.getName() + "]";
		}
	}
}