package com.openclassrooms.go4launch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static junit.framework.TestCase.assertNotNull;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.go4launch.model.User;
import com.openclassrooms.go4launch.ui.MainActivity;

import java.util.ArrayList;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    private FirebaseUser firebaseUser;
    private User mUser;
    private ArrayList likedRestaurants = new ArrayList();

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.openclassrooms.go4launch", appContext.getPackageName());
    }

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        assertNotNull(firebaseUser);
        mUser = new User("uid", "name", "email", "urlPicture", "", "", likedRestaurants);
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    // Show MapFragment
    @Test
    public void showMapFragment() {
        onView(withId(R.id.map_view)).perform(click());
        onView(withId(R.id.map_view)).check(matches(isDisplayed()));
    }

    // Show ListFragment
    @Test
    public void showListFragment() {
        onView(withId(R.id.list_view)).perform(click());
        onView(withId(R.id.list_view)).check(matches(isDisplayed()));
    }

    // Show WorkmatesFragment
    @Test
    public void showWorkmatesFragment() {
        onView(withId(R.id.workmates)).perform(click());
        onView(withId(R.id.workmates)).check(matches(isDisplayed()));
    }




}