package osu_mp3;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import javafx.scene.text.TextFlow;

public class SongPane extends Pane {

    public SongData songData;
    public String collectionName;
    private Label label;
    boolean isDragged = false;

    private final Font SONG_NAME_FONT = Font.font(
        Font.getDefault().getName(),
        FontWeight.BOLD,
        FontPosture.REGULAR,
        Font.getDefault().getSize()
    );


    public SongPane(SongData songData, String collectionName) {

        this.songData = songData;
        this.collectionName = collectionName;

        Text songText = new Text(songData.songName);
        songText.setFont(SONG_NAME_FONT);

        TextFlow textFlow = new TextFlow(
            new Text(songData.artistName + " - "),
            songText
        );

        label = new Label();
        label.setGraphic(textFlow);

        getChildren().add(label);

        setOnMouseClicked(event -> {
            if (!isDragged){
                MusicManager.getInstance().playMedia(this);
            }

        });

        setOnMouseDragged(event -> {
            isDragged = true;
        });

        setOnMousePressed(event -> {
            isDragged = false;
        });


    }

    public void setLabelColor(Color color) {
        ((TextFlow)label.getGraphic()).getChildren().forEach(text -> ((Text)text).setFill(Color.RED));
    }

    public String toString() {
        return songData.toString();
    }


}
