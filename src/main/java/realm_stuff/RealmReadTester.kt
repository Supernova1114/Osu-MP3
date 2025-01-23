package realm_stuff

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import java.nio.file.Path
import java.io.File as JavaFile

// References:
// https://github.com/kabiiQ/BeatmapExporter/blob/main/BeatmapExporterCore/Exporters/Lazer/LazerDB/LazerDatabase.cs

class RealmReadTester {

    companion object {
        @JvmStatic
        fun testRead() {

            val realmFile = JavaFile("D:\\Program Files\\osu!-lazer\\client.realm")
            val osuFilesPath: String = "D:\\Program Files\\osu!-lazer\\files"

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
                    BeatmapCollection::class,
                )
            )
            .name(realmFile.name)
            .directory(realmFile.parentFile.path)
            .schemaVersion(46)
            .build()

            val realm = Realm.open(config)


            // Set this configuration as the default
            //Realm.setDefaultConfiguration(config);

            val beatmapSets: RealmResults<BeatmapSet> = realm.query<BeatmapSet>().find()

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

//            var count: Int = 0
//
//            if (beatmapSets.isNotEmpty()) {
//                for (beatmapSet in beatmapSets) {
//                    for (beatmap in beatmapSet.Beatmaps!!) {
//                        println(beatmap.Metadata?.AudioFile)
//                        count++
//                    }
//                }
//            }
//
//            println("Beatmap Count: " + count)

            realm.close()
        }
    }

}
