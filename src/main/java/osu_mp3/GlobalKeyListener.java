package osu_mp3;

import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;
import javafx.application.Platform;

import java.util.logging.Level;
import java.util.logging.Logger;


public class GlobalKeyListener {

    private Provider provider;

    public GlobalKeyListener() {

        Logger.getLogger("com.tulskiy.keymaster.windows").setLevel(Level.OFF);

        HotKeyListener hotKeyListener = hotKey -> Platform.runLater(() -> {
            switch (hotKey.mediaKey){
                case MEDIA_PLAY_PAUSE:
                    MusicManager.getInstance().togglePause();
                    break;
                case MEDIA_NEXT_TRACK:
                    MusicManager.getInstance().nextSong();
                    break;
                case MEDIA_PREV_TRACK:
                    MusicManager.getInstance().prevSong();
                    break;
            }
        });

        provider = Provider.getCurrentProvider(false); //useSwingThread = false

        if (provider != null) {
            provider.register(MediaKey.MEDIA_PLAY_PAUSE, hotKeyListener);
            provider.register(MediaKey.MEDIA_NEXT_TRACK, hotKeyListener);
            provider.register(MediaKey.MEDIA_PREV_TRACK, hotKeyListener);
        } else {
            System.out.println("ERROR: Media Key Provider is NULL, will not be able to listen to media key presses!");
        }
    }

    public void cleanUp() {
        provider.reset();
        provider.stop();
    }

}
