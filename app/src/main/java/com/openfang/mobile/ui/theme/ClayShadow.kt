package com.openfang.mobile.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.dp

/**
 * Configuration for the Pastel Clay claymorphism shadow.
 *
 * Claymorphism = outer diffuse drop shadow + inset highlight (top-left) +
 * inset shadow (bottom-right). All values are in dp unless noted.
 */
object ClayShadowConfig {
    /**
     * Outer diffuse drop shadow elevation (simulated as an offset rect), in dp.
     * Drives the magnitude of the outer shadow offset in both [clayElevation] and [clayPressed].
     */
    val outerElevation = 6f

    /** Outer diffuse drop shadow color (soft black, translucent). */
    val outerColor = Color(0x1A000000)

    /** Inset highlight color — light rim at the top-left edge. */
    val insetHighlightColor = Color(0x45FFFFFF)

    /** Inset shadow color — dark rim at the bottom-right edge. */
    val insetShadowColor = Color(0x26000000)

    /** Inset rim width, in dp. */
    val insetWidth = 1f

    /** Corner radius of the clay container surface, in dp. */
    val containerCorner = 16f
}

/**
 * Outer shadow offset (dp) for the elevated state — derived from [ClayShadowConfig.outerElevation].
 */
internal val clayOuterOffsetDp: Float = ClayShadowConfig.outerElevation

/**
 * Outer shadow offset (dp) for the pressed state — reduced versus the elevated state.
 */
internal val clayPressedOuterOffsetDp: Float = ClayShadowConfig.outerElevation * 0.4f

/**
 * Claymorphism elevation modifier.
 *
 * Applies an outer diffuse drop shadow (offset translucent rect) plus an inset
 * bevel (light top-left highlight fading into a dark bottom-right shadow) so
 * the surface looks like soft, pillowy clay.
 */
fun Modifier.clayElevation(): Modifier = this.drawBehind {
    val shadowOffsetPx = clayOuterOffsetDp * this.density
    val insetPx = ClayShadowConfig.insetWidth * this.density

    // 1. Outer diffuse drop shadow: offset translucent rect behind the surface.
    this.drawRect(
        color = ClayShadowConfig.outerColor,
        topLeft = Offset(shadowOffsetPx, shadowOffsetPx),
        size = this.size,
    )

    // 2. Inset bevel: light at top-left, dark at bottom-right.
    this.inset(insetPx) {
        this.drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    ClayShadowConfig.insetHighlightColor,
                    ClayShadowConfig.insetShadowColor,
                ),
                start = Offset.Zero,
                end = Offset(this.size.width, this.size.height),
            ),
        )
    }
}

/**
 * Compressed shadow state for pressed interactions.
 *
 * Reduces the outer drop shadow so the surface appears pressed down into the
 * background. The inset bevel is kept subtle.
 */
fun Modifier.clayPressed(): Modifier = this.drawBehind {
    val shadowOffsetPx = clayPressedOuterOffsetDp * this.density
    val insetPx = ClayShadowConfig.insetWidth * this.density

    // 1. Outer drop shadow — shallower (smaller offset) when pressed.
    this.drawRect(
        color = ClayShadowConfig.outerColor,
        topLeft = Offset(shadowOffsetPx, shadowOffsetPx),
        size = this.size,
    )

    // 2. Inset bevel — softer when pressed.
    this.inset(insetPx) {
        this.drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    ClayShadowConfig.insetHighlightColor,
                    ClayShadowConfig.insetShadowColor,
                ),
                start = Offset.Zero,
                end = Offset(this.size.width, this.size.height),
            ),
        )
    }
}
