package realm_database

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import java.nio.file.Path
import kotlin.io.path.name

// References:
// https://github.com/kabiiQ/BeatmapExporter/blob/main/BeatmapExporterCore/Exporters/Lazer/LazerDB/LazerDatabase.cs

class RealmReadTester(val realmFilePath: Path, val osuFilesPath: Path) {

    object Constants {
        const val REALM_DB_VERSION: Long = 46
    }

    var realmDB: Realm? = null


    fun openDatabase() {

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
        realmDB?.close()
    }

    fun getBeatmapCollections() {
        val collName: String = "test"
        val beatmapCollections: RealmResults<BeatmapCollection>? = realmDB?.query<BeatmapCollection>("Name = $0", collName)?.find()

        if (beatmapCollections == null) {
            return
        }

        val hash: String? = beatmapCollections.first().BeatmapMD5Hashes[3]

        if (hash == null) {
            return
        }

        val beatmap: Beatmap? = realmDB?.query<Beatmap>("MD5Hash = $0", hash)?.find()?.first()

        if (beatmap == null) {
            return
        }


        val musicFileName: String? = beatmap.Metadata?.AudioFile

        val set: BeatmapSet? = beatmap.BeatmapSet

        val musicFileHash: String? = set?.Files?.find { it.Filename == musicFileName }?.File?.Hash

        var filePath: Path = Path.of(
            osuFilesPath.toString(),
            musicFileHash?.get(0).toString(),
            musicFileHash?.substring(0..1),
            musicFileHash
        )

        println(filePath.toString())
        println(beatmap.Metadata?.Title)
        println(beatmap.Metadata?.Artist)
    }

    fun testRead() {

//        val beatmapSets: RealmResults<BeatmapSet> = realmDB.query<BeatmapSet>().find()
//
//        var beatmapSet: BeatmapSet = beatmapSets.first()
//        var realmNamedFileUsage: RealmNamedFileUsage? = beatmapSet.Files?.first()
//
//        var fileHash: String? = realmNamedFileUsage?.File?.Hash
//
//

    }

}


