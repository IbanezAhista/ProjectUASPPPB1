package ibanez.pppb1.projectuas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ibanez.pppb1.projectuas.databinding.ActivityBerandaBinding

class BerandaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBerandaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBerandaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnLihatKatalog.setOnClickListener {
                val intent = Intent(this@BerandaActivity, KatalogActivity::class.java)
                startActivity(intent)
            }

            btnFavoritSaya.setOnClickListener {
                val intent = Intent(this@BerandaActivity, FavoritActivity::class.java)
                startActivity(intent)
            }

            btnProfile.setOnClickListener {
                val intent = Intent(this@BerandaActivity, ProfileActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
