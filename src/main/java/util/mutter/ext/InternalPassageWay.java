package util.mutter.ext;


/**
 * 同一のjvm内での廊下のサンプルです。<br/>
 * TCP/IPなどによる通信を使用する場合のサンプルとしてご利用ください。<br/>
 * {@link util.mutter.ext.SerializePassageWay#pickup(util.mutter.Message) SerializePassageWay.pickup()}
 * や
 * {@link util.mutter.PassageWay#send(util.mutter.Message) PassageWay.send()}を更にオーバーライドすればフィルタ機能を実装することもできます。
 */
public class InternalPassageWay extends SerializePassageWay {
	InternalPassageWay pair;
	/**
	 * 相手の連絡通路に接続。
	 * @param pair
	 */
	public void connect(InternalPassageWay pair) {
		this.pair = pair;
	}
	@Override
	protected void sender(byte[] bytes) throws Throwable {
		// シリアライズされたデータを相手に渡す通信
		pair.receive(bytes);
	}
}
