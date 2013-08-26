package com.jule.bomb.model;

import java.util.HashMap;


public class gameGlobal {

public	static HashMap<String, room> roomList = new HashMap<String, room>();

public static void putRoom(room rr) {
	roomList.put(rr.roomidString, rr);
}

}
