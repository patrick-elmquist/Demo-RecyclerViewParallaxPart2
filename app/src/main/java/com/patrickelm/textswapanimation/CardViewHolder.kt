package com.patrickelm.textswapanimation

import android.animation.FloatEvaluator
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.patrickelm.textswapanimation.databinding.CardItemBinding
import com.patrickelm.textswapanimation.model.Card
import kotlin.math.abs

private const val IMAGE_PARALLAX_FACTOR = 3f
private const val IMAGE_MIN_ALPHA = 0.85f
private const val IMAGE_MAX_ALPHA = 1.0f

private const val TITLE_LEFT_PARALLAX_FACTOR = 0.35f
private const val MESSAGE_LEFT_PARALLAX_FACTOR = 0.2f
private const val EXTRA_LEFT_PARALLAX_FACTOR = 0.35f

private const val TITLE_RIGHT_PARALLAX_FACTOR = 0.3f
private const val MESSAGE_RIGHT_PARALLAX_FACTOR = 1.5f
private const val EXTRA_RIGHT_PARALLAX_FACTOR = 1.0f

class CardViewHolder(private val binding: CardItemBinding) : RecyclerView.ViewHolder(binding.root) {
    private val linearEvaluator = FloatEvaluator()
    private val linearToSlowInterpolator = LinearOutSlowInInterpolator()

    /**
     * Scroll offset from when the card is in the center of the view
     */
    var parallaxOffset: Float = 0f
        set(v) {
            // Make sure that the value is limited to the range of [-1,1]
            field = v.coerceIn(-1f, 1f).also { applyParallax(it) }
        }

    fun bind(model: Card) = binding.apply {
        image.load(model.imageUrl)
        image.doOnPreDraw { img ->
            val parent = (itemView.parent as? View)?.width ?: 0
            val width = img.width.toFloat()
            val scale = (parent - (1f - 1f / IMAGE_PARALLAX_FACTOR) * (parent - width) / 2f) / width
            // Make the image the same width as the parent
            // We need the image to scale for the parallax but we want to scale it as little
            // as possible so that as much of it is visible within the clip bounds
            img.scaleX = scale
            img.scaleY = scale
        }
        title.text = model.city
        message.text = model.message
        extra.text = model.country
    }

    private fun applyParallax(offset: Float) = binding.run {
        val direction = if (offset < 0f) -1f else 1f
        val absoluteValue = abs(offset)
        val width = card.width

        // Add separate parallax effects for each text field and for each direction
        if (direction == -1f) { // moving left
            val fraction = -linearToSlowInterpolator.getInterpolation(absoluteValue)
            title.translationX = fraction * width * TITLE_LEFT_PARALLAX_FACTOR
            message.translationX = fraction * width * MESSAGE_LEFT_PARALLAX_FACTOR
            extra.translationX = fraction * width * EXTRA_LEFT_PARALLAX_FACTOR
        } else { // moving right
            // Invert the interpolator to have the translation slow down when
            // approaching offset = 0
            val fraction = 1f - linearToSlowInterpolator.getInterpolation(1f - absoluteValue)
            title.translationX = fraction * width * TITLE_RIGHT_PARALLAX_FACTOR
            message.translationX = fraction * width * MESSAGE_RIGHT_PARALLAX_FACTOR
            extra.translationX = fraction * width * EXTRA_RIGHT_PARALLAX_FACTOR
        }

        image.translationX = -(absoluteValue * direction * width / IMAGE_PARALLAX_FACTOR)
        image.alpha = linearEvaluator.evaluate(absoluteValue, IMAGE_MAX_ALPHA, IMAGE_MIN_ALPHA)
    }
}

