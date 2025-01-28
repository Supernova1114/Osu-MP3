package osu_mp3;

import com.tagtraum.audioplayer4j.AudioPlayer;
import com.tagtraum.audioplayer4j.AudioPlayerFactory;
import com.tagtraum.audioplayer4j.AudioPlayerListener;
import com.tagtraum.audioplayer4j.java.JavaPlayer;
import com.tagtraum.audioplayer4j.javafx.JavaFXPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.function.Function;
import java.util.logging.Logger;

public class MusicPlayer {

    private final double DEFAULT_VOLUME = 0.5;
    private double volume = DEFAULT_VOLUME;
    private AudioPlayer audioPlayer = null;
    private Function<Long, Void> timeChangedCallback = null;
    private Function endOfMediaCallback = null;
    private Function startOfMediaCallback = null;
    private boolean isPlayerInitialized = false;

    public MusicPlayer() {
        // Disable logger for audio player library.
        Logger logger = Logger.getLogger(JavaPlayer.class.getName());
        logger.setFilter((log) -> false);
    }

    public void playMedia(String soundFilePath) {
        initializeAudioPlayer(soundFilePath);
        play();
    }

    private void initializeAudioPlayer(String soundFilePath) {

        try {

            isPlayerInitialized = false;
            dispose();

            AudioPlayerFactory.setJavaEnabled(true);
            AudioPlayerFactory.setJavaFXEnabled(false);
            AudioPlayerFactory.setNativeEnabled(false);

            audioPlayer = AudioPlayerFactory.open(formatFilePath(Path.of(soundFilePath)).toUri());

            audioPlayer.setVolume((float)volume);

            // On Started and On Finished events.
            audioPlayer.addAudioPlayerListener(new AudioPlayerListener() {
                @Override
                public void started(final AudioPlayer audioPlayer, final URI uri) {
                    if (startOfMediaCallback != null) {
                        startOfMediaCallback.apply(null);
                    }
                }

                @Override
                public void finished(final AudioPlayer audioPlayer, final URI uri, final boolean endOfMedia) {
                    if (endOfMedia) {
                        if (endOfMediaCallback != null) {
                            endOfMediaCallback.apply(null);
                        }
                    }
                }
            });

            // Time progressing event.
            audioPlayer.addPropertyChangeListener(evt -> {
                if (evt.getPropertyName().equals("time")) {
                    if (timeChangedCallback != null && evt.getNewValue() != null) {
                        timeChangedCallback.apply(((Duration) evt.getNewValue()).getSeconds());
                    }
                }
            });

            isPlayerInitialized = true;

        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        if (audioPlayer != null) {
            audioPlayer.close();
        }
    }

    public void play() {
        if (isPlayerInitialized) {
            audioPlayer.play();
        }
    }

    public void pause() {
        if (isPlayerInitialized) {
            audioPlayer.pause();
        }
    }

    public void togglePause() {
        if (isPlayerInitialized) {

            if (audioPlayer.isPaused()) {
                play();
            } else {
                pause();
            }
        }
    }

    public void setVolume(double percent) {

        // Volume should still be set, as during media player
        // initialization this will take effect.
        volume = percent;

        if (isPlayerInitialized) {
            audioPlayer.setVolume((float)percent);
        }
    }

    // TODO - this should include partial seconds as well
    public void seek(double seconds) {
        audioPlayer.setTime(Duration.ofSeconds((long)seconds));
    }

    public void setTimeChangedCallback(Function<Long, Void> callback) {
        timeChangedCallback = callback;
    }

    public void setStartOfMediaCallback(Function callback) {
        startOfMediaCallback = callback;
    }

    public void setEndOfMediaCallback(Function callback) {
        endOfMediaCallback = callback;
    }

    // Add a dot if filename does not contain one.
    // Tricks the AudioPlayer into actually playing the file.
    private Path formatFilePath(Path filePath) {

        if (filePath.getFileName().toString().lastIndexOf('.') == -1) {
            return Path.of(filePath.toString() + '.');
        }

        return filePath;
    }

    public Duration getDuration() {
        return audioPlayer.getDuration();
    }
}
