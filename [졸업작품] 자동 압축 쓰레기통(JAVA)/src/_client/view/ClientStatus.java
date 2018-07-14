package _client.view;

import java.io.DataOutputStream;
import java.io.IOException;

public class ClientStatus implements Runnable {
	
	private SendStatus send = new SendStatus();
	private DataOutputStream out;
	
	ClientStatus(DataOutputStream out){
		this.out = out;
	}
	
	@Override
	public void run() {
		try{
			while(!Thread.currentThread().isInterrupted()){		//스레드가 인터럽트되면 while문을 빠져나옴
				
				/*
				 * trashStatus, stayStatus
				 * 위 두개의 값이 다르면 전송하고 같으면 전송하지 않음
				 * 아래 설정을 해두지 않으면 상태값을 너무 많이 보내게 됨
				 */
				Thread.sleep(1000);
				System.out.println("Pi trashStatus: " + PiControl.trashStatus);
				System.out.println("PC stayStatus: " + ClientTrash.stayStatus);
				send.StatusToServer(out);
			}
		} catch(InterruptedException e){ 			// 스레드가 멈췄을 때 예외
			System.out.println("상태값 전달 중지");
		} catch (IOException e) {					//서버와 연결이 끊어졌을 때 예외
			e.printStackTrace();
		}
		finally {
		}
	}
}
