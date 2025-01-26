package osu_mp3;

import java.nio.file.Path;

public class Main {

    public static void main(final String[] args) throws Exception {
//        App.launch(App.class, args);

        MusicPlayer player = new MusicPlayer();

        String soundFile = "C:\\Users\\Super\\Downloads\\temp\\e436467cf87203e19f4733d79d126f46e9eb7b1911935a26c2835981950bbaae";

        player.playMedia(Path.of(soundFile));

        while (true);
    }

}
