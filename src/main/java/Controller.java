import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

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
    @FXML
    MenuItem exportSongListMenuItem;

    public static double seekBarValue = 0.0;

    public static boolean isSeekBarPressed = false;

    @FXML
    public void collectGarbo(){
        //System.gc();
    }

    @FXML
    public void ExportSongList(){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Export Location");
        chooser.setInitialFileName("SongList-OsuMP3");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text File", "*.txt"));

        File exportDirectory = chooser.showSaveDialog(Main.primaryStage);
        System.out.println(exportDirectory);

        if (exportDirectory != null){

            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {

                    Path songListFile = Files.createFile(Paths.get(exportDirectory.getPath()));
                    System.out.println("Created: " + songListFile);

                    BufferedWriter bufferedWriter = null;

                    try {
                        bufferedWriter = new BufferedWriter(new FileWriter(songListFile.toFile()));

                        bufferedWriter.write("Osu-MP3 Exported Song List");
                        bufferedWriter.newLine();
                        bufferedWriter.newLine();

                        for (ArrayList<SongPane> songCollection: Main.songPaneCollectionList){
                            for (SongPane song: songCollection){
                                bufferedWriter.write(song.name);
                                bufferedWriter.newLine();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        if (bufferedWriter != null)
                        bufferedWriter.close();
                    }




                    return null;
                }
            };
            worker.execute();

        }//if
    }//ExportSongList()

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
        gridPane.setDisable(true);
        exportSongListMenuItem.setDisable(true);

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
        //volumeSlider.setSnapToTicks(true);
        volumeSlider.setMax(50);
        volumeSlider.setMin(0);

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                MusicPlayer.setVolume( (((int)newValue.doubleValue()) * 1.0) / 100 );
            }
        });


        seekBar.setValue(0);

        seekBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MusicPlayer.player.seek(Duration.seconds(seekBar.getValue()));
            }
        });

        seekBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MusicPlayer.player.seek(Duration.seconds(seekBar.getValue()));
            }
        });



    }


    public void setCurrentSeekTime(double currSeconds){
        seekBar.setValue(currSeconds);

        double hours = (currSeconds / 3600);
        double minutes = ((currSeconds % 3600) / 60);
        double seconds = (currSeconds % 60);
        //System.out.println("CurrSec: " + currSeconds + " sec: " + seconds);

        seekCurrentLabel.setText((int)hours + ":" + (int)minutes + ":" + (seconds < 10 ? "0" + (int)seconds : (int)seconds));


    }

    public void refreshSeekBar(Duration max){
        seekBar.setMin(0);
        seekBar.setValue(0);
        seekBar.setMax(max.toSeconds());


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


