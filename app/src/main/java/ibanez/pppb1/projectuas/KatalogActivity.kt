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
import ibanez.pppb1.projectuas.databinding.ActivityKatalogBinding
import ibanez.pppb1.projectuas.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class KatalogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKatalogBinding
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var mNotesDao: NoteDao
    private lateinit var executorService: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!
        executorService = Executors.newSingleThreadExecutor()

        menuAdapter = MenuAdapter(isForKatalog = true)
        binding.rvMenu.adapter = menuAdapter

        menuAdapter.setOnFavoriteClickListener(object : MenuAdapter.OnFavoriteClickListener {
            override fun onFavoriteClick(note: NoteResponse) {
                Log.i("test", note.id)
                var favorite = false
                if (!note.isFavorite) {
                    favorite = false
                } else {
                    favorite = true
                }
                val updatedNote = Note(note.nama, note.deskripsi, note.harga, favorite)
                val updateNoteResponse = NoteResponse(note.id, note.nama, note.deskripsi, note.harga, note.isFavorite)
                saveNoteToApi(note.id, updatedNote)
                if (favorite) {
                    insert(updateNoteResponse)
                } else {
                    delete(updateNoteResponse)
                }
            }
        })

        getAllNotes()

        with(binding) {
            btnKembali.setOnClickListener {
                val intent = Intent(this@KatalogActivity, BerandaActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun insert(note: NoteResponse) {
        executorService.execute {
            mNotesDao.insert(note)
            Log.i("Insert ", "Berhasil")
        }
    }

    private fun delete(note: NoteResponse) {
        executorService.execute {
            mNotesDao.delete(note)
            Log.i("Delete Insyaallah", "Berhasil")
        }
    }

    private fun getAllNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.api.getNote()
                if (response.isSuccessful) {
                    val note = response.body() ?: emptyList()
                    Log.d("API_RESPONSE", "Fetched notes: $note")
                    withContext(Dispatchers.Main) {
                        menuAdapter.setNotes(note)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@KatalogActivity,
                            "Failed to Fetch Data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@KatalogActivity, "Exception : $e", Toast.LENGTH_SHORT)
                        .show()
                }
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
                            this@KatalogActivity,
                            "Data Berhasil Diperbarui di Server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "Code: ${response.code()}, Error: $errorBody")
                    runOnUiThread {
                        Toast.makeText(
                            this@KatalogActivity,
                            "Gagal Memperbarui Data: $errorBody",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Exception: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@KatalogActivity,
                        "Server Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
