package com.jule.bomb.srv.biz;

import com.jule.bomb.model.room;

public interface IBombBiz {
	// ������������
	// ����	����š��������֡�ը����ըʱ��(��)
	// ����ֵ 0�ɹ���	-1 �����Ѿ����ڣ�
	public abstract int createRoom(String roomIdString, String hostUsername, int bombTime);
	
	
	// �����޸ķ����ը��ʱ��
	// ����	����š��������֡�ը����ըʱ��(��)
	// ����ֵ 0�ɹ���	-1 ���䲻���ڣ�		-2���Ƿ�����	-3������Ϸ�޷��޸�
	public abstract int updateRoom(String roomIdString, String hostUsername, int bombTime);
	
		
	// ����ɾ������
	// ����	����š��������֡�ը����ըʱ��(��)
	// ����ֵ 0�ɹ���	-1 ���䲻���ڣ�	-2���Ƿ�����	-3������Ϸ��
	public abstract int deleteRoom(String roomIdString, String hostUsername);
		
	// ������Ҽ��뷿��
	// ������	����š�����û���
	// ����ֵ 0�ɹ���	-1 ���䲻����;	-2��ǰ����Ѿ��ڷ�����
	public abstract int joinRoom(String roomIdString, String username);
	
	
	// ��ȡ��ǰ����״̬
	// ������	����š�����û���
	// ����ֵ�� null ��ȡʧ��;
	public abstract room getRoomState(String roomIdString);	
	
	// �������׼�����˵�״̬
	// ������	����š�����û���
	// ����ֵ 0�ɹ���	-1 ���䲻���ڣ�		-2��Ϸ���ڽ���
	public abstract int setUserReady(String roomIdString, String username);
				
	// ������ʼ��Ϸ
	// ����	����š���������
	// ����ֵ 0�ɹ���ʼ��Ϸ��	-1 ���䲻���ڣ�		-2 ���Ƿ���;		-3��Ϸ���ڽ���
	public abstract int startGame(String roomIdString, String hostUsername);
	
	// ���ܣ�  ����ը��
	// ����	��ǰ����ID�š���ǰը��ӵ����
	// ����ֵ�� 0�ɹ����ݸ�����		1����̫���ˣ��ִ������Լ���	-1û�и÷��䣻	-2��Ϸû�п�ʼ;  -3�Ѿ�������-4��Ϸ�Ѿ�����ǰը��ӵ����Ϊʧ����	-5���ǵ�ǰ��ը��ӵ���ߣ��޷�����;  	
	public abstract int passBombRandom(String roomIdString, String oldBombUser);

}
