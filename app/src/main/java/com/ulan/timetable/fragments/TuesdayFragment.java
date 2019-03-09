package com.ulan.timetable.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ulan.timetable.adapters.WeekAdapter;
import com.ulan.timetable.utils.DbHelper;
import com.ulan.timetable.R;
import com.ulan.timetable.utils.FragmentHelper;

public class TuesdayFragment extends Fragment {

    public static final String KEY_TUESDAY_FRAGMENT = "Tuesday";
    private WeekAdapter adapter;
    private ListView listView;
    private DbHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tuesday, container, false);
        setupAdapter(view);
        setupListViewMultiSelect();
        return view;
    }

    private void setupAdapter(View view) {
        db = new DbHelper(getActivity());
        listView = view.findViewById(R.id.tuesdaylist);
        adapter = new WeekAdapter(getActivity(), listView, R.layout.listview_week_adapter, db.getWeek(KEY_TUESDAY_FRAGMENT));
        listView.setAdapter(adapter);
    }

    private void setupListViewMultiSelect() {
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(FragmentHelper.setupListViewMultiSelect(getActivity(), listView, adapter, db));
    }
}