package com.example.appintencaovoto

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.appintencaovoto.databinding.ActivityRankingBinding
import com.example.appintencaovoto.databinding.ItemRankingBinding

/** Quantos votos cada opção recebeu até agora, da mais votada para a menos. */
class RankingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRankingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingBinding.inflate(layoutInflater)
        setContentView(binding.main)

        val outras = OUTRAS_OPCOES.map { getString(it) }
        val resultados = Pesquisa.ranking(this, outras)
        val total = resultados.sumOf { it.votos }
        val maior = resultados.maxOf { it.votos }

        binding.tvTotal.text =
            resources.getQuantityString(R.plurals.formato_entrevistas, total, total)

        resultados.forEachIndexed { posicao, resultado ->
            adicionar(posicao + 1, resultado, maior)
        }

        binding.btNovaEntrevista.setOnClickListener { novaEntrevista() }
    }

    /** Monta uma posição do ranking. A barra é proporcional ao mais votado. */
    private fun adicionar(posicao: Int, resultado: Resultado, maior: Int) {
        val linha = ItemRankingBinding.inflate(layoutInflater, binding.listaRanking, false)

        linha.tvPosicao.text = posicao.toString()
        linha.tvNome.text = resultado.nome
        linha.tvVotos.text = resultado.votos.toString()

        if (resultado.partido == null) {
            linha.tvPartido.visibility = View.GONE
        } else {
            linha.tvPartido.text = resultado.partido
        }

        if (resultado.foto == null) {
            linha.ivFoto.visibility = View.GONE
        } else {
            linha.ivFoto.setImageResource(resultado.foto)
        }

        // Sem nenhum voto ainda, a barra fica vazia em vez de dividir por zero
        val cheia = if (maior == 0) 0f else resultado.votos.toFloat() / maior
        linha.vPreenchida.layoutParams = LinearLayout.LayoutParams(0, MATCH, cheia)
        linha.vRestante.layoutParams = LinearLayout.LayoutParams(0, MATCH, 1f - cheia)

        binding.listaRanking.addView(linha.root)
    }

    private fun novaEntrevista() {
        val inicio = Intent(this, EstimuladaActivity::class.java)
        inicio.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(inicio)
        finish()
    }

    companion object {
        private const val MATCH = LinearLayout.LayoutParams.MATCH_PARENT

        /** As mesmas opções sem foto que a pesquisa estimulada oferece. */
        val OUTRAS_OPCOES = intArrayOf(
            R.string.voto_branco, R.string.voto_nulo, R.string.voto_nao_sei
        )
    }
}
