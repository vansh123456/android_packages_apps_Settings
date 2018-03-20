/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.settings.password;

import static com.google.common.truth.Truth.assertThat;
import static org.robolectric.RuntimeEnvironment.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.view.View;
import android.widget.Button;

import com.android.internal.widget.LockPatternView;
import com.android.settings.R;
import com.android.settings.SetupRedactionInterstitial;
import com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment;
import com.android.settings.password.ChooseLockPattern.IntentBuilder;
import com.android.settings.testutils.SettingsRobolectricTestRunner;
import com.android.settings.testutils.shadow.SettingsShadowResources;
import com.android.settings.testutils.shadow.SettingsShadowResourcesImpl;
import com.android.settings.testutils.shadow.ShadowUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowPackageManager;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.ReflectionHelpers.ClassParameter;

import java.util.Collections;
import java.util.List;

@RunWith(SettingsRobolectricTestRunner.class)
@Config(shadows = {
    SettingsShadowResourcesImpl.class,
    SettingsShadowResources.SettingsShadowTheme.class,
    ShadowUtils.class
})
public class SetupChooseLockPatternTest {

    private SetupChooseLockPattern mActivity;

    @Before
    public void setUp() {
        application.getPackageManager().setComponentEnabledSetting(
                new ComponentName(application, SetupRedactionInterstitial.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        mActivity = Robolectric.buildActivity(
                SetupChooseLockPattern.class,
                SetupChooseLockPattern.modifyIntentForSetup(
                        application,
                        new IntentBuilder(application)
                                .setUserId(UserHandle.myUserId())
                                .build()))
                .setup().get();
    }

    @Test
    public void chooseLockSaved_shouldEnableRedactionInterstitial() {
        findFragment(mActivity).onChosenLockSaveFinished(false, null);

        ShadowPackageManager spm = Shadows.shadowOf(application.getPackageManager());
        ComponentName cname = new ComponentName(application, SetupRedactionInterstitial.class);
        final int componentEnabled = spm.getComponentEnabledSettingFlags(cname)
            & PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        assertThat(componentEnabled).isEqualTo(PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
    }

    @Config(qualifiers = "sw400dp")
    @Test
    public void selectPattern_shouldHideOptionsButton() {
        Button button = mActivity.findViewById(R.id.screen_lock_options);
        assertThat(button).isNotNull();
        assertThat(button.getVisibility()).isEqualTo(View.VISIBLE);

        LockPatternView lockPatternView = mActivity.findViewById(R.id.lockPattern);
        ReflectionHelpers.callInstanceMethod(lockPatternView, "notifyPatternDetected");
    }

    @Config(qualifiers = "sw400dp")
    @Test
    public void sw400dp_shouldShowScreenLockOptions() {
        Button button = mActivity.findViewById(R.id.screen_lock_options);
        assertThat(button).isNotNull();
        assertThat(button.getVisibility()).isEqualTo(View.VISIBLE);

        button.performClick();
        AlertDialog chooserDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(chooserDialog).isNotNull();
        int count = Shadows.shadowOf(chooserDialog).getAdapter().getCount();
        assertThat(count).named("List items shown").isEqualTo(3);
    }

    @Test
    public void smallScreens_shouldHideScreenLockOptions() {
        Button button = mActivity.findViewById(R.id.screen_lock_options);
        assertThat(button).isNotNull();
        assertThat(button.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void skipButton_shouldBeVisible_duringNonFingerprintFlow() {
        Button button = mActivity.findViewById(R.id.footerLeftButton);
        assertThat(button).isNotNull();
        assertThat(button.getVisibility()).isEqualTo(View.VISIBLE);

        button.performClick();
        AlertDialog chooserDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(chooserDialog).isNotNull();
    }

    @Test
    public void skipButton_shouldNotBeVisible_duringFingerprintFlow() {
        mActivity = Robolectric.buildActivity(
                SetupChooseLockPattern.class,
                SetupChooseLockPattern.modifyIntentForSetup(
                        application,
                        new IntentBuilder(application)
                                .setUserId(UserHandle.myUserId())
                                .setForFingerprint(true)
                                .build()))
                .setup().get();
        Button button = mActivity.findViewById(R.id.footerLeftButton);
        assertThat(button).isNotNull();
        assertThat(button.getVisibility()).isEqualTo(View.GONE);
    }

    private ChooseLockPatternFragment findFragment(Activity activity) {
        return (ChooseLockPatternFragment)
                activity.getFragmentManager().findFragmentById(R.id.main_content);
    }
}
