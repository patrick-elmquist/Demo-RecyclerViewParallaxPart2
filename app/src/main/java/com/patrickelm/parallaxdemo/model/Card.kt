package com.patrickelm.parallaxdemo.model

data class Card(val city: String, val message: String, val country: String, val imageUrl: String) {
    companion object {
        val MOCKED_ITEMS = listOf(
            Card(
                "New York",
                "The city that never sleeps",
                "United States",
                "https://images.unsplash.com/photo-1440330033336-7dcff4630cef?ixlib=rb-1.2.1&auto=format&fit=crop&w=1271&q=80"
            ),
            Card(
                "Barcelona",
                "A relaxed afternoon",
                "Spain",
                "https://images.unsplash.com/photo-1495562569060-2eec283d3391?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1350&q=80"
            ),
            Card(
                "Santorini",
                "Beautiful sunset",
                "Greece",
                "https://images.unsplash.com/photo-1585767928941-f71ce7702b89?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1352&q=80"
            )
        )
    }
}
