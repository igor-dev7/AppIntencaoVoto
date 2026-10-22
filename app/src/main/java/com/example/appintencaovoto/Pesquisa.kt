package com.example.appintencaovoto

import android.content.Context

/** Uma opção da pesquisa com o total de votos que recebeu. */
data class Resultado(
    val nome: String,
    val partido: String?,
    val foto: Int?,
    val votos: Int
)

/**
 * Guarda as entrevistas e a contagem de votos no próprio aparelho.
 * É daqui que a tela de ranking tira os números.
 */
object Pesquisa {

    private const val ARQUIVO = "entrevistas"
    private const val PREFIXO_VOTO = "voto_"

    /** Salva a entrevista e soma um voto para a opção escolhida. */
    fun registrar(contexto: Context, chave: String, registro: String, voto: String) {
        val arquivo = contexto.getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE)
        arquivo.edit()
            .putString("entrevista_$chave", registro)
            .putInt(PREFIXO_VOTO + voto, votos(contexto, voto) + 1)
            .apply()
    }

    fun votos(contexto: Context, opcao: String): Int =
        contexto.getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE)
            .getInt(PREFIXO_VOTO + opcao, 0)

    /**
     * Todas as opções com o total de cada uma, da mais votada para a menos.
     * Empate fica na ordem em que a opção aparece na pesquisa.
     */
    fun ranking(contexto: Context, outras: List<String>): List<Resultado> {
        val candidatos = CANDIDATOS.map {
            Resultado(it.nome, it.partido, it.foto, votos(contexto, it.nome))
        }
        val demais = outras.map {
            Resultado(it, null, null, votos(contexto, it))
        }
        return (candidatos + demais).sortedByDescending { it.votos }
    }

    /** Quantas entrevistas já foram registradas. */
    fun totalDeVotos(contexto: Context, outras: List<String>): Int =
        ranking(contexto, outras).sumOf { it.votos }
}
