package com.example.appintencaovoto

import android.content.Context
import androidx.core.content.edit

/** Uma opção da pesquisa com o total de votos que recebeu. */
data class Resultado(
    val nome: String,
    val partido: String?,
    val foto: Int?,
    val votos: Int
)

/** Uma entrevista inteira, do jeito que o admin precisa ver. */
data class Entrevista(
    val nome: String,
    val celular: String,
    val espontanea: String,
    val voto: String,
    val problemas: String,
    val dataHora: String,
    val cidade: String,
    val endereco: String,
    val coordenada: String
)

/**
 * Guarda as entrevistas e a contagem de votos no próprio aparelho.
 * É daqui que as telas do admin tiram a lista de eleitores e os números.
 */
object Pesquisa {

    private const val ARQUIVO = "entrevistas"
    private const val PREFIXO_VOTO = "voto_"
    private const val PREFIXO_ENTREVISTA = "entrevista_"
    private const val TOTAL = "total"

    /** Separa os campos dentro de um registro. É um caractere que não se digita. */
    private const val SEPARADOR = "\u001F"

    private fun arquivo(contexto: Context) =
        contexto.getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE)

    /** Salva a entrevista e soma um voto para a opção escolhida. */
    fun registrar(contexto: Context, entrevista: Entrevista) {
        val indice = total(contexto)
        val campos = listOf(
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

        arquivo(contexto).edit {
            putString(PREFIXO_ENTREVISTA + indice, campos.joinToString(SEPARADOR))
            putInt(TOTAL, indice + 1)
            putInt(PREFIXO_VOTO + entrevista.voto, votos(contexto, entrevista.voto) + 1)
        }
    }

    /** Quantas entrevistas já foram registradas. */
    fun total(contexto: Context): Int = arquivo(contexto).getInt(TOTAL, 0)

    /** Todas as entrevistas, da mais recente para a mais antiga. */
    fun entrevistas(contexto: Context): List<Entrevista> {
        val guardadas = arquivo(contexto)
        val lista = mutableListOf<Entrevista>()

        for (indice in total(contexto) - 1 downTo 0) {
            val linha = guardadas.getString(PREFIXO_ENTREVISTA + indice, null) ?: continue
            val campos = linha.split(SEPARADOR)
            if (campos.size < 9) continue // registro de uma versão antiga do app

            lista.add(
                Entrevista(
                    nome = campos[0],
                    celular = campos[1],
                    espontanea = campos[2],
                    voto = campos[3],
                    problemas = campos[4],
                    dataHora = campos[5],
                    cidade = campos[6],
                    endereco = campos[7],
                    coordenada = campos[8]
                )
            )
        }
        return lista
    }

    fun votos(contexto: Context, opcao: String): Int =
        arquivo(contexto).getInt(PREFIXO_VOTO + opcao, 0)

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

    /** Apaga tudo: as entrevistas e a contagem de votos. */
    fun limpar(contexto: Context) {
        arquivo(contexto).edit { clear() }
    }
}
