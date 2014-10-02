package util.mutter;

import static org.junit.Assert.*;

import org.junit.Test;

import util.mutter.ext.InternalPassageWay;
import util.mutter.sample.TestUser;

/**
 * ロジック動作のテストコードです。
 */
public class MutterLogicTest {
	/**
	 * テスト
	 * @throws Exception 異常時の例外
	 */
	@Test
	public void test() throws Exception {
		Room room1 = Room.getRoom("room1");
		Room room2 = Room.getRoom("room2");
		InternalPassageWay passageway1 = new InternalPassageWay();
		room1.addPassageWay(passageway1);
		InternalPassageWay passageway2 = new InternalPassageWay();
		room2.addPassageWay(passageway2);
		passageway1.connect(passageway2);
		passageway2.connect(passageway1);

		// --------------------------------------------------------------------------------
		Seat room1_seat1 = room1.getSeat("seat1");
		int room1_user1_expected = 0;
		TestUser room1_user1 = new TestUser("user1");
		room1_seat1.sitDown(room1_user1);

		// --------------------------------------------------------------------------------
		Seat room1_seat2 = room1.getSeat("seat2");
		int room1_user2_expected = 0;
		TestUser room1_user2 = new TestUser("user2");
		room1_seat2.sitDown(room1_user2);

		// --------------------------------------------------------------------------------
		Seat room1_seat3 = room1.getSeat("seat3");
		int room1_user3_expected = 0;
		TestUser room1_user3 = new TestUser("user3");
		room1_seat3.sitDown(room1_user3);

		// --------------------------------------------------------------------------------
		Seat room2_seat1 = room2.getSeat("seat1");
		int room2_user1_expected = 0;
		TestUser room2_user1 = new TestUser("user1");
		room2_seat1.sitDown(room2_user1);

		// --------------------------------------------------------------------------------
		Seat room2_seat2 = room2.getSeat("seat2");
		int room2_user2_expected = 0;
		TestUser room2_user2 = new TestUser("user2");
		room2_seat2.sitDown(room2_user2);

		// --------------------------------------------------------------------------------
		Seat room2_seat3 = room2.getSeat("seat3");
		int room2_user3_expected = 0;
		TestUser room2_user3 = new TestUser("user3");
		room2_seat3.sitDown(room2_user3);

		// --------------------------------------------------------------------------------
		assertEquals(room1_user1_expected, room1_user1.getCount());
		assertEquals(room1_user2_expected, room1_user2.getCount());
		assertEquals(room1_user3_expected, room1_user3.getCount());
		assertEquals(room2_user1_expected, room2_user1.getCount());
		assertEquals(room2_user2_expected, room2_user2.getCount());
		assertEquals(room2_user3_expected, room2_user3.getCount());

		room1_user3.follow(room1_seat1.getName());
		room1_user1_expected++;	// followの分
//		room1_user2_expected++;
//		room1_user3_expected++;
		room2_user1_expected++;	// followの分
//		room2_user2_expected++;
		room2_user3_expected++;	// 別の部屋の自分がfollowした通知の分
		assertEquals(room1_user1_expected, room1_user1.getCount());
		assertEquals(room1_user2_expected, room1_user2.getCount());
		assertEquals(room1_user3_expected, room1_user3.getCount());
		assertEquals(room2_user1_expected, room2_user1.getCount());
		assertEquals(room2_user2_expected, room2_user2.getCount());
		assertEquals(room2_user3_expected, room2_user3.getCount());

		room2_user3.follow(room1_seat1.getName());
		room1_user1_expected++;	// followの分
//		room1_user2_expected++;
		room1_user3_expected++;	// 別の部屋の自分がfollowした通知の分
		room2_user1_expected++;	// followの分
//		room2_user2_expected++;
//		room2_user3_expected++;
		assertEquals(room1_user1_expected, room1_user1.getCount());
		assertEquals(room1_user2_expected, room1_user2.getCount());
		assertEquals(room1_user3_expected, room1_user3.getCount());
		assertEquals(room2_user1_expected, room2_user1.getCount());
		assertEquals(room2_user2_expected, room2_user2.getCount());
		assertEquals(room2_user3_expected, room2_user3.getCount());

		room2_user1.follow(room2_seat3.getName());
		room1_user1_expected++;	// 別の部屋の自分がfollowした通知の分
//		room1_user2_expected++;
		room1_user3_expected++;	// followの分
//		room2_user1_expected++;
//		room2_user2_expected++;
		room2_user3_expected++;	// followの分
		assertEquals(room1_user1_expected, room1_user1.getCount());
		assertEquals(room1_user2_expected, room1_user2.getCount());
		assertEquals(room1_user3_expected, room1_user3.getCount());
		assertEquals(room2_user1_expected, room2_user1.getCount());
		assertEquals(room2_user2_expected, room2_user2.getCount());
		assertEquals(room2_user3_expected, room2_user3.getCount());

		room2_user1.unfollow(room2_seat3.getName());
		room1_user1_expected++;	// 別の部屋の自分がunfollowした通知の分
//		room1_user2_expected++;
		room1_user3_expected++;	// unfollowの分
//		room2_user1_expected++;
//		room2_user2_expected++;
		room2_user3_expected++;	// unfollowの分
		assertEquals(room1_user1_expected, room1_user1.getCount());
		assertEquals(room1_user2_expected, room1_user2.getCount());
		assertEquals(room1_user3_expected, room1_user3.getCount());
		assertEquals(room2_user1_expected, room2_user1.getCount());
		assertEquals(room2_user2_expected, room2_user2.getCount());
		assertEquals(room2_user3_expected, room2_user3.getCount());

		// --------------------------------------------------------------------------------
		room1_user1.mutter("message", null);
//		room1_user1_expected++;		自分にメッセージは届かない
//		room1_user2_expected++;		follow していない
		room1_user3_expected++;
		room2_user1_expected++;
//		room2_user2_expected++;		follow していない
		room2_user3_expected++;
		assertEquals(room1_user1_expected, room1_user1.getCount());
		assertEquals(room1_user2_expected, room1_user2.getCount());
		assertEquals(room1_user3_expected, room1_user3.getCount());
		assertEquals(room2_user1_expected, room2_user1.getCount());
		assertEquals(room2_user2_expected, room2_user2.getCount());
		assertEquals(room2_user3_expected, room2_user3.getCount());

		// --------------------------------------------------------------------------------
		room1_user1.mutterLocal("message", null);
//		room1_user1_expected++;		自分にメッセージは届かない
//		room1_user2_expected++;		follow していない
		room1_user3_expected++;
//		room2_user1_expected++;		local なので届かない
//		room2_user2_expected++;		local なので届かない
//		room2_user3_expected++;		local なので届かない
		assertEquals(room1_user1_expected, room1_user1.getCount());
		assertEquals(room1_user2_expected, room1_user2.getCount());
		assertEquals(room1_user3_expected, room1_user3.getCount());
		assertEquals(room2_user1_expected, room2_user1.getCount());
		assertEquals(room2_user2_expected, room2_user2.getCount());
		assertEquals(room2_user3_expected, room2_user3.getCount());

		// --------------------------------------------------------------------------------
		// Allメッセージは届く
		room1_user1.mutterAll("message", null);
		room1_user1_expected++;
		room1_user2_expected++;
		room1_user3_expected++;
		room2_user1_expected++;
		room2_user2_expected++;
		room2_user3_expected++;
		assertEquals(room1_user1_expected, room1_user1.getCount());
		assertEquals(room1_user2_expected, room1_user2.getCount());
		assertEquals(room1_user3_expected, room1_user3.getCount());
		assertEquals(room2_user1_expected, room2_user1.getCount());
		assertEquals(room2_user2_expected, room2_user2.getCount());
		assertEquals(room2_user3_expected, room2_user3.getCount());

		// --------------------------------------------------------------------------------
		// LocalAllメッセージは届く
		room1_user1.mutterLocalAll("message", null);
		room1_user1_expected++;
		room1_user2_expected++;
		room1_user3_expected++;
//		room2_user1_expected++;		local なので届かない
//		room2_user2_expected++;		local なので届かない
//		room2_user3_expected++;		local なので届かない
		assertEquals(room1_user1_expected, room1_user1.getCount());
		assertEquals(room1_user2_expected, room1_user2.getCount());
		assertEquals(room1_user3_expected, room1_user3.getCount());
		assertEquals(room2_user1_expected, room2_user1.getCount());
		assertEquals(room2_user2_expected, room2_user2.getCount());
		assertEquals(room2_user3_expected, room2_user3.getCount());

	}
}
