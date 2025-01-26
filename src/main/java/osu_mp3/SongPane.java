package osu_mp3;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class SongPane extends Pane {

    public SongData songData;
    public String collectionName;
    public Label label;
    boolean isDragged = false;


    public SongPane(SongData songData, String collectionName) {

        this.songData = songData;
        this.collectionName = collectionName;

        getChildren().add(label = new Label(songData.artistName + " - " + songData.songName));

        setOnMouseClicked(event -> {
            if (!isDragged){
                ((Label) getChildren().get(0)).setTextFill(Color.RED);
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

    public String toString() {
        return songData.toString();
    }


}
