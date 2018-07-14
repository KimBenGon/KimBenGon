package _client.view;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.DataOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class ArduinoControl implements Runnable{
    /*public ArduinoControl()
    {
        super();
    }*/
	DataOutputStream out;
	public static boolean weightFlag = true;		// true: 가벼움, false: 무거움
	public static boolean weightOne = true; 		// true: 1kg 보다 가벼움, false: 1kg 보다 무거움
	public static boolean pressFlag = false;		// true: 압축해라!, false: 압축 ㄴㄴ
	
	ArduinoControl(){
		
	}
	
	ArduinoControl(DataOutputStream out){
		this.out = out;
	}
    
    void connect ( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        System.out.println("아두이노 connect 함수 진입!");
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            //클래스 이름을 식별자로 사용하여 포트 오픈
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
                //포트 설정(통신속도 설정. 기본 9600으로 사용)
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                //Input,OutputStream 버퍼 생성 후 오픈
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                
                 //읽기, 쓰기 쓰레드 작동
                System.out.println("스트림 스레드 생성 직전!");
                (new Thread(new SerialReader(in))).start();
                (new Thread(new SerialWriter(out))).start();

            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }
    
    //데이터 수신
    /*
     * 1. 무게센서에서 bye형식의 데이터를 받아오고
     * 2. byte를 float형으로 바꿔준다.
     * 
     * 3-1. Float로 바꾼 값이 5보다 크면
     * 4-1. heavyCount를 1씩 증가시킨다.
     * 5-1. 이전에 들어온 값이 현재 들어온 값과 차이가 많이 나면
     * 5-2. heavyCount를 증가시키지 않는다.
     * 
     * 4-2. heavyCount = 3이 성립하면
     * 5-1. 무게가 최대 부피를 초과했다는 값을 설정한다.
     * 
     * 5. 이때 무게가 가벼워진다면
     * 6-1. lightCount를 증가시켜준다. 
     * 6-2. lightCount = 3이고 heavyCount가 3이 된다면
     * 6-3. 무게가 가벼워졌다는 값을 설정한다.
     * 
     * 3-2. Float로 바꾼 값이 5보다 작고
     * 4-1. 값이 1보다 작다면 
     * 5-1. weightOne 값을 true로 변경한다.
     * 
     * 4-2. 값이 1보다 크다면
     * 5-2. weightOne 값을 false로 변경한다.
     */
    public class SerialReader implements Runnable 
    {
        InputStream in;
        
        public SerialReader ( InputStream in)
        {
            this.in = in;
        }
        
        public void run ()
        {
        	String weight;
        	int heavyCount = 0;
        	int lightCount = 0;
            byte[] buffer = new byte[1024];
            int len = -1;
            
            try
            {

            	Float weightFloat;
            	Float preFloat = (float)0;
                while ( ( len = this.in.read(buffer)) > -1 )
                {	
                	Thread.sleep(1000);
                	weight = new String(buffer, 0, len);

                	String[] strarr = weight.split("\n");
                	for(int i = 0; i < strarr.length; i++){
                		if(!strarr[i].trim().equals("")) {
                			if(strarr[i].equals("-\n"))
                				break;
                			
	                		weightFloat = Float.parseFloat(strarr[i]);
	                		
	                		
	                		if(weightFloat > 5) {
	                			if(weightFloat - preFloat >= 2 || weightFloat - preFloat <= -2){
	                				System.out.println("값 튐 return^");
	                				System.out.println("무거움: " + weightFloat + "kg, " + heavyCount);
	                				preFloat = weightFloat;
	                				
	                				if(heavyCount >= 3){
		                				break;
		                			}
	                				
	                				heavyCount = 0;
	                			}
	                			
	                			heavyCount++;
	                			System.out.println("무거움: " + weightFloat + "kg, " + heavyCount);
	                			if(heavyCount == 3) {
	                				weightFlag = false;
	                				lightCount = 0;
	                			}
	                			
	                		} else if(weightFloat <= 5) {
	                			if(weightFloat - preFloat >= 2 || weightFloat - preFloat <= -2){
	                				System.out.println("값 튐 return");
	                				System.out.println("가벼움: " + weightFloat + "kg, " + lightCount);
	                				preFloat = weightFloat;
	                				
	                				if(lightCount >= 3){
		                				break;
		                			}
	                				lightCount = 0;
	                			}
	                			
	                			lightCount++;
	                			System.out.println("가벼움: " + weightFloat + "kg, " + lightCount);
	                			if(lightCount == 3) {
	                				weightFlag = true;
	                				heavyCount = 0;
	                			}
	                		}
	                		break;
                		}
                	}
                }
            }
            catch ( IOException | InterruptedException e )
            {
                e.printStackTrace();
            }    
        }
    }
    
    public static class SerialWriter implements Runnable {
        OutputStream out;
        
        public SerialWriter ( OutputStream out )
        {
            this.out = out;
        }
        
        public void run ()
        {
            try
            {
                byte b = '1';
                while ( true ) {
                	Thread.sleep(1000);
                	System.out.println("송신 대기");
                	if(pressFlag == true) {
                		this.out.write(b);
                		System.out.println("########## 리니어 모터 작동 ##########");
                		pressFlag = false;
                	}
                }                
            }
            catch ( IOException | InterruptedException e )
            {
                e.printStackTrace();
            }            
        }
    }
    
	@Override
	public void run() {
		try{
			connect("/dev/ttyACM0");
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}