package com.jule.bomb.srv;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jule.bomb.model.room;
import com.jule.bomb.srv.udpserver.IRecvDataHandle;
import com.jule.bomb.srv.biz.bombBiz;

/**
 * Servlet implementation class bombSvr
 */
@WebServlet("/bombSvr")
public class bombSvr extends HttpServlet {
	private static final long serialVersionUID = 1L;

	udpserver udpserv=null;
	bombBiz biz = new bombBiz();
	
	public class OnRecvDataImp implements IRecvDataHandle
	{
		@Override
		public void OnRecvData(String recvData, String destIp, int port) {
			
			System.out.println("recved data from "+destIp.substring(1)+" port="+ Integer.toString(port) + " data:"+recvData );
			udpserv.sendUdpData("�������Ļظ�-->"+recvData, destIp.substring(1), port);
		}
	}
	
	
    /**
     * Default constructor. 
     */
    public bombSvr() {
        // TODO Auto-generated constructor stub
    	if(udpserv==null)
    	{
    		IRecvDataHandle recvDataHandle = new OnRecvDataImp();
    		udpserv = new udpserver();
    		udpserv.Init(recvDataHandle);
    	}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String opString = request.getParameter("op");
		String roomIdString = request.getParameter("roomid");
		String usernameString = request.getParameter("username");
		String retString = "";
		
		if(opString == null || roomIdString==null || usernameString == null)
		{
			response.getWriter().write("REQUEST_PARM_ERROR");
			return;
		}
		
		
		if(opString.equals("create"))
		{
			retString = doCreateRoom(request, response);
		}
		else if(opString.equals("join"))
		{
			retString = doJoinRoom(request, response);
		}
		else if(opString.equals("delete"))
		{
			retString = doDeleteRoom(request, response);
		}
		else if(opString.equals("start"))
		{
			retString = doStartGame(request, response);
		}
		else if(opString.equals("updateroom"))
		{
			retString = doUpdateRoom(request, response);
		}
		else if(opString.equals("setready"))
		{
			retString = doSetReady(request, response);
		}
		else if(opString.equals("getstate"))
		{
			retString = doGetState(request, response);
		}
		else if(opString.equals("passbomb"))
		{
			retString = doPassBomb(request, response);
		}
		else if(opString.equals("quitgame"))
		{
			retString = doQuitGame(request, response);
		}
		else {
			retString = "NO_SUCH_OP";
		}
		
		// ����requestʱ��
		doRefreshReq(roomIdString, usernameString);

		//System.out.println("hi you just visit!");
		System.out.println("op="+opString);
		System.out.println("ret="+retString);
		
	}

	private String doRefreshReq(String roomIdString, String usernameString)
	{
		String retString = "";
		
		Lock lock = new ReentrantLock();  
		// ��ȡ��  
		lock.lock();  
	    try {  
	    	
	        biz.refreshRequestTime(roomIdString, usernameString);
	        biz.checkUserOnline(roomIdString);
	        biz.checkBombed(roomIdString);
	        biz.checkUserAllQuitGame(roomIdString);
	    	
	    } finally {  
	        // �ͷ���  
	        lock.unlock();  
	    } 
	    return retString;
	}
	
	private String doCreateRoom(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String retString = "";
		String roomIdString = request.getParameter("roomid");
		String hostuser = request.getParameter("username");
		String bombtime = request.getParameter("bombtime");
		
		if(bombtime == null)
		{
			retString = "CREATE_REQ_NO_BOMBTIME";
			response.getWriter().println(retString);
		    return retString;
		}
		Lock lock = new ReentrantLock();  
		// ��ȡ��  
		lock.lock();  
	    try {  
	    	int bombtt = Integer.parseInt(bombtime);
	        // access the resource protected by this lock  
	    	int ret = biz.createRoom(roomIdString, hostuser, bombtt);
	    	switch (ret) {
			case 0:
				retString = "CREATE_OK";
				break;
				
			case -1:
				retString = "CREATE_ROOMID_EXIST";
				break;
			
			default:
				retString = "CREATE_ERROR";
				break;
			}
	    	
	    } catch(Exception e)
	    {
	    	System.out.println(e.getMessage());	    	
	    }
	    finally {  
	        // �ͷ���  
	        lock.unlock();  
	    } 
	    response.getWriter().println(retString);
	    return retString;
	}
	
