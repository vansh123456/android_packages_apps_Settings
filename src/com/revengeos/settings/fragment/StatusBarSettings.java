/*
 * Copyright (C) 2019 RevengeOS
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
 * limitations under the License.
 */

package com.revengeos.settings.fragment;

import android.os.Bundle;
import android.content.Context;
import com.android.settings.R;

import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.awaken.preferences.SystemSettingMasterSwitchPreference;

import com.android.internal.logging.nano.MetricsProto;

import java.util.ArrayList;
import java.util.List;

public class StatusBarSettings extends SettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.status_bar_settings);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.REVENGEOS;
    }

}

 private void updateMasterPrefs() {
        mNetworkTraffic = (SystemSettingMasterSwitchPreference) findPreference(NETWORK_TRAFFIC);
        mNetworkTraffic.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.NETWORK_TRAFFIC_STATE, 0) == 1));
        mNetworkTraffic.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
		if (preference == mNetworkTraffic) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_STATE, value ? 1 : 0);
            return true;
		}
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMasterPrefs();
    }

    @Override
    public void onPause() {
        super.onPause();
        updateMasterPrefs();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.REVENGEOS;
    }

}