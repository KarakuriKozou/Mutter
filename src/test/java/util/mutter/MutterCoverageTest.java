package util.mutter;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

import util.mutter.ext.EventDrivenUser;
import util.mutter.ext.MessageAdapter;
import util.mutter.sample.TestInternalPassageWay;
import util.mutter.sample.TestMicrophone;
import util.mutter.sample.TestUser;
import util.mutter.util.StringUtil;
import util.mutter.util.TestUtil;

/**
 * カバレッジによる安定化を目的としたテストコードです。
 */
public class MutterCoverageTest {

	static void checkFollow() {
		TestUtil.dumpStatics(System.out);
		for (String roomname : Room.listRoomNames()) {
			Room room = Room.getRoom(roomname);
			for (String name : room.listSeatNames()) {
				HashSet<String> names = new HashSet<String>(room.listFollowerNames(name));
				for (Seat seat : room.listFollowers(name)) {
//					System.out.println(roomname + " : " + name + " <- " + seat.getName());
					assertTrue(names.contains(seat.getName()));
					names.remove(seat.getName());
				}
				assertEquals(0, names.size());
			}
		}
	}

	/**
	 * テスト
	 * @throws Exception 異常時の例外
	 */
	@Test
	public void test() throws Exception {
		// coverage対策コード
		MessageAdapter a = new MessageAdapter();
		a.followFrom(null);
		a.followTo(null);
		a.mutterFrom(null);
		a.mutterTo(null);
		a.mutterAll(null);
		a.unfollowFrom(null);
		a.unfollowTo(null);
		EventDrivenUser e = new EventDrivenUser();
		e.received(null);

		// 部屋の準備
		Room room1 = Room.getRoom("room.1");
		Room room2 = Room.getRoom("room.2");
		assertEquals(room1, Room.getRoom(room1.getName()));
		assertEquals(room2, Room.getRoom(room2.getName()));
		assertEquals(Room.listRoomNames().size(), 2);
		// デバッグ用の盗聴器を設置
		TestMicrophone mic1 = new TestMicrophone("microphone.room.1");
		TestMicrophone mic2 = new TestMicrophone("microphone.room.2");
		assertEquals(0, room1.listMicrophones().size());
		room1.addMicrophone(mic1);
		assertEquals(1, room1.listMicrophones().size());
		room1.addMicrophone(mic1);
		assertEquals(1, room1.listMicrophones().size());
		room1.removeMicrophone(mic1);
		assertEquals(0, room1.listMicrophones().size());
		room1.addMicrophone(mic1);
		assertEquals(1, room1.listMicrophones().size());
		room2.addMicrophone(mic2);

		// 廊下1
		TestInternalPassageWay passageway1 = new TestInternalPassageWay();
		assertEquals(0, room1.listPassageWays().size());
		assertFalse(room1.listPassageWays().contains(passageway1));
		assertEquals(null, passageway1.getRoom());
		room1.addPassageWay(passageway1);
		assertEquals(1, room1.listPassageWays().size());
		assertTrue(room1.listPassageWays().contains(passageway1));
		assertEquals(room1, passageway1.getRoom());

		// 廊下の撤去
		room1.removePassageWay(passageway1);
		assertEquals(0, room1.listPassageWays().size());
		assertFalse(room1.listPassageWays().contains(passageway1));
		assertEquals(null, passageway1.getRoom());
		room1.addPassageWay(passageway1);
		assertEquals(1, room1.listPassageWays().size());
		assertTrue(room1.listPassageWays().contains(passageway1));
		assertEquals(room1, passageway1.getRoom());

		// 廊下2
		TestInternalPassageWay passageway2 = new TestInternalPassageWay();
		assertEquals(0, room2.listPassageWays().size());
		assertFalse(room2.listPassageWays().contains(passageway2));
		assertEquals(null, passageway2.getRoom());
		room2.addPassageWay(passageway2);
		assertEquals(1, room2.listPassageWays().size());
		assertTrue(room2.listPassageWays().contains(passageway2));
		assertEquals(room2, passageway2.getRoom());

		// 廊下の接続
		passageway1.connect(passageway2);
		passageway2.connect(passageway1);

		try {
			passageway1.pickup(null);
		} catch (Throwable ex) {
			ex.printStackTrace();
			fail("fail");
		}
		try {
			passageway1.sendnull();
		} catch (Throwable ex) {
			fail(ex.getMessage());
		}

		// 利用者を用意 (User.3はフォロー関係を設定しない利用者)
		final TestUser room1_User1 = new TestUser("User.1(room.1)");
		final User room1_User2 = new TestUser("User.2(room.1)");
		final User room1_User3 = new TestUser("User.3(room.1)");
		User room2_User1 = new TestUser("User.1(room.2)");
		User room2_User2 = new TestUser("User.2(room.2)");
		User room2_User3 = new TestUser("User.3(room.2)");

		// 着席前に操作
		try {
			room1_User1.mutter(null, null);
			fail("fail");
		} catch (Exception ex) {
			// OK
		}
		try {
			room1_User1.mutterAll(null, null);
			fail("fail");
		} catch (Exception ex) {
			// OK
		}
		try {
			room1_User1.mutterLocal(null, null);
			fail("fail");
		} catch (Exception ex) {
			// OK
		}
		try {
			room1_User1.mutterLocalAll(null, null);
			fail("fail");
		} catch (Exception ex) {
			// OK
		}

		try {
			room1_User1.follow("dummy");
			fail("fail");
		} catch (Exception ex) {
			// OK
			assertFalse(room1.listSeatNames().contains("dummy"));
		}
		try {
			room1_User1.unfollow("dummy");
			fail("fail");
		} catch (Exception ex) {
			// OK
			assertFalse(room1.listSeatNames().contains("dummy"));
		}

		// 座席を用意
		Seat room1_seat1 = room1.getSeat("seat.1");
		assertEquals(room1, room1_seat1.getRoom());
		mic1.setPickupError(true);	// 盗聴器にエラーを起こさせる
		room1_seat1.mutter("microphone-error", null);
		mic1.setPickupError(false);
		Seat room1_seat2 = room1.getSeat("seat.2");
		Seat room1_seat3 = room1.getSeat("seat.3");
		Seat room2_seat1 = room2.getSeat("seat.1");
		Seat room2_seat2 = room2.getSeat("seat.2");
		Seat room2_seat3 = room2.getSeat("seat.3");

		// 着席
		assertTrue(room1_User1.toString().endsWith("[]"));
		assertTrue(room1_seat1.isVacant());
		assertEquals(null, room1_seat1.getUser());
		room1_seat1.sitDown(room1_User1);
		assertTrue(room1_User1.toString().endsWith("[" + room1_seat1.getName() + "]"));
		assertFalse(room1_seat1.isVacant());
		assertEquals(room1_User1, room1_seat1.getUser());
		room1_seat2.sitDown(room1_User2);
		room1_seat3.sitDown(room1_User3);
		room2_seat1.sitDown(room2_User1);
		room2_seat2.sitDown(room2_User2);
		room2_seat3.sitDown(room2_User3);

		// 離席
		assertFalse(room1_seat1.isVacant());
		assertEquals(room1_User1, room1_seat1.getUser());
		room1_seat1.standUp(room1_User1);
		assertTrue(room1_seat1.isVacant());
		assertEquals(null, room1_seat1.getUser());
		try {
			// 座っていない席から立とうとした
			room1_seat1.standUp(room1_User1);
			fail("fail");
		} catch (Exception ex) {
			// OK
		}
		try {
			// 自分が座っていない席から立とうとした
			room1_seat2.standUp(room1_User1);
			fail("fail");
		} catch (Exception ex) {
			// OK
		}
		room1_seat1.sitDown(room1_User1);
		assertFalse(room1_seat1.isVacant());
		assertEquals(room1_User1, room1_seat1.getUser());
		room1_User1.setStamdupError(true);
		try {
			// 内部エラー
			room1_seat1.standUp(room1_User1);
			fail("fail");
		} catch (Exception ex) {
			// OK
		}
		assertFalse(room1_seat1.isVacant());
		assertEquals(room1_User1, room1_seat1.getUser());
		room1_User1.setStamdupError(false);

		// 同じ座席に着席させて問題ないかチェック
		room1_seat1.sitDown(room1_User1);
		room1_seat2.sitDown(room1_User2);
		room1_seat3.sitDown(room1_User3);
		room2_seat1.sitDown(room2_User1);
		room2_seat2.sitDown(room2_User2);
		room2_seat3.sitDown(room2_User3);

		try {
			// null な座席を得ようとした
			room1.getSeat(null);
			fail("fail");
		} catch (Exception ex) {
			// OK
		}
		try {
			// "" な座席を得ようとした
			room1.getSeat("");
			fail("fail");
		} catch (Exception ex) {
			// OK
		}
		try {
			// 席にnullを座らせようとした
			room1_seat1.sitDown(null);
			fail("fail");
		} catch (Exception ex) {
			// OK
		}
		try {
			// すでに別の利用者(User1)が座っている席に(User2を)座らせようとした
			room1_seat1.sitDown(room1_User2);
			fail("fail");
		} catch (Exception ex) {
			// OK
		}
		room1.getSeat(room1_seat2.getName()).sitDown(room1_User2);
		try {
			// すでに席(seat.1)に座っている利用者(User1)を別の席(seat.1b)に座らせようとした
			room1.getSeat("seat.1b").sitDown(room1_User1);
			fail("fail");
		} catch (Exception ex) {
			// OK
		}

		// フォロー設定
		checkFollow();
		assertEquals(0, room1.listFollowers("seat.1").size());
		assertEquals(0, room1_seat1.listFollowers().size());
		assertEquals(0, room2.listFollowers("seat.1").size());
		assertEquals(0, room2_seat1.listFollowers().size());
		room1_User2.follow(room1_seat1.getName());
		assertEquals(1, room1.listFollowers("seat.1").size());
		assertEquals(1, room1_seat1.listFollowers().size());
		assertEquals(1, room2.listFollowers("seat.1").size());
		assertEquals(1, room2_seat1.listFollowers().size());
		checkFollow();
		// 自己フォローは無視される
		room1_User2.follow(room1_User2.getSeat().getName());
		assertEquals(1, room1.listFollowers("seat.1").size());
		assertEquals(1, room1_seat1.listFollowers().size());
		assertEquals(1, room2.listFollowers("seat.1").size());
		assertEquals(1, room2_seat1.listFollowers().size());
		checkFollow();

		// 空フォロー
		try {
			room1_User2.follow(null);
			fail("fail");
		} catch (Exception ex) {
			// OK
		}
		assertEquals(1, room1.listFollowers("seat.1").size());
		assertEquals(1, room1_seat1.listFollowers().size());
		assertEquals(1, room2.listFollowers("seat.1").size());
		assertEquals(1, room2_seat1.listFollowers().size());
		checkFollow();
		// 空フォロー
		try {
			room1_User2.follow("");
			fail("fail");
		} catch (Exception ex) {
			// OK
		}
		assertEquals(1, room1.listFollowers("seat.1").size());
		assertEquals(1, room1_seat1.listFollowers().size());
		assertEquals(1, room2.listFollowers("seat.1").size());
		assertEquals(1, room2_seat1.listFollowers().size());
		checkFollow();
		// 空アンフォロー
		try {
			room1_User2.unfollow(null);
			fail("fail");
		} catch (Exception ex) {
			// OK
		}
		assertEquals(1, room1.listFollowers("seat.1").size());
		assertEquals(1, room1_seat1.listFollowers().size());
		assertEquals(1, room2.listFollowers("seat.1").size());
		assertEquals(1, room2_seat1.listFollowers().size());
		checkFollow();
		// 空アンフォロー
		try {
			room1_User2.unfollow("");
			fail("fail");
		} catch (Exception ex) {
			// OK
		}
		assertEquals(1, room1.listFollowers("seat.1").size());
		assertEquals(1, room1_seat1.listFollowers().size());
		assertEquals(1, room2.listFollowers("seat.1").size());
		assertEquals(1, room2_seat1.listFollowers().size());
		checkFollow();

		// 複数追加
		room1_User1.follow(room1_seat2.getName());
		room2_User2.follow(room2_seat1.getName());
		room2_User1.follow(room2_seat2.getName());
		checkFollow();
		// 二重フォロー
		room1_User2.follow(room1_seat1.getName());
		room1_User1.follow(room1_seat2.getName());
		room2_User2.follow(room2_seat1.getName());
		room2_User1.follow(room2_seat2.getName());
		checkFollow();
		// getSeat() していない座席をフォローしても問題ないかチェック
		room1_User2.follow("seat.1-unknown");
		room1_User1.follow("seat.2-unknown");
		room2_User2.follow("seat.1-unknown");
		room2_User1.follow("seat.2-unknown");
		checkFollow();
		// フォロー解除
		room2_User2.unfollow("seat.1-unknown");
		checkFollow();
		assertEquals(0, room1.listFollowers("seat.1-unknown").size());
		assertEquals(0, room2.listFollowers("seat.1-unknown").size());

		String text;
		Object value;

		value = new Object[] {"test1", 1, new Date()};
		room2_User2.mutterLocal(value, null);
		value = new Object[] {"test1", 1, new Date()};
		room2_User2.mutterLocalAll(value, null);

		text = StringUtil.jsonEncode(value);
		room2_User1.mutter(text, null);
		room2_User2.mutter(text, null);
		room2_User3.mutter(text, null);

		// 自動ではシリアライズ出来ないつぶやき
		value = new HashMap<String, Object>() {{
			put("intval", 1234);
			put("floatval", 1234.5678);
			put("bigintval", new BigInteger("1234567890123456789012345678901234567890"));
			put("bigdecval", new BigDecimal("1234567890123456789012345678901234567890.1"));
			put("message", "廊下を通すには自力でシリアライズ");
			put("date", new Date());
		}};
		room1_User1.mutterLocal(value, null);
		// 例外は起きても終了しないチェック (SerializePassageWayがそう作ってある)
		room1_User1.mutter(value, null);
		// 自力でシリアライズ (jsonなので日付は文字列になる)
		text = StringUtil.jsonEncode(value);
		room1_User1.mutter(text, null);
		room1_User2.mutter(text, null);
		// 便乗して廊下でのエラー
		passageway1.setDecodeError(true);
		room1_User3.mutter(text, null);
		passageway1.setDecodeError(false);
		passageway1.setProcessError(true);
		room1_User3.mutter(text, null);
		passageway1.setProcessError(false);

		Thread[] threads = new Thread[] {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						room1_User1.mutterAll("all", null);
					} catch (Exception e) {
						e.printStackTrace();
						fail("fail");
					}
				}
			}), new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						room1_User2.mutterAll("all", null);
					} catch (Exception e) {
						e.printStackTrace();
						fail("fail");
					}
				}
			}), new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						room1_User3.mutterAll("all", null);
					} catch (Exception e) {
						e.printStackTrace();
						fail("fail");
					}
				}
			})
		};
		for (Thread t : threads) {
			t.start();
		}
		for (Thread t : threads) {
			t.join();
		}
	}

	/**
	 * テスト
	 * @throws Exception 異常時の例外
	 */
	@Test
	public void cleaningTest() throws Exception {
		Room room = Room.getRoom("cleaningTest");
		room.cleaning();
		assertEquals(0, room.listSeatNames().size());

		room.getSeat("test1");
		assertEquals(1, room.listSeatNames().size());

		TestUtil.dumpStatics(System.out);
		room.cleaning();
		assertEquals(0, room.listSeatNames().size());

		room.getSeat("test1");
		assertEquals(1, room.listSeatNames().size());
		room.getSeat("test2");
		assertEquals(2, room.listSeatNames().size());
		room.getSeat("test2").follow("test1");
		assertEquals(2, room.listSeatNames().size());
		room.getSeat("test1").follow("test3");
		assertEquals(3, room.listSeatNames().size());

		TestUtil.dumpStatics(System.out);
		room.cleaning();
		assertEquals(0, room.listSeatNames().size());

		room.getSeat("test1");
		assertEquals(1, room.listSeatNames().size());
		room.getSeat("test2");
		assertEquals(2, room.listSeatNames().size());
		room.getSeat("test2").follow("test1");
		assertEquals(2, room.listSeatNames().size());
		room.getSeat("test1").follow("test3");
		assertEquals(3, room.listSeatNames().size());
		room.getSeat("test2").sitDown(new User() {
			@Override
			public void received(Message message) {
			}
		});

		TestUtil.dumpStatics(System.out);
		room.cleaning();
		assertEquals(3, room.listSeatNames().size());
		TestUtil.dumpStatics(System.out);
	}
}
