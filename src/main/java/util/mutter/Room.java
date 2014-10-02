package util.mutter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import util.mutter.util.StringUtil;

/**
 * 部屋です。
 * つぶやきは（廊下が無ければ）この部屋だけで聞こえます。
 */
public final class Room {
	/**
	 * 部屋を生成します。
	 * {@link Room#getRoom(String)} からシングルトンとして作成されます。
	 * @param name 識別名
	 */
	protected Room(String name) {
		this.name = name.replace('@', ' ');
	}

	private String name = "";
	/**
	 * 部屋の識別名を返します。
	 * @return 識別名
	 */
	public String getName() {
		return name;
	}

	/**
	 * メッセージの配達を管理します。<br/>
	 * FOLLOWメッセージにあるフォロー先の座席が無ければ生成します。
	 * @param message メッセージ
	 */
	protected void send(Message message) {
		// 盗聴器にはエラーの起こるデータも食わせるために前に配置
		for (Microphone microphone : microphones) {
			try {
				microphone.pickup(message);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		String from = message.getFromSeatName();
		String to = message.getToSeatName();
		Set<Seat> endpoints = modlistFollowers(from);
		Set<Seat> followers;
		Seat endpoint;
		// 廊下からのメッセージ
		boolean isalien = (false == getName().equals(message.getRoomName()));
//		if (from.equals(to)) {
//			// 自己メッセージはシャットアウト
//			return;
//		}
		switch (message.getMessageType()) {
		case MUTTER:
			if (isalien) {
				endpoint = getSeat(from);
				endpoint.receive(message);
			}
			for (Seat target : Collections.unmodifiableSet(endpoints)) {
				target.receive(message);
			}
			break;
		case MUTTERALL:
			for (Seat target : Collections.unmodifiableCollection(seatDictionary.values())) {
				target.receive(message);
			}
			break;
		case FOLLOW:
			if (isalien) {
				getSeat(from).receive(message);	// MUTTERALLでなければ、自分(from)へも転送
			}
			to = StringUtil.rtrim(to);
			if (to.length()==0) {
				throw new IllegalArgumentException("empty follow target : " + message.toString());
			}
			endpoint = getSeat(from);
			followers = modlistFollowers(to);
			synchronized (followers) {
				followers.add(endpoint);
			}
			endpoint = getSeat(to);
			endpoint.receive(message);
			break;
		case UNFOLLOW:
			if (isalien) {
				getSeat(from).receive(message);	// MUTTERALLでなければ、自分(from)へも転送
			}
			to = StringUtil.rtrim(to);
			if (to.length()==0) {
				throw new IllegalArgumentException("empty unfollow target : " + message.toString());
			}
			endpoint = getSeat(from);
			followers = modlistFollowers(to);
			synchronized (followers) {
				followers.remove(endpoint);
			}
			endpoint = getSeat(to);
			endpoint.receive(message);
			break;
//		default:
//			// 列挙なので設定できない
//			throw new IllegalArgumentException("unknown message type : " + message.toString());
		}
		// この部屋が発信元だった場合にメッセージを廊下に送り出します
		// 廊下には問題のないメッセージだけ送るので後ろに配置
		if (false==message.isLocalOnly() && message.getRoomName().equals(name)) {
			for (PassageWay bridge : passageways) {
				try {
					bridge.pickup(message);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

	/**
	 * 部屋の辞書。
	 */
	private static HashMap<String, Room> roomDictionary = new HashMap<String, Room>();
	/**
	 * 部屋を取得・生成します。
	 * 部屋が存在しない場合には生成します。
	 * @param roomName 部屋の識別名
	 * @return 部屋
	 */
	public static Room getRoom(String roomName) {
		Room room;
		synchronized (roomDictionary) {
			if (roomDictionary.containsKey(roomName)) {
				room = roomDictionary.get(roomName);
			} else {
				room = new Room(roomName);
				roomDictionary.put(roomName, room);
			}
		}
		return room;
	}
	/**
	 * 部屋名一覧(unmodifiable)を返します。
	 * @return 部屋名一覧
	 */
	public static Set<String> listRoomNames() {
		synchronized (roomDictionary) {
			return Collections.unmodifiableSet(roomDictionary.keySet());
		}
	}

	/**
	 * 座席の辞書。
	 */
	private HashMap<String, Seat> seatDictionary = new HashMap<String, Seat>();
	/**
	 * 座席を取得します。<br/>
	 * 座席が存在しない場合には生成します。
	 * @param seatName 座席の識別名
	 * @return 座席
	 */
	public Seat getSeat(String seatName) {
		if (seatName==null || seatName.length()==0) {
			throw new IllegalArgumentException("request null endpoint.");
		}
		Seat seat;
		synchronized (seatDictionary) {
			if (seatDictionary.containsKey(seatName)) {
				seat = seatDictionary.get(seatName);
			} else {
				seat = new Seat(seatName, this);
				seatDictionary.put(seatName, seat);
			}
		}
		return seat;
	}
	/**
	 * 座席名一覧(unmodifiable)を返します。
	 * @return 座席名一覧
	 */
	public Set<String> listSeatNames() {
		synchronized (seatDictionary) {
			return Collections.unmodifiableSet(seatDictionary.keySet());
		}
	}
	/**
	 * 利用者もなく、フォローもされていない座席を片付けます。<br/>
	 * cleaning() は内部的に再帰的に実行され、片付けられる座席が見つからなくなるまで制御を返しません。
	 * @return 片付けた座席の数
	 */
	public int cleaning() {
		int count = 0;
		synchronized (seatDictionary) {
			// 削除対象リスト
			LinkedList<Seat> list = new LinkedList<Seat>();
			LinkedList<String> namelist = new LinkedList<String>();
			synchronized (followerDictionary) {
				for (Seat seat : seatDictionary.values()) {
					if (seat.isVacant()) {
						if (0==modlistFollowers(seat.getName()).size()) {
							// 利用者もなく、フォローもされていない座席
							list.add(seat);
							namelist.add(seat.getName());
						}
					}
				}
				if (list.size()==0) {
					return 0;
				}
				for (String seatName : namelist) {
					followerDictionary.remove(seatName);
				}
				for (LinkedHashSet<Seat> followers : followerDictionary.values()) {
					followers.removeAll(list);
				}
			}
			for (Seat seat : list) {
				seatDictionary.remove(seat.getName());
			}
			count = list.size();
		}
		count += cleaning();
		return count;
	}

	/**
	 * 廊下の一覧。
	 */
	private LinkedHashSet<PassageWay> passageways = new LinkedHashSet<PassageWay>();
	/**
	 * 廊下の一覧(unmodifiable)を返します。
	 * @return 廊下の一覧
	 */
	public Set<PassageWay> listPassageWays() {
		synchronized (passageways) {
			return Collections.unmodifiableSet(passageways);
		}
	}
	/**
	 * 廊下を部屋に設定します。
	 * @param passageWay 廊下
	 */
	protected void addPassageWay(PassageWay passageWay) {
		synchronized (passageways) {
			this.passageways.add(passageWay);
			passageWay.setRoom(this);
		}
	}
	/**
	 * 廊下を部屋から撤去します。
	 * @param passageWay 廊下
	 */
	protected void removePassageWay(PassageWay passageWay) {
		synchronized (passageways) {
			this.passageways.remove(passageWay);
			passageWay.setRoom(null);
		}
	}

	/**
	 * 部屋の内部でユニークなIDのソース
	 */
	private Object idmasterlock = new Object();
	/**
	 * 直前に供給したid。<br/>
	 * idmasterlock で排他制御するためvolatileは付けない。
	 */
	private long idmaster = System.currentTimeMillis();
	/**
	 * 部屋の内部でユニークなIDを生成します。<br/>
	 * 部屋の識別名と組み合わせることでシステム上でユニークなIDとなります。<br/>
	 * 生成されるIDは UTC の 1970/01/01 00:00 からのミリ秒を16進表記したもので、時計が操作されない限りは重複しません。
	 * @return ユニークなID
	 */
	protected String createSerial() {
		synchronized (idmasterlock) {
			long work = System.currentTimeMillis();
			while (idmaster==work) {
				work = System.currentTimeMillis();
			}
			idmaster = work;
			return Long.toHexString(idmaster);
		}
	}

	/**
	 * 座席毎のつぶやきを配信する先の座席一覧。
	 */
	private HashMap<String, LinkedHashSet<Seat>> followerDictionary = new HashMap<String, LinkedHashSet<Seat>>();
	/**
	 * フォロワーの一覧(unmodifiable)を返します。
	 * @param name エンドポイントの識別名
	 * @return フォロワーの一覧
	 */
	public Set<Seat> listFollowers(String name) {
		return Collections.unmodifiableSet(modlistFollowers(name));
	}
	/**
	 * フォロワーの一覧を返します。
	 * @param name エンドポイントの識別名
	 * @return フォロワーの一覧
	 */
	private Set<Seat> modlistFollowers(String name) {
		synchronized (followerDictionary) {
			if (followerDictionary.containsKey(name)) {
				return followerDictionary.get(name);
			} else {
				LinkedHashSet<Seat> seats = new LinkedHashSet<Seat>();
				followerDictionary.put(name, seats);
				return seats;
			}
		}
	}
	/**
	 * フォロワーの座席名一覧(unmodifiable)を返します。
	 * @param name エンドポイントの識別名
	 * @return フォロワーの座席名一覧
	 */
	public Set<String> listFollowerNames(String name) {
		HashSet<String> set = new HashSet<String>();
		for (Seat seat : modlistFollowers(name)) {
			set.add(seat.getName());
		}
		return Collections.unmodifiableSet(set);
	}

	/**
	 * 盗聴器の一覧。
	 */
	private HashSet<Microphone> microphones = new HashSet<Microphone>();
	/**
	 * 盗聴器を仕掛けます。
	 * @param microphone 盗聴器
	 */
	public void addMicrophone(Microphone microphone) {
		synchronized (microphones) {
			microphones.add(microphone);
		}
	}
	/**
	 * 盗聴器を除去します。
	 * @param microphone 盗聴器
	 */
	public void removeMicrophone(Microphone microphone) {
		synchronized (microphones) {
			microphones.remove(microphone);
		}
	}
	/**
	 * 盗聴器の一覧(unmodifiable)を返します。
	 * @return 盗聴器の一覧
	 */
	public Set<Microphone> listMicrophones() {
		synchronized (microphones) {
			return Collections.unmodifiableSet(microphones);
		}
	}
}