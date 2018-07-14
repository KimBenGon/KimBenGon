package _client.view;

import com.pi4j.io.gpio.*;
import com.pi4j.component.motor.impl.GpioStepperMotorComponent;

public class PiControl implements Runnable {
	public static int trashStatus = 0;
	public static boolean doorFlag = false;		//false:열림 , true: 닫힘
	public static boolean autoOff = false;
	public static int sensorCount = -1;
	public static int sonicCount = 0;
	public static int sonicCount2 = 0;
	public static int sonicCount3 = 0;
	public static int pressWork = 0;		//0 안함, 1: 한 상태
	public static boolean root = false;
	
	private final static float SOUND_SPEED = 340.29f;  		// 음속
    private final static int TRIG_DURATION_IN_MICROS = 10; 	// trigger duration of 10 micro s
    private final static int WAIT_DURATION_IN_MILLIS = 1000;	 // wait 60 milli s
    private final static int TIMEOUT = 2100;
    public final static GpioController gpio = GpioFactory.getInstance();
    
    public Pin pin29 = RaspiPin.GPIO_29; // PI4J custom numbering (pin 40)
	public Pin pin28 = RaspiPin.GPIO_28; // PI4J custom numbering (pin 38)
	public Pin pin25 = RaspiPin.GPIO_25; // PI4J custom numbering (pin 37)
	public Pin pin24 = RaspiPin.GPIO_24; // PI4J custom numbering (pin 35)
	public Pin pin23 = RaspiPin.GPIO_23; // PI4J custom numbering (pin 33)
	public Pin pin07 = RaspiPin.GPIO_07; // PI4J custom numbering (pin 07)
	public Pin pin03 = RaspiPin.GPIO_03; // PI4J custom numbering (pin 15)
	public Pin pin02 = RaspiPin.GPIO_02; // PI4J custom numbering (pin 13)
	public Pin pin00 = RaspiPin.GPIO_00; // PI4J custom numbering (pin 11)
	/*
	public Pin pin27 = RaspiPin.GPIO_27;	// PI4J custom numbering (pin 33)	
	public Pin pin26 = RaspiPin.GPIO_26;	// PI4J custom numbering (pin 33)
	*/
	public Pin pin22 = RaspiPin.GPIO_22;	// PI4J custom numbering (pin 31)
	public Pin pin21 = RaspiPin.GPIO_21;	// PI4J custom numbering (pin 29)
	public Pin pin05 = RaspiPin.GPIO_05;	// PI4J custom numbering (pin 18)
	public Pin pin04 = RaspiPin.GPIO_04;	// PI4J custom numbering (pin 16)
	
    
    public GpioPinDigitalInput echo;
    public GpioPinDigitalInput echo2;
    public GpioPinDigitalInput echo3;
    public GpioPinDigitalOutput trig;
    public GpioPinDigitalOutput trig2;
    public GpioPinDigitalOutput trig3;
    public GpioPinDigitalOutput red;
    public GpioPinDigitalOutput yellow;
    public GpioPinDigitalOutput green;
    
    public GpioPinDigitalOutput[] pins = new GpioPinDigitalOutput[4];
    public GpioPinDigitalOutput in1;
    public GpioPinDigitalOutput in2;
    public GpioPinDigitalOutput in3;
    public GpioPinDigitalOutput in4;
    /*
    public GpioPinDigitalOutput enA;
    public GpioPinDigitalOutput enB;
   */
    Runnable motorRun;
    Thread piMotorRun;
    
    PiControl(){
   
    }
  
