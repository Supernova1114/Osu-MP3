package realm_stuff

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import java.nio.file.Path
import kotlin.io.path.name
import java.io.File as JavaFile

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
        val beatmapCollections: RealmResults<BeatmapCollection>? = realmDB?.query<BeatmapCollection>()?.find()
        
    }

    fun testRead() {

        val beatmapSets: RealmResults<BeatmapSet> = realmDB.query<BeatmapSet>().find()

        var beatmapSet: BeatmapSet = beatmapSets.first()
        var realmNamedFileUsage: RealmNamedFileUsage? = beatmapSet.Files?.first()

        var fileHash: String? = realmNamedFileUsage?.File?.Hash

        var filePath: Path = Path.of(
            osuFilesPath,
            fileHash?.get(0).toString(),
            fileHash?.substring(0..1),
            fileHash
        )

        println(filePath.toString())
        println(realmNamedFileUsage?.Filename)
        println(beatmapSet.Beatmaps?.first()?.Metadata?.Title)
        println(beatmapSet.Beatmaps?.first()?.Metadata?.Artist)

    }

}


