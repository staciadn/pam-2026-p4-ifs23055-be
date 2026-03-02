package org.delcom.repositories

import org.delcom.dao.DongengDAO
import org.delcom.entities.Dongeng
import org.delcom.helpers.DongengDAOtoModel
import org.delcom.helpers.daoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.DongengTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class DongengRepository : IDongengRepository {
    override suspend fun getDongengs(search: String): List<Dongeng> = suspendTransaction {
        if (search.isBlank()) {
            DongengDAO.all()
                .orderBy(DongengTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::DongengDAOtoModel)
        } else {
            val keyword = "%${search.lowercase()}%"

            DongengDAO
                .find {
                    DongengTable.judul.lowerCase() like keyword
                }
                .orderBy(DongengTable.judul to SortOrder.ASC)
                .limit(20)
                .map(::DongengDAOtoModel)
        }
    }

    override suspend fun getDongengById(id: String): Dongeng? = suspendTransaction {
        DongengDAO
            .find { (DongengTable.id eq UUID.fromString(id)) }
            .limit(1)
            .map(::DongengDAOtoModel)
            .firstOrNull()
    }

    override suspend fun getDongengByJudul(judul: String): Dongeng? = suspendTransaction {
        DongengDAO
            .find { (DongengTable.judul eq judul) }
            .limit(1)
            .map(::DongengDAOtoModel)
            .firstOrNull()
    }

    override suspend fun addDongeng(dongeng: Dongeng): String = suspendTransaction {
        val dongengDAO = DongengDAO.new {
            judul = dongeng.judul
            pathGambar = dongeng.pathGambar
            sinopsis = dongeng.sinopsis
            pesan = dongeng.pesan
            asal = dongeng.asal
            createdAt = dongeng.createdAt
            updatedAt = dongeng.updatedAt
        }

        dongengDAO.id.value.toString()
    }

    override suspend fun updateDongeng(id: String, newDongeng: Dongeng): Boolean = suspendTransaction {
        val dongengDAO = DongengDAO
            .find { DongengTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (dongengDAO != null) {
            dongengDAO.judul = newDongeng.judul
            dongengDAO.pathGambar = newDongeng.pathGambar
            dongengDAO.sinopsis = newDongeng.sinopsis
            dongengDAO.pesan = newDongeng.pesan
            dongengDAO.asal = newDongeng.asal
            dongengDAO.updatedAt = newDongeng.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun removeDongeng(id: String): Boolean = suspendTransaction {
        val rowsDeleted = DongengTable.deleteWhere {
            DongengTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}