    //gpio핀 설정 함수
    public void setup(){
    	
    	echo = gpio.provisionDigitalInputPin(pin29);
    	echo2 = gpio.provisionDigitalInputPin(pin00);
    	echo3= gpio.provisionDigitalInputPin(pin02);
        trig = gpio.provisionDigitalOutputPin(pin28);
        trig2 = gpio.provisionDigitalOutputPin(pin07);
        trig3 = gpio.provisionDigitalOutputPin(pin03);
        trig.low();
        trig2.low();
        trig3.low();

        red = gpio.provisionDigitalOutputPin(pin25);
        yellow = gpio.provisionDigitalOutputPin(pin24);
        green = gpio.provisionDigitalOutputPin(pin23);
       
        
        pins = new GpioPinDigitalOutput[] { 
        		gpio.provisionDigitalOutputPin(pin04), 
	    		gpio.provisionDigitalOutputPin(pin05), 
	    		gpio.provisionDigitalOutputPin(pin21),
	    		gpio.provisionDigitalOutputPin(pin22)
	    		/*gpio.provisionDigitalOutputPin(pin04, PinState.LOW), 
	    		gpio.provisionDigitalOutputPin(pin05, PinState.LOW), 
	    		gpio.provisionDigitalOutputPin(pin21, PinState.LOW),
	    		gpio.provisionDigitalOutputPin(pin22, PinState.LOW)
	    		*/
	    		};
    }
    /*
    public void setup2(){
    	echo = gpio.provisionDigitalInputPin(pin29);
    	trig = gpio.provisionDigitalOutputPin(pin28);
    	trig.low();
    }
    
    public void setup3(){
    	echo2 = gpio.provisionDigitalInputPin(pin00);
    	trig2 = gpio.provisionDigitalOutputPin(pin07);
    	trig2.low();
    }
    
    public void setup4(){
    	echo3= gpio.provisionDigitalInputPin(pin02);
    	trig3 = gpio.provisionDigitalOutputPin(pin03);
    	trig3.low();
    }
    
    public void cleanup2(){
    	gpio.unprovisionPin(echo);
		gpio.unprovisionPin(trig);
    }
    
    public void cleanup3(){
    	gpio.unprovisionPin(echo2);
		gpio.unprovisionPin(trig2);
    }
    
    public void cleanup4(){
    	gpio.unprovisionPin(echo3);
		gpio.unprovisionPin(trig3);
    }
    */
    //gpio핀 해제 함수
    public void cleanup(){
		gpio.shutdown();
		
		gpio.unprovisionPin(echo);
		gpio.unprovisionPin(trig);
		gpio.unprovisionPin(echo2);
		gpio.unprovisionPin(trig2);
		gpio.unprovisionPin(echo3);
		gpio.unprovisionPin(trig3);
		
		gpio.unprovisionPin(red);
		gpio.unprovisionPin(yellow);
		gpio.unprovisionPin(green);
		gpio.unprovisionPin(pins);
    }

    public float measureDistance() throws TimeoutException, InterruptedException {
        this.triggerSensor();
        this.waitForSignal();
        long duration = this.measureSignal();
        
        return duration * SOUND_SPEED / ( 2 * 10000 );
    }

    //트리거 센서 함수
    private void triggerSensor() throws InterruptedException {
    	if(sensorCount == 0){
    		this.trig.high();
            Thread.sleep( 0, TRIG_DURATION_IN_MICROS * 1000 );
            this.trig.low(); 
            
    	} else if(sensorCount == 1){
    		this.trig2.high();
            Thread.sleep( 0, TRIG_DURATION_IN_MICROS * 1000 );
            this.trig2.low(); 
            
    	} else if(sensorCount == 2){
    		this.trig3.high();
            Thread.sleep( 0, TRIG_DURATION_IN_MICROS * 1000 );
            this.trig3.low(); 
    	}
    }
    
    //일정시간동안 초음파가 들어오지 않으면 timeout 예외 실행
    private void waitForSignal() throws TimeoutException {
        int countdown = TIMEOUT;
        if(sensorCount == 0){
        	while( this.echo.isLow() && countdown > 0 ) {
                countdown--;
            }
            
    	} else if(sensorCount == 1){
    		while( this.echo2.isLow() && countdown > 0 ) {
                countdown--;
            }
            
    	} else if(sensorCount == 2){
    		while( this.echo3.isLow() && countdown > 0 ) {
                countdown--;
            }
    	}

        if( countdown <= 0 ) {
            throw new TimeoutException( "Timeout waiting for signal start" + sensorCount + 1 );
        }
    }
    
