package com.example.appintencaovoto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.appintencaovoto.databinding.ActivityAdminBinding

/** Tela do admin: quantas entrevistas existem e o que dá para fazer com elas. */
class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.main)

        binding.btEleitores.setOnClickListener {
            startActivity(Intent(this, EleitoresActivity::class.java))
        }

        binding.btResultado.setOnClickListener {
            startActivity(Intent(this, RankingActivity::class.java))
        }

        binding.btLimpar.setOnClickListener { confirmarLimpeza() }
        binding.btSair.setOnClickListener { sairParaLogin() }
        binding.btFinalizar.setOnClickListener { finishAndRemoveTask() }
    }

    /** O total é relido a cada volta: entrevistas podem ter entrado nesse meio-tempo. */
    override fun onResume() {
        super.onResume()
        val total = Pesquisa.total(this)
        binding.tvTotal.text = getString(R.string.admin_total, total)
        binding.btEleitores.isEnabled = total > 0
        binding.btResultado.isEnabled = total > 0
        binding.btLimpar.isEnabled = total > 0
    }

    /** Apagar é irreversível, então pergunta antes. */
    private fun confirmarLimpeza() {
        AlertDialog.Builder(this)
            .setTitle(R.string.admin_limpar)
            .setMessage(R.string.admin_limpar_pergunta)
            .setNegativeButton(R.string.admin_cancelar, null)
            .setPositiveButton(R.string.admin_limpar_confirmar) { _, _ -> limpar() }
            .show()
    }

    private fun limpar() {
        Pesquisa.limpar(this)
        Toast.makeText(this, R.string.admin_limpo, Toast.LENGTH_SHORT).show()
        onResume() // atualiza o total e desliga os botões
    }
}
