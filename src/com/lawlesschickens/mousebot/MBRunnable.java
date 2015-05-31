package com.lawlesschickens.mousebot;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.Random;

import javafx.application.Platform;

public class MBRunnable extends Thread{

	private MBUserInterface mouseBotUI;
	private Robot robot;
	private int currentMainLoop= 0;
	private int currentCellLoop= 0;
	private int mainLoopLeft = 0;
	private int totalCellLoop = 0;
	
	public MBRunnable(MBUserInterface mouseBotUI) {
		super();
		this.mouseBotUI = mouseBotUI;
	}
	
	@Override
	public void start() {

	}
	
	@Override
	public void run() {
		int loopLimit = Integer.parseInt(mouseBotUI.textLoopTotal.getText());
		mainLoopLeft = loopLimit;
		for(currentMainLoop = 0; currentMainLoop<loopLimit; currentMainLoop++) {
			for (MBAction o : mouseBotUI.table.getItems()) {
				int x = Integer.parseInt(mouseBotUI.table.getColumns().get(1).getCellData(o).toString());
				int y = Integer.parseInt(mouseBotUI.table.getColumns().get(2).getCellData(o).toString());
				//String action = mouseBotUI.table.getColumns().get(3).getCellData(o).toString();
				float timer = Float.parseFloat(mouseBotUI.table.getColumns().get(4).getCellData(o).toString())*1000;
				int jitter = (int)mouseBotUI.table.getColumns().get(5).getCellData(o);
				int loop = Integer.parseInt(mouseBotUI.table.getColumns().get(6).getCellData(o).toString());
				totalCellLoop = loop;
				for(currentCellLoop = 0; currentCellLoop<totalCellLoop; currentCellLoop++) {
					try {
						Thread.sleep((int)timer);
						Platform.runLater(() ->{
							mouseBotUI.table.requestFocus();
							mouseBotUI.table.getSelectionModel().select(o);
							mouseBotUI.table.getFocusModel().focus(0);
							try {
								runRobot(x, y, jitter);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							mouseBotUI.textLeftValue.setText(Integer.toString(mainLoopLeft));
							mouseBotUI.textLoop.setText(Integer.toString(currentMainLoop));
						 });
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			mainLoopLeft--;
		}
    }
	
    private void runRobot(int x, int y, int jitter) throws AWTException {
    	// Random mouse movement:
    	Random rn = new Random();
    	x += rn.nextInt(jitter-0+1)+0;
    	y += rn.nextInt(jitter-0+1)+0;
    	robot = new Robot();
    	robot.mouseMove(x, y);
    	robot.mousePress(InputEvent.BUTTON1_MASK);
    	robot.mouseRelease(InputEvent.BUTTON1_MASK);
    	int subLoopLeft = totalCellLoop - currentCellLoop;
    	mouseBotUI.textAddNode.setText(currentCellLoop + "/" + totalCellLoop + " " + subLoopLeft + " left");
    }
}
