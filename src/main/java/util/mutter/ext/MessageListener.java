package util.mutter.ext;

import util.mutter.Message;

/**
 * メッセージを定型的に解析した結果のリスナー。<br/>
 * 参照：
 * {@link util.mutter.ext.EventDrivenUser#setMessageListener(MessageListener)}
 */
public interface MessageListener {
	/**
	 * 自席がフォローされた。
	 * @param message メッセージ
	 */
	void followFrom(Message message);

	/**
	 * 別の部屋の自席の利用者がどこかの席をフォローした。
	 * @param message メッセージ
	 */
	void followTo(Message message);

	/**
	 * フォローした席の利用者がつぶやいた。
	 * @param message メッセージ
	 */
	void mutterFrom(Message message);

	/**
	 * 別の部屋の自席の利用者がつぶやいた。
	 * @param message メッセージ
	 */
	void mutterTo(Message message);

	/**
	 * どこかの利用者がブロードキャストでつぶやいた。
	 * @param message メッセージ
	 */
	void mutterAll(Message message);

	/**
	 * 自席がフォロー解除された。
	 * @param message メッセージ
	 */
	void unfollowFrom(Message message);

	/**
	 * 別の部屋の自席の利用者がどこかの席をフォロー解除した。
	 * @param message メッセージ
	 */
	void unfollowTo(Message message);
}
