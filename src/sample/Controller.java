package sample;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Controller {

    @FXML
    GridPane gridPane;

    private boolean isPlaying = false;

    public static MediaPlayer player;

    public void initialize(){
    }

    public void addToGrid(Node node, int col, int row){
        gridPane.add(node, col, row);
    }


}


