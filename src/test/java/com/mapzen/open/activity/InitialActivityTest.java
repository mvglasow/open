package com.mapzen.open.activity;

import com.mapzen.open.MapzenApplication;
import com.mapzen.open.TestMapzenApplication;
import com.mapzen.open.support.MapzenTestRunner;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.fest.assertions.api.Assertions;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.scribe.model.Token;

import android.content.Intent;
import android.net.Uri;

import javax.inject.Inject;

import static com.mapzen.open.support.TestHelper.initBaseActivity;
import static com.mapzen.open.support.TestHelper.initInitialActivity;
import static com.mapzen.open.util.MixpanelHelper.Event.INITIAL_LAUNCH;
import static com.mapzen.open.util.MixpanelHelper.Payload.LOGGED_IN_KEY;
import static org.fest.assertions.api.ANDROID.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.robolectric.Robolectric.application;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.Robolectric.getShadowApplication;
import static org.robolectric.Robolectric.shadowOf;

@Config(emulateSdk = 18)
@RunWith(MapzenTestRunner.class)
public class InitialActivityTest {
    private BaseActivity baseActivity;
    @Inject MixpanelAPI mixpanelAPI;

    @Before
    public void setUp() throws Exception {
        ((TestMapzenApplication) Robolectric.application).inject(this);
        baseActivity = initBaseActivity();
    }

    @Test
    public void shouldNotBeNull() throws Exception {
        InitialActivity activity = initInitialActivity();
        assertThat(activity).isNotNull();
    }

    @Test
    public void shouldNotHaveActionBar() throws Exception {
        InitialActivity activity = initInitialActivity();
        assertThat(activity.getActionBar()).isNull();
    }

    @Test
    public void shouldForwardIntentDataToBaseActivity() throws Exception {
        simulateLogin();
        String data = "http://maps.example.com/";
        Intent intent = new Intent();
        intent.setData(Uri.parse(data));
        buildActivity(InitialActivity.class).withIntent(intent).create();
        assertThat(getShadowApplication().getNextStartedActivity()).hasData(data);
    }

    private void simulateLogin() {
        baseActivity.setAccessToken(new Token("token", "fun"));
    }
}