	private String doJoinRoom(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String retString = "";
		String roomIdString = request.getParameter("roomid");
		String username = request.getParameter("username");
		//String bombtime = request.getParameter("bombtime");
		Lock lock = new ReentrantLock();  
		// ��ȡ��  
		lock.lock();  
	    try {  
	    	
	        // access the resource protected by this lock  
	    	int ret = biz.joinRoom(roomIdString, username);
	    	switch (ret) {
			case 0:
				retString = "JOIN_OK";
				break;
				
			case -1:
				retString = "JOIN_NOT_EXIST";
				break;
			case -2:
				retString = "JOIN_ALREADY";
				break;
			default:
				retString = "JOIN_ERROR";
				break;
			}
	    	
	    } finally {  
	        // �ͷ���  
	        lock.unlock();  
	    } 
	    response.getWriter().println(retString);
	    return retString;
	}
	
	private String doUpdateRoom(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String retString = "";
		String roomIdString = request.getParameter("roomid");
		String hostuser = request.getParameter("username");
		String bombtime = request.getParameter("bombtime");
		Lock lock = new ReentrantLock();  
		// ��ȡ��  
		lock.lock();  
	    try {  
	    	
	        // access the resource protected by this lock  
	    	int ret = biz.updateRoom(roomIdString, hostuser, Integer.parseInt(bombtime));
	    	switch (ret) {
			case 0:
				retString = "UPDATE_OK";
				break;
				
			case -1:
				retString = "UPDATE_NOT_EXIST";
				break;
			case -2:
				retString = "UPDATE_NOT_HOST";
				break;
			case -3:
				retString = "UPDATE_GAMING";
				break;
			default:
				retString = "UPDATE_ERROR";
				break;
			}
	    	
	    } finally {  
	        // �ͷ���  
	        lock.unlock();  
	    } 
	    response.getWriter().println(retString);
	    return retString;
	}
	
	
	private String doDeleteRoom(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String retString = "";
		String roomIdString = request.getParameter("roomid");
		String hostuser = request.getParameter("username");
		Lock lock = new ReentrantLock();  
		// ��ȡ��  
		lock.lock();  
	    try {  
	    	
	        // access the resource protected by this lock  
	    	int ret = biz.deleteRoom(roomIdString, hostuser);
	    	switch (ret) {
			case 0:
				retString = "DELETE_OK";
				break;
				
			case -1:
				retString = "DELETE_NOT_EXIST";
				break;
			case -2:
				retString = "DELETE_NOT_HOST";
				break;
			case -3:
				retString = "DELETE_GAMING";
				break;
			default:
				retString = "DELETE_ERROR";
				break;
			}
	    	
	    } finally {  
	        // �ͷ���  
	        lock.unlock();  
	    } 
	    response.getWriter().println(retString);
	    return retString;
	}
	private String doSetReady(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String retString = "";
		String roomIdString = request.getParameter("roomid");
		String username = request.getParameter("username");
		Lock lock = new ReentrantLock();  
		// ��ȡ��  
		lock.lock();  
	    try {  
	        // access the resource protected by this lock  
	    	int ret = biz.setUserReady(roomIdString, username);
	    	switch (ret) {
			case 0:
				retString = "SETREADY_OK";
				break;
			case -1:
				retString = "SETREADY_NOT_EXIST";
				break;
			case -2:
				retString = "SETREADY_GAMING";
				break;
			case -3:
				retString = "SETREADY_NOT_IN_ROOM";
				break;
			default:
				retString = "SETREADY_ERROR";
				break;
			}
	    	
	    } finally {  
	        // �ͷ���  
	        lock.unlock();  
	    } 
	    response.getWriter().println(retString);
	    return retString;
	}
	private String doStartGame(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String retString = "";
		String roomIdString = request.getParameter("roomid");
		String hostuser = request.getParameter("username");
		Lock lock = new ReentrantLock();  
		// ��ȡ��  
		lock.lock();  
	    try {	    	
	        // access the resource protected by this lock  
	    	int ret = biz.startGame(roomIdString, hostuser);
	    	switch (ret) {
			case 0:
				retString = "START_OK";
				break;				
			case -1:
				retString = "START_NOT_EXIST";
				break;
			case -2:
				retString = "START_NOT_HOST";
				break;
			case -3:
				retString = "START_GAMING";
				break;
			case -4:
				retString = "START_NOT_ALL_READY";
				break;
			default:
				retString = "START_ERROR";
				break;
			}	    	
	    } finally {  
	        // �ͷ���  
	        lock.unlock();  
	    } 
	    response.getWriter().println(retString);
	    return retString;
	}
	
