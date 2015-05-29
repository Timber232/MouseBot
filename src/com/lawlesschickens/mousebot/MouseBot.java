package com.lawlesschickens.mousebot;

import java.awt.MouseInfo;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseMotionListener;

public class MouseBot extends Application{
	@FXML
	private Label currentMouseLocation;
	private boolean isRecording = false;
	private MBUserInterface mouseBotUI = new MBUserInterface();
	private Thread mbThread;
	

    @Override
    public void start(Stage stage) throws Exception {
    	
    	
    	setUserAgentStylesheet(STYLESHEET_MODENA);
    	// Turn off logging (JNativeHook)
    	Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    	logger.setLevel(Level.OFF);
    	
    	// Setup NativeKeyListener
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.exit(1);
        }
        
        // Create the root and scene
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 675, 240);
        
        // Put together the menu, table and footer
        mouseBotUI.getEverything().getChildren().add(mouseBotUI.addNavigationMenu());
        mouseBotUI.getEverything().getChildren().add(mouseBotUI.addMouseBotTable());
        mouseBotUI.getEverything().getChildren().add(mouseBotUI.addFooter());
        root.getChildren().add(mouseBotUI.getEverything());
        
        // "Record" button listener
        mouseBotUI.buttonRecord.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				toogleRecordBtn();
			}
        });
        
        // "Play" button listener
        mouseBotUI.buttonPlay.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event){
				isRecording = true;
				toogleRecordBtn();
				mouseBotUI.textLoop.setText("0");
				mbThread = new Thread(new MBRunnable(mouseBotUI));
				mbThread.start();
			}
		});
        
        // Loop input "On Change" listener
        mouseBotUI.textFieldLoop.textProperty().addListener((observable, oldValue, newValue) -> {
        	if(newValue.isEmpty()) {
        		mouseBotUI.textLeftValue.setText("1");
        		mouseBotUI.textLoopTotal.setText("1");
        	} else if (newValue.matches("\\d*") && mouseBotUI.textFieldLoop.getLength() != 5) {
        		mouseBotUI.textLoop.setText("0");
        		mouseBotUI.textLeftValue.setText(newValue);
        		mouseBotUI.textLoopTotal.setText(newValue);
        	} else {
        		Platform.runLater(new Runnable() {
    				@Override public void run() {
    					mouseBotUI.textFieldLoop.setText(oldValue);
    					mouseBotUI.textFieldLoop.positionCaret(mouseBotUI.textFieldLoop.getLength());
    				}
    			});
        	}
        });
        
        stage.setTitle("MouseBot");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.show();
        
        // Shutdown JNativeHook on close:
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
            	try {
					GlobalScreen.unregisterNativeHook();
				} catch (NativeHookException e) {
					System.err.println("Unable to close JNativeHook");
				}            	
            }
        });
        
        // Global keyboard listener:
        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
			
			@Override
			public void nativeKeyTyped(NativeKeyEvent e) {
				
			}
			
			@Override
			public void nativeKeyReleased(NativeKeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@SuppressWarnings("deprecation")
			@Override
			public void nativeKeyPressed(NativeKeyEvent e) {
				Platform.runLater(() ->{
					if(isRecording) {
						if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("a") || NativeKeyEvent.getKeyText(e.getKeyCode()).equals("A") ){
							mouseBotUI.data.add(new MBAction(
									"...", 
									MouseInfo.getPointerInfo().getLocation().x, 
									MouseInfo.getPointerInfo().getLocation().y, 
									"(Choose One)", 
									0.1f, 
									0, 
									1
							));
						}
					} else if (NativeKeyEvent.getKeyText(e.getKeyCode()).equals("S") ){
						mbThread.stop();
						mouseBotUI.textAddNode.setText("Stopped");
					}
				});
			}
		});
        
        // Global mouse listener:
        GlobalScreen.addNativeMouseListener(new NativeMouseListener() {
			@Override
			public void nativeMouseReleased(NativeMouseEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void nativeMousePressed(NativeMouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void nativeMouseClicked(NativeMouseEvent e) {
				// TODO Auto-generated method stub
				//info.setText("Mouse Pressed: " + e.getX() + ", " + e.getY());
				
			}
		});
        
		// Global mouse movement listener:
        GlobalScreen.addNativeMouseMotionListener(new NativeMouseMotionListener() {
			@Override
			public void nativeMouseMoved(NativeMouseEvent e) {
				Platform.runLater(new Runnable() {
					@Override public void run() {
						mouseBotUI.textCoordinate.setText(e.getX() + ", " + e.getY());
					}
				});
			}
			
			@Override
			public void nativeMouseDragged(NativeMouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
    }
    
    private void toogleRecordBtn() {
    	isRecording = isRecording == false ? true: false;
		if(isRecording) {
			mouseBotUI.buttonRecord.setText("Stop");
			mouseBotUI.textAddNode.setText("Press 'a' to add a node.");
		} else {
			mouseBotUI.buttonRecord.setText("Record");
			mouseBotUI.textAddNode.setText("");
		}
    }

    public static void main(String [] args) {
        launch(args);
    }
}