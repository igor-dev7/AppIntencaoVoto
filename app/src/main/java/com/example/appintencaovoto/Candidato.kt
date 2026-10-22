package com.example.appintencaovoto

import androidx.annotation.DrawableRes

/** Um candidato da pesquisa estimulada. */
data class Candidato(
    val nome: String,
    val partido: String,
    @DrawableRes val foto: Int
)

/**
 * Candidatos que aparecem na tela. Esta lista é a única fonte dos dados:
 * para incluir, tirar ou trocar alguém, basta mexer aqui.
 */
val CANDIDATOS = listOf(
    Candidato("Justin Bieber", "Partido Comum", R.drawable.candidato_1),
    Candidato("Ayrton Senna", "Partido Trabalhista", R.drawable.candidato_2),
    Candidato("Tony Montana", "Partido Republicano", R.drawable.candidato_3),
    Candidato("Mike Tyson", "Partido Social Democrata", R.drawable.candidato_4),
    Candidato("Tupac Shakur", "Partido Feliz", R.drawable.candidato_5)
)
