package br.gov.meugovb.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(Intent(this@SplashActivity, NavHostActivity::class.java)) {
            startActivity(this)
            this@SplashActivity.finish()
        }
    }
}