    // 시간계산 함수
    private long measureSignal() throws TimeoutException {
        int countdown = TIMEOUT;
        long start = System.nanoTime();
        
        if(sensorCount == 0){
        	this.echo2.isHigh();
        	this.echo3.isHigh();
        	 while( this.echo.isHigh() ) {
             }
            
    	} else if(sensorCount == 1){
    		this.echo.isHigh();
        	this.echo2.isHigh();
    		 while( this.echo2.isHigh() ) {
    	        }
            
    	} else if(sensorCount == 2){
    		this.echo.isHigh();
        	this.echo2.isHigh();
    		 while( this.echo3.isHigh() ) {
    	        }
    	}
  
        long end = System.nanoTime();
        
        if( countdown <= 0 ) {
            throw new TimeoutException( "Timeout waiting for signal end"  + sensorCount + 1);
        }
        
        return (long)Math.ceil( ( end - start ) / 1000.0 ); 
    }
    
    //타임아웃 예외 함수
    private static class TimeoutException extends Exception {

		private static final long serialVersionUID = -1464063358281663453L;
		private final String reason;
        
        public TimeoutException( String reason ) {
            this.reason = reason;
        }
        
        @Override
        public String toString() {
            return this.reason;
        }
    }
    
    //초음파 센서 함수
    private void TrashSonic() throws InterruptedException{ 
            try {
                System.out.printf( "%1$d,%2$.3f%n", System.currentTimeMillis(), measureDistance() );
                TrashLED(measureDistance());
            }
            catch( TimeoutException e ) {
                System.err.println( e );
            }
            Thread.sleep( WAIT_DURATION_IN_MILLIS );
    }
    
    //LED 제어 함수
    private void TrashLED(float distance) throws InterruptedException{
    	
    
    	
    	if(doorFlag == false) {
    		if(autoOff == false) {
    			if(ArduinoControl.weightFlag == false) {
    				trashStatus = 1;
    			}
    			else if(ArduinoControl.weightFlag == true) {
    				if(distance < 20.0) {
    					trashStatus = 2;
    				}
    				else if (distance >= 20.0) {
    					trashStatus = 3;
    				}
    			}	
    		}
    		else if(autoOff == true) {
    			if(ArduinoControl.weightFlag == false) {
    				trashStatus = 4;
    			}
    			else if(ArduinoControl.weightFlag == true) {
    				if(distance < 20.0) {
    					trashStatus = 5;
    				}
    				else if(distance >= 20.0) {
    					trashStatus = 6;
    				}
    			}
    		}
    	}
    	else if(doorFlag == true) {
    		if(autoOff == false) {
    			if(ArduinoControl.weightFlag == false) {
    				trashStatus = 7;
    			}
    			else if(ArduinoControl.weightFlag == true) {
    				if(distance < 20.0) {
    					trashStatus = 8;
    				}
    				else if (distance >= 20.0) {
    					trashStatus = 9;
    				}
    			}	
    		}
    		else if(autoOff == true) {
    			if(ArduinoControl.weightFlag == false) {
    				trashStatus = 10;
    			}
    			else if(ArduinoControl.weightFlag == true) {
    				if(distance < 20.0) {
    					trashStatus = 11;
    				}
    				else if(distance >= 20.0) {
    					trashStatus = 12;
    				}
    			}
    		}
    	}
    	
    	if(distance >= 20){
    		if(trashStatus % 3 == 2){
    			trashStatus++;
    		}
    	}

    	if(pressWork == 1){
    		trashStatus = 15;
    	}
  
    	if(trashStatus % 3 == 0) {
    		this.red.low();
        	this.yellow.low();
        	this.green.high();
    	}
    	else if(trashStatus % 3 == 1) {
    		this.red.high();
        	this.yellow.low();
        	this.green.low();
    	}
    	else if(trashStatus % 3 == 2) {
    		this.red.low();
        	this.yellow.high();
        	this.green.low();
    	}
    	System.out.println("trashStatus: " + trashStatus);
    	
    }
     
