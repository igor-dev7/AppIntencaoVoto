package com.example.appintencaovoto

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Vai formatando o celular enquanto o usuário digita: (11) 95959-5949.
 * Aceita no máximo 11 dígitos e ignora o que não for número.
 */
class MascaraTelefone(private val campo: EditText) : TextWatcher {

    /** Evita que a própria escrita do campo dispare o watcher de novo. */
    private var escrevendo = false

    override fun afterTextChanged(texto: Editable?) {
        if (escrevendo) return

        val digitos = texto.toString().filter { it.isDigit() }.take(MAXIMO_DIGITOS)
        val formatado = formatar(digitos)

        escrevendo = true
        campo.setText(formatado)
        campo.setSelection(formatado.length) // cursor sempre no fim
        escrevendo = false
    }

    private fun formatar(digitos: String): String = when {
        digitos.isEmpty() -> ""
        digitos.length <= 2 -> "(${digitos}"
        digitos.length <= 6 -> "(${digitos.take(2)}) ${digitos.substring(2)}"
        digitos.length <= 10 ->
            "(${digitos.take(2)}) ${digitos.substring(2, 6)}-${digitos.substring(6)}"
        else ->
            "(${digitos.take(2)}) ${digitos.substring(2, 7)}-${digitos.substring(7)}"
    }

    override fun beforeTextChanged(texto: CharSequence?, inicio: Int, antes: Int, depois: Int) = Unit
    override fun onTextChanged(texto: CharSequence?, inicio: Int, antes: Int, depois: Int) = Unit

    companion object {
        private const val MAXIMO_DIGITOS = 11
    }
}
