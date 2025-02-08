package osu_mp3;

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

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.List;

public class Controller {

    @FXML GridPane gridPane;
    @FXML Slider volumeSlider;
    @FXML Button pauseButton;
    @FXML ScrollPane scrollPane;
    @FXML Label songTitleLabel;
    @FXML CheckMenuItem showArtistsCheckMenu;
    @FXML Slider seekBar;
    @FXML Label maxTimeLabel;
    @FXML Label currentTimeLabel;
    @FXML MenuItem exportSongListMenuItem;
    @FXML SearchableComboBoxImproved<SongCollection> comboBox;
    @FXML ToggleGroup osuDBModeToggleGroup;
    @FXML RadioMenuItem osuLazerDBModeToggle;
    @FXML RadioMenuItem osuStableDBModeToggle;


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
    public void SetOsuStableFolder() {
        File file = chooseDirectory("Find Osu! Stable Folder");
        if (file != null) {
            App.osuStableFolderPath = file.toPath();
            deselectCurrentOsuDBModeToggle();
            osuStableDBModeToggle.setSelected(true);
        }
    }

    @FXML
    public void SetOsuLazerFolder() {
        File file = chooseDirectory("Find Osu! Lazer Data Folder");
        if (file != null) {
            App.osuLazerFolderPath = file.toPath();
            deselectCurrentOsuDBModeToggle();
            osuLazerDBModeToggle.setSelected(true);
        }
    }

    private File chooseDirectory(String title) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);
        return chooser.showDialog(App.primaryStage);
    }

    private void deselectCurrentOsuDBModeToggle() {
        Toggle toggle = osuDBModeToggleGroup.getSelectedToggle();
        if (toggle != null) {
            toggle.setSelected(false);
        }
    }

    @FXML
    public void CloseProgram() { App.exitApplication(); }

    @FXML
    public void NextTrack() { MusicManager.getInstance().nextSong(); }

    @FXML
    public void PrevTrack() { MusicManager.getInstance().prevSong(); }

    @FXML
    public void TogglePauseAction() { MusicManager.getInstance().togglePause(); }


    @FXML
    public void ShowArtists(){}

    // Runs prior to App start() method.
    public void initialize() {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100);
        gridPane.getColumnConstraints().add(columnConstraints);

        exportSongListMenuItem.setDisable(true);

        songTitleLabel.setText("Song Title");

        pauseButton.setText(">");

        showArtistsCheckMenu.setSelected(true);

        scrollPane.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { App.rootNode.requestFocus(); }
        });

        initializeVolumeSlider();
        initializeSeekBar();
        initializeOsuDatabaseModeToggleGroup();
    }

    private void initializeOsuDatabaseModeToggleGroup() {

        String dbMode = App.osuDatabaseMode;
        if (dbMode.equals("lazer")) {
            osuLazerDBModeToggle.setSelected(true);
        } else if (dbMode.equals("stable")) {
            osuStableDBModeToggle.setSelected(true);
        }

        osuDBModeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioMenuItem item = (RadioMenuItem)newValue;
            if (newValue != null) {
                App.switchOsuDBModes((String)item.getUserData());
            }
        });
    }

    private void initializeVolumeSlider() {
        volumeSlider.setMin(0);
        volumeSlider.setMax(1);
        volumeSlider.setValue(0.5);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            MusicManager.getInstance().setVolume(newValue.doubleValue());
        });
    }

    private void initializeSeekBar() {
        seekBar.setOnMouseReleased(event -> {
            MusicManager.getInstance().seek((long)seekBar.getValue());
            isSeekBarPressed = false;
        });

        seekBar.setOnMousePressed(event -> isSeekBarPressed = true);

        seekBar.valueProperty().addListener((observable, oldValue, newValue) -> {
            currentTimeLabel.setText(getDurationText(Duration.ofMillis(newValue.longValue())));
        });

        prepareSeekBar(Duration.ZERO);
    }

    public void setSeekBarPosition(Duration duration) {
        seekBar.setValue(duration.toMillis());
    }

    public void prepareSeekBar(Duration duration) {
        seekBar.setMin(0);
        seekBar.setValue(0);
        seekBar.setMax(duration.toMillis());
        currentTimeLabel.setText("0:0:00");
        maxTimeLabel.setText(getDurationText(duration));
    }

    public String getDurationText(Duration duration) {
        int seconds = duration.toSecondsPart();
        return duration.toHoursPart() + ":" + duration.toMinutesPart() + ":" + (seconds < 10 ? "0" + seconds : seconds);
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


