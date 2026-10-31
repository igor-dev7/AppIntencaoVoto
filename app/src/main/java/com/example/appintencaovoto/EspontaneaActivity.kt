package com.example.appintencaovoto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appintencaovoto.databinding.ActivityEspontaneaBinding

/**
 * Pesquisa espontânea: primeira pergunta da entrevista. O entrevistado diz
 * um nome de cabeça, sem lista para escolher.
 */
class EspontaneaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEspontaneaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEspontaneaBinding.inflate(layoutInflater)
        setContentView(binding.main)

        binding.btConfirmar.setOnClickListener { confirmar() }
        binding.btSair.setOnClickListener { sairParaLogin() }
    }

    private fun confirmar() {
        val resposta = binding.etResposta.text.toString().trim()

        if (resposta.isEmpty()) {
            Toast.makeText(this, R.string.erro_espontanea, Toast.LENGTH_SHORT).show()
            return // Para a execução do código aqui
        }

        val telaEstimulada = Intent(this, EstimuladaActivity::class.java)
        telaEstimulada.putExtra(EXTRA_ESPONTANEA, resposta)
        startActivity(telaEstimulada)
    }

    companion object {
        const val EXTRA_ESPONTANEA = "espontanea"
    }
}
