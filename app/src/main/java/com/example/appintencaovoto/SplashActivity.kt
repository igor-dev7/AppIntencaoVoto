package com.example.appintencaovoto

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

/** Tela de carregamento: mostra a marca por 2 segundos e segue para o login. */
class SplashActivity : AppCompatActivity() {

    private val aguardar = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        aguardar.postDelayed({ abrirLogin() }, DURACAO_MS)
    }

    private fun abrirLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish() // tira a splash da pilha: o botão voltar não retorna para ela
    }

    override fun onDestroy() {
        super.onDestroy()
        aguardar.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val DURACAO_MS = 2000L
    }
}
