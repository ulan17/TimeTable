package com.ulan.timetable.appwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ulan.timetable.R;
import com.ulan.timetable.appwidget.Dao.AppWidgetDao;
import com.ulan.timetable.utils.PreferenceUtil;

import java.util.Map;

/**
 * From https://github.com/SubhamTyagi/TimeTable
 */
public class AppWidgetConfigureActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, RadioGroup
        .OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {

    private int mAppWidgetId;
    private RadioGroup mRgBgColor;
    private RadioGroup mRgTimeStyle;
    private SeekBar mSbIntensity;
    private TextView mTvIntensity;
    private TextView mTvTimeStyle;
    private CheckBox mCbWeek;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceUtil.getGeneralTheme(this));
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }

        mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        setContentView(R.layout.activity_appwidget_configure);

        initView();
        setListener();

        Map<String, Integer> configMap = AppWidgetDao.getAppWidgetConfig(mAppWidgetId, getApplicationContext());
        if (configMap != null) {
            setConfig(configMap);
        }
    }

    private void initView() {
        mRgBgColor = findViewById(R.id.rg_bg_color);
        mTvIntensity = findViewById(R.id.tv_intensity);
        mSbIntensity = findViewById(R.id.sb_intensity);
        mRgTimeStyle = findViewById(R.id.rg_time_style);
        mTvTimeStyle = findViewById(R.id.tv_time_style);
        mCbWeek = findViewById(R.id.cb_week);
    }

    private void setListener() {
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        mSbIntensity.setOnSeekBarChangeListener(this);
        mRgTimeStyle.setOnCheckedChangeListener(this);
        mCbWeek.setOnCheckedChangeListener(this);
    }

    private void setConfig(Map<String, Integer> configMap) {
        Integer backgroundColor = configMap.get("backgroundColor");
        if (backgroundColor != null && backgroundColor != -1) {
            int r = (int) (((backgroundColor >> 16) & 0xff) / 255.0f * 100);
            if (r == 0) {

                mRgBgColor.check(R.id.rb_black);
            } else {
                mRgBgColor.check(R.id.rb_white);
            }

            int a = Math.round(((backgroundColor >> 24) & 0xff) / 255.0f * 100);
            mSbIntensity.setProgress(a);
        }

        Integer timeStyle = configMap.get("timeStyle");
        if (timeStyle != null && timeStyle != -1) {
            switch (timeStyle) {
                case AppWidgetConstants.TIME_STYLE_SECOND:
                    mRgTimeStyle.check(R.id.rb_time_style_2);
                    break;
                case AppWidgetConstants.TIME_STYLE_THIRD:
                    mRgTimeStyle.check(R.id.rb_time_style_3);
                    break;
                case AppWidgetConstants.TIME_STYLE_FIRST:
                    mRgTimeStyle.check(R.id.rb_time_style_1);
                default:
                    break;
            }
        }

        Integer weekStyle = configMap.get("weekStyle");
        if (weekStyle != null && weekStyle != -1) {
            mCbWeek.setChecked(weekStyle == AppWidgetConstants.WEEK_STYLE_ENABLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_confirm:
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                DayAppWidgetProvider.updateAppWidgetConfig(appWidgetManager, mAppWidgetId, getSettingColor(), getTimeStyle(), getWeekStyle(), this);
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
                break;
        }
    }

    private int getWeekStyle() {
        return mCbWeek.isChecked() ? AppWidgetConstants.WEEK_STYLE_ENABLE : AppWidgetConstants.WEEK_STYLE_DISABLE;
    }

    public int getSettingColor() {
        int progress = mSbIntensity.getProgress();
        int alpha = progress * 255 / 100;

        if (mRgBgColor.getCheckedRadioButtonId() == R.id.rb_black) {
            return Color.argb(alpha, 0, 0, 0);
        } else {
            return Color.argb(alpha, 255, 255, 255);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.sb_intensity) {
            mTvIntensity.setText(getString(R.string.app_widget_configure_intensity, progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_time_style_1:
                mTvTimeStyle.setText(getString(R.string.app_widget_configure_time_style, "Morning section"));
                break;
            case R.id.rb_time_style_2:
                mTvTimeStyle.setText(getString(R.string.app_widget_configure_time_style, "Previous"));
                break;
            case R.id.rb_time_style_3:
                mTvTimeStyle.setText(getString(R.string.app_widget_configure_time_style, "Upper 1"));
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.cb_week) {
            setWeekHintText(isChecked);
        }
    }

    private void setWeekHintText(boolean isChecked) {
        if (isChecked) {
            mCbWeek.setText(R.string.app_widget_configure_week_style_enable);
        } else {
            mCbWeek.setText(R.string.app_widget_configure_week_style_disable);
        }
    }

    public int getTimeStyle() {
        switch (mRgTimeStyle.getCheckedRadioButtonId()) {
            case R.id.rb_time_style_2:
                return AppWidgetConstants.TIME_STYLE_SECOND;
            case R.id.rb_time_style_3:
                return AppWidgetConstants.TIME_STYLE_THIRD;
            case R.id.rb_time_style_1:
            default:
                return AppWidgetConstants.TIME_STYLE_FIRST;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
