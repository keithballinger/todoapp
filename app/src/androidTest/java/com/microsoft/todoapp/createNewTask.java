package com.microsoft.todoapp;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.allOf;
import android.support.test.runner.AndroidJUnit4;
import com.xamarin.testcloud.espresso.Factory;
import com.xamarin.testcloud.espresso.ReportHelper;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class createNewTask {
    @Rule
    public ActivityTestRule<com.microsoft.todoapp.activities.MainActivity> mActivityRule = new ActivityTestRule(com.microsoft.todoapp.activities.MainActivity.class);

    @Rule
    public ReportHelper reportHelper = Factory.getReportHelper();

    @Test
    public void testCreateNote() {

        ViewInteraction floatingActionButton = onView(
                Matchers.allOf(withId(R.id.add_button), isDisplayed()));
        floatingActionButton.perform(click());
        reportHelper.label("New task button clicked");

        ViewInteraction appCompatEditText = onView(
                Matchers.allOf(withId(R.id.task), isDisplayed()));
        appCompatEditText.perform(replaceText("Prepare TechReady talk"), closeSoftKeyboard());
        reportHelper.label("Add task text");

        ViewInteraction appCompatButton = onView(
                Matchers.allOf(withId(android.R.id.button1), withText("Add")));
        appCompatButton.perform(scrollTo(), click());
        reportHelper.label("Save task clicked");

        ViewInteraction textView = onView(
                Matchers.allOf(withId(R.id.task_title), withText("Prepare TechReady talk"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.list_todo),
                                        0),
                                0),
                        isDisplayed()));
        reportHelper.label("New Task displayed");
        textView.check(matches(withText("Prepare TechReady talk")));

    }


   // mobile-center test run espresso --app "webinardemo/mydemoapp" --devices 5e27d955 --app-path app/build/outputs/apk/app-debug.apk  --test-series "master" --locale "en_US" --build-dir app/build/outputs/apk/
    @Test
    public void testEmptyNote() {

        ViewInteraction floatingActionButton = onView(
                Matchers.allOf(withId(R.id.add_button), isDisplayed()));
        floatingActionButton.perform(click());
        reportHelper.label("New task button clicked");

        ViewInteraction appCompatButton = onView(
                Matchers.allOf(withId(android.R.id.button1), withText("Add")));
        appCompatButton.perform(scrollTo(), click());
        reportHelper.label("Save task clicked");

        ViewInteraction textView = onView(
                Matchers.allOf(withId(R.id.task_title), withText(""),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.list_todo),
                                        0),
                                0),
                        isDisplayed()));
        reportHelper.label("Empty Task displayed");
        textView.check(matches(withText("")));

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
}