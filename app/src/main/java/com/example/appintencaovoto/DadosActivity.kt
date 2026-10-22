package com.example.appintencaovoto

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
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
 * Dados do entrevistado. Ao confirmar, grava a entrevista com a data e hora
 * do aparelho e a posição geográfica, junto com o voto e os problemas.
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

        val voto = intent.getStringExtra(EstimuladaActivity.EXTRA_VOTO) ?: ""
        val problemas = intent.getStringExtra(ProblemasActivity.EXTRA_PROBLEMAS) ?: ""
        val momento = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val local = posicaoGeografica()

        val registro =
            getString(R.string.registro_texto, nome, celular, voto, problemas, momento, local)

        // Guarda a entrevista e soma o voto para o ranking
        Pesquisa.registrar(this, momento, registro, voto)

        binding.tvRegistro.text = registro
        binding.blocoRegistro.visibility = View.VISIBLE
        confirmado = true

        Toast.makeText(this, R.string.msg_salvo, Toast.LENGTH_SHORT).show()
    }

    /** Última posição conhecida do aparelho. Devolve um aviso quando não há. */
    private fun posicaoGeografica(): String {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return getString(R.string.local_sem_permissao)
        }

        val gerente = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var posicao: Location? = null
        for (fonte in listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)) {
            try {
                posicao = posicao ?: gerente.getLastKnownLocation(fonte)
            } catch (erro: SecurityException) {
                return getString(R.string.local_sem_permissao)
            }
        }

        return if (posicao == null) {
            getString(R.string.local_indisponivel)
        } else {
            getString(R.string.formato_local, posicao.latitude, posicao.longitude)
        }
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

    /** Encerra a entrevista e mostra o ranking com os votos acumulados. */
    private fun finalizar() {
        if (!confirmado) {
            Toast.makeText(this, R.string.msg_confirme_antes, Toast.LENGTH_SHORT).show()
            return
        }

        startActivity(Intent(this, RankingActivity::class.java))
        finish()
    }

    companion object {
        private const val PEDIDO_LOCALIZACAO = 1
    }
}
