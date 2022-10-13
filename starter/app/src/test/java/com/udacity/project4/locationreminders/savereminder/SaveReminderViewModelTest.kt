package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @After
    fun afterTest() {
        stopKoin()
    }

    @Test
    fun check_loading() {
        fakeDataSource = FakeDataSource()
        saveReminderViewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.validateAndSaveReminder(
            ReminderDataItem(
                "Title1",
                "Description1",
                "Location1",
                0.0,
                0.0
            )
        )
        Assert.assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )
    }

    @Test
    fun shouldReturnErrorLocationNull() {
        fakeDataSource = FakeDataSource(null)
        saveReminderViewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        saveReminderViewModel.validateAndSaveReminder(
            ReminderDataItem(
                "Title1",
                "Description1",
                null,
                0.0,
                0.0
            )
        )
        Assert.assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), CoreMatchers.`is`(R.string.err_select_location))
    }

    @Test
    fun shouldReturnErrorTitleNull() {
        fakeDataSource = FakeDataSource(null)
        saveReminderViewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        saveReminderViewModel.validateAndSaveReminder(
            ReminderDataItem(
                null,
                "Description1",
                "Location1",
                0.0,
                0.0
            )
        )
        Assert.assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), CoreMatchers.`is`(R.string.err_enter_title))
    }
}