package ibanez.pppb1.projectuas

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ibanez.pppb1.projectuas.database.NoteDao
import ibanez.pppb1.projectuas.database.NoteResponse
import ibanez.pppb1.projectuas.database.NoteRoomDatabase
import ibanez.pppb1.projectuas.databinding.ActivityHomeBinding
import ibanez.pppb1.projectuas.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var mNotesDao: NoteDao
    private lateinit var executorService: ExecutorService
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager.getInstance(this)

        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!
        executorService = Executors.newSingleThreadExecutor()

        menuAdapter = MenuAdapter(isForKatalog = false)
        binding.rvMenu.adapter = menuAdapter

        with(binding) {
            btnTambahMenu.setOnClickListener {
                val intent = Intent(this@HomeActivity, EntryActivity::class.java)
                startActivity(intent)
            }

            btnLogout.setOnClickListener {
                prefManager.clear()

                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getAllNotes()
    }

    private fun getAllNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.api.getNote()
                if (response.isSuccessful) {
                    val note = response.body()?: emptyList()
                    Log.i("test", note.toString())
                    withContext(Dispatchers.Main) {
                        menuAdapter.setNotes(note)

                        menuAdapter.setOnItemClickListener(object : MenuAdapter.OnItemClickListener{
                            override fun onItemClick(note: NoteResponse) {
                                val intent = Intent(this@HomeActivity, EditActivity::class.java).apply {
                                    putExtra("noteId", note.id)
                                    putExtra("noteNama", note.nama)
                                    putExtra("noteDeskripsi", note.deskripsi)
                                    putExtra("noteHarga", note.harga)
                                }
                                startActivity(intent)
                            }
                        })

                        menuAdapter.setOnItemLongClickListener(object : MenuAdapter.OnItemLongClickListener {
                        override fun onItemLongClick(note: NoteResponse) {
                        AlertDialog.Builder(this@HomeActivity)
                            .setTitle("Hapus Menu")
                            .setMessage("Apakah Anda yakin ingin menghapus menu ini?")
                            .setPositiveButton("Ya") { _, _ ->
                                executorService.execute {
                                    mNotesDao.delete(note)
                                    Log.i("idUntukDihapus", note.id)
                                    deleteNote(note.id)
                                }
                                Toast.makeText(this@HomeActivity, "Menu Dihapus", Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton("Tidak", null)
                            .show()
                            }
                        })
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@HomeActivity, "Failed to Fetch Data", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeActivity, "Exception : $e", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteNote(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.api.deleteNote(id)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@HomeActivity, "Menu Berhasil Dihapus di Database API", Toast.LENGTH_SHORT).show()
                        getAllNotes()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@HomeActivity, "Menu Gagal Dihapus", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeActivity, "Exception delete error: $e", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
