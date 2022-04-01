package com.openclassrooms.go4launch;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.input.ReaderInputStream;
import com.openclassrooms.go4launch.model.Result;
import com.openclassrooms.go4launch.model.User;

import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    // Users Info for Test
    private final String uid = "TlZleG6WEuXQXLX3AOCRGTHHeSd2";
    private final String name = "Mohamed GHERBAL";
    private final String email = "gherbal.mohamed@gmail.com";
    private final String urlPicture = "https://lh3.googleusercontent.com/a/AATXAJx8KxN8K9Qekq_e-3dAq4fkEk5i1LJrJSjSFO5X=s96-c";
    private final String restId = "ChIJOYvCo1W3j4AR1LAifgk13rs";
    private final String restName = "Century Cinema 16";
    private final ArrayList likedRestaurant = new ArrayList();


    // Simple Test
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    // Workmates Tests
    @Test
    public void checkWorkmates_isCorrect() {
        User checkWorkmates = new User(
                uid,
                name,
                email,
                urlPicture,
                null,
                null,
                null);

        assertEquals(checkWorkmates.getUid(), uid);
        assertEquals(checkWorkmates.getName(), name);
        assertEquals(checkWorkmates.getEmail(), email);
        assertEquals(checkWorkmates.getUrlPicture(), urlPicture);
    }

    @Test
    public void workmates_withNoRestaurant() {
        User workmatesWithNoRestaurant = new User(
                uid,
                name,
                email,
                urlPicture,
                null,
                null,
                null);

        assertEquals(workmatesWithNoRestaurant.getRestId(), null);
        assertEquals(workmatesWithNoRestaurant.getRestName(), null);
    }

    @Test
    public void workmates_withRestaurant() {
        User workmatesWithRestaurant = new User(
                uid,
                name,
                email,
                urlPicture,
                restId,
                restName,
                null);

        assertEquals(workmatesWithRestaurant.getRestId(), restId);
        assertEquals(workmatesWithRestaurant.getRestName(), restName);
    }

}