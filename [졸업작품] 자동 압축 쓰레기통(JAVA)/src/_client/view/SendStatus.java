package _client.view;

import java.io.DataOutputStream;
import java.io.IOException;

public class SendStatus  {
  public void StatusToServer(DataOutputStream out) throws InterruptedException, IOException{
	  /*
	   * stayStatus와 trashStatus 값이 달라야 서버로 전송함
	   */
	  if(ClientTrash.stayStatus != PiControl.trashStatus){
		  //System.out.println("ClientStatus: " + ClientPc.stayStatus + "   " + PiControl.trashStatus);
		  out.writeUTF("상태");
		  String msg = Integer.toString(PiControl.trashStatus);
		  out.writeUTF(msg);
		  out.flush();
		  System.out.println("서버로 보내는 상태값: "+ PiControl.trashStatus);
		  ClientTrash.stayStatus = PiControl.trashStatus;
		}  
  	}
}
