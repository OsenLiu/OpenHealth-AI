package com.osen.sanoai.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * An organic 'blob' shape for cards.
 */
class OrganicBlobShape(
    private val seed: Int = 0
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(size.width * 0.15f, size.height * 0.05f)
            cubicTo(
                size.width * 0.4f, 0f,
                size.width * 0.8f, size.height * 0.1f,
                size.width * 0.95f, size.height * 0.25f
            )
            cubicTo(
                size.width * 1.1f, size.height * 0.5f,
                size.width * 0.9f, size.height * 0.85f,
                size.width * 0.75f, size.height * 0.95f
            )
            cubicTo(
                size.width * 0.5f, size.height * 1.05f,
                size.width * 0.1f, size.height * 0.9f,
                size.width * 0.05f, size.height * 0.6f
            )
            cubicTo(
                0f, size.height * 0.3f,
                size.width * 0.05f, size.height * 0.1f,
                size.width * 0.15f, size.height * 0.05f
            )
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * A speech bubble shape for AI advice.
 */
class SpeechBubbleShape(
    private val cornerRadius: Float = 48f,
    private val tipWidth: Float = 32f,
    private val tipHeight: Float = 24f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            // Main bubble body (rounded rect)
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height - tipHeight,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius)
                )
            )
            
            // Tail at bottom left (matching VitaMind reference)
            moveTo(40f, size.height - tipHeight)
            lineTo(20f, size.height)
            lineTo(80f, size.height - tipHeight)
            close()
        }
        return Outline.Generic(path)
    }
}
