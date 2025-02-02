package osu_mp3;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import org.controlsfx.control.SearchableComboBox;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.List;

public class Controller {

    @FXML
    GridPane gridPane;
    @FXML
    Slider volumeSlider;
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
    @FXML
    SearchableComboBox<String> comboBox;

    public static boolean isSeekBarPressed = false;

    @FXML
    public void GotoGithub() {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(URI.create("https://github.com/Supernova1114/Osu-MP3"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void ExportSongList() {}

    @FXML
    public void GetOsuFolder() throws Exception {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Find Osu! Folder");
        File osuFolder = chooser.showDialog(App.primaryStage);
        System.out.println(App.osuStableFolderPath);

        if (osuFolder != null) {
            //App.setOsuStableFolderPath(osuFolder);
            //App.Begin();
        }
    }

    @FXML
    public void CloseProgram() {
        System.out.println("Closing Program");
        App.primaryStage.close();
        System.exit(0);
    }

    @FXML
    public void NextTrack() throws InterruptedException {
        MusicManager.getInstance().nextSong();
    }

    @FXML
    public void PrevTrack() throws InterruptedException {
        MusicManager.getInstance().prevSong();
    }

    @FXML
    public void TogglePauseAction(){
        TogglePause();
    }

    public void TogglePause() {
        MusicManager.getInstance().togglePause();
    }

    @FXML
    public void ShowArtists(){
        showArtists(showArtistsCheckMenu.isSelected());
    }

    public void showArtists(boolean b) {

    }


    public void initialize() {
        gridPane.setDisable(true);
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100);
        gridPane.getColumnConstraints().add(columnConstraints);

        exportSongListMenuItem.setDisable(true);

        songTitleLabel.setText("Song Title");

        pauseButton.setText(">");

        showArtistsCheckMenu.setSelected(true);

        scrollPane.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                App.rootNode.requestFocus();
            }
        });

        volumeSlider.setMax(100);
        volumeSlider.setMin(0);
        volumeSlider.setValue(50);

        seekCurrentLabel.setText("0:0:00");
        seekMaxLabel.setText("0:0:00");

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            MusicManager.getInstance().setVolume( (newValue.floatValue() / 100.0f) );
        });

        seekBar.setMin(0);
        seekBar.setMax(0);
        seekBar.setValue(0);

        seekBar.setOnMouseReleased(event -> {
            MusicManager.getInstance().seek(seekBar.getValue());
            isSeekBarPressed = false;
        });

        seekBar.setOnMousePressed(event -> isSeekBarPressed = true);

        seekBar.valueProperty().addListener((observable, oldValue, newValue) -> {
            double durationSeconds = newValue.doubleValue();
            double durationMillis = durationSeconds * 1000;

            Duration duration = Duration.ofMillis((long)durationMillis);

            int seconds = duration.toSecondsPart();
            seekCurrentLabel.setText(duration.toHoursPart() + ":" + duration.toMinutesPart() + ":" + (seconds < 10 ? "0" + seconds : seconds));
        });

    }

    public void setCurrentSeekTime(Duration duration) {
        double totalMillis = duration.toMillis();
        seekBar.setValue(totalMillis / 1000);
    }

    public void refreshSeekBar(Duration duration) {
        seekBar.setValue(0);

        double totalMillis = duration.toMillis();
        seekBar.setMax(totalMillis / 1000);

        int seconds = duration.toSecondsPart();
        seekMaxLabel.setText(duration.toHoursPart() + ":" + duration.toMinutesPart() + ":" + (seconds < 10 ? "0" + seconds : seconds));

        seekBar.setDisable(false);
    }

    public void addToGrid(Node node, int col, int row){
        gridPane.add(node, col, row);
    }

    public void addColumnToGrid(int index, List<Node> nodes) {
        gridPane.addColumn(index, nodes.toArray(new Node[nodes.size()]));
    }

    public void clearGrid() {
        Node gridData = gridPane.getChildren().get(0); // Contains gridlines
        gridPane.getChildren().clear();
        gridPane.getChildren().add(gridData);
    }



}


