package place.client.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import place.*;
import place.network.NetworkClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class PlaceGUI extends Application implements Observer {

    private PlaceBoardObservable model;
    private NetworkClient networkClient;
    private List<String> params = null;

    private String username;

    private final GridPane grid = new GridPane();

    private static int dim;

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

        Pane grid = createGrid();
        border.setCenter(grid);

        Pane colorsPane = createColorsPane();
        border.setBottom(colorsPane);

        Scene scene = new Scene(border);
        setStage(primaryStage, scene);
    }

    private void setStage(Stage primaryStage, Scene scene) {
        primaryStage.setTitle("Place: " + username);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
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
     * @return pane of color options
     */
    private HBox createColorsPane() {
        HBox colorsPane = new HBox();
        ToggleGroup colors = new ToggleGroup();
        for (int i = 0; i < PlaceColor.TOTAL_COLORS; i++){
            ToggleButton btn = new ToggleButton(Integer.toHexString(i));
            btn.setStyle("-fx-background-color:" + PlaceColorUtil.getColor(i).name());
            btn.setToggleGroup(colors);
            // default to black
            if (i == 0){
                btn.setSelected(true);
            }
            colorsPane.getChildren().addAll(btn);
        }
        return colorsPane;
    }

    private void initializeGrid() {
        // build grid
        for (int r = 0; r < dim; ++r) {
            for (int c = 0; c < dim; ++c) {
                PlaceTile current = model.getTile(r, c);
                Rectangle tile = createTileRect(current);
                grid.add(tile, r, c);
            }
        }
    }

    private Rectangle createTileRect(PlaceTile current) {
        Rectangle tile = new Rectangle();
        tile.setWidth(50);
        tile.setHeight(50);
        Color color = Color.rgb(current.getColor().getRed(), current.getColor().getGreen(), current.getColor().getBlue());
        tile.setFill(color);

        Rectangle tileGraphic = new Rectangle();
        tileGraphic.setWidth(25);
        tileGraphic.setHeight(25);
        tileGraphic.setFill(color);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date d = new Date(current.getTime());
        Tooltip t = new Tooltip("(" + current.getRow() + ", " + current.getCol() + ")\n" + current.getOwner() + "\n" + sdf.format(d) + "\n" + current.getColor().name());
        t.setGraphic(tileGraphic);
        Tooltip.install(tile, t);
        return tile;
    }

    /**
     * updates a specific tile on tile change req
     * @param tile the tile to change
     */
    private void updateTile(PlaceTile tile) {
        Rectangle newTileRect = createTileRect(tile);
        grid.add(newTileRect, tile.getRow(), tile.getCol());
    }

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
