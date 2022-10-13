package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RemindersListViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var reminderList: MutableList<ReminderDTO>;

    @Before
    fun setupViewModel() {
        val reminder1 = ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0)
        val reminder2 = ReminderDTO("Title2", "Description2", "Location2", 10.0, 0.10)
        val reminder3 = ReminderDTO("Title3", "Description3", "Location3", 20.0, 0.20)
        val reminder4 = ReminderDTO("Title4", "Description4", "Location4", 30.0, 0.30)
        val reminder5 = ReminderDTO("Title5", "Description5", "Location5", 40.0, 0.40)
        reminderList = mutableListOf(reminder1, reminder2, reminder3, reminder4, reminder5)
    }

    @After
    fun afterTest() {
        stopKoin()
    }

    @Test
    fun check_loading() {
        fakeDataSource = FakeDataSource(reminderList)
        remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()
        Assert.assertThat(
            remindersListViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )
    }

    @Test
    fun shouldReturnError() {
        fakeDataSource = FakeDataSource(null)
        remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        remindersListViewModel.loadReminders()
        Assert.assertThat(
            remindersListViewModel.showSnackBar.getOrAwaitValue(),
            CoreMatchers.`is`("No Data")
        )
    }

}