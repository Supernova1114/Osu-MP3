import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;
import javafx.application.Platform;

import static org.junit.Assert.assertNotNull;

public class GlobalKeyListener {

    private Provider provider;

    public GlobalKeyListener() {

        HotKeyListener hotKeyListener = new HotKeyListener() {
            @Override
            public void onHotKey(HotKey hotKey) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        switch (hotKey.mediaKey){
                            case MEDIA_PLAY_PAUSE:
                                Main.togglePause();
                                break;
                            case MEDIA_NEXT_TRACK:
                                try {
                                    MusicPlayer.nextSong();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case MEDIA_PREV_TRACK:
                                try {
                                    MusicPlayer.prevSong();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }//switch
                    }
                });
            }
        };

        provider = Provider.getCurrentProvider(false);//useSwingThread = false

        assertNotNull(provider);

        provider.register(MediaKey.MEDIA_PLAY_PAUSE, hotKeyListener);

        provider.register(MediaKey.MEDIA_NEXT_TRACK, hotKeyListener);

        provider.register(MediaKey.MEDIA_PREV_TRACK, hotKeyListener);




    }

    public void cleaUp(){
        provider.reset();
        provider.stop();
    }

}
