package com.example.appintencaovoto

/**
 * Cores das fatias do gráfico, na ordem em que as opções aparecem.
 * Quando houver mais opções do que cores, a lista recomeça do início.
 */
val CORES_GRAFICO = intArrayOf(
    0xFF2D6FE0.toInt(), // azul
    0xFFD94A3D.toInt(), // vermelho
    0xFF2E9E58.toInt(), // verde
    0xFFE8A33D.toInt(), // amarelo
    0xFF7B52C7.toInt(), // roxo
    0xFF2AA6B8.toInt(), // azul-petróleo
    0xFFD9608C.toInt(), // rosa
    0xFF6B7280.toInt()  // cinza
)

/** A cor da fatia na posição informada. */
fun corDoGrafico(posicao: Int): Int = CORES_GRAFICO[posicao % CORES_GRAFICO.size]
