package org.delcom.repositories

import org.delcom.entities.Dongeng

interface IDongengRepository {
    suspend fun getDongengs(search: String): List<Dongeng>
    suspend fun getDongengById(id: String): Dongeng?
    suspend fun getDongengByJudul(judul: String): Dongeng?
    suspend fun addDongeng(dongeng: Dongeng): String
    suspend fun updateDongeng(id: String, newDongeng: Dongeng): Boolean
    suspend fun removeDongeng(id: String): Boolean
}