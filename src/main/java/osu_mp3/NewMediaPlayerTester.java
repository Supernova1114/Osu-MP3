package osu_mp3;

import com.tagtraum.audioplayer4j.AudioPlayer;
import com.tagtraum.audioplayer4j.AudioPlayerFactory;
import com.tagtraum.audioplayer4j.AudioPlayerListener;
import com.tagtraum.audioplayer4j.java.JavaPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

class NewMediaPLayerTester {

    private String audioFilePath = "C:\\Users\\Super\\Downloads\\temp\\e436467cf87203e19f4733d79d126f46e9eb7b1911935a26c2835981950bbaae.";

    public void test() throws Exception{

        // Disable logger for media player library.
        Logger logger = Logger.getLogger(JavaPlayer.class.getName());
        logger.setFilter((log)-> false);

        try (final AudioPlayer player = AudioPlayerFactory.open(Path.of(audioFilePath).toUri())) {

            // add a listener, so that we are notified,
            // once playback has stopped.
            final CountDownLatch finished = new CountDownLatch(1);
            player.addAudioPlayerListener(new AudioPlayerListener() {
                @Override
                public void started(final AudioPlayer audioPlayer, final URI uri) {
                }

                @Override
                public void finished(final AudioPlayer audioPlayer, final URI uri, final boolean endOfMedia) {
                    finished.countDown();
                    System.out.println("Finished!!!");
                }

            });

            // start playback
            player.play();

            System.out.println(player.getDuration().getSeconds());
            player.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals("time")) {
                        Duration dur = (Duration) evt.getNewValue();
                        System.out.println(dur.getSeconds());
                    }
                }
            });

            // wait until playback has finished.
            finished.await();
        }
    }

}