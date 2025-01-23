package realm_stuff

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.*
import io.realm.kotlin.types.annotations.PrimaryKey

// https://github.com/yadPe/osu-lazer-db-reader/blob/master/src/shapes.ts
// https://github.com/ppy/osu/blob/master/osu.Game/Beatmaps/BeatmapInfo.cs#L16



class Beatmap : RealmObject {
    @PrimaryKey
    var ID: RealmUUID = RealmUUID.random()

    var DifficultyName: String? = null
    var Ruleset: Ruleset? = null
    var Difficulty: BeatmapDifficulty? = null
    var UserSettings: BeatmapUserSettings? = null
    var BeatmapSet: BeatmapSet? = null
    var Status: Int = 0
    var OnlineID: Int = 0
    var Length = 0.0
    var BPM = 0.0
    var Hash: String? = null
    var StarRating = 0.0
    var MD5Hash: String? = null
    var OnlineMD5Hash: String? = null
    var LastLocalUpdate: RealmInstant? = null
    var LastOnlineUpdate: RealmInstant? = null
    var Hidden: Boolean = false
    var EndTimeObjectCount: Int = 0
    var TotalObjectCount: Int = 0
    var LastPlayed: RealmInstant? = null
    var BeatDivisor: Int = 0
    var EditorTimestamp: Double? = 0.0
    var Metadata: BeatmapMetadata? = null

}

class BeatmapMetadata : RealmObject {
    var Title: String? = null
    var TitleUnicode: String? = null
    var Artist: String? = null
    var ArtistUnicode: String? = null
    var Author: RealmUser? = null
    var Source: String? = null
    var Tags: String? = null
    var PreviewTime: Int = 0
    var AudioFile: String? = null
    var BackgroundFile: String? = null
}

class BeatmapSet : RealmObject {
    @PrimaryKey
    var ID: RealmUUID = RealmUUID.random()

    var Beatmaps: RealmList<Beatmap>? = null
    var OnlineID: Int = 0
    var DateAdded: RealmInstant = RealmInstant.now()
    var DateRanked: RealmInstant? = null
    var DateSubmitted: RealmInstant? = null
    var Files: RealmList<RealmNamedFileUsage>? = null
    var Status: Int = 0
    var DeletePending = false
    var Hash: String? = null
    var Protected = false

}

class Ruleset : RealmObject {

    @PrimaryKey
    var ShortName: String? = null

    var OnlineID: Int = 0
    var Name: String? = null
    var InstantiationInfo: String? = null
    var LastAppliedDifficultyVersion: Int = 0
    var Available: Boolean = false
}

class BeatmapDifficulty : EmbeddedRealmObject {

    var DrainRate: Float = 0.0f
    var CircleSize: Float = 0.0f
    var OverallDifficulty: Float = 0.0f
    var ApproachRate: Float = 0.0f
    var SliderMultiplier: Double = 0.0
    var SliderTickRate: Double = 0.0
}

class BeatmapUserSettings : EmbeddedRealmObject {
    var Offset: Double = 0.0
}

class RealmUser : EmbeddedRealmObject {
    var OnlineID: Int = 0
    var Username: String? = null
    var CountryCode: String? = null
}

class RealmNamedFileUsage : EmbeddedRealmObject {
    var File: File? = null
    var Filename: String? = null
}

class File : RealmObject {
    @PrimaryKey
    var Hash: String? = null
}

class BeatmapCollection : RealmObject {

    @PrimaryKey
    var ID: RealmUUID = RealmUUID.random()

    var Name: String? = null
    var BeatmapMD5Hashes: RealmList<String?> = realmListOf()
    var LastModified: RealmInstant = RealmInstant.now()
}