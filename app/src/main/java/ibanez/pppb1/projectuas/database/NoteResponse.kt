package ibanez.pppb1.projectuas.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "note_table")
data class NoteResponse(
    @PrimaryKey
    @SerializedName("_id")
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "nama")
    val nama: String,
    @ColumnInfo(name = "deskripsi")
    val deskripsi: String,
    @ColumnInfo(name = "harga")
    val harga: String,
    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean = false
): Parcelable