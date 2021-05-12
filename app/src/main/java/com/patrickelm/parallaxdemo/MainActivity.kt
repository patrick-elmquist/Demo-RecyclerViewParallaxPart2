package com.patrickelm.parallaxdemo

import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.patrickelm.parallaxdemo.databinding.ActivityMainBinding
import com.patrickelm.parallaxdemo.model.Card
import com.patrickelm.parallaxdemo.util.HorizontalSpaceItemDecoration
import com.patrickelm.parallaxdemo.util.SlowScrollingLinearLayoutManager
import com.patrickelm.parallaxdemo.util.SlowScrollingPagerSnapHelper

class MainActivity : Activity() {
    private val viewBounds = Rect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            recyclerView.init(withParallax = true)
            recyclerView2.init(withParallax = false)
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
