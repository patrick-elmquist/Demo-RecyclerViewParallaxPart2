package com.patrickelm.parallaxdemo

import android.graphics.Rect
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.patrickelm.parallaxdemo.databinding.ActivityMainBinding
import com.patrickelm.parallaxdemo.model.Card
import com.patrickelm.parallaxdemo.util.HorizontalSpaceItemDecoration
import com.patrickelm.parallaxdemo.util.SCROLL_SLOW_FACTOR
import com.patrickelm.parallaxdemo.util.SlowScrollingLinearLayoutManager
import com.patrickelm.parallaxdemo.util.SlowScrollingPagerSnapHelper
import kotlinx.coroutines.delay
import kotlin.math.floor

class MainActivity : AppCompatActivity() {
    private val viewBounds = Rect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            recyclerView1.init(withParallax = true)
            recyclerView2.init(withParallax = false)
            lifecycleScope.launchWhenResumed {
                val order = listOf(0, 1, 2, 1, 0)
                var i = 0
                while (true) {
                    val position = order[i % order.size]
                    recyclerView1.smoothScrollToPosition(position)
                    recyclerView2.smoothScrollToPosition(position)
                    i++
                    delay(floor(300 * SCROLL_SLOW_FACTOR).toLong())
                }
            }
        }
    }

    private fun RecyclerView.init(withParallax: Boolean) {
        adapter = CardAdapter(Card.MOCKED_ITEMS)
        layoutManager = SlowScrollingLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL)

        SlowScrollingPagerSnapHelper(context).attachToRecyclerView(this)

        val spacing = resources.getDimensionPixelOffset(R.dimen.spacing_card)
        addItemDecoration(HorizontalSpaceItemDecoration(spacing))

        if (withParallax) setupParallax()
    }

    private fun RecyclerView.setupParallax() {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = layoutManager as? LinearLayoutManager ?: return
                val scrollOffset = recyclerView.computeHorizontalScrollOffset()
                layoutManager.visiblePositions.forEach { position ->
                    val viewHolder = findViewHolderForAdapterPosition(position) as? CardViewHolder
                            ?: return@forEach

                    // Since we are using the scroll offset as a way to calculate the parallax
                    // factor, we also need to take the space between the cards into account,
                    // hence we are getting the decorated bounds and not just using the view width.
                    recyclerView.getDecoratedBoundsWithMargins(viewHolder.itemView, viewBounds)

                    // Worth noting is that this solution assumes that all the cards have the same
                    // size. If the card width would vary, a more complex solution would be needed.
                    val width = viewBounds.width().toFloat()
                    val viewStart = position * width
                    viewHolder.parallaxOffset = (viewStart - scrollOffset) / width
                }
            }
        })
    }

    private val LinearLayoutManager.visiblePositions: IntRange
        get() = (findFirstVisibleItemPosition()..findLastVisibleItemPosition())
}
