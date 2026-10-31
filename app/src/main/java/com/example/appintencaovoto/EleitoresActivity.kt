package com.example.appintencaovoto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appintencaovoto.databinding.ActivityEleitoresBinding
import com.example.appintencaovoto.databinding.ItemEleitorBinding

/** Lista de quem foi entrevistado, da entrevista mais recente para a mais antiga. */
class EleitoresActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEleitoresBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEleitoresBinding.inflate(layoutInflater)
        setContentView(binding.main)

        val entrevistas = Pesquisa.entrevistas(this)
        binding.tvTotal.text = getString(R.string.admin_total, entrevistas.size)

        for (entrevista in entrevistas) {
            adicionar(entrevista)
        }

        binding.btVoltar.setOnClickListener { finish() }
    }

    /** Monta uma ficha do eleitor e pendura na lista da tela. */
    private fun adicionar(entrevista: Entrevista) {
        val ficha = ItemEleitorBinding.inflate(layoutInflater, binding.listaEleitores, false)

        ficha.tvNome.text = entrevista.nome
        ficha.tvCelular.text = entrevista.celular
        ficha.tvCidade.text = entrevista.cidade
        ficha.tvDataHora.text = entrevista.dataHora
        ficha.tvLocalizacao.text = entrevista.coordenada

        binding.listaEleitores.addView(ficha.root)
    }
}
