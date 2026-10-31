package com.example.appintencaovoto

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appintencaovoto.databinding.ActivityEstimuladaBinding
import com.example.appintencaovoto.databinding.ItemOpcaoBinding

/** Pesquisa estimulada: o entrevistado escolhe uma opção da lista. */
class EstimuladaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEstimuladaBinding

    /** Uma linha já montada na tela, com o texto que vai para o registro. */
    private class Opcao(val linha: ItemOpcaoBinding, val texto: String)

    private val opcoes = mutableListOf<Opcao>()
    private var escolhida: Opcao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEstimuladaBinding.inflate(layoutInflater)
        setContentView(binding.main)

        // Primeiro os candidatos da lista, depois branco, nulo e não sei
        for (candidato in CANDIDATOS) {
            adicionar(candidato.nome, candidato.partido, candidato.foto)
        }
        for (texto in RankingActivity.OUTRAS_OPCOES) {
            adicionar(getString(texto), partido = null, foto = null)
        }

        binding.btConfirmar.setOnClickListener { confirmar() }
    }

    /** Monta uma linha e pendura na lista da tela. */
    private fun adicionar(nome: String, partido: String?, foto: Int?) {
        val linha = ItemOpcaoBinding.inflate(layoutInflater, binding.listaOpcoes, false)

        linha.tvNome.text = nome

        if (partido == null) {
            linha.tvPartido.visibility = View.GONE
        } else {
            linha.tvPartido.text = partido
        }

        if (foto == null) {
            linha.ivFoto.visibility = View.GONE
        } else {
            linha.ivFoto.setImageResource(foto)
        }

        val opcao = Opcao(linha, nome)
        linha.root.setOnClickListener { marcar(opcao) }

        binding.listaOpcoes.addView(linha.root)
        opcoes.add(opcao)
    }

    /** Deixa marcada só a linha tocada: a escolha do voto é única. */
    private fun marcar(escolha: Opcao) {
        escolhida = escolha
        for (opcao in opcoes) {
            val marcada = opcao === escolha
            opcao.linha.root.isActivated = marcada
            opcao.linha.ivCheck.visibility = if (marcada) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun confirmar() {
        val escolha = escolhida

        if (escolha == null) {
            Toast.makeText(this, R.string.erro_candidato, Toast.LENGTH_SHORT).show()
            return // Para a execução do código aqui
        }

        val telaProblemas = Intent(this, ProblemasActivity::class.java)
        telaProblemas.putExtra(
            EspontaneaActivity.EXTRA_ESPONTANEA,
            intent.getStringExtra(EspontaneaActivity.EXTRA_ESPONTANEA)
        )
        telaProblemas.putExtra(EXTRA_VOTO, escolha.texto)
        startActivity(telaProblemas)
    }

    companion object {
        const val EXTRA_VOTO = "voto"
    }
}
