package com.example.appintencaovoto

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import java.util.Locale
import java.util.concurrent.Executors

/** Onde a entrevista foi feita: a cidade, o endereço e a coordenada. */
data class Local(val endereco: String, val coordenada: String, val cidade: String)

/**
 * Pede uma leitura nova ao GPS e traduz a coordenada em endereço.
 * Se o GPS não responder a tempo, tenta a rede e, por último, a leitura
 * guardada no aparelho. O resultado sempre volta na thread principal.
 */
object Localizacao {

    /** Quanto esperar por fonte antes de tentar a próxima. */
    private const val ESPERA_MS = 15_000L

    private val trabalho = Executors.newSingleThreadExecutor()
    private val principal = Handler(Looper.getMainLooper())

    fun obter(contexto: Context, aoTerminar: (Local) -> Unit) {
        if (semPermissao(contexto)) {
            aoTerminar(recado(contexto, R.string.local_sem_permissao))
            return
        }

        val gerente = contexto.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val fontes = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            .filter { gerente.isProviderEnabled(it) }

        if (fontes.isEmpty()) {
            aoTerminar(recado(contexto, R.string.local_desligado))
            return
        }

        tentar(contexto, gerente, fontes, 0, aoTerminar)
    }

    /** Tenta uma fonte por vez: primeiro o GPS, depois a rede. */
    private fun tentar(
        contexto: Context,
        gerente: LocationManager,
        fontes: List<String>,
        indice: Int,
        aoTerminar: (Local) -> Unit
    ) {
        if (indice >= fontes.size) {
            val guardada = ultimaGuardada(contexto, gerente, fontes)
            if (guardada == null) {
                aoTerminar(recado(contexto, R.string.local_indisponivel))
            } else {
                descrever(contexto, guardada, aoTerminar)
            }
            return
        }

        var respondeu = false
        val sinal = CancellationSignal()

        val desistir = Runnable {
            if (respondeu) return@Runnable
            respondeu = true
            sinal.cancel()
            tentar(contexto, gerente, fontes, indice + 1, aoTerminar)
        }
        principal.postDelayed(desistir, ESPERA_MS)

        try {
            LocationManagerCompat.getCurrentLocation(
                gerente, fontes[indice], sinal, trabalho
            ) { posicao ->
                principal.post {
                    if (respondeu) return@post
                    respondeu = true
                    principal.removeCallbacks(desistir)

                    if (posicao == null) {
                        tentar(contexto, gerente, fontes, indice + 1, aoTerminar)
                    } else {
                        descrever(contexto, posicao, aoTerminar)
                    }
                }
            }
        } catch (erro: SecurityException) {
            principal.removeCallbacks(desistir)
            aoTerminar(recado(contexto, R.string.local_sem_permissao))
        }
    }

    private fun ultimaGuardada(
        contexto: Context,
        gerente: LocationManager,
        fontes: List<String>
    ): Location? {
        if (semPermissao(contexto)) return null
        for (fonte in fontes) {
            try {
                gerente.getLastKnownLocation(fonte)?.let { return it }
            } catch (erro: SecurityException) {
                return null
            }
        }
        return null
    }

    /** Monta a coordenada e procura o endereço correspondente. */
    private fun descrever(contexto: Context, posicao: Location, aoTerminar: (Local) -> Unit) {
        val coordenada = String.format(
            Locale.getDefault(), "%.5f, %.5f", posicao.latitude, posicao.longitude
        )

        trabalho.execute {
            val achado = procurar(contexto, posicao)
            val endereco = achado?.first ?: contexto.getString(R.string.local_sem_endereco)
            val cidade = achado?.second ?: contexto.getString(R.string.local_sem_cidade)
            principal.post { aoTerminar(Local(endereco, coordenada, cidade)) }
        }
    }

    /**
     * Converte a coordenada em endereço e cidade. Precisa de rede; sem ela devolve null.
     * A versão sem callback está marcada como obsoleta no Android 13, mas é a
     * que funciona desde o minSdk do projeto, e aqui ela roda fora da thread principal.
     */
    @Suppress("DEPRECATION")
    private fun procurar(contexto: Context, posicao: Location): Pair<String, String>? {
        if (!Geocoder.isPresent()) return null

        return try {
            val achados = Geocoder(contexto, Locale.getDefault())
                .getFromLocation(posicao.latitude, posicao.longitude, 1)

            achados?.firstOrNull()?.let { achado ->
                val endereco = listOfNotNull(
                    achado.thoroughfare,
                    achado.subThoroughfare,
                    achado.subLocality,
                    achado.locality ?: achado.subAdminArea,
                    achado.adminArea
                ).joinToString(", ")

                val cidade = achado.locality
                    ?: achado.subAdminArea
                    ?: contexto.getString(R.string.local_sem_cidade)

                if (endereco.isBlank()) null else endereco to cidade
            }
        } catch (erro: Exception) {
            null // sem rede ou serviço fora do ar
        }
    }

    private fun semPermissao(contexto: Context): Boolean =
        ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED

    /** Quando não dá para ler a posição, o aviso ocupa o lugar do endereço. */
    private fun recado(contexto: Context, texto: Int) = Local(
        endereco = contexto.getString(texto),
        coordenada = contexto.getString(R.string.local_indisponivel),
        cidade = contexto.getString(R.string.local_sem_cidade)
    )
}
