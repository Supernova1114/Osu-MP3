import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

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
    Label songTitleLabel;
    @FXML
    CheckMenuItem showArtistsCheckMenu;
    @FXML
    Slider seekBar;
    @FXML
    Label seekMaxLabel;
    @FXML
    Label seekCurrentLabel;

    public static double seekTime = 0.0;

    public static boolean isSeekBarPressed = false;

    @FXML
    public void collectGarbo(){
        //System.gc();
    }

    @FXML
    public void GetOsuFolder() throws Exception {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Find Osu! Folder");
        File osuFolder = chooser.showDialog(Main.primaryStage);
        System.out.println(Main.osuFolder);

        if (osuFolder != null) {
            Main.setOsuFolder(osuFolder);

            Main.Begin();
        }
    }

    @FXML
    public void CloseProgram(){
        System.out.println("Closing Program");
        Main.primaryStage.close();
        System.exit(0);
    }

    @FXML
    public void NextTrack() throws InterruptedException {
        MusicPlayer.nextSong();
    }

    @FXML
    public void PrevTrack() throws InterruptedException {
        MusicPlayer.prevSong();
    }

    @FXML
    public void TogglePauseAction(){
        TogglePause();
    }

    public void TogglePause() {
        if (MusicPlayer.isActive) {
            if (MusicPlayer.isPaused) {
                MusicPlayer.play();
            } else {
                MusicPlayer.pause();
            }
        }
    }

    @FXML
    public void ShowArtists(){
        showArtists(showArtistsCheckMenu.isSelected());
    }

    public void showArtists(boolean b){

    }


    public void initialize(){
        label.setText("");
        songTitleLabel.setText("Song Title");

        pauseButton.setText("| |");


        showArtistsCheckMenu.setSelected(true);

        scrollPane.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue){
                    Main.root.requestFocus();
                }
            }
        });

        volumeSlider.setValue(25);
        volumeSlider.setBlockIncrement(5);
        volumeSlider.setMajorTickUnit(25);
        volumeSlider.setMinorTickCount(5);
        volumeSlider.setSnapToTicks(true);
        volumeSlider.setMax(50);
        volumeSlider.setMin(0);

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                MusicPlayer.setVolume( (((int)newValue.doubleValue()) * 1.0) / 100 );
            }
        });


        seekBar.setValue(0);

        seekBar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (MusicPlayer.isActive){
                    seekTime = newValue.doubleValue();
                }
            }
        });

        seekBar.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (MusicPlayer.isActive){
                    MusicPlayer.setSeek(seekTime);
                }
                isSeekBarPressed = false;
            }
        });

        seekBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                isSeekBarPressed = true;
            }
        });







    }


    public void setCurrentSeekTime(double currSeconds){
        int hours = (int)(currSeconds / 3600);
        int minutes = (int)((currSeconds % 3600) / 60);
        int seconds = (int)(currSeconds % 60);

        seekCurrentLabel.setText(hours + ":" + minutes + ":" + (seconds < 10 ? "0" + seconds : seconds));
        
        seekBar.setValue(currSeconds);
        // FIXME: 3/27/2021 fix clickable seekBar asjdlkasjdljaslkdj

    }

    public void refreshSeekBar(double min, Duration max, boolean disable){
        seekBar.setMin(min);
        seekBar.setMax(max.toSeconds());
        seekBar.setValue(min);

        int hours = (int)(max.toSeconds() / 3600);
        int minutes = (int)((max.toSeconds() % 3600) / 60);
        int seconds = (int)(max.toSeconds() % 60);

        seekMaxLabel.setText(hours + ":" + minutes + ":" + (seconds < 10 ? "0" + seconds : seconds));

        seekBar.setDisable(false);
    }

    public void addToGrid(Node node, int col, int row){
        gridPane.add(node, col, row);
    }



}


