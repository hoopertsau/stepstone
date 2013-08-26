package com.jule.bomb.srv.biz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jule.bomb.model.gameGlobal;
import com.jule.bomb.model.room;
import com.jule.bomb.model.user;

// 未使用持久化存储，使用了全局的静态成员 gameGlobal来对各类状态进行存储
public class bombBiz implements IBombBiz{

	// 房主建立房间
	// 参数	房间号、房主名字、炸弹爆炸时间(秒)
	// 返回值 0成功；	-1 房间已经存在；
	public int createRoom(String roomIdString, String hostUsername, int bombTime) {
		
		if(gameGlobal.roomList.containsKey(roomIdString))
		{
			return -1;
		}
		room rr = new room();
		
		rr.roomidString = roomIdString;
		rr.gamingState = room.STATE_NOTSTART;
		rr.userList.clear();
		rr.userList.add(hostUsername);
		rr.hostUsernameString = hostUsername;
		rr.bombTime = bombTime;

		gameGlobal.roomList.put(roomIdString, rr);
		
		return 0;
	}
	
	// 其他玩家加入房间
	// 参数：	房间号、玩家用户名
	// 返回值 0成功；	-1 房间不存在;	-2当前玩家已经在房间中
	public int joinRoom(String roomIdString, String username) {
		
		if(gameGlobal.roomList.containsKey(roomIdString) == false)
		{
			return -1;
		}
		room rr = gameGlobal.roomList.get(roomIdString);
		if(rr.userList.contains(username))
		{
			return -2;
		}
		rr.userList.add(username);		
		return 0;
	}
	
	// 玩家设置准备好了的状态
	// 参数：	房间号、玩家用户名
	// 返回值 0成功；	-1 房间不存在；		-2游戏正在进行或者还没退出;		-3该玩家不是这个房间的
	public int setUserReady(String roomIdString, String username) {
		if(gameGlobal.roomList.containsKey(roomIdString)==false)
		{
			return -1;
		}
		
		room rr = gameGlobal.roomList.get(roomIdString);
		if(rr.gamingState.equals(room.STATE_GAMING)|| rr.gamingState.equals(room.STATE_BOMBED))
		{
			// gaming
			return -2;
		}
		if(rr.userList.contains(username)==false)
		{
			return -3;
		}
		if(rr.readyUserList.contains(username)==false){
			rr.readyUserList.add(username);
		}
		return 0;
	}
		

		
	// 房主开始游戏
	// 参数	房间号、房主名字
	// 返回值 0成功开始游戏；	-1 房间不存在；		-2 不是房主;		-3游戏正在进行
	//		-4 游戏玩家还没有都准备好
	public int startGame(String roomIdString, String hostUsername) {
		
		if(gameGlobal.roomList.containsKey(roomIdString)==false)
		{
			return -1;
		}
		
		room rr = gameGlobal.roomList.get(roomIdString);
		if(rr.hostUsernameString.equals(hostUsername) == false)
		{
			return -2;
		}
		if(rr.gamingState.equals(room.STATE_GAMING))
		{
			// gaming
			return -3;
		}
		if(rr.userList.size() != rr.readyUserList.size())
		{
			return -4;
		}
		
		rr.startTime = new Date();
		rr.endTime = new Date(rr.startTime.getTime() + 1000*rr.bombTime);
		rr.gamingState = room.STATE_GAMING;
		rr.readyUserList.clear();
		rr.quitGameUserList.clear();
		rr.bombUsernameString = rr.userList.get( getRandom(rr.userList.size()) ); 
		
		return 0;
	}
	
