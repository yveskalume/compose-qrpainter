package com.yveskalume.compose.qrpainter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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

/**
 * Creates a [BitmapPainter] that draws a QR code for the given [content].
 * The [size] parameter defines the size of the QR code in dp.
 * The [padding] parameter defines the padding of the QR code in dp.
 */
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

@Composable
fun rememberQrBitmapPainter(
    content: String,
    logo: Bitmap,
    size: Dp = 600.dp,
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
        val bitmap = generateQrCodeWithOverlay(content, logo, sizePx, paddingPx)
        bitmapState.value = bitmap
    }

    val bitmap = bitmapState.value ?: createDefaultBitmap(sizePx)

    return remember(bitmap) {
        BitmapPainter(bitmap.asImageBitmap())
    }
}


/**
 * Generates a QR code bitmap for the given [content].
 * The [sizePx] parameter defines the size of the QR code in pixels.
 * The [paddingPx] parameter defines the padding of the QR code in pixels.
 * Returns null if the QR code could not be generated.
 * This function is suspendable and should be called from a coroutine is thread-safe.
 */
private suspend fun generateQrBitmap(
    content: String,
    sizePx: Int,
    paddingPx: Int
): Bitmap? = withContext(Dispatchers.IO) {
    val qrCodeWriter = QRCodeWriter()

    // Set the QR code margin to the given padding
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

/**
 * Creates a default bitmap with the given [sizePx].
 * The bitmap is transparent.
 * This is used as a fallback if the QR code could not be generated.
 * The bitmap is created on the UI thread.
 */
private fun createDefaultBitmap(sizePx: Int): Bitmap {
    return Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888).apply {
        eraseColor(Color.TRANSPARENT)
    }
}

/**
 * Generate qr with logo inside
 */

private fun generateQrCodeWithOverlay(
    qrCodeData: String,
    image: Bitmap,
    sizePx: Int,
    paddingPx: Int
): Bitmap? {

    // The Error Correction level H provide a QR Code that can be covered by 30%
    val encodeHints = HashMap<EncodeHintType?, Any?>().apply {
//        this[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        this[EncodeHintType.MARGIN] = paddingPx
    }

    val qrWriter = QRCodeWriter()

    return try {
        val bitmapMatrix = qrWriter
            .encode(qrCodeData, BarcodeFormat.QR_CODE, sizePx, sizePx, encodeHints)

        val matrixWidth = bitmapMatrix.width
        val matrixHeight = bitmapMatrix.height

        val qrCodeBitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888)

        val qrCodeCanvas = Canvas(qrCodeBitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.BLACK

        for (y in 0 until sizePx) {
            for (x in 0 until sizePx) {
                if (bitmapMatrix[x, y]) {
                    qrCodeCanvas.drawPoint(x.toFloat(), y.toFloat(), paint)
                }
            }
        }


        val scaleFactor = 8

        image.density = image.density * scaleFactor

        val scaledWidth = image.width / scaleFactor
        val scaledHeight = image.height / scaleFactor

        val xImage = (sizePx - scaledWidth) / 2f
        val yImage = (sizePx - scaledHeight) / 2f

        qrCodeCanvas.drawBitmap(image, xImage, yImage, null)
        qrCodeBitmap
    } catch (e: Exception) {
        null
    }
}




