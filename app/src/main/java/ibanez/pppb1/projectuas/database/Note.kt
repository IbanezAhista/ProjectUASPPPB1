package ibanez.pppb1.projectuas.database

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    @SerializedName("nama")
    val nama: String,
    @SerializedName("deskripsi")
    val deskripsi: String,
    @SerializedName("harga")
    val harga: String,
    @SerializedName("isFavorite")
    var isFavorite: Boolean = false
) : Parcelable