	private String doQuitGame(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String retString = "";
		String roomIdString = request.getParameter("roomid");
		String hostuser = request.getParameter("username");
		Lock lock = new ReentrantLock();  
		// ��ȡ��  
		lock.lock();  
	    try {	    	
	        // access the resource protected by this lock  
	    	int ret = biz.setQuitGame(roomIdString, hostuser);
	    	switch (ret) {
			case 0:
				retString = "QUITGAME_OK";
				break;				
			case -1:
				retString = "QUITGAME_NOT_EXIST";
				break;
			case -2:
				retString = "QUITGAME_GAMING";
				break;
			case -3:
				retString = "QUITGAME_NOT_IN_ROOM";
				break;
			case -4:
				retString = "QUITGAME_NOT_START";
				break;
			default:
				retString = "QUITGAME_ERROR";
				break;
			}	    	
	    } finally {  
	        // �ͷ���  
	        lock.unlock();  
	    } 
	    response.getWriter().println(retString);
	    return retString;
	}
	
	private String doPassBomb(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String retString = "";
		String roomIdString = request.getParameter("roomid");
		String username = request.getParameter("username");
		Lock lock = new ReentrantLock();  
		// ��ȡ��  
		lock.lock();  
	    try {  
	        // access the resource protected by this lock  
	    	// ����ֵ�� 0�ɹ����ݸ�����		1����̫���ˣ��ִ������Լ���	-1û�и÷��䣻	-2��Ϸû�п�ʼ;  
	    	//       -3�Ѿ������� -4��Ϸ�Ѿ�����ǰը��ӵ����Ϊʧ����	-5���ǵ�ǰ��ը��ӵ���ߣ��޷�����; 
	    	int ret = biz.passBombRandom(roomIdString, username);
	    	switch (ret) {
			case 0:
				retString = "PASS_OK";
				break;
			case 1:
				retString = "PASS_BACK";
				break;
			case -1:
				retString = "PASS_NOT_EXIST";
				break;
			case -2:
				retString = "PASS_NOT_START";
				break;
			case -3:
				retString = "PASS_BOMBED";
				break;
			case -4:
				retString = "PASS_BOMBED_YOUSELF";
				break;
			case -5:
				retString = "PASS_NOT_BOMB_KEEPER";
				break;
			default:
				retString = "PASS_ERROR";
				break;
			}
	    	
	    } finally {  
	        // �ͷ���  
	        lock.unlock();  
	    } 
	    response.getWriter().println(retString);
	    return retString;
	}
	
	private String doGetState(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String retString = "";
		String roomIdString = request.getParameter("roomid");
//		String username = request.getParameter("username");
		Lock lock = new ReentrantLock();  
		// ��ȡ��  
		lock.lock();  
	    try {    	
	    	room rmRoom = biz.getRoomState(roomIdString);
	    	if(rmRoom == null)
	    	{
	    		retString = "GETSTATE_ERROR";
	    	}
	    	else 
	    	{
	    		if(rmRoom.gamingState.equals(room.STATE_NOTSTART))
	    		{
	    			retString = buildRoomInfo("ROOM_INFO", rmRoom.roomidString,
	    					rmRoom.hostUsernameString, rmRoom.bombTime, rmRoom.userList, rmRoom.readyUserList);
	    		}
	    		else 
	    		{
	    			retString = buildGameInfo("GAME_INFO",rmRoom.gamingState, 
	    					rmRoom.bombUsernameString);
				}
	    		System.out.println("game remain (ms)"+ ((new Date()).getTime()-rmRoom.endTime.getTime()));
			}	    	
	    } finally {  
	        // �ͷ���  
	        lock.unlock();  
	    } 
	    response.getWriter().println(retString);
	    
	    return retString;		
	}

	/*
	 * 
	 * 
	 * 
	 * */
	private String buildRoomInfo(String type, String roomid, String hostname, int bombtime, List<String> userList, List<String> readyUserList) {
		String infoString = "";
		infoString += type +"\r\n";
		infoString += "ROOM_ID="+roomid + "\r\n";
		infoString += "BOMB_TIME="+Integer.toString(bombtime);
		for(int i=0;i<userList.size();i++)
		{
			infoString +=  "\r\n" + userList.get(i);
			if(readyUserList.contains(userList.get(i)))
			{
				infoString +=  "-->READY";
			}
			else {
				infoString +=  "-->NOT_READY";
			}
		}
		return infoString;
	}
	
