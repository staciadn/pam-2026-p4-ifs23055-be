package org.delcom.dao

import org.delcom.tables.DongengTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID


class DongengDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, DongengDAO>(DongengTable)

    var judul by DongengTable.judul
    var pathGambar by DongengTable.pathGambar
    var sinopsis by DongengTable.sinopsis
    var pesan by DongengTable.pesan
    var asal by DongengTable.asal
    var createdAt by DongengTable.createdAt
    var updatedAt by DongengTable.updatedAt
}