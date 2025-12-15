package com.example.game

import android.content.Context
import androidx.room.*

@Entity(tableName = "scores")
data class Score(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nickname: String,
    val timeSurvived: Float
)

@Dao
interface ScoreDao {

    @Query("SELECT * FROM scores ORDER BY timeSurvived DESC LIMIT 10")
    suspend fun getTopScores(): List<Score>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(score: Score): Long

    @Query("DELETE FROM scores WHERE id NOT IN (SELECT id FROM scores ORDER BY timeSurvived DESC LIMIT 10)")
    suspend fun deleteExtraScores(): Int

    @Query("DELETE FROM scores")
    suspend fun clearAll()
}


@Database(entities = [Score::class], version = 1, exportSchema = false)
abstract class ScoreDatabase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao

    companion object {
        @Volatile
        private var INSTANCE: ScoreDatabase? = null

        fun getDatabase(context: Context): ScoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScoreDatabase::class.java,
                    "score_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

