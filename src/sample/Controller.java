package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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
    @FXML
    Slider volumeSlider;

    @FXML
    public void SetVolume(){
        //MusicPlayer.setVolume( 1.0f * (((int) volumeSlider.getValue()) / 100));//0 - 100 converted to percent
        int temp = (int)volumeSlider.getValue();
        double temp2 = (1.0 * temp) / 100;
        System.out.println( temp2 );
    }

    private boolean isPlaying = false;

    public void initialize(){
        volumeSlider.setValue(50);
        volumeSlider.setBlockIncrement(10);
        volumeSlider.setMajorTickUnit(50);
        volumeSlider.setMinorTickCount(5);
        volumeSlider.setSnapToTicks(true);

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                MusicPlayer.setVolume( (((int)newValue.doubleValue()) * 1.0) / 100 );
            }
        });

        //volumeSlider.setShowTickMarks(true);
    }

    public void addToGrid(Node node, int col, int row){
        gridPane.add(node, col, row);
    }


}


