package com.jule.bomb.srv.biz;

import com.jule.bomb.model.room;

public interface IBombBiz {
	// 房主建立房间
	// 参数	房间号、房主名字、炸弹爆炸时间(秒)
	// 返回值 0成功；	-1 房间已经存在；
	public abstract int createRoom(String roomIdString, String hostUsername, int bombTime);
	
	
	// 房主修改房间的炸弹时间
	// 参数	房间号、房主名字、炸弹爆炸时间(秒)
	// 返回值 0成功；	-1 房间不存在；		-2不是房主；	-3正在游戏无法修改
	public abstract int updateRoom(String roomIdString, String hostUsername, int bombTime);
	
		
	// 房主删除房间
	// 参数	房间号、房主名字、炸弹爆炸时间(秒)
	// 返回值 0成功；	-1 房间不存在；	-2不是房主；	-3正在游戏中
	public abstract int deleteRoom(String roomIdString, String hostUsername);
		
	// 其他玩家加入房间
	// 参数：	房间号、玩家用户名
	// 返回值 0成功；	-1 房间不存在;	-2当前玩家已经在房间中
	public abstract int joinRoom(String roomIdString, String username);
	
	
	// 获取当前房间状态
	// 参数：	房间号、玩家用户名
	// 返回值： null 获取失败;
	public abstract room getRoomState(String roomIdString);	
	
	// 玩家设置准备好了的状态
	// 参数：	房间号、玩家用户名
	// 返回值 0成功；	-1 房间不存在；		-2游戏正在进行
	public abstract int setUserReady(String roomIdString, String username);
				
	// 房主开始游戏
	// 参数	房间号、房主名字
	// 返回值 0成功开始游戏；	-1 房间不存在；		-2 不是房主;		-3游戏正在进行
	public abstract int startGame(String roomIdString, String hostUsername);
	
	// 功能：  传递炸弹
	// 参数	当前房间ID号、当前炸弹拥有者
	// 返回值： 0成功传递给他人		1运气太背了，又传给了自己；	-1没有该房间；	-2游戏没有开始;  -3已经结束；-4游戏已经，当前炸弹拥有者为失败者	-5不是当前的炸弹拥有者，无法传递;  	
	public abstract int passBombRandom(String roomIdString, String oldBombUser);

}
