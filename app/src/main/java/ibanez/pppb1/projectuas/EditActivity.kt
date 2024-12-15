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
import ibanez.pppb1.projectuas.databinding.ActivityEditBinding
import ibanez.pppb1.projectuas.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private lateinit var mNotesDao: NoteDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!

        binding.btnKembali.setOnClickListener {
            val intent = Intent(this@EditActivity, HomeActivity::class.java)
            startActivity(intent)
        }

        val noteId = intent.getStringExtra("noteId")
        Log.i("nilai noteId", noteId.toString())
        val noteNama = intent.getStringExtra("noteNama") ?: ""
        val noteDeskripsi = intent.getStringExtra("noteDeskripsi") ?: ""
        val noteHarga = intent.getStringExtra("noteHarga") ?: ""

        binding.txtNama.setText(noteNama)
        binding.txtDeskripsi.setText(noteDeskripsi)
        binding.txtHarga.setText(noteHarga)

        binding.btnSimpan.setOnClickListener {
            val updateNoteId = noteId.toString()
            val updatedNama = binding.txtNama.text.toString()
            val updatedDeskripsi = binding.txtDeskripsi.text.toString()
            val updatedHarga = binding.txtHarga.text.toString()

            if (noteId != null) {
                val updatedNote = Note(updatedNama, updatedDeskripsi, updatedHarga)
                val updateNoteLokal = NoteResponse(updateNoteId, updatedNama, updatedDeskripsi, updatedHarga)
                Thread {
                    mNotesDao.update(updateNoteLokal)
                    runOnUiThread {
                        Toast.makeText(this@EditActivity, "Data Berhasil Diperbarui di Lokal", Toast.LENGTH_SHORT).show()
                    }
                }.start()
                saveNoteToApi(updateNoteId, updatedNote)
            } else {
                Toast.makeText(this, "ID Catatan Tidak Valid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveNoteToApi(id: String, note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.api.updateNote(id, note)
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@EditActivity, "Data Berhasil Diperbarui di Server", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "Code: ${response.code()}, Error: $errorBody")
                    runOnUiThread {
                        Toast.makeText(this@EditActivity, "Gagal Memperbarui Data: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Exception: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@EditActivity, "Server Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
