import io.realm.kotlin.types.*
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

// https://github.com/yadPe/osu-lazer-db-reader/blob/master/src/shapes.ts
// https://github.com/ppy/osu/blob/master/osu.Game/Beatmaps/BeatmapInfo.cs#L16



class Beatmap : RealmObject {
    @PrimaryKey
    var ID: RealmUUID? = RealmUUID.from("");

    var DifficultyName: String? = null
    var MetaData: BeatmapMetadata? = null
    var CountdownOffset = 0.0
    var TimelineZoom = 0.0
    var GridSize = 0.0
    var BeatDivisor = 0.0
    var DistanceSpacing = 0.0
    var StackLeniency = 0.0
    var AudioLeadIn = 0.0
    var BPM = 0.0
    var Length = 0.0
    var OnlineID = 0.0
    var Status = 0.0
    var MD5Hash: String? = null
    var Hash: String? = null
    var StarRating = 0.0
    var Hidden = 0.0
    var SamplesMatchPlaybackRate = false
    var EpilepsyWarning = false
    var WidescreenStoryboard = false
    var LetterboxInBreaks = false
    var SpecialStyle = false
    var BeatmapSet: BeatmapSet? = null
    var Difficulty: RealmAny? = null
    var Ruleset: RealmAny? = null
    var UserSettings: RealmAny? = null
}

class BeatmapMetadata : RealmObject {
    var Title: String? = null
    var TitleUnicode: String? = null
    var Artist: String? = null
    var ArtistUnicode: String? = null
    var Author: String? = null
    var Source: String? = null
    var Tags: String? = null
    var PreviewTime = 0.0
    var AudioFile: String? = null
    var BackgroundFile: String? = null
}

class BeatmapSet : RealmObject {
    @PrimaryKey
    var ID: RealmUUID? = RealmUUID.from("");
    var Beatmaps: RealmList<Beatmap>? = null
    var OnlineID: Int = 0
    var DateAdded: RealmInstant = RealmInstant.now()
    var DateRanked: RealmInstant? = null
    var DateSubmitted: RealmInstant? = null
    var Files: RealmAny? = null
    var Status: Int = 0
    var DeletePending = false
    var Hash: String? = null
    var Protected = false

}