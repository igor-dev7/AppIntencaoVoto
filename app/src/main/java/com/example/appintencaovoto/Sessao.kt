package com.example.appintencaovoto

import android.app.Activity
import android.content.Intent

/**
 * Sai da sessão e volta para o login. Limpa a pilha para que o botão voltar
 * não devolva ninguém para dentro do app depois do logout.
 */
fun Activity.sairParaLogin() {
    val login = Intent(this, LoginActivity::class.java)
    login.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(login)
    finish()
}
