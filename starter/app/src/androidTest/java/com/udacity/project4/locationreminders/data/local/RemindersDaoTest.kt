package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        // GIVEN - Insert a reminder.
        val reminder = ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0)
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the task by id from the database.
        val loaded = database.reminderDao().getReminderById(reminder.id)

        // THEN - The loaded data contains the expected values.
        Assert.assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.location, `is`(reminder.location))
        assertThat(loaded.longitude, `is`(reminder.longitude))
        assertThat(loaded.latitude, `is`(reminder.latitude))
    }

    @Test
    fun insertReminderAndGetAll() = runBlockingTest {
        // GIVEN - Insert a reminder.
        val reminder = ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0)
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the task by id from the database.
        val loaded = database.reminderDao().getReminders()

        // THEN - The loaded data contains the expected values.
        assertThat(loaded[0].id, `is`(reminder.id))
        assertThat(loaded[0].title, `is`(reminder.title))
        assertThat(loaded[0].description, `is`(reminder.description))
        assertThat(loaded[0].location, `is`(reminder.location))
        assertThat(loaded[0].longitude, `is`(reminder.longitude))
        assertThat(loaded[0].latitude, `is`(reminder.latitude))
    }

    @Test
    fun insertReminderAndDeleteAll() = runBlockingTest {
        // GIVEN - Insert a reminder.
        val reminder = ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0)
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the task by id from the database.
        val loaded = database.reminderDao().deleteAllReminders()

        // THEN - The loaded data contains the expected values.
        val retriveed = database.reminderDao().getReminders()
        Assert.assertThat(retriveed.isEmpty(), `is`(true))
    }

    @Test
    fun retriveNotFoundReminder() = runBlockingTest {
        // WHEN - Get the task by id from the database.
        val loaded = database.reminderDao().getReminderById(UUID.randomUUID().toString())

        // THEN - The loaded data contains the expected values.
        Assert.assertNull(loaded)
    }
}