	// 功能：  传递炸弹
	// 参数	当前房间ID号、当前炸弹拥有者
	// 返回值： 0成功传递给他人		1运气太背了，又传给了自己；	-1没有该房间；	-2游戏没有开始;  -3已经结束；-4游戏已经，当前炸弹拥有者为失败者	-5不是当前的炸弹拥有者，无法传递;  	
	public int passBombRandom(String roomIdString, String oldBombUser)
	{
		if(gameGlobal.roomList.containsKey(roomIdString)==false)
		{
			return -1;
		}
		
		room rr = gameGlobal.roomList.get(roomIdString);
		if(rr.gamingState.equals(room.STATE_NOTSTART)){
			return -2;
		}
		if(rr.gamingState.equals(room.STATE_BOMBED)){
			if(rr.bombUsernameString.equals(oldBombUser))
			{
				return -4;
			}
			else
			{
				return -3;
			}			
		}
		
		if(rr.bombUsernameString.equals(oldBombUser)==false)
		{
			return -5;
		}
		
		
		int rdIndex = getRandom(rr.userList.size());
		rr.bombUsernameString = rr.userList.get(rdIndex);
		System.out.println("pass bomb from "+oldBombUser+" to .."+rr.bombUsernameString);
		
		if(rr.bombUsernameString.equals(oldBombUser))
		{
			return 1;
		}
		
		return 0;
	}
	
		
	// 获取0-max随机数，不包括max
	private int getRandom(int max) {
		int b=(int)(Math.random()*max);//产生0-max的整数随机数		
		return b;
	}
	// 获得当前炸弹所在的玩家名字
	// 参数：		房间号
	// 返回值：	null，房间不存在或者当前房间没有进行游戏;  字符串，当前玩家姓名
	private String getBombUsername(String roomIdString) {
		if(gameGlobal.roomList.containsKey(roomIdString)==false)
		{
			return null;
		}
		
		room rr = gameGlobal.roomList.get(roomIdString);
		if(rr.gamingState.equals(room.STATE_GAMING) == false){
			return null;
		}
		
		return rr.bombUsernameString;
	}

	// 房主修改房间的炸弹时间
	// 参数	房间号、房主名字、炸弹爆炸时间(秒)
	// 返回值 0成功；	-1 房间不存在；		-2不是房主；	-3正在游戏或者游戏还没有结束无法修改
	@Override
	public int updateRoom(String roomIdString, String hostUsername, int bombTime) {

		if(gameGlobal.roomList.containsKey(roomIdString)==false)
		{
			return -1;
		}
		
		room rr = gameGlobal.roomList.get(roomIdString);
		if(rr.hostUsernameString.equals(hostUsername) == false)
		{
			return -2;
		}
		if(rr.gamingState.equals(room.STATE_GAMING) 
				|| rr.gamingState.equals(room.STATE_BOMBED))
		{
			return -3;
		}
		
		rr.bombTime = bombTime;		
		return 0;
	}

	// 房主删除房间
	// 参数	房间号、房主名字、炸弹爆炸时间(秒)
	// 返回值 0成功；	-1 房间不存在；	-2不是房主；	-3正在游戏中
	@Override
	public int deleteRoom(String roomIdString, String hostUsername) {
		
		if(gameGlobal.roomList.containsKey(roomIdString)==false)
		{
			return -1;
		}
		
		room rr = gameGlobal.roomList.get(roomIdString);
		if(rr.hostUsernameString.equals(hostUsername) == false)
		{
			return -2;
		}
		if(rr.gamingState.equals(room.STATE_GAMING))
		{
			return -3;
		}
		
		gameGlobal.roomList.remove(roomIdString);	
		return 0;
	}

	
	
	public List<String> getRoomUsers(String roomIdString)
	{
		if(gameGlobal.roomList.containsKey(roomIdString)==false)
		{
			return null;
		}
		
		room rr = gameGlobal.roomList.get(roomIdString);
		return rr.userList;
	}
	
	// 获取当前房间状态
	// 参数：	房间号、玩家用户名
	// 返回值： null 获取失败;
	public room getRoomState(String roomIdString)
	{
		if(gameGlobal.roomList.containsKey(roomIdString)==false)
		{
			return null;
		}
		return gameGlobal.roomList.get(roomIdString);
	}
	
