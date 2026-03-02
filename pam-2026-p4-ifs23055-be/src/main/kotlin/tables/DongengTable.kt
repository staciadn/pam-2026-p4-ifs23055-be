package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object DongengTable : UUIDTable("dongeng") {
    val judul = varchar("judul", 100)
    val pathGambar = varchar("path_gambar", 255)
    val sinopsis = text("sinopsis")
    val pesan = text("pesan")
    val asal = varchar("asal", 100)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}
