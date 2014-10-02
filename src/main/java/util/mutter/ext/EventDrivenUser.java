package util.mutter.ext;

import util.mutter.Message;
import util.mutter.User;

/**
 * 受信イベントをリスナーで整理した利用者
 */
public class EventDrivenUser extends User {
	MessageListener listener;
	/**
	 * メッセージ受信イベントのリスナーを設定します。
	 * @param listener メッセージ受信イベントのリスナー
	 */
	public void setMessageListener(MessageListener listener) {
		this.listener = listener;
	}
	@Override
	public void received(Message message) {
		if (listener==null) {
			return;
		}
		String me = seat.getName();
		String from = message.getFromSeatName();
		String to = message.getToSeatName();
		switch (message.getMessageType()) {
		case FOLLOW:
			if (me.equals(to)) {
				listener.followFrom(message);
			} else {
				listener.followTo(message);
			}
			break;
		case MUTTER:
			if (me.equals(from)) {
				listener.mutterTo(message);
			} else {
				listener.mutterFrom(message);
			}
			break;
		case MUTTERALL:
			listener.mutterAll(message);
			break;
		case UNFOLLOW:
			if (me.equals(to)) {
				listener.unfollowFrom(message);
			} else {
				listener.unfollowTo(message);
			}
			break;
		}
	}
}
