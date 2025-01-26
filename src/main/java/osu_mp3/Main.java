package osu_mp3;

import java.nio.file.Path;

public class Main {

    public static void main(final String[] args) {
//        App.launch(App.class, args);


        MusicPlayer musicPlayer = new MusicPlayer();
        musicPlayer.playMedia("D:\\Program Files\\osu!-lazer\\files\\5\\5a\\5adb2e95e1c1173903d6f161a55928ba6c4b74b203e2ad768d7aa96db5fb7328");
        while (true);
    }

}