	private String buildGameInfo(String type, String gameState, String bombUsername) {
		String infoString = "";
		infoString += type +"\r\n";
		infoString += "GAME_STATE=" + gameState + "\r\n";
		infoString += "BOMB_OWNER=" + bombUsername;
		return infoString;
	}

	/*
	 * 
	 *  
	 * c->s
	 * 
	 * 1������������������
	 * op=create && roomid=roomid123 && username=hostusername123 && bombtime=30
	 * ����ֵ��������Ϣ��ʽ 
	 * 		stateȡֵ:CREATE_OK �����ɹ�;	CREATE_ROOMID_EXIST ������Ѵ���; CREATE_ERROR ʧ��
	 * 
	 * 
	 * 
	 * 2����Ҽ��뷿������
	 * op=join && roomid=roomid123 && username=username456
	 * ����ֵ��������Ϣ��ʽ
	 * 		stateȡֵ:JOIN_OK ����ɹ�;	JOIN_NOT_EXIST ����Ų�����; JOIN_ALREADY ʧ��(��ǰ�Ѿ���ĳ��������)
	 * 
	 * 
	 * 3�������޸ķ�����Ϣ����:
	 * op=update && roomid=roomid123 && hostuser=hostusername123 && bombtime=30
	 * ����ֵ��UPDATE_OK�޸ĳɹ���  UPDATE_ERROR�޸�ʧ��
	 * 
	 * 
	 * 4������ɾ����������
	 * op=delete && roomid=roomid123 && hostuser=hostusername123
	 * ����ֵ��
	 * 
	 * 
	 * 
	 * 5�����뷿��ǰ��������
	 * op=getstate && roomid=roomid123 && username=username123
	 * ����ֵ��
	 * 
	 * 
	 * 
	 * 6����ҽ��뷿�����������
	 * op=getstate && roomid=roomid123 && username=username123
	 * ����ֵ��
	 * 
	 * 
	 * s->c
	 * �ͻ��˼��뷿��󣬶��ڷ�������������������˳���ȡ��ǰ����Ϸ״̬��Ϣ��
	 * 1�������Ϸû�п�ʼ���򽫷�����Ϣ���͸��ͻ��ˣ�������Ϣ��ʽ��
	 * 2�������Ϸ�Ѿ���ʼ����ը����ǰ������Ϣ���͸��ͻ��ˣ���Ϸ��Ϣ��ʽ��
	 * 
	 * ���������ͻ��˵Ļظ���Ϣ��ʽ��
	 * 1��������Ϣ��ʽ
		<room>
			<id>bomb_room_abc</id>
			<state>waiting</state> 	<!-- roomNotExist/waiting -->
			<hostname>hostusername</hostname>
			<users>
				<u>user1</u>
				<u>user2</u>
				<u>user3</u>
				<u>user4</u>
				<u>user5</u>
			</users>
			<bombtime>30</bombtime>
		</room>
	

	 * 2����Ϸ��Ϣ��ʽ
	   <gaming>
	   		<state>gaming</state>  <!-- gaming/bombed -->
			<bombusername>user123</bombusername>
	   </gaming>
	
	
	
	����
		create
	C1 ---------> 		Server					    
	   <<<<<<<<<<
	 	create_ok
										      join
										   <---------	C2
										   >>>>>>>>>>
										     join ok	     
	   get state
	   --------->
	   <<<<<<<<<<
	   room state
	 									   get state
										   <---------
										   >>>>>>>>>>
										   room state
	 	........	
	 	......							   ........
	 	.....	
										   get state
										   <---------
										   >>>>>>>>>>
										   room state
	 	update
	   --------->
	   <<<<<<<<<<
	   update_ok
	   									   set ready
										   <---------
										   >>>>>>>>>>
										   setready_ok
	   set ready
	   --------->
	   <<<<<<<<<<
	   setready_ok
	   
	     start
	   ---------->							.....
	   <<<<<<<<<<<
	   	start_ok
	 									   get state
										   <---------
										   >>>>>>>>>>
										   game state
	   get state
	   --------->
	   <<<<<<<<<<
	   game state
	   
	   
	   
	   pass bomb
	   --------->
	   <<<<<<<<<<
	   game state
	   
										   pass bomb
										   <---------
										   >>>>>>>>>>
										   game state
										   
										   
	 * */
}
