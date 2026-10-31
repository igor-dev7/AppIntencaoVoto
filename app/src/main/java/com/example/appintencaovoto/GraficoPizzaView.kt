package com.example.appintencaovoto

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/** Uma fatia da pizza: quanto vale e de que cor é. */
data class Fatia(val valor: Int, val cor: Int)

/**
 * Gráfico de pizza desenhado no próprio Canvas, sem biblioteca externa.
 * Cada fatia ocupa o ângulo proporcional ao que recebeu do total.
 */
class GraficoPizzaView @JvmOverloads constructor(
    context: Context,
    atributos: AttributeSet? = null
) : View(context, atributos) {

    private val pincel = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val area = RectF()
    private var fatias = listOf<Fatia>()

    fun mostrar(novas: List<Fatia>) {
        fatias = novas.filter { it.valor > 0 }
        invalidate()
    }

    override fun onMeasure(larguraSugerida: Int, alturaSugerida: Int) {
        // A pizza é redonda: a altura acompanha a largura
        val lado = MeasureSpec.getSize(larguraSugerida)
        setMeasuredDimension(lado, lado)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val total = fatias.sumOf { it.valor }
        if (total == 0) return

        val lado = minOf(width, height).toFloat()
        val margem = lado * 0.04f
        area.set(margem, margem, lado - margem, lado - margem)

        var inicio = -90f // começa no topo, como num relógio
        for (fatia in fatias) {
            val angulo = 360f * fatia.valor / total
            pincel.color = fatia.cor
            canvas.drawArc(area, inicio, angulo, true, pincel)
            inicio += angulo
        }
    }
}
