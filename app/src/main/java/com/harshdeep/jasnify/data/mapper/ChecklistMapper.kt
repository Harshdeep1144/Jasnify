package com.harshdeep.jasnify.data.mapper

import com.harshdeep.jasnify.data.local.ChecklistEntity
import com.harshdeep.jasnify.domain.model.Checklist

// Instead of using the database entity everywhere in app, convert it to a domain model.
fun ChecklistEntity.toChecklist(): Checklist {
    return Checklist(
        id = id,
        ownerId = ownerId,
        eventId = eventId,
        title = title,
        dateTime = dateTime,
        items = items,
        bgColorHex = bgColorHex,
        pinned = pinned,
        archived = archived,
        lastUpdated = lastUpdated,
        createdAt = createdAt
    )
}

fun Checklist.toChecklistEntity(): ChecklistEntity {
    return ChecklistEntity(
        id = id,
        ownerId = ownerId,
        eventId = eventId,
        title = title,
        dateTime = dateTime,
        items = items,
        bgColorHex = bgColorHex,
        pinned = pinned,
        archived = archived,
        lastUpdated = lastUpdated,
        createdAt = createdAt
    )
}
