package ibanez.pppb1.projectuas.database

import androidx.room.*

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(note: NoteResponse)

    @Update
    fun update(note: NoteResponse)

    @Delete
    fun delete(note: NoteResponse)

    @Query("UPDATE note_table SET isFavorite = :isFavorite WHERE id = :noteId")
    suspend fun updateFavoriteStatus(noteId: String, isFavorite: Boolean)

    @Query("SELECT * from note_table")
    suspend fun getAllNotes(): List<NoteResponse>
}
