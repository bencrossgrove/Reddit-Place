package place.client.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import place.*;
import place.network.NetworkClient;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A JavaFx GUI version of Place
 *
 * @author Ben Crossgrove
 * @author Mitch Leadley
 */

public class PlaceGUI extends Application implements Observer {

    private PlaceBoardObservable model;
    private NetworkClient networkClient;
    private List<String> params = null;
    private String username;

    private final GridPane grid = new GridPane();
    private ToggleGroup colors = new ToggleGroup();

    private static int dim;
    private static int WINDOW_SIZE = 500;

    public static void main(String[] args) {
        // validate args
        if (args.length != 3) {
            System.err.println(
                    "Usage: java PlaceGUI <host name> <port number> <username>");
            System.exit(1);
        }
        Application.launch(args);
    }

    /**
     * Setup NetworkClient and get the current board
     */
    public void init() {
        String hostName = getParamAtIndex(0);
        int portNumber = Integer.parseInt(getParamAtIndex(1));
        username = getParamAtIndex(2);

        System.out.println("Client connecting to " + hostName + ":" + portNumber + "\n");

        this.networkClient = new NetworkClient(hostName, portNumber, username);

        this.model = networkClient.getModel();
        this.model.addObserver(this);
    }

    /**
     * Build the GUI window
     *
     * @param primaryStage top level container
     * @throws Exception if something goes wrong building GUI
     */
    public void start(Stage primaryStage) throws Exception {
        BorderPane border = new BorderPane();
        border.setPrefHeight(WINDOW_SIZE);
        border.setPrefWidth(WINDOW_SIZE);

        Pane grid = createGrid();
        border.setCenter(grid);

        Pane colorsPane = createColorsPane();
        border.setBottom(colorsPane);

        Scene scene = new Scene(border);
        setStage(primaryStage, scene);
    }

    /**
     * setup the gui window with scene created in start()
     *
     * @param primaryStage the top level container
     * @param scene        the contents to be added
     */
    private void setStage(Stage primaryStage, Scene scene) {
        primaryStage.setTitle("Place: " + username);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.out.println(username + " is exiting");
                primaryStage.close();
                networkClient.stop();
                System.exit(0);
            }
        });
    }

    /**
     * creates and returns a grid layout of buttons.
     */
    private Pane createGrid() {
        dim = model.getBoard().length;
        initializeGrid();
        return grid;
    }

    /**
     * create the colors pane to allow user to select colors
     *
     * @return pane of color options
     */
    private VBox createColorsPane() {
        VBox bottomPane = new VBox();
        HBox colorsPane = new HBox();
        HBox labelPane = new HBox();
        // will default to black
        Label selectedColor = new Label("Selected Color: " + PlaceColor.BLACK.name());
        for (int i = 0; i < PlaceColor.TOTAL_COLORS; i++) {
            ToggleButton btn = new ToggleButton(Integer.toHexString(i));
            PlaceColor currentColor = PlaceColorUtil.getColor(i);
            btn.setMaxWidth(WINDOW_SIZE / dim);
            btn.setStyle("-fx-background-color:" + currentColor.name());
            float[] hsbValue = PlaceColorUtil.getHSB(currentColor);
            float brightness = hsbValue[2];
            if (brightness < 0.6 || currentColor.name().equals("BLUE")) {
                btn.setTextFill(Color.WHITE);
            }
            btn.setToggleGroup(colors);
            // default selected color to black
            if (i == 0) {
                btn.setSelected(true);
            }
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    selectedColor.setText("Selected Color: " + currentColor.name());
                }
            });
            colorsPane.getChildren().addAll(btn);
        }

        labelPane.getChildren().add(selectedColor);
        colorsPane.setAlignment(Pos.CENTER);
        labelPane.setAlignment(Pos.CENTER);

        bottomPane.getChildren().addAll(colorsPane, labelPane);
        bottomPane.setAlignment(Pos.CENTER);
        return bottomPane;
    }

    /**
     * creates the grid based on the current board (model)
     */
    private void initializeGrid() {
        for (int r = 0; r < dim; ++r) {
            for (int c = 0; c < dim; ++c) {
                PlaceTile current = model.getTile(r, c);
                Rectangle tile = createTileRect(current);
                grid.add(tile, r, c);
            }
        }
        grid.setAlignment(Pos.CENTER);
    }

    /**
     * creates a rectangle that represents the tile to be placed in the grid
     *
     * @param current the tile to be placed
     * @return rectangle representation of tile
     */
    private Rectangle createTileRect(PlaceTile current) {
        Rectangle tile = new Rectangle();
        tile.setWidth(WINDOW_SIZE / dim);
        tile.setHeight(WINDOW_SIZE / dim);
        Color color = Color.rgb(current.getColor().getRed(), current.getColor().getGreen(), current.getColor().getBlue());
        tile.setFill(color);
        // create graphic for Tooltip
        Rectangle tileGraphic = new Rectangle();
        tileGraphic.setWidth(25);
        tileGraphic.setHeight(25);
        tileGraphic.setFill(color);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date d = new Date(current.getTime());
        Tooltip t = new Tooltip("(" + current.getRow() + ", " + current.getCol() + ")\n" + current.getOwner() + "\n" + sdf.format(d) + "\n" + current.getColor().name());
        t.setGraphic(tileGraphic);
        Tooltip.install(tile, t);

        tile.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                int x = current.getRow();
                int y = current.getCol();
                Logger.debug("Tile clicked: (" + x + ", " + y + ")");
                String colorString = getSelectedColorName();
                Logger.debug(colorString);
                PlaceTile newTile = new PlaceTile(x, y, username, PlaceColor.valueOf(colorString), System.currentTimeMillis());
                networkClient.sendChangeTileReq(newTile);
            }
        });

        return tile;
    }

    /**
     * get the color from the selected toggle button in the colors pane
     *
     * @return string / name of the color
     */
    private String getSelectedColorName() {
        ToggleButton selected = (ToggleButton) colors.getSelectedToggle();
        int start = selected.getStyle().indexOf(":");
        return selected.getStyle().substring(start + 1, selected.getStyle().length());
    }

    /**
     * updates a specific tile on tile change req
     *
     * @param tile the tile to change
     */
    private void updateTile(PlaceTile tile) {
        Rectangle newTileRect = createTileRect(tile);
        grid.add(newTileRect, tile.getRow(), tile.getCol());
    }

    /**
     * get argument at a specific index
     * checked that args.length == 3 beforehand
     *
     * @param index the index to get the argument from
     * @return the argument
     */
    private String getParamAtIndex(int index) {
        if (params == null) {
            params = super.getParameters().getRaw();
        }
        return params.get(index);
    }

    @Override
    public void update(Observable o, Object arg) {
        Logger.debug("Observable: " + o.toString());
        Logger.debug("Object: " + arg.toString());

        PlaceTile tile = (PlaceTile) arg;

        Platform.runLater(() -> this.updateTile(tile));
    }
}
