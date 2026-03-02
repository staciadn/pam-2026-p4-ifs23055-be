package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Dongeng(
    var id: String = UUID.randomUUID().toString(),
    var judul: String,
    var pathGambar: String,
    var sinopsis: String,
    var pesan: String,
    var asal: String,

    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)
