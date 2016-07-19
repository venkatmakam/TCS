

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.ImageIcon;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.Thread;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Random;

import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.image.*;


public class SS_TCS {

	static Connector connector = new Connector();
	static CarSpeedSensor CSS = new CarSpeedSensor();
	static SteeringAngleSensor SAS = new SteeringAngleSensor();
    static GyroSensor GS = new GyroSensor();
    static WheelSpeedSensor WSS = new WheelSpeedSensor();
    static ThrottleActuator TA = new ThrottleActuator();
    static BrakeValueActuator BVA = new BrakeValueActuator();
    
   private static final String TITLE = "Slip Ratio Graph";
   private static final String TITLE2 = "Throttle Valve Ratio Graph";
    private static final float MINMAX = 100;
    private static final int COUNT = 2 * 60;
    private static final Random random = new Random();
    private Timer timer;
    ConnectorThread t_con = new ConnectorThread();
    
    public SS_TCS(){
    	
    	try {
			connector.setUp();
			t_con.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
   
   public static void main(String[] args) throws IOException {
      // TODO Auto-generated method stub
	   
		
		new SS_TCS();
      
      
      BlockingQueue queueCarSpeedToThread3 = new LinkedBlockingQueue(50);
      BlockingQueue queueCarSpeedToThread4 = new LinkedBlockingQueue(50);
      BlockingQueue queueCarSpeedToThread5 = new LinkedBlockingQueue(50);
      
      BlockingQueue queueWheelRatioToThread4 = new LinkedBlockingQueue(50);
     
      BlockingQueue queueRoadStateToThread2 = new LinkedBlockingQueue(50);
      BlockingQueue queueRoadStateToThread3 = new LinkedBlockingQueue(50);
      BlockingQueue queueRoadStateToThread4 = new LinkedBlockingQueue(50);
      
      BlockingQueue queueTCSToThread2 = new LinkedBlockingQueue(50);
      BlockingQueue queueTCSToThread3 = new LinkedBlockingQueue(50);
      BlockingQueue queueTCSToThread4 = new LinkedBlockingQueue(50);
      
      BlockingQueue queueLaneChangeToThread3 = new LinkedBlockingQueue(50);
            
      BlockingQueue queueSteeringAngleToThread2 = new LinkedBlockingQueue(50);
      BlockingQueue queueGyroToThread2 = new LinkedBlockingQueue(50);
      
      Input Thread1 = new Input(queueCarSpeedToThread3, queueCarSpeedToThread4, queueCarSpeedToThread5, queueWheelRatioToThread4, queueRoadStateToThread2, queueRoadStateToThread3, queueRoadStateToThread4, queueTCSToThread2, queueTCSToThread3, queueTCSToThread4,  queueLaneChangeToThread3);
      Thread Thread2 = new Thread(new Brake(queueTCSToThread2, queueSteeringAngleToThread2, queueGyroToThread2, queueRoadStateToThread2)); 
      Thread Thread3 = new Thread(new DrivingState(queueCarSpeedToThread3, queueRoadStateToThread3, queueTCSToThread3, queueLaneChangeToThread3, queueSteeringAngleToThread2, queueGyroToThread2)); 
      //Thread Thread4 = new Thread(new SlipRatioGraph(TITLE));
      Thread Thread4 = new Thread(new SlipRatioGraph(TITLE, MINMAX, COUNT, random, queueCarSpeedToThread4, queueRoadStateToThread4, queueWheelRatioToThread4, queueTCSToThread4));
      Thread Thread5 = new Thread(new ThrottleValveGraph(TITLE2, MINMAX, COUNT, random, queueCarSpeedToThread5));
      
      Thread1.start();
      Thread2.start();
      Thread3.start();
      Thread4.start();
      Thread5.start();
   }
}
   
class ConnectorThread extends Thread {
	public void run() {
		while (true) {
			 try {
			 Thread.sleep(100);
			 } catch (InterruptedException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
			 }
			String getspeed = Float.toString(SS_TCS.CSS.getActualCarSpeed());
			String DL = Float.toString(SS_TCS.WSS.getDLWheelSpeed());
			String DR = Float.toString(SS_TCS.WSS.getDRWheelSpeed());
			String UL = Float.toString(SS_TCS.WSS.getULWheelSpeed());
			String UR = Float.toString(SS_TCS.WSS.getURWheelSpeed());
			String SteeringAngle = Integer.toString(SS_TCS.SAS.getSteeringAngle()+40);
			String Gyro = Integer.toString(SS_TCS.GS.getGyro()+40);
			

			SS_TCS.connector.sendCarSpeed((Object)getspeed);
			SS_TCS.connector.sendDLWheelSpeed((Object)DL);
			SS_TCS.connector.sendDRWheelSpeed((Object)DR);
			SS_TCS.connector.sendULWheelSpeed((Object)UL);
			SS_TCS.connector.sendURWheelSpeed((Object)UR);
			
			SS_TCS.connector.sendSteeringAngle((Object)SteeringAngle);
			SS_TCS.connector.sendGyro((Object)Gyro);

			Object ob = SS_TCS.connector.getThrottleActuator();
			float tmp = Integer.parseInt(ob.toString());
			SS_TCS.TA.setThrottle( tmp);
			
			
			Object ob2 = SS_TCS.connector.getULBrakeActuator();
			float tmp2 = Integer.parseInt(ob2.toString());
			SS_TCS.BVA.setULBrakeValue(tmp2);
			
			System.out.println(SS_TCS.connector.getURBrakeActuator());
			Object ob3 = SS_TCS.connector.getURBrakeActuator();
			float tmp3 = Integer.parseInt(ob3.toString());
			SS_TCS.BVA.setURBrakeValue(tmp3);
			
			Object ob4 = SS_TCS.connector.getDLBrakeActuator();
			float tmp4 = Integer.parseInt(ob4.toString());
			SS_TCS.BVA.setDLBrakeValue(tmp4);

			Object ob5 = SS_TCS.connector.getDRBrakeActuator();
			float tmp5 = Integer.parseInt(ob5.toString());
			SS_TCS.BVA.setDRBrakeValue(tmp5);
			
			
		}
	}
}

   class Input extends Thread{

      private BlockingQueue queueCarSpeedToThread3;
      private BlockingQueue queueCarSpeedToThread4;
      private BlockingQueue queueCarSpeedToThread5;
      private BlockingQueue queueWheelRatioToThread4;
      private BlockingQueue queueRoadStateToThread2;
      private BlockingQueue queueRoadStateToThread3;
      private BlockingQueue queueRoadStateToThread4;
      private BlockingQueue queueTCSToThread2;
      private BlockingQueue queueTCSToThread3;
      private BlockingQueue queueTCSToThread4;
      private BlockingQueue queueLaneChangeToThread3;
      
      
      String StringtfCarSpeed = "";
      
         public Input(BlockingQueue queueCarSpeedToThread3, BlockingQueue queueCarSpeedToThread4, BlockingQueue queueCarSpeedToThread5, BlockingQueue queueWheelRatioToThread4, BlockingQueue queueRoadStateToThread2, BlockingQueue queueRoadStateToThread3, BlockingQueue queueRoadStateToThread4, BlockingQueue queueTCSToThread2, BlockingQueue queueTCSToThread3, BlockingQueue queueTCSToThread4, BlockingQueue queueLaneChangeToThread3) {
         // TODO Auto-generated constructor stub
            
            this.queueCarSpeedToThread3 = queueCarSpeedToThread3;
            this.queueCarSpeedToThread4 = queueCarSpeedToThread4;
            this.queueCarSpeedToThread5 = queueCarSpeedToThread5;
            this.queueWheelRatioToThread4 = queueWheelRatioToThread4;
            this.queueRoadStateToThread2 = queueRoadStateToThread2;
            this.queueRoadStateToThread3 = queueRoadStateToThread3;
            this.queueRoadStateToThread4 = queueRoadStateToThread4;
            this.queueTCSToThread2 = queueTCSToThread2;
            this.queueTCSToThread3 = queueTCSToThread3;
            this.queueTCSToThread4 = queueTCSToThread4;
            this.queueLaneChangeToThread3 = queueLaneChangeToThread3;
            
      }

         public void run(){

            
            
            
            JFrame jframe = new JFrame("myJFrame");
            jframe.setTitle("Input");
            
            Container cp = jframe.getContentPane();
         
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
            
            JPanel pane1 = new JPanel();
            JPanel pane2 = new JPanel();
            JPanel pane3 = new JPanel();
            JPanel pane4 = new JPanel();
            JPanel pane5 = new JPanel();
            
            JPanel pane7 = new JPanel();
            
            JPanel pane21 = new JPanel();
            JPanel pane22 = new JPanel();
            JPanel pane23 = new JPanel();
            JPanel pane24 = new JPanel();
            
            pane1.setLayout(new FlowLayout(FlowLayout.LEFT));
            pane2.setLayout(new BoxLayout(pane2, BoxLayout.Y_AXIS));
            pane3.setLayout(new BoxLayout(pane3, BoxLayout.Y_AXIS));
            pane4.setLayout(new FlowLayout(FlowLayout.LEFT));
            pane5.setLayout(new FlowLayout(FlowLayout.LEFT));
            
            pane7.setLayout(new FlowLayout(FlowLayout.LEFT));
            
            pane21.setLayout(new FlowLayout(FlowLayout.CENTER));
            pane22.setLayout(new FlowLayout(FlowLayout.CENTER));
            pane23.setLayout(new FlowLayout(FlowLayout.CENTER));
            pane24.setLayout(new FlowLayout(FlowLayout.CENTER));
            
            JLabel labelCarSpeed = new JLabel("Accelerator:        ");
            JLabel labelTCS = new JLabel("TCS:                       ");
            JLabel labelRoadState = new JLabel("Road state:          ");
            
            
            JLabel labelLaneChange = new JLabel("Lane change:      ");
            JLabel labelKMPerSecond = new JLabel("km/s ");
            
            final JTextField tfCarSpeed = new JTextField(4);
            
            
            
            JButton buttonCarSpeed = new JButton("ENTER");
            JButton buttonTCSON = new JButton("  ON  ");
            JButton buttonTCSOFF = new JButton(" OFF ");
            JButton buttonRoadNORMAL = new JButton("NORMAL");
            JButton buttonRoadICY = new JButton("  ICY  ");
            JButton buttonRoadRAINY = new JButton(" RAINY ");
            JButton buttonLaneChangeLeft = new JButton("LEFT");
            JButton buttonLaneChangeRight = new JButton("RIGHT");
            
            JButton button1 = new JButton("1");
            JButton button2 = new JButton("2");
            JButton button3 = new JButton("3");
            JButton button4 = new JButton("4");
            JButton button5 = new JButton("5");
            JButton button6 = new JButton("6");
            JButton button7 = new JButton("7");
            JButton button8 = new JButton("8");
            JButton button9 = new JButton("9");
            JButton button0 = new JButton("0");
            JButton buttonPoint = new JButton(".");
            JButton buttonC = new JButton("C");
            
            ActionListener AL0 = new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent e){   
                     StringtfCarSpeed += "0";
                     tfCarSpeed.setText(StringtfCarSpeed);
               }
            };
            button0.addActionListener(AL0);
            
            ActionListener AL1 = new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent e){   
                     StringtfCarSpeed += "1";
                     tfCarSpeed.setText(StringtfCarSpeed);
               }
            };
            button1.addActionListener(AL1);
            
            ActionListener AL2 = new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent e){   
                     StringtfCarSpeed += "2";
                     tfCarSpeed.setText(StringtfCarSpeed);
               }
            };
            button2.addActionListener(AL2);
            
            ActionListener AL3 = new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent e){   
                     StringtfCarSpeed += "3";
                     tfCarSpeed.setText(StringtfCarSpeed);
               }
            };
            button3.addActionListener(AL3);
            
            ActionListener AL4 = new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent e){   
                     StringtfCarSpeed += "4";
                     tfCarSpeed.setText(StringtfCarSpeed);
               }
            };
            button4.addActionListener(AL4);
            
            ActionListener AL5 = new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent e){   
                     StringtfCarSpeed += "5";
                     tfCarSpeed.setText(StringtfCarSpeed);
               }
            };
            button5.addActionListener(AL5);
            
            ActionListener AL6 = new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent e){   
                     StringtfCarSpeed += "6";
                     tfCarSpeed.setText(StringtfCarSpeed);
               }
            };
            button6.addActionListener(AL6);
            
            ActionListener AL7 = new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent e){   
                     StringtfCarSpeed += "7";
                     tfCarSpeed.setText(StringtfCarSpeed);
               }
            };
            button7.addActionListener(AL7);
            
            ActionListener AL8 = new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent e){   
                     StringtfCarSpeed += "8";
                     tfCarSpeed.setText(StringtfCarSpeed);
               }
            };
            button8.addActionListener(AL8);
            
            ActionListener AL9 = new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent e){   
                     StringtfCarSpeed += "9";
                     tfCarSpeed.setText(StringtfCarSpeed);
               }
            };
            button9.addActionListener(AL9);
            
            ActionListener ALPoint = new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent e){   
                     StringtfCarSpeed += ".";
                     tfCarSpeed.setText(StringtfCarSpeed);
               }
            };
            buttonPoint.addActionListener(ALPoint);
            
            ActionListener ALC = new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent e){   
                     StringtfCarSpeed = "";
                     tfCarSpeed.setText(StringtfCarSpeed);
               }
            };
            buttonC.addActionListener(ALC);
            
            ActionListener ALENTER = new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent e){
                  
                  float speed = 0;
                 
                  
                  if(tfCarSpeed.getText() != "")
                  {
                	  SS_TCS.CSS.setCarSpeed(Float.parseFloat(tfCarSpeed.getText()));
                      speed = SS_TCS.CSS.getCarSpeed();    
                      
                      if(speed <= 200)
                      {
                    	  queueCarSpeedToThread3.add(speed);
                    	  queueCarSpeedToThread4.add(speed);
                    	  queueCarSpeedToThread5.add(speed);
                      }
                          
                  }
                  
                  
                     
                     
                     StringtfCarSpeed = "";
                     tfCarSpeed.setText("");         
               }
            };
            buttonCarSpeed.addActionListener(ALENTER);
            
            ActionListener ALRoadNORMAL = new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent e){
                  
                  float ratio = (float) 0.8;
 
                  queueWheelRatioToThread4.add(ratio);
                  queueRoadStateToThread2.add("NORMAL");
                  queueRoadStateToThread3.add("NORMAL");
                  queueRoadStateToThread4.add("NORMAL");
                       //System.out.println(queueULWheelSpeed.peek());
                           
               }
            };
            buttonRoadNORMAL.addActionListener(ALRoadNORMAL);
            
            ActionListener ALRoadICY = new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent e){
                  
            	   float ratio = (float) 0.2;
  
            	   queueWheelRatioToThread4.add(ratio);
            	   queueRoadStateToThread2.add("ICY");
            	   queueRoadStateToThread3.add("ICY");
            	   queueRoadStateToThread4.add("ICY");
                        //System.out.println(queueRoadState.peek());
                           
               }
            };
            buttonRoadICY.addActionListener(ALRoadICY);
            
            ActionListener ALRoadRAINY = new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent e){
                  
            	   float ratio = (float) 0.4;

            	   queueWheelRatioToThread4.add(ratio);
            	   queueRoadStateToThread2.add("RAINY");
            	   queueRoadStateToThread3.add("RAINY");
            	   queueRoadStateToThread4.add("RAINY");
                        //System.out.println(queueRoadState.peek());
               }
            };
            buttonRoadRAINY.addActionListener(ALRoadRAINY);
            
            ActionListener ALTCSON = new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                   
                   queueTCSToThread2.add("ON");
                   queueTCSToThread3.add("ON");
                   queueTCSToThread4.add("ON");
                        //System.out.println(queueULWheelSpeed.peek());
                            
                }
             };
             buttonTCSON.addActionListener(ALTCSON);
             
             ActionListener ALTCSOFF = new ActionListener(){
                 @Override
                 public void actionPerformed(ActionEvent e){
                    
                	queueTCSToThread2.add("OFF");
                    queueTCSToThread3.add("OFF");
                    queueTCSToThread4.add("OFF");
                         //System.out.println(queueULWheelSpeed.peek());
                             
                 }
              };
              buttonTCSOFF.addActionListener(ALTCSOFF);
              
              ActionListener ALLaneLeft = new ActionListener(){
                  @Override
                  public void actionPerformed(ActionEvent e){
                     
                     
                     queueLaneChangeToThread3.add("LEFT");
                     
                          //System.out.println(queueULWheelSpeed.peek());
                              
                  }
               };
               buttonLaneChangeLeft.addActionListener(ALLaneLeft);
               
               ActionListener ALLaneRight = new ActionListener(){
                   @Override
                   public void actionPerformed(ActionEvent e){
                      
                      
                      queueLaneChangeToThread3.add("RIGHT");
                      
                           //System.out.println(queueULWheelSpeed.peek());
                               
                   }
                };
                buttonLaneChangeRight.addActionListener(ALLaneRight);
            
            pane1.add(labelCarSpeed);
            pane1.add(tfCarSpeed);
            pane1.add(labelKMPerSecond);
            pane1.add(buttonCarSpeed);
            
            pane2.add(pane21);
            pane2.add(pane22);
            pane2.add(pane23);
            pane2.add(pane24);
            
            pane21.add(button7);
            pane21.add(button8);
            pane21.add(button9);
            pane22.add(button4);
            pane22.add(button5);
            pane22.add(button6);
            pane23.add(button1);
            pane23.add(button2);
            pane23.add(button3);
            pane24.add(button0);
            pane24.add(buttonPoint);
            pane24.add(buttonC);
            
            
            pane3.add(pane1);
            pane3.add(pane2);
            
            pane4.add(labelTCS);
            pane4.add(buttonTCSON);
            pane4.add(buttonTCSOFF);
            
            pane5.add(labelRoadState);
            
            pane5.add(buttonRoadNORMAL);
            pane5.add(buttonRoadICY);
            pane5.add(buttonRoadRAINY);
            
            
            
            pane7.add(labelLaneChange);
            pane7.add(buttonLaneChangeLeft);
            pane7.add(buttonLaneChangeRight);
            
            
            
            
            cp.add(pane1);
            cp.add(pane2);
            cp.add(pane3);
            cp.add(pane4);
            cp.add(pane5);
            
            cp.add(pane7);
            
            jframe.pack();
            jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jframe.setVisible(true);

         }

      }
   
   class Brake extends JFrame implements Runnable{

       JScrollPane scrollPane;
       ImageIcon[] Brake = new ImageIcon[10];
       
       static float p = 0;
       
       static String tempTCS = "";
       static int tempSteeringAngle = 0;
       static int tempGyro = 0;
       static String tempRoadState = "";
       static int countEntry = 0;
       private BlockingQueue queueTCSToThread2;
       private BlockingQueue queueSteeringAngleToThread2;
       private BlockingQueue queueGyroToThread2;
       private BlockingQueue queueRoadStateToThread2;
       
       
       public Brake(BlockingQueue queueTCSToThread2, BlockingQueue queueSteeringAngleToThread2, BlockingQueue queueGyroToThread2, BlockingQueue queueRoadStateToThread2){
      
          this.queueTCSToThread2 = queueTCSToThread2;
          this.queueSteeringAngleToThread2 = queueSteeringAngleToThread2;
          this.queueGyroToThread2 = queueGyroToThread2;
          this.queueRoadStateToThread2 = queueRoadStateToThread2;
       }
       
       public Brake(){
          Brake[0] = new ImageIcon("Brake.png");
          Brake[1] = new ImageIcon("4Brakes_UL.png");
          Brake[2] = new ImageIcon("4Brakes_UR.png");
          Brake[3] = new ImageIcon("4Brakes_DL.png");
          Brake[4] = new ImageIcon("4Brakes_DR.png");
          Brake[5] = new ImageIcon("4Brakes_ALL.png");
          Brake[6] = new ImageIcon("4Brakes_ALL_UL.png");
          Brake[7] = new ImageIcon("4Brakes_ALL_UR.png");
          Brake[8] = new ImageIcon("4Brakes_ALL_DL.png");
          Brake[9] = new ImageIcon("4Brakes_ALL_DR.png");
          
          
          JPanel panel = new JPanel(){
             public void paintComponent(Graphics g){
                
               if(tempTCS == "ON")
               {
            	   if(tempRoadState == "NORMAL")
            	   {
            		   if(tempSteeringAngle == 0 && tempGyro == 0)
                       {
            			   SS_TCS.BVA.setULBrakeAdd(1);
            			   SS_TCS.BVA.setURBrakeAdd(1);
            			   SS_TCS.BVA.setDLBrakeAdd(1);
            			   SS_TCS.BVA.setDRBrakeAdd(1);
            			   
                    	   g.drawImage(Brake[0].getImage(), 0, 0, null);
                           setOpaque(false);
                           super.paintComponent(g); 
                       }
                       
                       else if(tempSteeringAngle == -40 && tempGyro < 0 && tempGyro > -40)
                       {
                    	   SS_TCS.BVA.setULBrakeAdd(1);
            			   SS_TCS.BVA.setURBrakeAdd(1);
            			   SS_TCS.BVA.setDLBrakeAdd((float) 0.2);
            			   SS_TCS.BVA.setDRBrakeAdd(1);
            			   
                    	   g.drawImage(Brake[3].getImage(), 0, 0, null);
                           setOpaque(false);
                           super.paintComponent(g); 
                           
                       }
                       
                       else if(tempSteeringAngle == 0 && tempGyro < 0 && tempGyro > -40)
                       {
                    	   SS_TCS.BVA.setULBrakeAdd((float) 0.2);
            			   SS_TCS.BVA.setURBrakeAdd(1);
            			   SS_TCS.BVA.setDLBrakeAdd(1);
            			   SS_TCS.BVA.setDRBrakeAdd(1);
            			   
                    	   g.drawImage(Brake[1].getImage(), 0, 0, null);
                           setOpaque(false);
                           super.paintComponent(g); 
                       }
                       
                       else if(tempSteeringAngle == 40 && tempGyro > 0 && tempGyro < 40)
                       {
                    	   SS_TCS.BVA.setULBrakeAdd(1);
            			   SS_TCS.BVA.setURBrakeAdd(1);
            			   SS_TCS.BVA.setDLBrakeAdd(1);
            			   SS_TCS.BVA.setDRBrakeAdd((float) 0.2);
            			   
                    	   g.drawImage(Brake[4].getImage(), 0, 0, null);
                           setOpaque(false);
                           super.paintComponent(g); 
                       }
                       
                       else if(tempSteeringAngle == 0 && tempGyro > 0 && tempGyro < 40)
                       {
                    	   SS_TCS.BVA.setULBrakeAdd(1);
            			   SS_TCS.BVA.setURBrakeAdd((float) 0.2);
            			   SS_TCS.BVA.setDLBrakeAdd(1);
            			   SS_TCS.BVA.setDRBrakeAdd(1);
            			   
                    	   g.drawImage(Brake[2].getImage(), 0, 0, null);
                           setOpaque(false);
                           super.paintComponent(g); 
                       }
            	   }
            	   
            	   else if(tempRoadState == "ICY")
            	   {
            		   if(tempSteeringAngle == 0 && tempGyro == 0)
                       {
            			   SS_TCS.BVA.setULBrakeAdd(1);
            			   SS_TCS.BVA.setURBrakeAdd(1);
            			   SS_TCS.BVA.setDLBrakeAdd(1);
            			   SS_TCS.BVA.setDRBrakeAdd(1);
            			   
                    	   g.drawImage(Brake[5].getImage(), 0, 0, null);
                           setOpaque(false);
                           super.paintComponent(g); 
                       }
                       
                       else if(tempSteeringAngle == -40 && tempGyro < 0 && tempGyro > -40)
                       {
                    	   SS_TCS.BVA.setULBrakeAdd(1);
            			   SS_TCS.BVA.setURBrakeAdd(1);
            			   SS_TCS.BVA.setDLBrakeAdd((float) 0.5);
            			   SS_TCS.BVA.setDRBrakeAdd(1);
            			   
                    	   g.drawImage(Brake[8].getImage(), 0, 0, null);
                           setOpaque(false);
                           super.paintComponent(g); 
                       }
                       
                       else if(tempSteeringAngle == 0 && tempGyro < 0 && tempGyro > -40)
                       {
                    	   SS_TCS.BVA.setULBrakeAdd((float) 0.5);
            			   SS_TCS.BVA.setURBrakeAdd(1);
            			   SS_TCS.BVA.setDLBrakeAdd(1);
            			   SS_TCS.BVA.setDRBrakeAdd(1);
            			   
                    	   g.drawImage(Brake[6].getImage(), 0, 0, null);
                           setOpaque(false);
                           super.paintComponent(g); 
                       }
                       
                       else if(tempSteeringAngle == 40 && tempGyro > 0 && tempGyro < 40)
                       {
                    	   SS_TCS.BVA.setULBrakeAdd(1);
            			   SS_TCS.BVA.setURBrakeAdd(1);
            			   SS_TCS.BVA.setDLBrakeAdd(1);
            			   SS_TCS.BVA.setDRBrakeAdd((float) 0.5);
            			   
                    	   g.drawImage(Brake[9].getImage(), 0, 0, null);
                           setOpaque(false);
                           super.paintComponent(g); 
                       }
                       
                       else if(tempSteeringAngle == 0 && tempGyro > 0 && tempGyro < 40)
                       {
                    	   SS_TCS.BVA.setULBrakeAdd(1);
            			   SS_TCS.BVA.setURBrakeAdd((float) 0.5);
            			   SS_TCS.BVA.setDLBrakeAdd(1);
            			   SS_TCS.BVA.setDRBrakeAdd(1);
            			   
                    	   g.drawImage(Brake[7].getImage(), 0, 0, null);
                           setOpaque(false);
                           super.paintComponent(g); 
                       }
            		   
            		   
            	   }
            	   
            	   else if(tempRoadState == "RAINY")
            	   {
            		   if(tempSteeringAngle == 0 && tempGyro == 0)
                       {
            			   SS_TCS.BVA.setULBrakeAdd(1);
            			   SS_TCS.BVA.setURBrakeAdd(1);
            			   SS_TCS.BVA.setDLBrakeAdd(1);
            			   SS_TCS.BVA.setDRBrakeAdd(1);
            			   
                    	   g.drawImage(Brake[5].getImage(), 0, 0, null);
                           setOpaque(false);
                           super.paintComponent(g); 
                       }
                       
                       else if(tempSteeringAngle == -40 && tempGyro < 0 && tempGyro > -40)
                       {
                    	   SS_TCS.BVA.setULBrakeAdd(1);
            			   SS_TCS.BVA.setURBrakeAdd(1);
            			   SS_TCS.BVA.setDLBrakeAdd((float) 0.4);
            			   SS_TCS.BVA.setDRBrakeAdd(1);
            			   
                    	   g.drawImage(Brake[8].getImage(), 0, 0, null);
                           setOpaque(false);
                           super.paintComponent(g); 
                       }
                       
                       else if(tempSteeringAngle == 0 && tempGyro < 0 && tempGyro > -40)
                       {
                    	   SS_TCS.BVA.setULBrakeAdd((float) 0.4);
            			   SS_TCS.BVA.setURBrakeAdd(1);
            			   SS_TCS.BVA.setDLBrakeAdd(1);
            			   SS_TCS.BVA.setDRBrakeAdd(1);
            			   
                    	   g.drawImage(Brake[6].getImage(), 0, 0, null);
                           setOpaque(false);
                           super.paintComponent(g); 
                       }
                       
                       else if(tempSteeringAngle == 40 && tempGyro > 0 && tempGyro < 40)
                       {
                    	   SS_TCS.BVA.setULBrakeAdd(1);
            			   SS_TCS.BVA.setURBrakeAdd(1);
            			   SS_TCS.BVA.setDLBrakeAdd(1);
            			   SS_TCS.BVA.setDRBrakeAdd((float) 0.4);
            			   
                    	   g.drawImage(Brake[9].getImage(), 0, 0, null);
                           setOpaque(false);
                           super.paintComponent(g); 
                       }
                       
                       else if(tempSteeringAngle == 0 && tempGyro > 0 && tempGyro < 40)
                       {
                    	   SS_TCS.BVA.setULBrakeAdd(1);
            			   SS_TCS.BVA.setURBrakeAdd((float) 0.4);
            			   SS_TCS.BVA.setDLBrakeAdd(1);
            			   SS_TCS.BVA.setDRBrakeAdd(1);
            			   
                    	   g.drawImage(Brake[7].getImage(), 0, 0, null);
                           setOpaque(false);
                           super.paintComponent(g); 
                       }
            	   }
               }
               
               else
               {
            	   SS_TCS.BVA.setULBrakeAdd(1);
    			   SS_TCS.BVA.setURBrakeAdd(1);
    			   SS_TCS.BVA.setDLBrakeAdd(1);
    			   SS_TCS.BVA.setDRBrakeAdd(1);
    			   
            	   g.drawImage(Brake[0].getImage(), 0, 0, null);
                   setOpaque(false);
                   super.paintComponent(g); 
               }
            	   
            	 
               
                
             }
          };
          
          scrollPane = new JScrollPane(panel);
          setContentPane(scrollPane);
          
       }

           @Override
          public void run(){
              
        	   countEntry ++;
        	   if(countEntry == 1)
        	   {
        		   tempTCS = "OFF";
        		   tempRoadState = "NORMAL";
        	   }
        	   
             Brake brakeFrame = new Brake();
             brakeFrame.setTitle("brakeFrame");
             brakeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
             brakeFrame.setSize(269,474);
             brakeFrame.setVisible(true);
             
             while(true){
            	 
            	 try {
                     if(queueTCSToThread2.peek() != null)
                     	tempTCS =  (String) queueTCSToThread2.take();
                     
                     
                   
                  } catch (InterruptedException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
            	 
            	 try {
                     if(queueSteeringAngleToThread2.peek() != null){
                     	tempSteeringAngle =   (int) queueSteeringAngleToThread2.take();
                     }
                  } catch (InterruptedException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }//System.out.println(queueWheelRatioToThread4.peek());
                 
                 try {
                     if(queueGyroToThread2.peek() != null){
                     	tempGyro =  (int) queueGyroToThread2.take();
                     }
                  } catch (InterruptedException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
                 
                 try {
                     if(queueRoadStateToThread2.peek() != null)
                     	tempRoadState =  (String) queueRoadStateToThread2.take();
                     
                     
                   
                  } catch (InterruptedException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
            	 
                p+=1;
                
                brakeFrame.repaint();
                brakeFrame.setVisible(true);
                try{
                   Thread.sleep(10);
                }catch(Exception ex){};
             }
             
          }


    }
   
   class DrivingState extends JFrame implements Runnable{

        

      JScrollPane scrollPane;
         ImageIcon icon;
         
         ImageIcon[] icon2 = new ImageIcon[81];
         ImageIcon[] icon3 = new ImageIcon[81];
         ImageIcon iceTile;
         ImageIcon rainTile;
         
         
         static float k = 0;
         static float t = 0;
         static int im = 0;
         static float location = 430;
         static float tempLocation = 430;
         static int angle = 0;
         
         private BlockingQueue queueCarSpeedToThread3;
         private BlockingQueue queueRoadStateToThread3;
         
         private BlockingQueue queueTCSToThread3;
         private BlockingQueue queueLaneChangeToThread3;
         
         private BlockingQueue queueSteeringAngleToThread2;
         private BlockingQueue queueGyroToThread2;
         
         
         
         static String tempRoad = "";
         static String tempTCS = "";
         static String tempLaneChange = "";
         static int SteeringAngle = 0;
         static int Gyro = 0;
         
         
         public DrivingState(BlockingQueue queueCarSpeedToThread3, BlockingQueue queueRoadStateToThread3, BlockingQueue queueTCSToThread3, BlockingQueue queueLaneChangeToThread3, BlockingQueue queueSteeringAngleToThread2, BlockingQueue queueGyroToThread2){
            
            this.queueCarSpeedToThread3 = queueCarSpeedToThread3;
            this.queueRoadStateToThread3 = queueRoadStateToThread3;
            this.queueTCSToThread3 = queueTCSToThread3;
            this.queueLaneChangeToThread3 = queueLaneChangeToThread3;
            this.queueSteeringAngleToThread2 = queueSteeringAngleToThread2;
            this.queueGyroToThread2 = queueGyroToThread2;
          
         }
         
         public DrivingState(){
            
            icon = new ImageIcon("Road.png");
            
            icon2[0] = new ImageIcon("0.png");
            icon2[1] = new ImageIcon("1.png");
            icon2[2] = new ImageIcon("2.png");
            icon2[3] = new ImageIcon("3.png");
            icon2[4] = new ImageIcon("4.png");
            icon2[5] = new ImageIcon("5.png");
            icon2[6] = new ImageIcon("6.png");
            icon2[7] = new ImageIcon("7.png");
            icon2[8] = new ImageIcon("8.png");
            icon2[9] = new ImageIcon("9.png");
            icon2[10] = new ImageIcon("10.png");
            icon2[11] = new ImageIcon("11.png");
            icon2[12] = new ImageIcon("12.png");
            icon2[13] = new ImageIcon("13.png");
            icon2[14] = new ImageIcon("14.png");
            icon2[15] = new ImageIcon("15.png");
            icon2[16] = new ImageIcon("16.png");
            icon2[17] = new ImageIcon("17.png");
            icon2[18] = new ImageIcon("18.png");
            icon2[19] = new ImageIcon("19.png");
            icon2[20] = new ImageIcon("20.png");
            icon2[21] = new ImageIcon("21.png");
            icon2[22] = new ImageIcon("22.png");
            icon2[23] = new ImageIcon("23.png");
            icon2[24] = new ImageIcon("24.png");
            icon2[25] = new ImageIcon("25.png");
            icon2[26] = new ImageIcon("26.png");
            icon2[27] = new ImageIcon("27.png");
            icon2[28] = new ImageIcon("28.png");
            icon2[29] = new ImageIcon("29.png");
            icon2[30] = new ImageIcon("30.png");
            icon2[31] = new ImageIcon("31.png");
            icon2[32] = new ImageIcon("32.png");
            icon2[33] = new ImageIcon("33.png");
            icon2[34] = new ImageIcon("34.png");
            icon2[35] = new ImageIcon("35.png");
            icon2[36] = new ImageIcon("36.png");
            icon2[37] = new ImageIcon("37.png");
            icon2[38] = new ImageIcon("38.png");
            icon2[39] = new ImageIcon("39.png");
            icon2[40] = new ImageIcon("40.png");
            icon2[41] = new ImageIcon("39.png");
            icon2[42] = new ImageIcon("38.png");
            icon2[43] = new ImageIcon("37.png");
            icon2[44] = new ImageIcon("36.png");
            icon2[45] = new ImageIcon("35.png");
            icon2[46] = new ImageIcon("34.png");
            icon2[47] = new ImageIcon("33.png");
            icon2[48] = new ImageIcon("32.png");
            icon2[49] = new ImageIcon("31.png");
            icon2[50] = new ImageIcon("30.png");
            icon2[51] = new ImageIcon("29.png");
            icon2[52] = new ImageIcon("28.png");
            icon2[53] = new ImageIcon("27.png");
            icon2[54] = new ImageIcon("26.png");
            icon2[55] = new ImageIcon("25.png");
            icon2[56] = new ImageIcon("24.png");
            icon2[57] = new ImageIcon("23.png");
            icon2[58] = new ImageIcon("22.png");
            icon2[59] = new ImageIcon("21.png");
            icon2[60] = new ImageIcon("20.png");
            icon2[61] = new ImageIcon("19.png");
            icon2[62] = new ImageIcon("18.png");
            icon2[63] = new ImageIcon("17.png");
            icon2[64] = new ImageIcon("16.png");
            icon2[65] = new ImageIcon("15.png");
            icon2[66] = new ImageIcon("14.png");
            icon2[67] = new ImageIcon("13.png");
            icon2[68] = new ImageIcon("12.png");
            icon2[69] = new ImageIcon("11.png");
            icon2[70] = new ImageIcon("10.png");
            icon2[71] = new ImageIcon("9.png");
            icon2[72] = new ImageIcon("8.png");
            icon2[73] = new ImageIcon("7.png");
            icon2[74] = new ImageIcon("6.png");
            icon2[75] = new ImageIcon("5.png");
            icon2[76] = new ImageIcon("4.png");
            icon2[77] = new ImageIcon("3.png");
            icon2[78] = new ImageIcon("2.png");
            icon2[79] = new ImageIcon("1.png");
            icon2[80] = new ImageIcon("0.png");
            
            
            
            
            
            
            icon3[0] = new ImageIcon("0.png");
            icon3[1] = new ImageIcon("-1.png");
            icon3[2] = new ImageIcon("-2.png");
            icon3[3] = new ImageIcon("-3.png");
            icon3[4] = new ImageIcon("-4.png");
            icon3[5] = new ImageIcon("-5.png");
            icon3[6] = new ImageIcon("-6.png");
            icon3[7] = new ImageIcon("-7.png");
            icon3[8] = new ImageIcon("-8.png");
            icon3[9] = new ImageIcon("-9.png");
            icon3[10] = new ImageIcon("-10.png");
            icon3[11] = new ImageIcon("-11.png");
            icon3[12] = new ImageIcon("-12.png");
            icon3[13] = new ImageIcon("-13.png");
            icon3[14] = new ImageIcon("-14.png");
            icon3[15] = new ImageIcon("-15.png");
            icon3[16] = new ImageIcon("-16.png");
            icon3[17] = new ImageIcon("-17.png");
            icon3[18] = new ImageIcon("-18.png");
            icon3[19] = new ImageIcon("-19.png");
            icon3[20] = new ImageIcon("-20.png");
            icon3[21] = new ImageIcon("-21.png");
            icon3[22] = new ImageIcon("-22.png");
            icon3[23] = new ImageIcon("-23.png");
            icon3[24] = new ImageIcon("-24.png");
            icon3[25] = new ImageIcon("-25.png");
            icon3[26] = new ImageIcon("-26.png");
            icon3[27] = new ImageIcon("-27.png");
            icon3[28] = new ImageIcon("-28.png");
            icon3[29] = new ImageIcon("-29.png");
            icon3[30] = new ImageIcon("-30.png");
            icon3[31] = new ImageIcon("-31.png");
            icon3[32] = new ImageIcon("-32.png");
            icon3[33] = new ImageIcon("-33.png");
            icon3[34] = new ImageIcon("-34.png");
            icon3[35] = new ImageIcon("-35.png");
            icon3[36] = new ImageIcon("-36.png");
            icon3[37] = new ImageIcon("-37.png");
            icon3[38] = new ImageIcon("-38.png");
            icon3[39] = new ImageIcon("-39.png");
            icon3[40] = new ImageIcon("-40.png");
            icon3[41] = new ImageIcon("-39.png");
            icon3[42] = new ImageIcon("-38.png");
            icon3[43] = new ImageIcon("-37.png");
            icon3[44] = new ImageIcon("-36.png");
            icon3[45] = new ImageIcon("-35.png");
            icon3[46] = new ImageIcon("-34.png");
            icon3[47] = new ImageIcon("-33.png");
            icon3[48] = new ImageIcon("-32.png");
            icon3[49] = new ImageIcon("-31.png");
            icon3[50] = new ImageIcon("-30.png");
            icon3[51] = new ImageIcon("-29.png");
            icon3[52] = new ImageIcon("-28.png");
            icon3[53] = new ImageIcon("-27.png");
            icon3[54] = new ImageIcon("-26.png");
            icon3[55] = new ImageIcon("-25.png");
            icon3[56] = new ImageIcon("-24.png");
            icon3[57] = new ImageIcon("-23.png");
            icon3[58] = new ImageIcon("-22.png");
            icon3[59] = new ImageIcon("-21.png");
            icon3[60] = new ImageIcon("-20.png");
            icon3[61] = new ImageIcon("-19.png");
            icon3[62] = new ImageIcon("-18.png");
            icon3[63] = new ImageIcon("-17.png");
            icon3[64] = new ImageIcon("-16.png");
            icon3[65] = new ImageIcon("-15.png");
            icon3[66] = new ImageIcon("-14.png");
            icon3[67] = new ImageIcon("-13.png");
            icon3[68] = new ImageIcon("-12.png");
            icon3[69] = new ImageIcon("-11.png");
            icon3[70] = new ImageIcon("-10.png");
            icon3[71] = new ImageIcon("-9.png");
            icon3[72] = new ImageIcon("-8.png");
            icon3[73] = new ImageIcon("-7.png");
            icon3[74] = new ImageIcon("-6.png");
            icon3[75] = new ImageIcon("-5.png");
            icon3[76] = new ImageIcon("-4.png");
            icon3[77] = new ImageIcon("-3.png");
            icon3[78] = new ImageIcon("-2.png");
            icon3[79] = new ImageIcon("-1.png");
            icon3[80] = new ImageIcon("0.png");
            
            iceTile = new ImageIcon("iceTile2.png");
            rainTile = new ImageIcon("rainTile.jpg");
            
            
            JPanel panel = new JPanel(){
               public void paintComponent(Graphics g){
                  int u=(int) (k/1024);
                  int m=(int)t;
                  
                  g.drawImage(icon.getImage(), 0, (int) (u*1024-k), null);
                  setOpaque(false);
                  super.paintComponent(g);
                  
                  g.drawImage(icon.getImage(), 0, (int) ((u-1)*1024-k), null);
                  setOpaque(false);
                  super.paintComponent(g);
                  
                     if(tempRoad == "NORMAL")
                     {
                        g.drawImage(icon.getImage(), 0, (int) (u*1024-k), null);
                           setOpaque(false);
                           super.paintComponent(g);
                           
                           g.drawImage(icon.getImage(), 0, (int) ((u-1)*1024-k), null);
                           setOpaque(false);
                           super.paintComponent(g);
                     }
                        
                     else if(tempRoad == "ICY")
                     {
                    	 
                    	 
                        g.drawImage(iceTile.getImage(), 142, (int) (u*1024-k), null);
                           setOpaque(false);
                           super.paintComponent(g);
                           
                           g.drawImage(iceTile.getImage(), 142, (int) ((u-1)*1024-k), null);
                           setOpaque(false);
                           super.paintComponent(g);
                           
                           
                           g.drawImage(iceTile.getImage(), 142+325, (int) (u*1024-k), null);
                           setOpaque(false);
                           super.paintComponent(g);
                           
                           g.drawImage(iceTile.getImage(), 142+325, (int) ((u-1)*1024-k), null);
                           setOpaque(false);
                           super.paintComponent(g);
                     }
                     
                     else if(tempRoad == "RAINY")
                     {
                    	 
                    	 
                    	 g.drawImage(rainTile.getImage(), 142, (int) (u*1024-k), null);
                         setOpaque(false);
                         super.paintComponent(g);
                         
                         g.drawImage(rainTile.getImage(), 142, (int) ((u-1)*1024-k), null);
                         setOpaque(false);
                         super.paintComponent(g);
                           
                          
                     }
                  
               
                 
                  
                  
                  
                 
                  
                     
                  
                     
                     if(tempTCS == "ON")
                     {
                        if(tempLaneChange == "LEFT")
                        {
                           
                           if(angle == 0)
                              SS_TCS.SAS.setSteeringAngle(-40);   
                              
                             
                          
                                  
                           
                           if(angle == 40)
                        	   SS_TCS.SAS.setSteeringAngle(0);
                                 
                           
                           if(SS_TCS.SAS.getSteeringAngle() == -40)
                        	   SS_TCS.GS.setGyro(-angle);
                                
                           
                           if(SS_TCS.SAS.getSteeringAngle() == 0)
                        	   SS_TCS.GS.setGyro(-(80 - angle));
                                 
                           
                           
                             
                          
                           g.drawImage(icon3[angle].getImage(), (int)(location), 280, null);
                             setOpaque(false);
                             super.paintComponent(g); // аб 1д╜
                             
                             if(location > tempLocation - 130)
                             {
                                location -= 1.625 ;
                                angle += 1;
                                
                             }
                             
                             
                             
                             
                             
                             else if(location <= tempLocation - 130)
                             tempLaneChange = "";
                             
                                //System.out.println(location);
                                
                                
                                
                        }
                        
                        else if(tempLaneChange == "RIGHT")
                        {
                           if(angle == 0)
                        	   SS_TCS.SAS.setSteeringAngle(40);    
                           
                           if(angle == 40)
                        	   SS_TCS.SAS.setSteeringAngle(0);   
                           
                           if(SS_TCS.SAS.getSteeringAngle() == 40)
                        	   SS_TCS.GS.setGyro(angle);   
                           
                           if(SS_TCS.SAS.getSteeringAngle() == 0)
                        	   SS_TCS.GS.setGyro(80 - angle);
                           
                           //queueSteeringAngleToThread2.add(SAS.getSteeringAngle());
                             //queueGyroToThread2.add(GS.getGyro());
                           
                           g.drawImage(icon2[angle].getImage(), (int)(location), 280, null);
                             setOpaque(false);
                             super.paintComponent(g); // ©Л 1д╜

                             if(location < tempLocation + 130)
                             {
                                location += 1.625 ;
                                 angle += 1;
                             }
                                 
                                 else if(location >= tempLocation + 130)
                                 tempLaneChange = "";
                           
                        }
                        
                        else
                       {
                        	SS_TCS.SAS.setSteeringAngle(0);   
                        	SS_TCS.GS.setGyro(0);
                           
                           //queueSteeringAngleToThread2.add(SAS.getSteeringAngle());
                             //queueGyroToThread2.add(GS.getGyro());
                           
                          g.drawImage(icon3[0].getImage(), (int)(location), 280, null);
                             setOpaque(false);
                             super.paintComponent(g); 
                             tempLocation = location;
                             angle = 0;
                             
                             
                       }
                           
                        
                        
                     }
                     
                     else
                     {
                        if(tempRoad == "NORMAL")
                        {
                           if(tempLaneChange == "LEFT" )
                            {
                              if(angle == 0)
                            	  SS_TCS.SAS.setSteeringAngle(-40);    
                               
                               if(angle == 40)
                            	   SS_TCS.SAS.setSteeringAngle(0);
                               
                               if(SS_TCS.SAS.getSteeringAngle() == -40)
                            	   SS_TCS.GS.setGyro(-angle);   
                               
                               if(SS_TCS.SAS.getSteeringAngle() == 0)
                            	   SS_TCS.GS.setGyro(-(80 - angle));
                               
                               //queueSteeringAngleToThread2.add(SAS.getSteeringAngle());
                                 //queueGyroToThread2.add(GS.getGyro());
                                  
                               
                               g.drawImage(icon3[angle].getImage(), (int)(location), 280, null);
                                 setOpaque(false);
                                 super.paintComponent(g); // аб 1д╜
                                 
                                 if(location > tempLocation - 130)
                                 {
                                    location -= 1.625 ;
                                    angle += 1;
                                    
                                 }
                                 
                                 
                                 
                                 
                                 
                                 else if(location <= tempLocation - 130)
                                 tempLaneChange = "";
                                 
                                    //System.out.println(location);
                                    
                                    
                                    
                            }
                            
                            else if(tempLaneChange == "RIGHT")
                            {
                               if(angle == 0)
                            	   SS_TCS.SAS.setSteeringAngle(40);    
                               
                               if(angle == 40)
                            	   SS_TCS.SAS.setSteeringAngle(0);
                               
                               if(SS_TCS.SAS.getSteeringAngle() == 40)
                            	   SS_TCS.GS.setGyro(angle);   
                               
                               if(SS_TCS.SAS.getSteeringAngle() == 0)
                            	   SS_TCS.GS.setGyro(80 - angle);
                               
                               //queueSteeringAngleToThread2.add(SAS.getSteeringAngle());
                                 //queueGyroToThread2.add(GS.getGyro());
                               
                               g.drawImage(icon2[angle].getImage(), (int)(location), 280, null);
                                 setOpaque(false);
                                 super.paintComponent(g); // ©Л 1д╜

                                 if(location < tempLocation + 130)
                                 {
                                    location += 1.625 ;
                                     angle += 1;
                                 }
                                     
                                     else if(location >= tempLocation + 130)
                                     tempLaneChange = "";
                               
                            }
                            
                            else
                           {
                            	SS_TCS.SAS.setSteeringAngle(0);
                            	SS_TCS.GS.setGyro(0);
                               
                               //queueSteeringAngleToThread2.add(SAS.getSteeringAngle());
                                 //queueGyroToThread2.add(GS.getGyro());
                               
                              g.drawImage(icon3[0].getImage(), (int)(location), 280, null);
                                 setOpaque(false);
                                 super.paintComponent(g); 
                                 tempLocation = location;
                                 angle = 0;
                                 
                                 
                           }
                        }
                        
                        else if(tempRoad == "ICY")
                        {
                           if(tempLaneChange == "LEFT" )
                            {
                              if(angle == 0)
                            	  SS_TCS.SAS.setSteeringAngle(-40);    
                               
                               if(angle == 40)
                            	   SS_TCS.SAS.setSteeringAngle(0);
                               
                               if(SS_TCS.SAS.getSteeringAngle() == -40)
                            	   SS_TCS.GS.setGyro(-angle);   
                               
                               if(SS_TCS.SAS.getSteeringAngle() == 0)
                            	   SS_TCS.GS.setGyro(-(80 - angle));
                               
                               //queueSteeringAngleToThread2.add(SAS.getSteeringAngle());
                                 //queueGyroToThread2.add(GS.getGyro());
                                  
                               
                               g.drawImage(icon3[angle].getImage(), (int)(location), 280, null);
                                 setOpaque(false);
                                 super.paintComponent(g); // аб 1д╜
                                 
                                 if(location > tempLocation - 260)
                                 {
                                    location -= 3.25 ;
                                    angle += 1;
                                    
                                       
                                    
                                 }
                                 
                                 
                                 
                                 
                                 
                                 else if(location <= tempLocation - 260)
                                 tempLaneChange = "";
                                 
                                    //System.out.println(location);
                                    
                                    
                                    
                            }
                            
                            else if(tempLaneChange == "RIGHT")
                            {
                               if(angle == 0)
                            	   SS_TCS.SAS.setSteeringAngle(40);    
                               
                               if(angle == 40)
                            	   SS_TCS.SAS.setSteeringAngle(0);
                               
                               if(SS_TCS.SAS.getSteeringAngle() == 40)
                            	   SS_TCS.GS.setGyro(angle);   
                               
                               if(SS_TCS.SAS.getSteeringAngle() == 0)
                            	   SS_TCS.GS.setGyro(80 - angle);
                               
                               //queueSteeringAngleToThread2.add(SAS.getSteeringAngle());
                                 //queueGyroToThread2.add(GS.getGyro());
                               
                               g.drawImage(icon2[angle].getImage(), (int)(location), 280, null);
                                 setOpaque(false);
                                 super.paintComponent(g); // ©Л 1д╜

                                 if(location < tempLocation + 260)
                                 {
                                    location += 3.25 ;
                                     angle += 1;
                                 }
                                     
                                     else if(location >= tempLocation + 260)
                                     tempLaneChange = "";
                               
                            }
                            
                            else
                           {
                            	SS_TCS.SAS.setSteeringAngle(0);
                            	SS_TCS.GS.setGyro(0);
                               
                               //queueSteeringAngleToThread2.add(SAS.getSteeringAngle());
                                 //queueGyroToThread2.add(GS.getGyro());
                               
                              g.drawImage(icon3[0].getImage(), (int)(location), 280, null);
                                 setOpaque(false);
                                 super.paintComponent(g); 
                                 tempLocation = location;
                                 angle = 0;
                                 
                                 
                           }
                        }
                        
                        else if(tempRoad == "RAINY")
                        {
                           if(tempLaneChange == "LEFT" )
                            {
                              if(angle == 0)
                            	  SS_TCS.SAS.setSteeringAngle(-40);    
                               
                               if(angle == 40)
                            	   SS_TCS.SAS.setSteeringAngle(0);
                               
                               if(SS_TCS.SAS.getSteeringAngle() == -40)
                            	   SS_TCS.GS.setGyro(-angle);   
                               
                               if(SS_TCS.SAS.getSteeringAngle() == 0)
                            	   SS_TCS.GS.setGyro(-(80 - angle));
                               
                               //queueSteeringAngleToThread2.add(SAS.getSteeringAngle());
                                 //queueGyroToThread2.add(GS.getGyro());
                                  
                               
                               g.drawImage(icon3[angle].getImage(), (int)(location), 280, null);
                                 setOpaque(false);
                                 super.paintComponent(g); // аб 1д╜
                                 
                                 if(location > tempLocation - 195)
                                 {
                                    location -= 2.4375 ;
                                    angle += 1;
                                    
                                       
                                    
                                 }
                                 
                                 
                                 
                                 
                                 
                                 else if(location <= tempLocation - 195)
                                 tempLaneChange = "";
                                 
                                    //System.out.println(location);
                                    
                                    
                                    
                            }
                            
                            else if(tempLaneChange == "RIGHT")
                            {
                               if(angle == 0)
                            	   SS_TCS.SAS.setSteeringAngle(40);    
                               
                               if(angle == 40)
                            	   SS_TCS.SAS.setSteeringAngle(0);
                               
                               if(SS_TCS.SAS.getSteeringAngle() == 40)
                            	   SS_TCS.GS.setGyro(angle);   
                               
                               if(SS_TCS.SAS.getSteeringAngle() == 0)
                            	   SS_TCS.GS.setGyro(80 - angle);
                               
                               //queueSteeringAngleToThread2.add(SAS.getSteeringAngle());
                                 //queueGyroToThread2.add(GS.getGyro());
                               
                               g.drawImage(icon2[angle].getImage(), (int)(location), 280, null);
                                 setOpaque(false);
                                 super.paintComponent(g); // ©Л 1д╜

                                 if(location < tempLocation + 195)
                                 {
                                    location += 2.4375 ;
                                     angle += 1;
                                 }
                                     
                                     else if(location >= tempLocation + 195)
                                     tempLaneChange = "";
                               
                            }
                            
                            else
                           {
                            	SS_TCS.SAS.setSteeringAngle(0);
                            	SS_TCS.GS.setGyro(0);
                               
                               //queueSteeringAngleToThread2.add(SAS.getSteeringAngle());
                                 //queueGyroToThread2.add(GS.getGyro());
                               
                              g.drawImage(icon3[0].getImage(), (int)(location), 280, null);
                                 setOpaque(false);
                                 super.paintComponent(g); 
                                 tempLocation = location;
                                 angle = 0;
                                 
                                 
                           }
                        }
                        
                       
                        
                        
                     }
                     //System.out.println(GS.getGyro());
                     SteeringAngle = (int) SS_TCS.SAS.getSteeringAngle();
                     Gyro = (int) SS_TCS.GS.getGyro();
                     //System.out.println(Gyro);
                  
               }
            };
            
            scrollPane = new JScrollPane(panel);
            setContentPane(scrollPane);

         }
         
         
      @Override
      public void run() {
         // TODO Auto-generated method stub
         
         float ActualCarSpeed = 0;
         String tempCarSpeed = null;
         int countEntry = 0;
         
         
         
         DrivingState frame = new DrivingState();
         frame.setTitle("Driving State");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(934,500);
            frame.setVisible(true);
            
            
            
            while(true){
               
            	countEntry++;
            	
            	if(countEntry == 1)
            	{
            		tempRoad = "NORMAL";
            		tempTCS = "OFF";
            	}
            		
            	
            		
            	 try {
                     if(queueRoadStateToThread3.peek() != null)
                     	tempRoad =  (String) queueRoadStateToThread3.take();
                   
                  } catch (InterruptedException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
            	 
            	 try {
                     if(queueTCSToThread3.peek() != null)
                     	tempTCS =  (String) queueTCSToThread3.take();
                   
                  } catch (InterruptedException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
            	
               try {
                  if(queueCarSpeedToThread3.peek() != null)
                	  tempCarSpeed = Float.toString((float) queueCarSpeedToThread3.take());
                  
               } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
               
               try {
                   if(queueLaneChangeToThread3.peek() != null)
                      tempLaneChange = (String) queueLaneChangeToThread3.take();
                	  
                   
                } catch (InterruptedException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
                }//System.out.println(tempLaneChange);
               
               /*
               try {
            	   
				tempLaneChange = (String) queueLaneChangeToThread3.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//System.out.println(tempLaneChange);*/
               
               if(tempCarSpeed != null)
               {
            	   if(tempTCS == "OFF")
            	   {
            		   if(tempRoad == "NORMAL")
                  		  ActualCarSpeed = Float.parseFloat(tempCarSpeed);
                  	  
                  	  else if(tempRoad == "ICY")
                  		  ActualCarSpeed = (float) (Float.parseFloat(tempCarSpeed) * 0.3);
                  	  
                  	  else if(tempRoad == "RAINY")
                  		  ActualCarSpeed = (float) (Float.parseFloat(tempCarSpeed) * 0.5);
            	   }
            	   
            	   else
            	   {
            		   if(tempRoad == "NORMAL")
                   		  ActualCarSpeed = Float.parseFloat(tempCarSpeed);
                   	  
                   	  else if(tempRoad == "ICY")
                   		  ActualCarSpeed = (float) (Float.parseFloat(tempCarSpeed) * 0.4);
                   	  
                   	  else if(tempRoad == "RAINY")
                   		  ActualCarSpeed = (float) (Float.parseFloat(tempCarSpeed) * 0.8);
            	   }
            		   

            	   
               }
            	   
                 queueSteeringAngleToThread2.add(SteeringAngle);
                 queueGyroToThread2.add(Gyro);
                 
                 
                 
                //System.out.println(SteeringAngle);
                  k -= ActualCarSpeed * 0.1;
                   
                  /*  
                    if(ActualCarSpeed <10)
                       t += ActualCarSpeed * 0.080;
                       
                    else if(ActualCarSpeed >= 10 && ActualCarSpeed < 20)
                        t += ActualCarSpeed * 0.040;
                    
                    else if(ActualCarSpeed >= 20 && ActualCarSpeed < 50)
                        t += ActualCarSpeed * 0.025;
                    
                    else if(ActualCarSpeed >= 50 && ActualCarSpeed < 100)
                        t += ActualCarSpeed * 0.015;
                    
                    else
                       t += ActualCarSpeed * 0.010;
                   */
                  
                  
                  
                  
                 
               
                   
                  
               frame.repaint();
               frame.setVisible(true);
               try{
                  Thread.sleep(10);
               }catch(Exception ex){};
              
            }

         
      }

      

   }
   
   
   class SlipRatioGraph extends ApplicationFrame implements Runnable{
       
       private static String TITLE;      
       private static float MINMAX;
       private static int COUNT;
       private static Random random;
       private Timer timer;
    
       private BlockingQueue queueCarSpeedToThread4;
      private BlockingQueue queueWheelRatioToThread4;
      private BlockingQueue queueRoadStateToThread4;
      private BlockingQueue queueTCSToThread4;
      
      
      static String tempCarSpeed = null;
      static float tempWheelRatio = 0;
      static String tempRoad = "";
      static String tempTCS = "";
      static float ActualCarSpeed = 0;
      static float WheelRatio = 0;
      
      static int countEntry = 0;
      
      
      
       public SlipRatioGraph(final String title, float minmax, int count, Random random, BlockingQueue queueCarSpeedToThread4, BlockingQueue queueRoadStateToThread4, BlockingQueue queueWheelRatioToThread4, BlockingQueue queueTCSToThread4) {
           super(title);
           
           TITLE = title;        
           MINMAX = minmax;
           COUNT = count;                            
           this.random = random;
           
           this.queueCarSpeedToThread4 = queueCarSpeedToThread4;
           this.queueWheelRatioToThread4 = queueWheelRatioToThread4;
           this.queueRoadStateToThread4 = queueRoadStateToThread4;
           this.queueTCSToThread4 = queueTCSToThread4;
           
           
           final DynamicTimeSeriesCollection dataset =
               new DynamicTimeSeriesCollection(4, COUNT, new Second());//
           dataset.setTimeBase(new Second(0, 0, 0, 1, 1, 2011));
           dataset.addSeries(ULWheelSpeedData(), 0, "Front left wheel");
           dataset.addSeries(URWheelSpeedData(), 1, "Front right wheel");//
           dataset.addSeries(DLWheelSpeedData(), 2, "Rear left wheel");
           dataset.addSeries(DRWheelSpeedData(), 3, "Rear right wheel");
           
           JFreeChart chart = createChart(dataset);
    
          
    
           this.add(new ChartPanel(chart), BorderLayout.CENTER);
           
           
           
           
           timer = new Timer(30, new ActionListener() {
        	   
               float[] newData = new float[4];//
               
               @Override
               public void actionPerformed(ActionEvent e) {
            	   ReceiveFromInput();
            	   
            	   /*
            	   newData[0] = 0;                 
                   newData[1] = 10;
                   newData[2] = 20;
                   newData[3] = 30;
            	   */
            	   
                   newData[0] = CalcSlipRatio(SS_TCS.CSS.getActualCarSpeed(), (float) (SS_TCS.WSS.getULWheelSpeed()- SS_TCS.BVA.getULBrakeValue()*0.01 * SS_TCS.BVA.getULBrakeAdd()));                 
                   newData[1] = CalcSlipRatio(SS_TCS.CSS.getActualCarSpeed(), (float) (SS_TCS.WSS.getURWheelSpeed()- SS_TCS.BVA.getURBrakeValue()*0.01 * SS_TCS.BVA.getURBrakeAdd()));//
                   newData[2] = CalcSlipRatio(SS_TCS.CSS.getActualCarSpeed(), (float) (SS_TCS.WSS.getDLWheelSpeed()- SS_TCS.BVA.getDLBrakeValue()*0.01 * SS_TCS.BVA.getDLBrakeAdd()));
                   newData[3] = CalcSlipRatio(SS_TCS.CSS.getActualCarSpeed(), (float) (SS_TCS.WSS.getDRWheelSpeed()- SS_TCS.BVA.getDRBrakeValue()*0.01 * SS_TCS.BVA.getDRBrakeAdd()));
                   /*System.out.println(CarSpeed);
                   System.out.println(WSS.getULWheelSpeed());
                   System.out.println(CalcSlipRatio(CarSpeed, WSS.getULWheelSpeed()));
                   */
                   dataset.advanceTime();
                   
                   
                   
                   dataset.appendData(newData);
               }
           });
       }
    
       private float randomValue() {
           return (float) (random.nextGaussian() * MINMAX / 3);
       }
    
       private float[] ULWheelSpeedData() {
           float[] a = new float[COUNT];
           for (int i = 0; i < a.length; i++) {
               a[i] = CalcSlipRatio(SS_TCS.CSS.getActualCarSpeed(), (float) (SS_TCS.WSS.getULWheelSpeed()- SS_TCS.BVA.getULBrakeValue()*0.01 * SS_TCS.BVA.getULBrakeAdd()));
           }
           return a;
       }
       
       
       private float[] URWheelSpeedData() {//
           float[] a = new float[COUNT];
           for (int i = 0; i < a.length; i++) {
               a[i] = CalcSlipRatio(SS_TCS.CSS.getActualCarSpeed(), (float) (SS_TCS.WSS.getURWheelSpeed()- SS_TCS.BVA.getURBrakeValue()*0.01 * SS_TCS.BVA.getURBrakeAdd()));
           }
           return a;
       }
       
       private float[] DLWheelSpeedData() {//
           float[] a = new float[COUNT];
           for (int i = 0; i < a.length; i++) {
               a[i] = CalcSlipRatio(SS_TCS.CSS.getActualCarSpeed(), (float) (SS_TCS.WSS.getDLWheelSpeed()- SS_TCS.BVA.getDLBrakeValue()*0.01 * SS_TCS.BVA.getDLBrakeAdd()));
           }
           return a;
       }
       
       private float[] DRWheelSpeedData() {//
           float[] a = new float[COUNT];
           for (int i = 0; i < a.length; i++) {
               a[i] = CalcSlipRatio(SS_TCS.CSS.getActualCarSpeed(), (float) (SS_TCS.WSS.getDRWheelSpeed()- SS_TCS.BVA.getDRBrakeValue()*0.01 * SS_TCS.BVA.getDRBrakeAdd()));
           }
           return a;
       }
       
    
       private JFreeChart createChart(final XYDataset dataset) {
           final JFreeChart result = ChartFactory.createTimeSeriesChart(
               TITLE, "hh:mm:ss", "Slip ratio", dataset, true, true, false);
           final XYPlot plot = result.getXYPlot();
           ValueAxis domain = plot.getDomainAxis();
           domain.setAutoRange(true);
           ValueAxis range = plot.getRangeAxis();
           range.setRange(-MINMAX, MINMAX);
           return result;
       }
    
       public void start() {
           timer.start();
       }
    
       

      
      

      @Override
      public void run() {
         // TODO Auto-generated method stub
                  
                  
                  SlipRatioGraph SRG = new SlipRatioGraph(TITLE, MINMAX, COUNT, random, queueCarSpeedToThread4, queueRoadStateToThread4, queueWheelRatioToThread4, queueTCSToThread4);
                  
                  
                  
                  
                  
                 
                  
                  
                  //SlipRatioGraph demo = new SlipRatioGraph(TITLE, START, STOP, MINMAX, COUNT, FAST, SLOW, random);
                  SRG.pack();
                     RefineryUtilities.centerFrameOnScreen(SRG);
                     SRG.setVisible(true);
                     SRG.start();
      }
      
      public float CalcSlipRatio(float ActualCarSpeed, float WheelSpeed){
    	  
    	  float SlipRatio = 0;
    	  
    	  if(ActualCarSpeed == 0)
    		  SlipRatio = 0;
    	  else
    	      SlipRatio = (WheelSpeed - ActualCarSpeed) * 100 / WheelSpeed;
    	  //System.out.println(ActualCarSpeed);
    	  //System.out.println(WheelSpeed);
    	  return SlipRatio;
      }
      
public void ReceiveFromInput(){
    	  
	countEntry++;
	if(countEntry == 1)
	{
		tempWheelRatio = (float) 0.8;
		tempRoad = "NORMAL";
		tempTCS = "OFF";
	}
	
	
    	  try {
              if(queueCarSpeedToThread4.peek() != null){
              	tempCarSpeed =  Float.toString((float) queueCarSpeedToThread4.take());
              }
           } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
           } 
    	  
    	  try {
              if(queueWheelRatioToThread4.peek() != null){
              	tempWheelRatio =  (float) queueWheelRatioToThread4.take();
              }
           } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
           }//System.out.println(queueWheelRatioToThread4.peek());
          
    	  try {
              if(queueRoadStateToThread4.peek() != null)
              	tempRoad =  (String) queueRoadStateToThread4.take();
            
           } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
           }
    	  
    	  try {
              if(queueTCSToThread4.peek() != null)
              	tempTCS =  (String) queueTCSToThread4.take();
            
           } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
           }
           
          
		if(tempTCS == "OFF")
          {
        	  if(tempCarSpeed != null)
              {
            	  if(tempRoad == "NORMAL")
            		  ActualCarSpeed = Float.parseFloat(tempCarSpeed);
            	  
            	  else if(tempRoad == "ICY")
            		  ActualCarSpeed = (float) (Float.parseFloat(tempCarSpeed) * 0.3);
            	  
            	  else if(tempRoad == "RAINY")
            		  ActualCarSpeed = (float) (Float.parseFloat(tempCarSpeed) * 0.5);
              }
        	  SS_TCS.CSS.setActualCarSpeed(ActualCarSpeed);
        	  if(tempWheelRatio != 0)
            	  WheelRatio = tempWheelRatio;
          }
          
          else
          {
        	
        	  if(tempCarSpeed != null)
              {
            	  if(tempRoad == "NORMAL")
            		  ActualCarSpeed = Float.parseFloat(tempCarSpeed);  //////////////////////////
            	  
            	  
            	  else if(tempRoad == "ICY")
            		  ActualCarSpeed = (float) (Float.parseFloat(tempCarSpeed) * 0.4);
            	  
            	  else if(tempRoad == "RAINY")
            		  ActualCarSpeed = (float) (Float.parseFloat(tempCarSpeed) * 0.5);
              }
        	  SS_TCS.CSS.setActualCarSpeed(ActualCarSpeed);
        	  if(tempWheelRatio != 0)
            	  WheelRatio = (float) 0.8;
          }
        	  
          
          
         ////////////////////////////////////////////////////////// 
		
		if(tempTCS == "ON")
		{
			SS_TCS.WSS.setULWheelSpeed((float) (ActualCarSpeed / WheelRatio - SS_TCS.BVA.getULBrakeValue()*0.01 * SS_TCS.BVA.getULBrakeAdd()));
			SS_TCS.WSS.setURWheelSpeed((float) (ActualCarSpeed / WheelRatio - SS_TCS.BVA.getURBrakeValue()*0.01 * SS_TCS.BVA.getURBrakeAdd()));
			SS_TCS.WSS.setDLWheelSpeed((float) (ActualCarSpeed / WheelRatio - SS_TCS.BVA.getDLBrakeValue()*0.01 * SS_TCS.BVA.getDLBrakeAdd()));
	        SS_TCS.WSS.setDRWheelSpeed((float) (ActualCarSpeed / WheelRatio - SS_TCS.BVA.getDRBrakeValue()*0.01 * SS_TCS.BVA.getDRBrakeAdd()));
	        System.out.println("UL"+SS_TCS.BVA.getULBrakeValue());
	        System.out.println("UR"+SS_TCS.BVA.getURBrakeValue());
	        System.out.println("DL"+SS_TCS.BVA.getDLBrakeValue());
	        System.out.println("DR"+SS_TCS.BVA.getDRBrakeValue());
	        System.out.println("Steering"+(SS_TCS.SAS.getSteeringAngle()+40));
	        System.out.println("Gyro"+(SS_TCS.GS.getGyro()+40));
		}
			
		else
		{
			SS_TCS.WSS.setULWheelSpeed(ActualCarSpeed / WheelRatio);
			SS_TCS.WSS.setURWheelSpeed(ActualCarSpeed / WheelRatio);
			SS_TCS.WSS.setDLWheelSpeed(ActualCarSpeed / WheelRatio);
	        SS_TCS.WSS.setDRWheelSpeed(ActualCarSpeed / WheelRatio);
	      
		}
		
		
		
		
        
      }
   }
   
class ThrottleValveGraph extends ApplicationFrame implements Runnable{
       
       private static String TITLE2;      
       private static float MINMAX;
       private static int COUNT;
       private static Random random;
       private Timer timer;
    
       private BlockingQueue queueCarSpeedToThread5;
      
      
      static String tempCarSpeed = null;
      
      
      static float Accelerator = 0;
      
      
      
      
      
      
       public ThrottleValveGraph(final String title, float minmax, int count, Random random, BlockingQueue queueCarSpeedToThread5) {
           super(title);
           
           TITLE2 = title;        
           MINMAX = minmax;
           COUNT = count;                            
           this.random = random;
           
           this.queueCarSpeedToThread5 = queueCarSpeedToThread5;
           
         
           
           final DynamicTimeSeriesCollection dataset =
               new DynamicTimeSeriesCollection(1, COUNT, new Second());//
           dataset.setTimeBase(new Second(0, 0, 0, 1, 1, 2011));
           dataset.addSeries(ThrottleValveGraph(), 0, "Throttle valve");
           
           
           JFreeChart chart = createChart(dataset);
    
          
    
           this.add(new ChartPanel(chart), BorderLayout.CENTER);
           
           
           
           
           timer = new Timer(30, new ActionListener() {
        	   
               float[] newData = new float[1];//
               
               @Override
               public void actionPerformed(ActionEvent e) {
            	   ReceiveFromInput();
            	   
            	   /*
            	   newData[0] = 0;                 
                   newData[1] = 10;
                   newData[2] = 20;
                   newData[3] = 30;
            	   */
            	   
                   newData[0] = SS_TCS.TA.getThrottle();                 
                   
                   /*System.out.println(CarSpeed);
                   System.out.println(WSS.getULWheelSpeed());
                   System.out.println(CalcSlipRatio(CarSpeed, WSS.getULWheelSpeed()));
                   */
                   dataset.advanceTime();
                   
                   
                   
                   dataset.appendData(newData);
               }
           });
       }
    
       private float randomValue() {
           return (float) (random.nextGaussian() * MINMAX / 3);
       }
    
       private float[] ThrottleValveGraph() {
           float[] a = new float[COUNT];
           for (int i = 0; i < a.length; i++) {
               a[i] = SS_TCS.TA.getThrottle();
           }
           return a;
       }
       
       
      
       
    
       private JFreeChart createChart(final XYDataset dataset) {
           final JFreeChart result = ChartFactory.createTimeSeriesChart(
               TITLE2, "hh:mm:ss", "Throttle Valve ratio", dataset, true, true, false);
           final XYPlot plot = result.getXYPlot();
           ValueAxis domain = plot.getDomainAxis();
           domain.setAutoRange(true);
           ValueAxis range = plot.getRangeAxis();
           range.setRange(-MINMAX, MINMAX);
           return result;
       }
    
       public void start() {
           timer.start();
       }
    
       

      
      

      @Override
      public void run() {
         // TODO Auto-generated method stub
                  
                  
                  ThrottleValveGraph TVG = new ThrottleValveGraph(TITLE2, MINMAX, COUNT, random, queueCarSpeedToThread5);
                  
                  
                  
                  
                  
                 
                  
                  
                  //SlipRatioGraph demo = new SlipRatioGraph(TITLE, START, STOP, MINMAX, COUNT, FAST, SLOW, random);
                  TVG.pack();
                     RefineryUtilities.centerFrameOnScreen(TVG);
                     TVG.setVisible(true);
                     TVG.start();
      }
      
      public float CalcThrottleValveRatio(float Accelerator){
    	  
    	  float ThrottleVavleRatio = 0;
    	  
    	  ThrottleVavleRatio = Accelerator / 2;
    	  //System.out.println( Accelerator);
    	  //System.out.println(WheelSpeed);
    	  return ThrottleVavleRatio;
      }
      
public void ReceiveFromInput(){
    	  
	
	
	
    	  try {
              if(queueCarSpeedToThread5.peek() != null){
              	tempCarSpeed =  Float.toString((float) queueCarSpeedToThread5.take());
              }
           } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
           } 
    	  
    	  
          
    	  
           
          
          if(tempCarSpeed != null)
        	  Accelerator = (float) (Float.parseFloat(tempCarSpeed));
          
        	  
          
          
      }
   }

