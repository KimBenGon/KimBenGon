package _client.view;

import com.pi4j.component.motor.impl.GpioStepperMotorComponent;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class MotorControl implements Runnable{
	
	//final GpioController gpio = GpioFactory.getInstance();
	
	/*
	public Pin pin22 = RaspiPin.GPIO_22;	// PI4J custom numbering (pin 33)
	public Pin pin21 = RaspiPin.GPIO_21;	// PI4J custom numbering (pin 33)
	public Pin pin05 = RaspiPin.GPIO_05;	// PI4J custom numbering (pin 33)
	public Pin pin04 = RaspiPin.GPIO_04;	// PI4J custom numbering (pin 33)
    */                                                                                             
	/*
	public GpioPinDigitalOutput[] pins = new GpioPinDigitalOutput[] { 
	    		gpio.provisionDigitalOutputPin(pin04, PinState.LOW), 
	    		gpio.provisionDigitalOutputPin(pin05, PinState.LOW), 
	    		gpio.provisionDigitalOutputPin(pin21, PinState.LOW),
	    		gpio.provisionDigitalOutputPin(pin22, PinState.LOW)
	    		};
	
	private void cleanup(){
		gpio.shutdown(); 
		gpio.unprovisionPin(pins);
	}
	*/
	public GpioPinDigitalOutput[] pins = new GpioPinDigitalOutput[4];
	
	MotorControl(GpioPinDigitalOutput[] pins){
		this.pins = pins;
	}
	
	@Override
	public void run() {
		try {
				GpioStepperMotorComponent motor = new GpioStepperMotorComponent(pins);
		    	
				PiControl.gpio.setShutdownOptions(true, PinState.LOW, pins);
				
		    	byte[] double_step_sequence = new byte[4];
		        double_step_sequence[0] = (byte) 0b0110;
		        double_step_sequence[1] = (byte) 0b0101;
		        double_step_sequence[2] = (byte) 0b1001;
		        double_step_sequence[3] = (byte) 0b1010;
		        
				
				byte[] single_step_sequence = new byte[4];
		        single_step_sequence[0] = (byte) 0b0001;
		        single_step_sequence[1] = (byte) 0b0010;
		        single_step_sequence[2] = (byte) 0b0100;
		        single_step_sequence[3] = (byte) 0b1000;
		        
		        //motor.setStepInterval(2);
		        motor.setStepInterval(4);
		        motor.setStepSequence(double_step_sequence);
		        motor.setStepsPerRevolution(2038);

		        if(PiControl.doorFlag == true){
			        System.out.println("셔터를 열고 있습니다.");
			        motor.step(508);
			       
			        System.out.println("셔터를 다 열었습니다.");
			        Thread.sleep(1000);
			        PiControl.doorFlag = false;
		        } else if(PiControl.doorFlag == false){
		        	System.out.println("셔터를 닫고 있습니다.");
				    motor.step(-508);
		        	
				    System.out.println("셔터를 완전히 닫았습니다.");
				    Thread.sleep(1000);
				    PiControl.doorFlag = true;
		        }
		        
		        motor.stop();
		    
		} catch (InterruptedException e){
			e.printStackTrace();
		} finally{
			
		}
		
    }

}

/*
package _client.view;

import com.pi4j.component.motor.impl.GpioStepperMotorComponent;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class MotorControl implements Runnable{
	
	@Override
	public void run() {
		try {
			
				final GpioController motorGpio = GpioFactory.getInstance();
				
			                                                 
				
				final GpioPinDigitalOutput[] pins = new GpioPinDigitalOutput[] { 
				    		motorGpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.LOW), 
				    		motorGpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, PinState.LOW), 
				    		motorGpio.provisionDigitalOutputPin(RaspiPin.GPIO_21, PinState.LOW),
				    		motorGpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, PinState.LOW)
				};
				motorGpio.setShutdownOptions(true, PinState.LOW, pins);
				
				GpioStepperMotorComponent motor = new GpioStepperMotorComponent(pins);
		    	
				
		    	byte[] double_step_sequence = new byte[4];
		        double_step_sequence[0] = (byte) 0b0011;
		        double_step_sequence[1] = (byte) 0b0110;
		        double_step_sequence[2] = (byte) 0b1100;
		        double_step_sequence[3] = (byte) 0b1001;
		        
				
				byte[] single_step_sequence = new byte[4];
		        single_step_sequence[0] = (byte) 0b0001;
		        single_step_sequence[1] = (byte) 0b0010;
		        single_step_sequence[2] = (byte) 0b0100;
		        single_step_sequence[3] = (byte) 0b1000;
		        
		        motor.setStepInterval(2);
		        motor.setStepSequence(single_step_sequence);
		        motor.setStepsPerRevolution(2038);

		        
		        System.out.println("   Motor FORWARD for 2038 steps.");
		        motor.step(2038);
		        System.out.println("   Motor STOPPED for 2 seconds.");
		        Thread.sleep(2000);
		        
		        System.out.println("   Motor REVERSE for 2038 steps.");
		        motor.step(-2038);
		        System.out.println("   Motor STOPPED for 2 seconds.");
		        Thread.sleep(2000);
		        
		        motor.stop();
		        System.out.println("Exiting StepperMotorGpioExample");
		        motorGpio.shutdown();
		} catch (InterruptedException e){

		} finally{
			
		}
		
    }

}
*/
