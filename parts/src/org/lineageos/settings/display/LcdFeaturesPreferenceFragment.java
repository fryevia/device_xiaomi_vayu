/*
 * Copyright (C) 2021 Paranoid Android
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

package org.lineageos.settings.display;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemProperties;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;

import org.lineageos.settings.R;
import org.lineageos.settings.utils.FileUtils;

public class LcdFeaturesPreferenceFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    public static final String HBM_NODE = "/sys/devices/platform/soc/soc:qcom,dsi-display-primary/hbm";
    public static final String CABC_NODE = "/sys/devices/platform/soc/soc:qcom,dsi-display-primary/cabc";

    public static final String HBM_PROP = "persist.deviceparts.lcd.hbm";
    public static final String CABC_PROP = "persist.deviceparts.lcd.cabc";

    private static final String KEY_HBM = "lcd_hbm_pref";
    private static final String KEY_CABC = "lcd_cabc_pref";

    private ListPreference mHbmPref;
    private ListPreference mCabcPref;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.lcd_features_settings);
        mHbmPref = (ListPreference) findPreference(KEY_HBM);
        mHbmPref.setOnPreferenceChangeListener(this);
        mCabcPref = (ListPreference) findPreference(KEY_CABC);
        mCabcPref.setOnPreferenceChangeListener(this);
        validateKernelSupport();
    }

    @Override
    public void onResume() {
        super.onResume();
        mHbmPref.setValue(SystemProperties.get(HBM_PROP, "0"));
        mHbmPref.setSummary(mHbmPref.getEntry());
        mCabcPref.setValue(SystemProperties.get(CABC_PROP, "0"));
        mCabcPref.setSummary(mCabcPref.getEntry());
        validateKernelSupport();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();

        if (key.equals(KEY_HBM)) {
            mHbmPref.setValue((String) newValue);
            mHbmPref.setSummary(mHbmPref.getEntry());
            SystemProperties.set(HBM_PROP, (String) newValue);
        } else if (key.equals(KEY_CABC)) {
            mCabcPref.setValue((String) newValue);
            mCabcPref.setSummary(mCabcPref.getEntry());
            SystemProperties.set(CABC_PROP, (String) newValue);
        }

        return true;
    }

    private void validateKernelSupport() {
        if (!FileUtils.fileExists(HBM_NODE)) {
            mHbmPref.setSummary(getResources().getString(R.string.kernel_not_supported));
            mHbmPref.setEnabled(false);
        }
        if (!FileUtils.fileExists(CABC_NODE)) {
            mCabcPref.setSummary(getResources().getString(R.string.kernel_not_supported));
            mCabcPref.setEnabled(false);
        }
    }
}
