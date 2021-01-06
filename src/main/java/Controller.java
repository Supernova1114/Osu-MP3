import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class Controller {

    @FXML
    GridPane gridPane;
    @FXML
    Slider volumeSlider;
    @FXML
    Label label;
    @FXML
    Button pauseButton;
    @FXML
    ScrollPane scrollPane;

    @FXML
    public void GetOsuFolder() throws Exception {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Find Osu! Folder");
        File osuFolder = chooser.showDialog(Main.primaryStage);
        Main.osuFolder = osuFolder;
        System.out.println(Main.osuFolder);

        Main.Begin();
    }

    @FXML
    public void CloseProgram(){
        System.out.println("Closing Program");
        Main.primaryStage.close();
        System.exit(0);
    }

    @FXML
    public void NextTrack(){

    }

    @FXML
    public void PrevTrack(){

    }

    @FXML
    public void TogglePauseAction(){
        TogglePause();
    }

    public void TogglePause(){
        if (MusicPlayer.isActive) {
            if (MusicPlayer.isPaused) {
                MusicPlayer.play();
            } else {
                MusicPlayer.pause();
            }
        }
    }


    public void initialize(){
        label.setText("");

        pauseButton.setText("| |");

        scrollPane.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue){
                    Main.root.requestFocus();
                }
            }
        });

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
    }

    public void addToGrid(Node node, int col, int row){
        gridPane.add(node, col, row);
    }


}


