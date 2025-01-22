import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import java.io.File

class RealmReadTester {

    companion object {
        @JvmStatic
        fun testRead() {

            val file = File("D:\\Program Files\\osu!-lazer\\client.realm")

            println("AAAAAAAAAAAAAAAAAAAAA!")

            val config: RealmConfiguration = RealmConfiguration.Builder(schema = setOf(BeatmapSet::class, Beatmap::class, BeatmapMetadata::class))
                .name(file.name)
                .directory(file.parentFile.path)
                .schemaVersion(46)
                .build()

            println("Hello World!")

            val realm = Realm.open(config)


            // Set this configuration as the default
            //Realm.setDefaultConfiguration(config);

            val beatmapSets: RealmResults<BeatmapSet> = realm.query<BeatmapSet>().find()

            if (beatmapSets.isNotEmpty()) {
                for (beatmapSet in beatmapSets) {
                    println(beatmapSet.Hash)
                }
            }

        }
    }

}
