package com.example.appintencaovoto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appintencaovoto.databinding.ActivityDadosBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Dados do entrevistado. Ao confirmar, lê a posição no GPS e grava a entrevista
 * com a data e hora do aparelho, o endereço e a coordenada, o voto e os problemas.
 */
class DadosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDadosBinding
    private var confirmado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDadosBinding.inflate(layoutInflater)
        setContentView(binding.main)

        binding.etCelular.addTextChangedListener(MascaraTelefone(binding.etCelular))

        pedirPermissaoDeLocalizacao()

        binding.btConfirmar.setOnClickListener { confirmar() }
        binding.btFinalizar.setOnClickListener { finalizar() }
    }

    private fun confirmar() {
        val nome = binding.etNome.text.toString().trim()
        val celular = binding.etCelular.text.toString().trim()

        if (nome.isEmpty() || celular.isEmpty()) {
            Toast.makeText(this, R.string.erro_dados, Toast.LENGTH_SHORT).show()
            return // Para a execução do código aqui
        }

        // A leitura do GPS demora, então o botão avisa enquanto ela acontece
        binding.btConfirmar.isEnabled = false
        binding.btConfirmar.setText(R.string.dados_obtendo_local)

        Localizacao.obter(this) { local ->
            gravar(nome, celular, local)
            binding.btConfirmar.isEnabled = true
            binding.btConfirmar.setText(R.string.dados_confirmar)
        }
    }

    private fun gravar(nome: String, celular: String, local: Local) {
        val entrevista = Entrevista(
            nome = nome,
            celular = celular,
            espontanea = intent.getStringExtra(EspontaneaActivity.EXTRA_ESPONTANEA) ?: "",
            voto = intent.getStringExtra(EstimuladaActivity.EXTRA_VOTO) ?: "",
            problemas = intent.getStringExtra(ProblemasActivity.EXTRA_PROBLEMAS) ?: "",
            dataHora = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
            cidade = local.cidade,
            endereco = local.endereco,
            coordenada = local.coordenada
        )

        Pesquisa.registrar(this, entrevista)

        binding.tvRegistro.text = getString(
            R.string.registro_texto,
            entrevista.nome,
            entrevista.celular,
            entrevista.espontanea,
            entrevista.voto,
            entrevista.problemas,
            entrevista.dataHora,
            entrevista.cidade,
            entrevista.endereco,
            entrevista.coordenada
        )
        binding.blocoRegistro.visibility = View.VISIBLE
        confirmado = true

        Toast.makeText(this, R.string.msg_salvo, Toast.LENGTH_SHORT).show()
    }

    private fun pedirPermissaoDeLocalizacao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PEDIDO_LOCALIZACAO
            )
        }
    }

    /** Fecha esta entrevista e volta para a espontânea, pronto para a próxima. */
    private fun finalizar() {
        if (!confirmado) {
            Toast.makeText(this, R.string.msg_confirme_antes, Toast.LENGTH_SHORT).show()
            return
        }

        val proxima = Intent(this, EspontaneaActivity::class.java)
        proxima.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(proxima)
    }

    companion object {
        private const val PEDIDO_LOCALIZACAO = 1
    }
}
