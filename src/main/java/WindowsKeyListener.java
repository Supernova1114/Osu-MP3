import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeConstants;
import javafx.application.Platform;

public class WindowsKeyListener {



    public WindowsKeyListener() throws InterruptedException {
        JIntellitype.getInstance().addIntellitypeListener(new IntellitypeListener() {
            @Override
            public void onIntellitype(int aCommand) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        switch (aCommand) {
                            case JIntellitype.APPCOMMAND_MEDIA_PLAY_PAUSE:
                                //System.out.println("Play/Pause message received " + Integer.toString(aCommand));
                                togglePause();
                                break;
                            case JIntellitypeConstants.APPCOMMAND_MEDIA_NEXTTRACK:
                                try {
                                    MusicPlayer.nextSong();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case JIntellitypeConstants.APPCOMMAND_MEDIA_PREVIOUSTRACK:
                                try {
                                    MusicPlayer.prevSong();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;

                            default:
                                System.out.println("Undefined INTELLITYPE message caught " + Integer.toString(aCommand));
                                break;
                        }
                    }
                });

            }
        });
    }

        /*try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        /*JIntellitype.getInstance().cleanUp();
        System.exit(0);*/

    public void togglePause(){
        Main.root.requestFocus();
        Main.controller.TogglePause();
    }

}
