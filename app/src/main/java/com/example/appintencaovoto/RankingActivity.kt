package com.example.appintencaovoto

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.appintencaovoto.databinding.ActivityRankingBinding
import com.example.appintencaovoto.databinding.ItemLegendaBinding
import com.example.appintencaovoto.databinding.ItemRankingBinding

/** Resultado da pesquisa: quantos votos cada opção teve, em número, percentual e pizza. */
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

        binding.tvTotal.text = getString(R.string.admin_total, Pesquisa.total(this))

        resultados.forEachIndexed { posicao, resultado ->
            adicionar(posicao, resultado, maior, total)
        }

        // Só as opções que receberam voto viram fatia, e a legenda acompanha
        binding.grafico.mostrar(
            resultados.mapIndexed { posicao, resultado ->
                Fatia(resultado.votos, corDoGrafico(posicao))
            }
        )

        resultados.forEachIndexed { posicao, resultado ->
            if (resultado.votos > 0) {
                legendar(resultado.nome, corDoGrafico(posicao))
            }
        }

        binding.btVoltar.setOnClickListener { finish() }
    }

    /** Monta uma posição do resultado. A barra é proporcional ao mais votado. */
    private fun adicionar(posicao: Int, resultado: Resultado, maior: Int, total: Int) {
        val linha = ItemRankingBinding.inflate(layoutInflater, binding.listaRanking, false)

        linha.tvPosicao.text = (posicao + 1).toString()
        linha.tvNome.text = resultado.nome
        linha.tvVotos.text = resultado.votos.toString()

        // Sem nenhum voto ainda, o percentual fica em zero em vez de dividir por zero
        val percentual = if (total == 0) 0f else 100f * resultado.votos / total
        linha.tvPercentual.text = getString(R.string.formato_percentual, percentual)

        // O ponto usa a mesma cor que a fatia desta opção no gráfico
        (linha.vCor.background as GradientDrawable).setColor(corDoGrafico(posicao))

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

        val cheia = if (maior == 0) 0f else resultado.votos.toFloat() / maior
        linha.vPreenchida.layoutParams = LinearLayout.LayoutParams(0, MATCH, cheia)
        linha.vRestante.layoutParams = LinearLayout.LayoutParams(0, MATCH, 1f - cheia)

        binding.listaRanking.addView(linha.root)
    }

    /** Uma entrada da legenda, com o ponto na mesma cor da fatia. */
    private fun legendar(nome: String, cor: Int) {
        val entrada = ItemLegendaBinding.inflate(layoutInflater, binding.legenda, false)
        entrada.tvNome.text = nome
        (entrada.vCor.background as GradientDrawable).setColor(cor)
        binding.legenda.addView(entrada.root)
    }

    companion object {
        private const val MATCH = LinearLayout.LayoutParams.MATCH_PARENT

        /** As mesmas opções sem foto que a pesquisa estimulada oferece. */
        val OUTRAS_OPCOES = intArrayOf(
            R.string.voto_branco, R.string.voto_nulo, R.string.voto_nao_sei
        )
    }
}
