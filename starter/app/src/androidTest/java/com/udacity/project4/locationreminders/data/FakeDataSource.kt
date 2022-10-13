package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeDataSource(var tasks: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        tasks?.let {
            return Result.Success(it)
        }
        return Result.Error("No Data")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        tasks?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val filteredList = tasks?.filter {
            it.id == id
        }
        if (!filteredList.isNullOrEmpty())
            return Result.Success(filteredList.get(0))
        return Result.Error("No Data")
    }

    override suspend fun deleteAllReminders() {
        tasks?.clear()
    }


}