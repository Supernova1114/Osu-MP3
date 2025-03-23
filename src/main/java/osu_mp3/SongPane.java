package osu_mp3;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import javafx.scene.text.TextFlow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SongPane extends Pane {

    public SongData songData;
    public int collectionID;
    private Label label;
    private boolean isDragged = false;

    private final Font SONG_NAME_FONT = Font.font(
        Font.getDefault().getName(),
        FontWeight.BOLD,
        FontPosture.REGULAR,
        Font.getDefault().getSize()
    );


    public SongPane(int collectionID, SongData songData) {
        this.collectionID = collectionID;
        this.songData = songData;

        Text songText = new Text(songData.songName);
        songText.setFont(SONG_NAME_FONT);

        TextFlow textFlow = new TextFlow(
            new Text(songData.artistName + " - "),
            songText
        );

        textFlow.setPadding(new Insets(0,5,0,5));

        label = new Label();
        label.setGraphic(textFlow);

        //setMinHeight(100);

//        try {
//            FileInputStream fis = new FileInputStream("D:\\Program Files\\osu!\\Songs\\1899277 Indila - Derniere Danse\\wp7313986.jpg");
//            Image image = new Image(fis, 2560, 1440, true, false);
//            fis.close();
////            PixelReader reader = image.getPixelReader();
////            WritableImage newImage = new WritableImage(reader, 0, 0, 2560, 100);
//
//
//            //Setting the image view
//            ImageView imageView = new ImageView(image);
//            imageView.setFitWidth(300);
//            imageView.setFitHeight(100);
//            getChildren().add(imageView);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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

    private void setLabelColor(Color color) {
        ((TextFlow)label.getGraphic()).getChildren().forEach(text -> ((Text)text).setFill(color));
    }

    public void setStatePlaying() {
        setLabelColor(Color.RED);
    }

    public void setStatePlayed() {
        setLabelColor(Color.DARKBLUE);
    }

    public void resetState() {
        setLabelColor(Color.BLACK);
    }

    public String toString() {
        return songData.toString();
    }
}
