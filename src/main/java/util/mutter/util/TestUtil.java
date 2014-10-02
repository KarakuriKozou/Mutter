package util.mutter.util;

import java.io.PrintStream;
import java.util.TreeMap;
import java.util.TreeSet;

import util.mutter.Room;

/**
 * デバッグ用のユーティリティです。
 */
public final class TestUtil {
	private TestUtil() {}
	static { new TestUtil(); }
	/**
	 * 相互フォロー状態のダンプ
	 * @param out ダンプ出力先
	 */
	public static void dumpStatics(PrintStream out) {
		out.println("========================================");
		for (String roomName : Room.listRoomNames()) {
			System.out.println("Room : " + roomName);
			Room room = Room.getRoom(roomName);
			// どの席がどの席からフォローされているか
			TreeMap<String, TreeSet<String>> followFrom = new TreeMap<String, TreeSet<String>>();
			// どの席がどの席をフォローしているか
			TreeMap<String, TreeSet<String>> followTo = new TreeMap<String, TreeSet<String>>();
			for (String seatName : room.listSeatNames()) {
				followFrom.put(seatName, new TreeSet<String>());
				followTo.put(seatName, new TreeSet<String>());
			}
			for (String seatName : room.listSeatNames()) {
				for (String followerName : room.listFollowerNames(seatName)) {
					// どの席がどの席からフォローされているか
					followFrom.get(seatName).add(followerName);
					// どの席がどの席をフォローしているか
					followTo.get(followerName).add(seatName);
				}
			}
			for (String seatName : room.listSeatNames()) {
				String from = ArrayUtil.join(followFrom.get(seatName).toArray(), ", ");
				String to = ArrayUtil.join(followTo.get(seatName).toArray(), ", ");
				from = from.length()==0 ? "(empty)" : from;
				to = to.length()==0 ? "(empty)" : to;
				String used = room.getSeat(seatName).isVacant() ? "(vacant)" : "(used)";
				out.println("Seat : " + seatName + " " + used + ", from : " + from + ", to : " + to);
			}
			out.println("----------");
		}
	}
	/**
	 * 一定時間待機する。
	 * @param monitor モニタ
	 * @param millisecond 待機時間(ms)
	 */
	public static void waitMilli(Object monitor, long millisecond) {
		synchronized (monitor) {
			try {
				monitor.wait(millisecond);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
