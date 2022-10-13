package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class ReminderListFragmentTest {
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var reminderListViewModel: RemindersListViewModel

    @Before
    fun init() {
        fakeDataSource = FakeDataSource()
        reminderListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        stopKoin()

        val myModule = module {
            single {
                reminderListViewModel
            }
        }
        startKoin {
            modules(listOf(myModule))
        }
    }

    @After
    fun afterTest() = runBlockingTest {
        stopKoin()
    }


    @Test
    fun whenAddRemindersExpectDataExist() = runBlockingTest {

        val reminder1 = ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0)

        fakeDataSource.saveReminder(reminder1)

        // GIVEN -
        val reminders = (fakeDataSource.getReminders() as? Result.Success)?.data

        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)

        onView(withText(reminders!!.get(0).title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withText(reminders!!.get(0).description))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withText("DESCRIPTIONssss")).check(ViewAssertions.doesNotExist())

        activityScenario.close()
    }

    @Test
    fun whenRemoveAllReminderExpectNoData() = runBlockingTest {
        fakeDataSource.deleteAllReminders()
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        onView(withText("No Data"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun clickAddTaskButton_navigateToAddEditFragment() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - Click on the "+" button
        onView(withId(R.id.addReminderFAB)).perform(click())

        // THEN - Verify that we navigate to the add screen
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())

        activityScenario.close()
    }
}