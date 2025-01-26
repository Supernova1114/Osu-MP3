package osu_mp3;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(final String[] args) throws Exception {
//        App.launch(App.class, args);

        List<SongData> songList = new ArrayList<>();

        songList.add(new SongData(
            "Song 1",
            "Artist 1",
            "C:\\Users\\Super\\Downloads\\temp\\e436467cf87203e19f4733d79d126f46e9eb7b1911935a26c2835981950bbaae"
        ));

        songList.add(new SongData(
                "Song 2",
                "Artist 2",
                "C:\\Users\\Super\\Downloads\\temp\\ad87c1519c8beca1da4a773e53debf5f93e870d6b4b979f4a3c78cd67e1e9983"
        ));

        songList.add(new SongData(
                "Song 3",
                "Artist 3",
                "C:\\Users\\Super\\Downloads\\temp\\audio.ogg"
        ));

        songList.add(new SongData(
                "Song 4",
                "Artist 4",
                "C:\\Users\\Super\\Downloads\\temp\\sacrifice.mp3"
        ));

        MusicPlayer player = new MusicPlayer();

        MusicQueueManager queueManager = new MusicQueueManager(songList);
        queueManager.shuffle();

        player.setEndOfMediaCallback((p)->{
            SongData songData = queueManager.nextSong();
            player.playMedia(songData.filePath);

            System.out.println(songData);
            return null;
        });

        player.setTimeChangedCallback((seconds)->{
            if (seconds >= 10) {
                SongData songData = queueManager.nextSong();
                player.playMedia(songData.filePath);

                System.out.println(songData);
            }
            return null;
        });

        SongData songData = queueManager.getSong();
        player.playMedia(songData.filePath);

        System.out.println(songData);

        while (true);
    }

}
