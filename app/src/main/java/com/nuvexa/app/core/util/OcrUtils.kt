package com.nuvexa.app.core.util

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** Runs Google ML Kit's on-device (bundled-model, fully offline) text recognizer over
 * [bitmap] and returns the recognized text. */
suspend fun recognizeTextInImage(bitmap: Bitmap): String = suspendCancellableCoroutine { continuation ->
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val image = InputImage.fromBitmap(bitmap, 0)
    recognizer.process(image)
        .addOnSuccessListener { result -> continuation.resume(result.text) }
        .addOnFailureListener { error -> continuation.resumeWithException(error) }
}
