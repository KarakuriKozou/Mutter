package util.mutter.ext;

import util.mutter.Message;

/**
 * MessageListener をデフォルト実装したもの。
 */
public class MessageAdapter implements MessageListener {
	@Override
	public void followFrom(Message message) {}
	@Override
	public void followTo(Message message) {}
	@Override
	public void mutterFrom(Message message) {}
	@Override
	public void mutterTo(Message message) {}
	@Override
	public void mutterAll(Message message) {}
	@Override
	public void unfollowFrom(Message message) {}
	@Override
	public void unfollowTo(Message message) {}
}
