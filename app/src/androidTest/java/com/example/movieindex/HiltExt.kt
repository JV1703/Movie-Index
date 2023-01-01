package com.example.movieindex

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.core.util.Preconditions
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.testing.R.style.FragmentScenarioEmptyFragmentActivityTheme
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi

//inline fun <reified T : Fragment> launchFragmentInHiltContainer(
//    fragmentArgs: Bundle? = null,
//    @StyleRes themeResId: Int = FragmentScenarioEmptyFragmentActivityTheme,
//    navHostController: NavHostController? = null,
//    fragmentFactory: FragmentFactory? = null,
//    crossinline action: T.() -> Unit = {}
//): ActivityScenario<HiltTestActivity>? {
//    val startActivityIntent = Intent.makeMainActivity(
//        ComponentName(
//            ApplicationProvider.getApplicationContext(),
//            HiltTestActivity::class.java
//        )
//    )
//        .putExtra("androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",
//            themeResId)
//
//    return ActivityScenario.launch<HiltTestActivity>(startActivityIntent).onActivity { activity ->
//        fragmentFactory?.let {
//            activity.supportFragmentManager.fragmentFactory = it
//        }
//        val fragment: Fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
//            Preconditions.checkNotNull(T::class.java.classLoader),
//            T::class.java.name
//        )
//
//        fragment.arguments = fragmentArgs
//
//        navHostController?.let {
//            fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
//                if (viewLifecycleOwner != null) {
//                    Navigation.setViewNavController(fragment.requireView(), it)
//                }
//            }
//        }
//
//        activity.supportFragmentManager
//            .beginTransaction()
//            .add(android.R.id.content, fragment, "")
//            .commitNow()
//
//        (fragment as T).action()
//    }
//}

@ExperimentalCoroutinesApi
inline fun <reified T : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    themeResId: Int = FragmentScenarioEmptyFragmentActivityTheme,
    fragmentFactory: FragmentFactory? = null,
    crossinline action: T.() -> Unit = {},
) {
    val mainActivityIntent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            HiltTestActivity::class.java
        )
    )
        .putExtra("androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",
            themeResId)

    ActivityScenario.launch<HiltTestActivity>(mainActivityIntent).onActivity { activity ->
        fragmentFactory?.let {
            activity.supportFragmentManager.fragmentFactory = it
        }
        val fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            Preconditions.checkNotNull(T::class.java.classLoader),
            T::class.java.name
        )
        fragment.arguments = fragmentArgs

        activity.supportFragmentManager.beginTransaction()
            .add(android.R.id.content, fragment, "")
            .commitNow()

        (fragment as T).action()
    }

}