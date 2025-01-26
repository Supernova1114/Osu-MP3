package osu_mp3;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;
import javafx.application.Platform;


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
                                App.togglePause();
                                break;
                            case MEDIA_NEXT_TRACK:
                                MusicManager.getInstance().nextSong();
                                break;
                            case MEDIA_PREV_TRACK:
                                MusicManager.getInstance().prevSong();
                                break;
                        }//switch
                    }
                });
            }
        };

        provider = Provider.getCurrentProvider(false);//useSwingThread = false

        assert provider != null;

        provider.register(MediaKey.MEDIA_PLAY_PAUSE, hotKeyListener);

        provider.register(MediaKey.MEDIA_NEXT_TRACK, hotKeyListener);

        provider.register(MediaKey.MEDIA_PREV_TRACK, hotKeyListener);




    }

    public void cleaUp(){
        provider.reset();
        provider.stop();
    }

}
