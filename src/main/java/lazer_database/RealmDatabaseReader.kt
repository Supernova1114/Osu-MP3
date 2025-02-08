package lazer_database

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import osu_mp3.SongCollection
import osu_mp3.SongData
import java.nio.file.Path
import kotlin.io.path.name

// References:
// https://github.com/kabiiQ/BeatmapExporter/blob/main/BeatmapExporterCore/Exporters/Lazer/LazerDB/LazerDatabase.cs

class RealmDatabaseReader(private val realmFilePath: Path, private val osuFilesPath: Path) {

    object Constants {
        // DB version set as max value so that we can ignore non-schema-breaking changes
        const val REALM_DB_VERSION: Long = Long.MAX_VALUE
    }

    private var realmDB: Realm

    init {
        val config: RealmConfiguration = RealmConfiguration.Builder(
            schema = setOf(
                BeatmapSet::class,
                Beatmap::class,
                BeatmapMetadata::class,
                Ruleset::class,
                BeatmapDifficulty::class,
                BeatmapUserSettings::class,
                RealmUser::class,
                RealmNamedFileUsage::class,
                File::class,
                BeatmapCollection::class
            )
        )
        .name(realmFilePath.name)
        .directory(realmFilePath.parent.toString())
        .schemaVersion(Constants.REALM_DB_VERSION)
        .build()

        realmDB = Realm.open(config)
    }

    fun closeDatabase() {
        realmDB.close()
    }

    private fun getAllBeatmaps(): RealmResults<Beatmap> {
        return realmDB.query<Beatmap>().find()
    }

    private fun getBeatmap(md5Hash: String): Beatmap? {
        val result: RealmResults<Beatmap> = realmDB.query<Beatmap>("MD5Hash = $0", md5Hash).find()
        return if (result.isNotEmpty()) { result.first() } else { null }
    }

    private fun getBeatmapCollections(): RealmResults<BeatmapCollection> {
        return realmDB.query<BeatmapCollection>().find()
    }

    private fun getSongFilePath(beatmap: Beatmap): Path {

        val audioFileName: String? = beatmap.Metadata?.AudioFile

        val musicFileHash: String? = beatmap.BeatmapSet?.Files?.find { it.Filename?.lowercase() == audioFileName?.lowercase() }?.File?.Hash

        // Osu! file structure is as follows:
        // Where "hash" is the file's SHA256 hash.
        // The file itself is named as its own hash.
        // Example directory: files/hash[0]/hash[0]+hash[1]/hash

        return Path.of(
            osuFilesPath.toString(),
            musicFileHash?.get(0).toString(),
            musicFileHash?.substring(0..1),
            musicFileHash
        )
    }

    fun getSongCollections(): List<SongCollection> {

        val beatmapCollections: RealmResults<BeatmapCollection> = getBeatmapCollections()
        val songCollections: MutableList<SongCollection> = mutableListOf()

        for (beatmapCollection in beatmapCollections) {

            val songList: MutableList<SongData> = mutableListOf()

            for (hash: String? in beatmapCollection.BeatmapMD5Hashes) {

                // The database schema requires "String?" we should go to next iteration if hash is indeed null
                if (hash == null) { continue }

                val beatmap: Beatmap = getBeatmap(hash) ?: continue

                val songFilePath: Path = getSongFilePath(beatmap)

                val songData = SongData()
                songData.songName = beatmap.Metadata?.Title
                songData.artistName = beatmap.Metadata?.Artist
                songData.filePath = songFilePath.toString()

                songList.add(songData)
            } // for

            songCollections.add(SongCollection(beatmapCollection.Name, songList))
        } // for

        return songCollections
    }





}


