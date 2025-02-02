package osu_mp3;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.tagtraum.audioplayer4j.AudioPlayer;
import com.tagtraum.audioplayer4j.AudioPlayerFactory;
import com.tagtraum.audioplayer4j.AudioPlayerListener;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MusicPlayer {

    private final double DEFAULT_VOLUME = 0.5;
    private double volume = DEFAULT_VOLUME;
    private AudioPlayer audioPlayer = null;
    private Function<Duration, Void> timeChangedCallback = null;
    private Function endOfMediaCallback = null;
    private Function startOfMediaCallback = null;
    private boolean isPlayerInitialized = false;
    private Duration audioDuration = null;


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

            Logger.getLogger("com.tagtraum.audioplayer4j.java").setLevel(Level.OFF);
            Logger.getLogger("com.tagtraum.ffsampledsp").setLevel(Level.OFF);

            audioPlayer = AudioPlayerFactory.open(formatFilePath(Path.of(soundFilePath)).toUri());

            try {
                // Alternate library to get duration of mp3 files as AudioPlayer library gives a poor duration estimate.
                audioDuration = Duration.ofMillis(new Mp3File(soundFilePath).getLengthInMilliseconds());
            } catch (InvalidDataException | UnsupportedTagException e) {
                audioDuration = audioPlayer.getDuration();
            }

            audioPlayer.setVolume((float)volume);

            // On Started and On Finished events.
            audioPlayer.addAudioPlayerListener(new AudioPlayerListener() {
                @Override
                public void started(final AudioPlayer audioPlayer, final URI uri) {
                    if (startOfMediaCallback != null) { startOfMediaCallback.apply(null); }
                }

                @Override
                public void finished(final AudioPlayer audioPlayer, final URI uri, final boolean endOfMedia) {
                    //isPlayerInitialized = false;
                    if (endOfMedia && endOfMediaCallback != null) { endOfMediaCallback.apply(null); }
                }
            });

            // Time progressing event.
            audioPlayer.addPropertyChangeListener(evt -> {
                if (evt.getPropertyName().equals("time") && timeChangedCallback != null && evt.getNewValue() != null) {
                    timeChangedCallback.apply(((Duration) evt.getNewValue()));
                }
            });

            isPlayerInitialized = true;

        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        if (audioPlayer != null) { audioPlayer.close(); }
    }

    public void play() {
        if (isPlayerInitialized) { audioPlayer.play(); }
    }

    public void pause() {
        if (isPlayerInitialized) { audioPlayer.pause(); }
    }

    public boolean togglePause() {
        if (!isPlayerInitialized) { return false; }
        audioPlayer.playPause();
        return !audioPlayer.isPaused();
    }

    public void setVolume(double percent) {

        // Volume should still be set, as during media player
        // initialization this will take effect.
        volume = percent;

        if (isPlayerInitialized) {
            audioPlayer.setVolume((float)percent);
        }
    }

    public void restartSong() {
        if (isPlayerInitialized) { audioPlayer.reset(); }
    }

    public void seek(long milliseconds) {
        Duration maxDur = getDuration();
        if (!isPlayerInitialized || maxDur.compareTo(Duration.ZERO) == 0) { return; }

        Duration targetDur = Duration.ofMillis(milliseconds);

        // Make sure duration is not greater than max duration.
        // If seeking to end, set to (max - 100 millis).
        // The millis subtraction is a workaround fix as MP3 duration estimates seem to be inaccurate.
        if (maxDur.compareTo(targetDur) <= 0) {
            targetDur = maxDur.minusMillis(100);
        }

        audioPlayer.setTime(targetDur);
    }

    public void setTimeChangedCallback(Function<Duration, Void> callback) {
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
        return filePath.getFileName().toString().lastIndexOf('.') == -1 ? Path.of(filePath.toString() + '.') : filePath;
    }

    public Duration getDuration() {
        return audioDuration != null ? audioDuration : Duration.ZERO;
    }
}
