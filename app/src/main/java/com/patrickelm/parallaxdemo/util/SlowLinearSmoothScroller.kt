package com.patrickelm.parallaxdemo.util

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

private const val SCROLL_SLOW_FACTOR = 3.0f
class SlowLinearSmoothScroller(
    context: Context,
    target: Int = RecyclerView.NO_POSITION
) : LinearSmoothScroller(context) {
    init {
        targetPosition = target
    }
    override fun calculateSpeedPerPixel(dm: DisplayMetrics?) =
        super.calculateSpeedPerPixel(dm) * SCROLL_SLOW_FACTOR
}

class SlowScrollingPagerSnapHelper(private val context: Context) : PagerSnapHelper() {
    override fun createScroller(lm: RecyclerView.LayoutManager) =
        SlowLinearSmoothScroller(context)
}

class SlowScrollingLinearLayoutManager(
    private val context: Context,
    orientation: Int
) : LinearLayoutManager(context, orientation, false) {
    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?,
        state: RecyclerView.State?,
        position: Int
    ) = startSmoothScroll(SlowLinearSmoothScroller(context, position))
}
