package com.jule.bomb.srv.biz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jule.bomb.model.gameGlobal;
import com.jule.bomb.model.room;
import com.jule.bomb.model.user;

// δʹ�ó־û��洢��ʹ����ȫ�ֵľ�̬��Ա gameGlobal���Ը���״̬���д洢
public class bombBiz implements IBombBiz{

	// ������������
	// ����	����š��������֡�ը����ըʱ��(��)
	// ����ֵ 0�ɹ���	-1 �����Ѿ����ڣ�
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
	
	// ������Ҽ��뷿��
	// ������	����š�����û���
	// ����ֵ 0�ɹ���	-1 ���䲻����;	-2��ǰ����Ѿ��ڷ�����
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
	
	// �������׼�����˵�״̬
	// ������	����š�����û���
	// ����ֵ 0�ɹ���	-1 ���䲻���ڣ�		-2��Ϸ���ڽ��л��߻�û�˳�;		-3����Ҳ�����������
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
		

		
	// ������ʼ��Ϸ
	// ����	����š���������
	// ����ֵ 0�ɹ���ʼ��Ϸ��	-1 ���䲻���ڣ�		-2 ���Ƿ���;		-3��Ϸ���ڽ���
	//		-4 ��Ϸ��һ�û�ж�׼����
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
	
	// ���ܣ�  ����ը��
	// ����	��ǰ����ID�š���ǰը��ӵ����
	// ����ֵ�� 0�ɹ����ݸ�����		1����̫���ˣ��ִ������Լ���	-1û�и÷��䣻	-2��Ϸû�п�ʼ;  -3�Ѿ�������-4��Ϸ�Ѿ�����ǰը��ӵ����Ϊʧ����	-5���ǵ�ǰ��ը��ӵ���ߣ��޷�����;  	
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
	
		
	// ��ȡ0-max�������������max
	private int getRandom(int max) {
		int b=(int)(Math.random()*max);//����0-max�����������		
		return b;
	}
	// ��õ�ǰը�����ڵ��������
	// ������		�����
	// ����ֵ��	null�����䲻���ڻ��ߵ�ǰ����û�н�����Ϸ;  �ַ�������ǰ�������
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

	// �����޸ķ����ը��ʱ��
	// ����	����š��������֡�ը����ըʱ��(��)
	// ����ֵ 0�ɹ���	-1 ���䲻���ڣ�		-2���Ƿ�����	-3������Ϸ������Ϸ��û�н����޷��޸�
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

	// ����ɾ������
	// ����	����š��������֡�ը����ըʱ��(��)
	// ����ֵ 0�ɹ���	-1 ���䲻���ڣ�	-2���Ƿ�����	-3������Ϸ��
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
	
	// ��ȡ��ǰ����״̬
	// ������	����š�����û���
	// ����ֵ�� null ��ȡʧ��;
	public room getRoomState(String roomIdString)
	{
		if(gameGlobal.roomList.containsKey(roomIdString)==false)
		{
			return null;
		}
		return gameGlobal.roomList.get(roomIdString);
	}
	
	// �����û��Ƿ����ߣ�ͨ���ж��û��ϴ������ʱ�����ж����������30sû���κ���������������
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
				// ֱ��������
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
	
	// �����û��Ƿ����ߣ�ͨ���ж��û��Ƿ��Ѿ��յ�BOMBED״̬���˳���Ϸ���ȴ���ʼ 
	// 1���Ѿ��ύ��quitgame��	1û��ȫ���ύ��		-1 ���䲻���� 
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
	
	// �����Ƿ�ը���Ѿ���ը
	// 1��ը�� ; 0û��   ;-1û�и÷��䣻 -2������Ϸ��
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
	
	// ˢ������ʱ�䣬���ⱻ������������
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
	
	// ���ܣ� �˳���Ϸ
	// ����	��ǰ����š�����û���
	// ����ֵ��0�ɹ���	-1��ǰ���䲻���ڣ�	-2������Ϸ�޷��˳���	-3��ǰ��ұ����Ͳ��ڷ����У�   -4��Ϸ��û��ʼ
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
