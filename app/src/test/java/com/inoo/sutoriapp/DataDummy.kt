package com.inoo.sutoriapp

import com.inoo.sutoriapp.data.remote.response.story.ListStoryItem

class DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..30) {
            val story = ListStoryItem(
                id = i.toString(),
                name = "Story${i}",
                description = "StoryDesc${i}",
                createdAt = "2024-10-${i}",
                photoUrl = "https://xyz.serv00.net/sutoriapp-testing/sampleimage.webp",
                lat = 1.23,
                lon = 4.56
            )
            items.add(story)
        }
        return items
    }
}