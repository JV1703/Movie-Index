package com.example.movieindex.test.auth

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.example.movieindex.R
import com.example.movieindex.core.common.EspressoIdlingResource
import com.example.movieindex.feature.auth.AuthViewModel
import com.example.movieindex.feature.auth.ui.login.LoginFragment
import com.example.movieindex.launchFragmentInHiltContainer
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.endsWith
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@MediumTest
@HiltAndroidTest
class LoginFragmentTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mainDispatcherRule = MainCoroutineRule()

    @Inject
    lateinit var navController: TestNavHostController

    @Inject
    lateinit var testDataFactory: TestDataFactory

    //    private val mockWebServer: MockWebServer = MockWebServer()
//    @Inject
//    lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        launchFragmentInHiltContainer<LoginFragment>(fragmentArgs = bundleOf()) {
            navController.setGraph(R.navigation.root_nav_graph)
            Navigation.setViewNavController(requireView(), navController)

        }
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun clickForgetPassword_navigateToRegisterFragmentWithCorrectNavArgs() {
        onView(withId(R.id.forget_password)).perform(click())
        assertEquals((navController.currentDestination?.id), R.id.registerFragment)
        assertTrue(navController.backStack.last().arguments?.toString()
            ?.contains(AuthViewModel.AuthNavigationArgs.ForgetPassword.name) ?: false)
    }

    @Test
    fun clickCreateAnAccount_navigateToRegisterFragmentWithCorrectNavArgs() {
        onView(withId(R.id.register_cta_highlight)).perform(click())
        assertEquals((navController.currentDestination?.id), R.id.registerFragment)
        assertTrue(navController.backStack.last().arguments?.toString()
            ?.contains(AuthViewModel.AuthNavigationArgs.Register.name) ?: false)
    }

    @Test
    fun emptyFields_noNavigationOccurs() {
        onView(withId(R.id.login_button)).perform(click())
        val destination = navController.currentDestination?.id
        assertEquals(R.id.loginFragment, destination)
    }

    @Test
    fun emptyUsernameField_noNavigationOccurs() {
        onView(withId(R.id.password_tiet)).perform(typeText("Random"))

        onView(withId(R.id.login_button)).perform(click())
        val currentDestination = navController.currentDestination?.id
        assertEquals(R.id.loginFragment, currentDestination)
    }

    @Test
    fun emptyPasswordField_noNavigationOccurs() {
        onView(withId(R.id.username_tiet)).perform(typeText("Random"))

        onView(withId(R.id.login_button)).perform(click())
        val currentDestination = navController.currentDestination?.id
        assertEquals(R.id.loginFragment, currentDestination)
    }

    @Test
    fun login_fails() {

        onView(withId(R.id.password_tiet)).perform(typeText("Random Password")).perform(
            pressImeActionButton())

        onView(withId(R.id.username_tiet)).perform(typeText("Random Username")).perform(
            pressImeActionButton())

        onView(withId(R.id.login_button)).perform(click())
        val currentDestination = navController.currentDestination?.id
        assertEquals(R.id.loginFragment, currentDestination)

    }

    @Test
    fun login_success() = runTest {

        onView(withId(R.id.password_tiet)).perform(typeText("Random Password")).perform(
            pressImeActionButton())

        onView(withId(R.id.username_tiet)).perform(typeText("Random Username")).perform(
            pressImeActionButton())

        onView(withId(R.id.login_button)).perform(click())

        val currentDestination = navController.currentDestination?.displayName
        advanceUntilIdle()
        assertEquals(R.id.homeFragment, currentDestination)

    }

    fun isEditTextInLayout(parentViewId: Int): Matcher<View> {
        return allOf(
            isDescendantOfA(withId(parentViewId)),
            withClassName(endsWith("EditText"))
        )
    }

}