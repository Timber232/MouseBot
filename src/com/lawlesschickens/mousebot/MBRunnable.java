package com.lawlesschickens.mousebot;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.Random;

import javafx.application.Platform;

public class MBRunnable implements Runnable{

	private MBUserInterface mouseBotUI;
	private Robot robot;
	private int currentMainLoop= 0;
	private int currentCellLoop= 0;
	private int mainLoopLeft = 0;
	private int totalCellLoop = 0;
	private boolean isRunning = true;
	
	public MBRunnable(MBUserInterface mouseBotUI) {
		super();
		this.mouseBotUI = mouseBotUI;
	}
	
	public void end() {
		this.isRunning = false;
	}
	
	@Override
	public void run() {
		int loopLimit = Integer.parseInt(mouseBotUI.textLoopTotal.getText());
		mainLoopLeft = loopLimit;
		for(currentMainLoop = 0; currentMainLoop<loopLimit; currentMainLoop++) {
			for (MBAction o : mouseBotUI.table.getItems()) {
				int x = Integer.parseInt(mouseBotUI.table.getColumns().get(1).getCellData(o).toString());
				int y = Integer.parseInt(mouseBotUI.table.getColumns().get(2).getCellData(o).toString());
				MBAction.Action action = (MBAction.Action) mouseBotUI.table.getColumns().get(3).getCellData(o);
				float timer = Float.parseFloat(mouseBotUI.table.getColumns().get(4).getCellData(o).toString())*1000;
				int jitter = Integer.parseInt(mouseBotUI.table.getColumns().get(5).getCellData(o).toString());
				int loop = Integer.parseInt(mouseBotUI.table.getColumns().get(6).getCellData(o).toString());
				totalCellLoop = loop;
				if(!this.isRunning) return;
				for(currentCellLoop = 0; currentCellLoop<totalCellLoop; currentCellLoop++) {
					try {
						Thread.sleep((int)timer);
						Platform.runLater(() ->{
							if(!this.isRunning) return;
							mouseBotUI.table.requestFocus();
							mouseBotUI.table.getSelectionModel().select(o);
							mouseBotUI.table.getFocusModel().focus(0);
							try {
								runRobot(x, y, jitter, action);
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
	
    private void runRobot(int x, int y, int jitter, MBAction.Action action) throws AWTException {
    	// Random mouse movement:
    	Random rn = new Random();
    	x += rn.nextInt(jitter-0+1)+0;
    	y += rn.nextInt(jitter-0+1)+0;
    	
    	robot = new Robot();
    	robot.mouseMove(x, y);
    	switch (action) {
		case MOUSE_LEFT_CLICK:
	    	robot.mousePress(InputEvent.BUTTON1_MASK);
	    	robot.mouseRelease(InputEvent.BUTTON1_MASK);
			break;
		case MOUSE_LEFT_CLICK_HOLD:
			robot.mousePress(InputEvent.BUTTON1_MASK);
			break;
		case MOUSE_LEFT_CLICK_RELEASE:
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			break;
		case MOUSE_RIGHT_CLICK:
	    	robot.mousePress(InputEvent.BUTTON3_MASK);
	    	robot.mouseRelease(InputEvent.BUTTON3_MASK);
			break;
		case MOUSE_RIGHT_CLICK_HOLD:
			robot.mousePress(InputEvent.BUTTON3_MASK);
			break;
		case MOUSE_RIGHT_CLICK_RELEASE:
			robot.mouseRelease(InputEvent.BUTTON3_MASK);
		default:
			break;
		}
    	
    	int subLoopLeft = totalCellLoop - currentCellLoop;
    	mouseBotUI.textAddNode.setText(currentCellLoop + "/" + totalCellLoop + " " + subLoopLeft + " left");
    }
}
