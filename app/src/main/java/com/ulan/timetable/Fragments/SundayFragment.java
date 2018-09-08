package com.ulan.timetable.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ulan.timetable.Adapters.WeekListAdapter;
import com.ulan.timetable.Utils.DbHelper;
import com.ulan.timetable.R;

/**
 * Created by Ulan on 06.09.2018.
 */
public class SundayFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sunday, container, false);
        DbHelper db = new DbHelper(getActivity());
        ListView listView = view.findViewById(R.id.sundaylist);

        WeekListAdapter adapter = new WeekListAdapter(getActivity(), R.layout.adapter_listview_layout, db.getData("Sunday"));
        listView.setAdapter(adapter);
        return view;
    }
}
