package com.jule.bomb.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class room {
	
	public static String STATE_NOTSTART = "NOT_START";
	public static String STATE_GAMING = "GAMING";
	public static String STATE_BOMBED = "STATE_BOMBED";
	public String gamingState = STATE_NOTSTART;// notstart ---> gaming ---> bombed
	
	public String roomidString = "";
	public String hostUsernameString = "";
	public List<String> userList = new ArrayList<String>();
	public List<String> readyUserList = new ArrayList<String>();
	public List<String> quitGameUserList = new ArrayList<String>();
	public HashMap<String, Long> userRequestTimeHash = new HashMap<String, Long>();// username ---> last request time (æ¯∂‘ ±º‰£¨∫¡√Î)
	
	public String bombUsernameString = "";
	public int bombTime = 0;//√Î
	public Date startTime = new Date();
	public Date endTime = new Date();
}
