package com.example.appintencaovoto

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appintencaovoto.databinding.ActivityProblemasBinding

/** Principais problemas apontados pelo entrevistado. Aceita mais de um. */
class ProblemasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProblemasBinding
    private lateinit var caixas: List<CheckBox>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProblemasBinding.inflate(layoutInflater)
        setContentView(binding.main)

        caixas = listOf(
            binding.cbSaude,
            binding.cbEducacao,
            binding.cbTransporte,
            binding.cbViolencia
        )

        // O campo livre só aceita digitação quando a caixa está marcada
        binding.cbOutro.setOnCheckedChangeListener { _, marcada ->
            binding.etOutro.isEnabled = marcada
            if (marcada) {
                binding.etOutro.requestFocus()
            } else {
                binding.etOutro.setText("")
            }
        }

        binding.btConfirmar.setOnClickListener { confirmar() }
    }

    private fun confirmar() {
        val marcados = caixas.filter { it.isChecked }.map { it.text.toString() }.toMutableList()

        val outro = binding.etOutro.text.toString().trim()
        if (binding.cbOutro.isChecked) {
            if (outro.isEmpty()) {
                Toast.makeText(this, R.string.erro_outro, Toast.LENGTH_SHORT).show()
                return // Para a execução do código aqui
            }
            marcados.add(outro)
        }

        if (marcados.isEmpty()) {
            Toast.makeText(this, R.string.erro_problema, Toast.LENGTH_SHORT).show()
            return
        }

        val telaDados = Intent(this, DadosActivity::class.java)
        telaDados.putExtra(
            EstimuladaActivity.EXTRA_VOTO,
            intent.getStringExtra(EstimuladaActivity.EXTRA_VOTO)
        )
        telaDados.putExtra(EXTRA_PROBLEMAS, marcados.joinToString(", "))
        startActivity(telaDados)
    }

    companion object {
        const val EXTRA_PROBLEMAS = "problemas"
    }
}
