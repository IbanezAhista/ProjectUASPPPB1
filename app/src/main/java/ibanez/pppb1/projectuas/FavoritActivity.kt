package ibanez.pppb1.projectuas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ibanez.pppb1.projectuas.database.Note
import ibanez.pppb1.projectuas.database.NoteDao
import ibanez.pppb1.projectuas.database.NoteResponse
import ibanez.pppb1.projectuas.database.NoteRoomDatabase
import ibanez.pppb1.projectuas.databinding.ActivityFavoritBinding
import ibanez.pppb1.projectuas.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoritActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoritBinding
    private lateinit var mNotesDao: NoteDao
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var executorService: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = NoteRoomDatabase.getDatabase(this)
        if (db == null) {
            Log.e("FavoritActivity", "Database is Null")
            return
        }

        mNotesDao = db.noteDao()
        executorService = Executors.newSingleThreadExecutor()

        menuAdapter = MenuAdapter(isForKatalog = true)
        binding.rvMenu.adapter = menuAdapter

        menuAdapter.setOnFavoriteClickListener(object : MenuAdapter.OnFavoriteClickListener {
            override fun onFavoriteClick(note: NoteResponse) {
                CoroutineScope(Dispatchers.IO).launch {
                    mNotesDao.updateFavoriteStatus(note.id, note.isFavorite)
                    var favorite = false
                    val updatedNote = Note(note.nama, note.deskripsi, note.harga, favorite)
                    val updateNoteResponse = NoteResponse(note.id, note.nama, note.deskripsi, note.harga, note.isFavorite)
                    saveNoteToApi(note.id, updatedNote)
                    delete(updateNoteResponse)
                    getAllNotes()
                }
            }
        })

        getAllNotes()

        with(binding) {
            btnKembali.setOnClickListener {
                val intent = Intent(this@FavoritActivity, BerandaActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun delete(note: NoteResponse) {
        executorService.execute {
            mNotesDao.delete(note)
            Log.i("Delete", "Berhasil")
        }
    }

    private fun getAllNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val notes = mNotesDao.getAllNotes()
                val favoriteNotes = notes.filter { it.isFavorite }

                Log.i("Favorite Notes", favoriteNotes.toString())

                withContext(Dispatchers.Main) {
                    menuAdapter.setNotes(favoriteNotes)
                }
            } catch (e: Exception) {
                Log.e("FavoritActivity", "Error Fetching Favorite Notes", e)
            }
        }
    }

    private fun saveNoteToApi(id: String, isFavorite: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(
                    "API_REQUEST",
                    "Sending data to API: id=$id, isFavorite=${isFavorite.isFavorite}"
                )
                val response = ApiClient.api.updateNote(id, isFavorite)
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(
                            this@FavoritActivity,
                            "Data Berhasil Diperbarui di Server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "Code: ${response.code()}, Error: $errorBody")
                    runOnUiThread {
                        Toast.makeText(
                            this@FavoritActivity,
                            "Gagal Memperbarui Data: $errorBody",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Exception: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@FavoritActivity,
                        "Server Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
