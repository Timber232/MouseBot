package com.lawlesschickens.mousebot;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class MBUserInterface {
	private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
	public IntegerProperty currentlySelectedCell = new SimpleIntegerProperty();
	
	private VBox everything;
	// Top menu buttons
	public Button buttonRecord 	= new Button("Record");	// Record Button
    public Button buttonRemove 	= new Button("Remove");	// Remove Button
    public Button buttonPlay	= new Button("Play");	// Play Button
    
    // Top menu labels
    public Label labelLoop = new Label("Loops: ");		// Loops label
    public Text textLoop = new Text("0"); 				// Loop value
    public Text textSeperator = new Text("/");			// Separator ("0/1000")
    public Text textLoopTotal = new Text("1");			// Total Loops
    public Text textSpaceSeperator = new Text("   ");	// 
    public Text textLeftValue = new Text("0");			// Leftover loops
    public Label labelLeft = new Label(" left"); 		// Append an indicator to how much loops are left
    
    public Text textAddNode = new Text("");
    public Text textCoordinate = new Text();
    public Text textVersion = new Text("Version 0.5");
    
    public TextField textFieldLoop = new TextField();
    public TableView<MBAction> table = new TableView<MBAction>();
    public final ObservableList<MBAction> data = FXCollections.observableArrayList();

	public MBUserInterface() {
		this.everything = new VBox();
		this.everything.setSpacing(8);
		this.textFieldLoop.setPromptText("Loops");
		this.textFieldLoop.setPrefWidth(65);
		this.textLeftValue.setText(textLoopTotal.getText());
	}
		
	public BorderPane addNavigationMenu() {
		BorderPane topMenu = new BorderPane();
        HBox topMenuContainer = new HBox();
        topMenuContainer.setPadding(new Insets(10, 10, 0, 10));
        topMenuContainer.setSpacing(10);
        // Label container
        HBox labelContainer = new HBox();
        labelContainer.setAlignment(Pos.CENTER_LEFT);
        labelContainer.getChildren().addAll(labelLoop, textLoop, 
        		textSeperator, textLoopTotal, textSpaceSeperator,
        		textLeftValue, labelLeft);
        // Loop text box
        HBox loopUserInputContainer = new HBox();
        loopUserInputContainer.setPadding(new Insets(10, 10, 0, 10));
        loopUserInputContainer.setAlignment(Pos.CENTER);
        loopUserInputContainer.getChildren().addAll(textFieldLoop);
        
        topMenuContainer.getChildren().addAll(buttonRecord, buttonRemove, buttonPlay, labelContainer);
        topMenu.setLeft(topMenuContainer);
        topMenu.setRight(loopUserInputContainer);
        return topMenu;
	}
	
	@SuppressWarnings("unchecked")
	public TableView<MBAction> addMouseBotTable() {
        table.setEditable(true);
        
        // Allow drag/drop
        // TODO: Analyze this code later
        // Got this code from:
        // http://stackoverflow.com/questions/28603224/sort-tableview-with-drag-and-drop-rows
        table.setRowFactory(tv -> {
        	TableRow<MBAction> row = new TableRow<>();
        	row.setOnDragDetected(event -> {
                if (! row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });
        	
            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != ((Integer)db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    MBAction draggedCell = table.getItems().remove(draggedIndex);

                    int dropIndex ; 

                    if (row.isEmpty()) {
                        dropIndex = table.getItems().size() ;
                    } else {
                        dropIndex = row.getIndex();
                    }

                    table.getItems().add(dropIndex, draggedCell);

                    event.setDropCompleted(true);
                    table.getSelectionModel().select(dropIndex);
                    event.consume();
                }
            });
            
        	return row;
        });
        
        // Table columns
        TableColumn<MBAction, String> titleColumn = new TableColumn<MBAction, String>("Title");
        TableColumn<MBAction, Integer> xCoordColumn = new TableColumn<MBAction, Integer>("X-Coord");
        TableColumn<MBAction, Integer> yCoordColumn = new TableColumn<MBAction, Integer>("Y-Coord");
        TableColumn<MBAction, MBAction.Action> actionColumn = new TableColumn<MBAction, MBAction.Action>("Action");
        TableColumn<MBAction, Float> timerColumn 	= new TableColumn<MBAction, Float>("Timer");
        TableColumn<MBAction, Integer> jitterColumn = new TableColumn<MBAction, Integer>("Jitter");
        TableColumn<MBAction, Integer> loopColumn 	= new TableColumn<MBAction, Integer>("Loop");
        
        // Table Column Settings
        titleColumn.setCellValueFactory(new PropertyValueFactory<MBAction, String>("title"));
        titleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        titleColumn.setMinWidth(90);
        titleColumn.setMaxWidth(90);
        xCoordColumn.setCellValueFactory(new PropertyValueFactory<MBAction, Integer>("xCoord"));
        xCoordColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        yCoordColumn.setCellValueFactory(new PropertyValueFactory<MBAction, Integer>("yCoord"));
        yCoordColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        actionColumn.setCellValueFactory(new PropertyValueFactory<MBAction, MBAction.Action>("actionList"));
        actionColumn.setMinWidth(180);
        actionColumn.setMaxWidth(180);
        timerColumn.setCellValueFactory(new PropertyValueFactory<MBAction, Float>("timer"));
        timerColumn.setCellFactory(TextFieldTableCell.<MBAction, Float>forTableColumn(new FloatStringConverter()));
        jitterColumn.setCellValueFactory(new PropertyValueFactory<MBAction, Integer>("jitter"));
        jitterColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        loopColumn.setCellValueFactory(new PropertyValueFactory<MBAction, Integer>("loop"));
        loopColumn.setCellFactory(TextFieldTableCell.<MBAction, Integer>forTableColumn(new IntegerStringConverter()));
        
        try{
        	timerColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<MBAction,Float>>() {
    			@Override
    			public void handle(CellEditEvent<MBAction, Float> event) throws NumberFormatException {
    				event.getRowValue().setTimer(event.getNewValue());
    			}
    			
    		});
		} catch( NumberFormatException e){
			System.err.println(e.getMessage());
		}

        actionColumn.setCellFactory(ComboBoxTableCell.<MBAction, MBAction.Action>forTableColumn(MBAction.Action.values()));
        
        //actionColumn.setCellFactory(ComboBoxTableCell.forTableColumn(actionCombo));
        
        // Add table columns to the the actual table
        table.getColumns().addAll(titleColumn, xCoordColumn, yCoordColumn, 
        		actionColumn, timerColumn, jitterColumn, loopColumn);getClass();
		// Add data to the table
		table.setItems(data);
		
        // Get the index of the currently selected row
        table.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
				currentlySelectedCell.set(Integer.parseInt(newValue.toString()));
			}
        });
		
		return table;
	}
	
	public BorderPane addFooter() {
		BorderPane footer = new BorderPane();
        footer.setPadding(new Insets(0, 10, 10, 10));
        footer.setLeft(textAddNode);
        footer.setCenter(textCoordinate);
        footer.setRight(textVersion);
		return footer;
	}

	public VBox getEverything() {
		return everything;
	}
	
}
