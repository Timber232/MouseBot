package com.lawlesschickens.mousebot;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class MBAction {
	public enum Action {
		MOUSE_LEFT_CLICK, 
		MOUSE_RIGHT_CLICK, 
		MOUSE_LEFT_CLICK_HOLD, 
		MOUSE_LEFT_CLICK_RELEASE,
		MOUSE_RIGHT_CLICK_HOLD,
		MOUSE_RIGHT_CLICK_RELEASE,
		
	}; 

	private static final SimpleIntegerProperty mainLoop = new SimpleIntegerProperty(1);
	private final SimpleStringProperty title = new SimpleStringProperty("");
	private final SimpleIntegerProperty xCoord = new SimpleIntegerProperty(0);
	private final SimpleIntegerProperty yCoord = new SimpleIntegerProperty(0);
	private final SimpleObjectProperty<Action> actionList;
	private final SimpleFloatProperty timer = new SimpleFloatProperty(0.0f);
	private final SimpleIntegerProperty jitter = new SimpleIntegerProperty(0);
	private final SimpleIntegerProperty loop = new SimpleIntegerProperty(0);

	public MBAction() {
		this("",0,0, Action.MOUSE_LEFT_CLICK,0.0f,0,0);
	}

	public MBAction(String title, int xCoord, int yCoord, Action action, float timer, int jitter, int loop) {
		setTitle(title);
		setxCoord(xCoord);
		setyCoord(yCoord);
		this.actionList = new SimpleObjectProperty<>(action);
		setTimer(timer);
		setJitter(jitter);
		setLoop(loop);
	}

	public static int getMainLoop() {
		return mainLoop.get();
	}

	public Action getAction() {
        return actionList.get();
    }

    public void setAction(Action actionList) {
        this.actionList.set(actionList);
    }
    
	public static SimpleIntegerProperty mainLoopProperty() {
		return mainLoop;
	}

	public static void setMainLoop(int mainLoop) {
		MBAction.mainLoop.set(mainLoop);
	}

	public String getTitle() {
		return title.get();
	}

	public SimpleStringProperty titleProperty() {
		return title;
	}

	public void setTitle(String title) {
		this.title.set(title);
	}

	public int getxCoord() {
		return xCoord.get();
	}

	public SimpleIntegerProperty xCoordProperty() {
		return xCoord;
	}

	public void setxCoord(int xCoord) {
		this.xCoord.set(xCoord);
	}

	public int getyCoord() {
		return yCoord.get();
	}

	public SimpleIntegerProperty yCoordProperty() {
		return yCoord;
	}

	public void setyCoord(int yCoord) {
		this.yCoord.set(yCoord);
	}

	public float getTimer() {
		return timer.get();
	}

	public SimpleFloatProperty timerProperty() {
		return timer;
	}

	public void setTimer(float timer) {
		this.timer.set(timer);
	}

	public int getJitter() {
		return jitter.get();
	}

	public SimpleIntegerProperty jitterProperty() {
		return jitter;
	}

	public void setJitter(int jitter) {
		this.jitter.set(jitter);
	}

	public int getLoop() {
		return loop.get();
	}

	public SimpleIntegerProperty loopProperty() {
		return loop;
	}

	public void setLoop(int loop) {
		this.loop.set(loop);
	}
}