package com.example.appintencaovoto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appintencaovoto.databinding.ActivityLoginBinding

/**
 * Entrada do app. O usuário e a senha são fixos no código e decidem para onde ir:
 * o entrevistador cai na pesquisa, o admin cai no resultado.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.main)

        binding.btAcessar.setOnClickListener { acessar() }
        binding.btFinalizar.setOnClickListener { finishAndRemoveTask() }
    }

    private fun acessar() {
        val usuario = binding.etUsuario.text.toString().trim()
        val senha = binding.etSenha.text.toString()

        if (usuario.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, R.string.login_erro_vazio, Toast.LENGTH_SHORT).show()
            return // Para a execução do código aqui
        }

        val destino = when {
            usuario == ENTREVISTADOR && senha == ENTREVISTADOR -> EspontaneaActivity::class.java
            usuario == ADMIN && senha == ADMIN -> AdminActivity::class.java
            else -> null
        }

        if (destino == null) {
            Toast.makeText(this, R.string.login_erro_invalido, Toast.LENGTH_SHORT).show()
            binding.etSenha.setText("")
            return
        }

        startActivity(Intent(this, destino))
        finish() // impede voltar para o login pelo botão de voltar
    }

    companion object {
        private const val ENTREVISTADOR = "entrevistador"
        private const val ADMIN = "admin"
    }
}
