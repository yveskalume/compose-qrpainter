package com.yveskalume.compose.qrpainter

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun rememberQrBitmapPainter(
    content: String,
    size: Dp = 150.dp,
    padding: Dp = 0.dp
): BitmapPainter {

    check(content.isNotEmpty()) { "Content must not be empty" }
    check(size >= 0.dp) { "Size must be positive" }
    check(padding >= 0.dp) { "Padding must be positive" }

    val density = LocalDensity.current
    val sizePx = with(density) { size.roundToPx() }
    val paddingPx = with(density) { padding.roundToPx() }

    val bitmapState = remember {
        mutableStateOf<Bitmap?>(null)
    }

    // Use dependency on 'content' to re-trigger the effect when content changes
    LaunchedEffect(content) {
        val bitmap = generateQrBitmap(content, sizePx, paddingPx)
        bitmapState.value = bitmap
    }

    val bitmap = bitmapState.value ?: createDefaultBitmap(sizePx)

    return remember(bitmap) {
        BitmapPainter(bitmap.asImageBitmap())
    }
}

private suspend fun generateQrBitmap(
    content: String,
    sizePx: Int,
    paddingPx: Int
): Bitmap? = withContext(Dispatchers.IO) {
    val qrCodeWriter = QRCodeWriter()

    val encodeHints = mutableMapOf<EncodeHintType, Any?>()
        .apply {
            this[EncodeHintType.MARGIN] = paddingPx
        }

    try {
        val bitmapMatrix = qrCodeWriter.encode(
            content, BarcodeFormat.QR_CODE,
            sizePx, sizePx, encodeHints
        )

        val matrixWidth = bitmapMatrix.width
        val matrixHeight = bitmapMatrix.height

        val colors = IntArray(matrixWidth * matrixHeight) { index ->
            val x = index % matrixWidth
            val y = index / matrixWidth
            val shouldColorPixel = bitmapMatrix.get(x, y)
            if (shouldColorPixel) Color.BLACK else Color.WHITE
        }

        Bitmap.createBitmap(colors, matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888)
    } catch (ex: WriterException) {
        null
    }
}

private fun createDefaultBitmap(sizePx: Int): Bitmap {
    return Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888).apply {
        eraseColor(Color.TRANSPARENT)
    }
}