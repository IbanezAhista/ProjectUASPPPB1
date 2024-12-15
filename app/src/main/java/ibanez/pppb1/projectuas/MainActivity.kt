package ibanez.pppb1.projectuas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ibanez.pppb1.projectuas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager.getInstance(this)

        if (prefManager.isLoggedIn()) {
            val role = prefManager.getRole()
            when (role) {
                "admin" -> {
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                "customer" -> {
                    val intent = Intent(this@MainActivity, BerandaActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        with(binding) {
            btnLoginA.setOnClickListener {
                val username = txtUsername.text.toString()
                val password = txtPassword.text.toString()

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        "Mohon Isi Semua Data",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (username == "admin" && password == "123") {
                    prefManager.saveUsername(username)
                    prefManager.savePassword(password)
                    prefManager.saveRole("admin")
                    prefManager.setLoggedIn(true)
                    navigateToHome()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Username atau Password Salah",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            btnLoginC.setOnClickListener {
                val username = txtUsername.text.toString()
                val password = txtPassword.text.toString()

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        "Mohon Isi Semua Data",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    prefManager.saveUsername(username)
                    prefManager.savePassword(password)
                    prefManager.setLoggedIn(true)
                    prefManager.saveRole("customer")

                    val intent = Intent(this@MainActivity, BerandaActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun navigateToHome() {
        val role = prefManager.getRole()
        val intent = when (role) {
            "admin" -> Intent(this, HomeActivity::class.java)
            "user" -> Intent(this, BerandaActivity::class.java)
            else -> null
        }

        intent?.let {
            startActivity(it)
            finish()
        } ?: run {
            Toast.makeText(this, "Role Tidak Valid!", Toast.LENGTH_SHORT).show()
        }
    }
}