	@Override
	public void run() {
		try {		
			
			int pressCount = 0;
			
			setup();
			while(!Thread.currentThread().isInterrupted()){
				/*
				 * 서버 강제제어 플래그
				 * compulsionDoor 
				 * - true: 강제 제어 중
				 * - false: 강제 제어 아님
				 * 
				 * 무게 제어
				 * weightFlag
				 * - true: 가벼움
				 * - false: 무거움
				 * 
				 * 압축 플래그
				 * pressFlag
				 * - true: 압축하라는 플래그
				 * - false: 압축 X
				 * 
				 * 초음파 센서 값
				 * trashStatus % 3 
				 * - 1: 걸림
				 * - 2: 무게 무거움
				 * - 0: 양호
				 * 
				 * 문 개폐여부
				 * doorFlag
				 * - true: 문 닫힌 상태
				 * - false: 문 열린 상태
				 * 
				 * autoOff
				 * - true: 자동모드 OFF
				 * - false: 자동모드 ON
				 * 
				 * 서버가 제어해서 닫힌 상태 일때
				 * root
				 * - true: 서버가 제어함
				 * - false: 자체적으로 문을 닫은 상태임
				 * 
				 */
				
				/*
				 * 
				 * 1-1. 서버의 제어 명령이 내려왔을 때
				 * 	2-1. 문이 열려있으면 닫는다.
				 * 		root = 1 
				 * 	2-2. 문이 닫혀있을 때
				 * 		3-1. 문을 연다.
				 * 			root = 0
 				 * 
				 * 1-2. 서버의 제어명령이 없을 때
				 * 		2-1. 자동모드 OFF
				 * 			3-1. continue;
				 * 		2-2. 자동모드 ON
				 * 			3-1. 문이 닫혀있다면
				 * 				4-1. 서버가 제어해서 닫힌 상태일 때(root = 1)
				 * 					continue;
				 * 				4-2. 자체적으로 문을 닫은 상태라면(root = 0)
				 * 					5-1. 무게가 무겁다면
				 * 						continue;
				 * 					5-2. 무게가 가볍고
				 * 						6-1. 초음파에 걸릴 때
				 * 							continue;
				 * 						6-2. 초음파에 걸리지 않는다면
				 * 							7-1. 문을 연다.
				 * 			3-2. 문이 열려있을 때
				 * 				4-1. 무게가 무겁다면
				 * 					5-1. 문을 닫는다.
				 * 				4-2. 무게가 가벼울 때
				 * 					5-1. 초음파센서의 값이 클 때
				 * 						continue;
				 * 					5-2. 초음파센서의 값이 작으면
				 * 						6-1. 문을 닫는다.
				 * 						6-2. 문이 다 닫히면 압축을 한다.
				 * 						6-3. 초음파 센서를 동작한다.
				 * 							7-1. 만약 3초이내에 초음파센서에 다시 감지 된다면
				 * 								continue;
				 * 							7-2. 3초이내에 초음파 센서에 감지되지 않으면
				 * 								8-1. 문을 연다.
				*/
				sensorCount++;
				System.out.println("==================================");
				if(sensorCount == 3) {
					sensorCount = 0;
				}
				
				
				TrashSonic();
				System.out.println("Sonic Sensor: " + sensorCount);
				System.out.println("Sonic1 : " + sonicCount);
				System.out.println("Sonic2 : " + sonicCount2);
				System.out.println("Sonic3 : " + sonicCount3);
				//System.out.println("초음파 센서 빠져나와 버리기!");
				if(sensorCount == 0){
					if(trashStatus % 3 == 2)
						sonicCount++;
					else
						sonicCount = 0;
				}
				else if(sensorCount == 1) {
					if(trashStatus % 3 == 2)
						sonicCount2++;
					else
						sonicCount2 = 0;
				}
				else if(sensorCount == 2) {
					if(trashStatus % 3 == 2)
						sonicCount3++;
					else
						sonicCount3 = 0;
				}
				
				
				if (ClientTrash.compulsionDoor == true) {
					//System.out.println("어명이다.");
					if(doorFlag == false) {
						//System.out.println("어명이니까 문 닫아 병신아");
						motorRun = new MotorControl(pins);
					    piMotorRun = new Thread(motorRun);
					    piMotorRun.start();
					    Thread.sleep(6000);
					    root = true;
					    ClientTrash.compulsionDoor = false;
					} 
					else if(doorFlag == true) {
						//System.out.println("어명이니까 문열어 병신아");
						motorRun = new MotorControl(pins);
					    piMotorRun = new Thread(motorRun);
					    piMotorRun.start();
					    Thread.sleep(6000);
					    root = false;
					    ClientTrash.compulsionDoor = false;
					}
				} else if (ClientTrash.compulsionDoor == false) {
					//System.out.println("ㅇㅇ?");
					if(autoOff == true) {
						//System.out.println("응~ 일 안해~ ^^");
						continue;
					} 
					else if(autoOff == false) {
						//System.out.println("자동모드 ~");
						if(doorFlag == true) {
							//System.out.println("문 닫혀있음~");
							if(root == true) {
								//System.out.println("서바가 강제로 닫은거니까 꺼져 ^^");
								continue;
							}
							else if(root == false) {
								//System.out.println("미안 문 열게..");
								if(ArduinoControl.weightFlag == false) {
									//System.out.println("근데 무거우니까 안열꺼야 ㅄ아 ^^");
									continue;
								}
								else if(ArduinoControl.weightFlag == true) {
									
									//System.out.println("가벼워!");
									if(trashStatus % 3 == 2) {
										//System.out.println("근데 센서에 걸리니까 꺼져 ^^");
										pressCount = 0;
										continue;
									}
									else if(trashStatus % 3 == 0) {
										if(pressWork == 1) {
											pressCount++;
											System.out.println("pressCount: " + pressCount);
											if(pressCount == 6) {
												System.out.println("압축해서 문 닫은 거 열어주기(비워짐)");
												motorRun = new MotorControl(pins);
											    piMotorRun = new Thread(motorRun);
											    piMotorRun.start();
											    Thread.sleep(6000);
											    pressWork = 0;
											    pressCount = 0;
											    sonicCount = 0;
											    sonicCount2 = 0;
											    sonicCount3 = 0;
											}
											continue;
										} 
										else {
											
											System.out.println("미안 문 열어줄게");
											motorRun = new MotorControl(pins);
										    piMotorRun = new Thread(motorRun);
										    piMotorRun.start();
										    Thread.sleep(6000);
										    pressWork = 0;
											System.out.println("쓰레기통 비워짐");
											continue;
										}
									}
								}
							}
						}
						else if (doorFlag == false) {
							//System.out.println("문 열려있음!!");
							if(ArduinoControl.weightFlag == false) {
								//System.out.println("근데 무거우니까 닫을게!!!");
								motorRun = new MotorControl(pins);
							    piMotorRun = new Thread(motorRun);
							    piMotorRun.start();
							    Thread.sleep(6000);
							} 
							else if(ArduinoControl.weightFlag == true) {
								//System.out.println("가벼워!!!");
								if(trashStatus % 3 == 0) {
									//System.out.println("이상없으니까 꺼져!!!");
									
									if(pressWork == 1) {
										sonicCount = 0;
										sonicCount2 = 0;
										sonicCount3 = 0;
										pressWork = 0;
										System.out.println("쓰레기통 비워짐");
									}
									
									continue;
								}
								else if(trashStatus % 3 == 2) {
									//System.out.println("센서에 걸리네??? 압축할까???");
									
									if(sonicCount == 3 || sonicCount2 == 3 || sonicCount3 == 3) {
										System.out.println("압축시작");
										System.out.println("문 닫기");
										
										if(pressWork == 0) {
											pressWork = 1;
											motorRun = new MotorControl(pins);
										    piMotorRun = new Thread(motorRun);
										    piMotorRun.start();
										    Thread.sleep(6000);
										    
										    ArduinoControl.pressFlag = true;
										    Thread.sleep(15000);
										    System.out.println("압축 완료");
									    }
									}
									continue;
								}
							}
						}
					}
				}
			}
		} catch(InterruptedException e){
			e.printStackTrace();
		}finally {
			//System.out.println("어라 얘가 나오면 안되는데1");
			cleanup();
			sensorCount = -1;
		}
	}
}