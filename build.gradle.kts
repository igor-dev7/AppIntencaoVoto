// Arquivo de build da raiz: só declara os plugins que os módulos usam.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
