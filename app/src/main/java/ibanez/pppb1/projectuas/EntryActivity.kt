package ibanez.pppb1.projectuas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ibanez.pppb1.projectuas.database.Note
import ibanez.pppb1.projectuas.database.NoteDao
import ibanez.pppb1.projectuas.database.NoteResponse
import ibanez.pppb1.projectuas.database.NoteRoomDatabase
import ibanez.pppb1.projectuas.databinding.ActivityEntryBinding
import ibanez.pppb1.projectuas.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class EntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEntryBinding
    private lateinit var mNotesDao: NoteDao
    private lateinit var executorService: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!

        with(binding) {
            btnKembali.setOnClickListener {
                val intent = Intent(this@EntryActivity, HomeActivity::class.java)
                startActivity(intent)
            }

            btnSimpan.setOnClickListener(View.OnClickListener {
                val nama = txtNama.text.toString().trim()
                val deskripsi = txtDeskripsi.text.toString().trim()
                val harga = txtHarga.text.toString().trim()

                if (nama.isEmpty() || deskripsi.isEmpty() || harga.isEmpty()) {
                    val missingFields = mutableListOf<String>()
                    if (nama.isEmpty()) missingFields.add("Nama")
                    if (deskripsi.isEmpty()) missingFields.add("Deskripsi")
                    if (harga.isEmpty()) missingFields.add("Harga")

                    Toast.makeText(this@EntryActivity, "Harap Lengkapi: ${missingFields.joinToString(", ")}", Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }

                val note = Note(
                    nama = nama,
                    deskripsi = deskripsi,
                    harga = harga
                )

                saveNoteToFireBase(note)

                setEmptyField()

                val intent = Intent(this@EntryActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            })
        }
    }

    private fun saveNoteToFireBase(note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.api.createNote(note)
                if (response.isSuccessful)  {
                    runOnUiThread {
                        Toast.makeText(this@EntryActivity, "Menu Berhasil Disimpan", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@EntryActivity, "Menu Gagal Tersimpan", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@EntryActivity, "Server Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setEmptyField() {
        with(binding) {
            txtNama.setText("")
            txtDeskripsi.setText("")
            txtHarga.setText("")
        }
    }
}