	// 检验用户是否在线，通过判断用户上次请求的时间来判定，如果超过30s没有任何请求，则将其踢下线
	public int checkUserOnline(String roomIdString)
	{
		if(gameGlobal.roomList.containsKey(roomIdString)==false)
		{
			return -1;
		}
		room rr = gameGlobal.roomList.get(roomIdString);
		Date nowDate = new Date();
		long nowtime = nowDate.getTime();
		List<String> needRemoveList = new ArrayList<String>();
		for (String usernameString : rr.userList ) {
			Long requestTime = rr.userRequestTimeHash.get(usernameString);
			if((nowtime-requestTime)> 30*1000){
				// 直接踢下线
				needRemoveList.add(usernameString);
			}
		}
		for (String rmnameString : needRemoveList) {
			rr.userList.remove(rmnameString);
			rr.userRequestTimeHash.remove(rmnameString);
			rr.readyUserList.remove(rmnameString);
			rr.quitGameUserList.remove(rmnameString);
		}
		
		return 0;
	}
	
	// 检验用户是否在线，通过判断用户是否已经收到BOMBED状态，退出游戏，等待开始 
	// 1都已经提交了quitgame；	1没有全部提交；		-1 房间不存在 
	public int checkUserAllQuitGame(String roomIdString)
	{
		if(gameGlobal.roomList.containsKey(roomIdString)==false)
		{
			return -1;
		}
		room rr = gameGlobal.roomList.get(roomIdString);
		if(rr.userList.size()==rr.quitGameUserList.size())
		{
			rr.gamingState = room.STATE_NOTSTART;
			System.out.println("userlist count="+rr.userList.size()+" quitcount="+rr.quitGameUserList.size());
			return 1;
		}
		else 
		{
			System.out.println("userlist count="+rr.userList.size()+" quitcount="+rr.quitGameUserList.size());
			return 0;
		}		
	}
	
	// 检验是否炸弹已经爆炸
	// 1爆炸了 ; 0没有   ;-1没有该房间； -2不在游戏中
	public int checkBombed(String roomIdString)
	{
		if(gameGlobal.roomList.containsKey(roomIdString)==false)
		{
			return -1;
		}
		room rr = gameGlobal.roomList.get(roomIdString);
		if(rr.gamingState.equals(room.STATE_GAMING)==false)
		{
			return -2;
		}
		Date nowDate = new Date();
		if(nowDate.getTime()>rr.endTime.getTime())
		{
			rr.gamingState = room.STATE_BOMBED;
			return 1;
		}
		else 
		{
			return 0;
		}
	}
	
	// 刷新请求时间，避免被服务器踢下线
	public void refreshRequestTime(String roomIdString, String username)
	{
		if(gameGlobal.roomList.containsKey(roomIdString)==false)
		{
			return;
		}
		room rr = gameGlobal.roomList.get(roomIdString);
		if(rr.userList.contains(username))
		{
			rr.userRequestTimeHash.put(username,  new Long((new Date()).getTime()) );
		}
	}
	
	// 功能： 退出游戏
	// 参数	当前房间号、玩家用户名
	// 返回值：0成功；	-1当前房间不存在；	-2正在游戏无法退出；	-3当前玩家本来就不在房间中；   -4游戏还没开始
	public int setQuitGame(String roomIdString, String usernameString) {
		if(gameGlobal.roomList.containsKey(roomIdString)==false)
		{
			return -1;
		}
		room rr = gameGlobal.roomList.get(roomIdString);
		if(rr.gamingState.equals(room.STATE_GAMING))
		{
			return -2;
		}
		if(rr.userList.contains(usernameString)==false)
		{
			return -3;
		}
		if(rr.gamingState.equals(room.STATE_NOTSTART))
		{
			return -4;
		}
		rr.quitGameUserList.add(usernameString);
		return 0;
	}


	public int quitRoom() {
		// TODO Auto-generated method stub
		return 0;
	}
}
