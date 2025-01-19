//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
//
//import java.nio.file.Path;
//import java.util.function.Function;
//
//public class MusicPlayer {
//
//    private final double DEFAULT_VOLUME = 0.5f;
//    private double volume = DEFAULT_VOLUME;
//    private MediaPlayer mediaPlayer = null;
//    private boolean isPlayerInitialized = false;
//    private Function<Double, Void> currentTimeChangedEvent = null;
//    private Function endOfMediaEvent = null;
//
//
//    public void playMedia(Path soundFilePath) {
//        initializeMedia(soundFilePath);
//        play();
//    }
//
//    private void initializeMedia(Path soundFilePath) {
//
//        isPlayerInitialized = false;
//
//        if (mediaPlayer != null) {
//            mediaPlayer.dispose();
//        }
//
//        Media media = new Media(soundFilePath.toString());
//        mediaPlayer = new MediaPlayer(media);
//        mediaPlayer.setVolume(volume);
//
//        mediaPlayer.currentTimeProperty().addListener(
//            (observable, oldValue, newValue) -> currentTimeChangedEvent.apply(newValue.toSeconds())
//        );
//
//        mediaPlayer.setOnEndOfMedia(() -> endOfMediaEvent.apply(null));
//
//        // Wait until media content is loaded.
//        while (mediaPlayer.getStatus() != MediaPlayer.Status.READY);
//
//        isPlayerInitialized = true;
//    }
//
//    public void play() {
//        if (isPlayerInitialized) {
//            mediaPlayer.play();
//        }
//    }
//
//    public void pause() {
//        if (isPlayerInitialized) {
//            mediaPlayer.pause();
//        }
//    }
//
//    public void togglePause() {
//        if (isPlayerInitialized) {
//
//            MediaPlayer.Status status = mediaPlayer.getStatus();
//
//            if (status == MediaPlayer.Status.PAUSED) {
//                play();
//            } else if (status == MediaPlayer.Status.PLAYING) {
//                pause();
//            }
//        }
//    }
//
//    public void setVolume(double percent) {
//
//        // Volume should still be set as during media player
//        // initialization this will take effect.
//        volume = percent;
//
//        if (isPlayerInitialized) {
//            mediaPlayer.setVolume(percent);
//        }
//    }
//
//    public void setCurrentTimeChangedEvent(Function<Double, Void> callback) {
//        currentTimeChangedEvent = callback;
//    }
//
//    public void setEndOfMediaEvent(Function callback) {
//        endOfMediaEvent = callback;
//    }
//
//}
