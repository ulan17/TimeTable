package com.ulan.timetable.adapters;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.widget.ImageViewCompat;

import com.pd.chocobar.ChocoBar;
import com.ulan.timetable.R;
import com.ulan.timetable.model.Teacher;
import com.ulan.timetable.utils.AlertDialogsHelper;
import com.ulan.timetable.utils.ColorPalette;
import com.ulan.timetable.utils.DbHelper;
import com.ulan.timetable.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Ulan on 08.10.2018.
 */
public class TeachersAdapter extends ArrayAdapter<Teacher> {

    private AppCompatActivity mActivity;
    private int mResource;
    private ArrayList<Teacher> teacherlist;
    private Teacher teacher;
    private ListView mListView;

    private static class ViewHolder {
        TextView name;
        TextView post;
        TextView phonenumber;
        TextView email;
        CardView cardView;
        ImageView popup;
    }

    public TeachersAdapter(AppCompatActivity activity, ListView listView, int resource, ArrayList<Teacher> objects) {
        super(activity, resource, objects);
        mActivity = activity;
        mListView = listView;
        mResource = resource;
        teacherlist = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        String name = Objects.requireNonNull(getItem(position)).getName();
        String post = Objects.requireNonNull(getItem(position)).getPost();
        String phonenumber = Objects.requireNonNull(getItem(position)).getPhonenumber();
        String email = Objects.requireNonNull(getItem(position)).getEmail();
        int color = Objects.requireNonNull(getItem(position)).getColor();

        teacher = new Teacher(name, post, phonenumber, email, color);
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.nameteacher);
            holder.post = convertView.findViewById(R.id.postteacher);
            holder.phonenumber = convertView.findViewById(R.id.numberteacher);
            holder.email = convertView.findViewById(R.id.emailteacher);
            holder.cardView = convertView.findViewById(R.id.teacher_cardview);
            holder.popup = convertView.findViewById(R.id.popupbtn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Setup colors based on Background
        int textColor = ColorPalette.pickTextColorBasedOnBgColorSimple(color, Color.WHITE, Color.BLACK);
        holder.name.setTextColor(textColor);
        holder.post.setTextColor(textColor);
        holder.phonenumber.setTextColor(textColor);
        holder.email.setTextColor(textColor);
        ImageViewCompat.setImageTintList(convertView.findViewById(R.id.personimage), ColorStateList.valueOf(textColor));
        ImageViewCompat.setImageTintList(convertView.findViewById(R.id.imageView4), ColorStateList.valueOf(textColor));
        ImageViewCompat.setImageTintList(convertView.findViewById(R.id.imageView5), ColorStateList.valueOf(textColor));
        ImageViewCompat.setImageTintList(convertView.findViewById(R.id.popupbtn), ColorStateList.valueOf(textColor));
        convertView.findViewById(R.id.line).setBackgroundColor(textColor);


        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);

        holder.name.setText(teacher.getName());

        holder.post.setText(teacher.getPost());
        holder.post.setBackgroundResource(outValue.resourceId);
        holder.post.setOnClickListener((View v) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("geo:0,0?q=" + teacher.getPost()));
            if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
                try {
                    mActivity.startActivity(intent);
                } catch (ActivityNotFoundException e2) {
                    ChocoBar.builder().setActivity(mActivity).setText(mActivity.getString(R.string.no_navigation_app)).setDuration(ChocoBar.LENGTH_LONG).red().show();
                }
            }
        });

        holder.phonenumber.setText(teacher.getPhonenumber());
        holder.phonenumber.setBackgroundResource(outValue.resourceId);
        holder.phonenumber.setBackgroundResource(outValue.resourceId);
        holder.phonenumber.setOnClickListener((View v) -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phonenumber));
            mActivity.startActivity(intent);
        });


        holder.email.setText(teacher.getEmail());
        holder.email.setBackgroundResource(outValue.resourceId);
        holder.email.setOnClickListener((View v) -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + teacher.getEmail()));
            try {
                mActivity.startActivity(emailIntent);
            } catch (Exception e) {
                try {
                    emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(emailIntent);
                } catch (ActivityNotFoundException e2) {
                    ChocoBar.builder().setActivity(mActivity).setText(mActivity.getString(R.string.no_email_app)).setDuration(ChocoBar.LENGTH_LONG).red().show();
                }
            }
        });

        holder.cardView.setCardBackgroundColor(teacher.getColor());
        holder.popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DbHelper db = new DbHelper(mActivity);
                ContextThemeWrapper theme = new ContextThemeWrapper(mActivity, PreferenceUtil.isDark(getContext()) ? R.style.Widget_AppCompat_PopupMenu : R.style.Widget_AppCompat_Light_PopupMenu);
                final PopupMenu popup = new PopupMenu(theme, holder.popup);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete_popup:
                                db.deleteTeacherById(getItem(position));
                                db.updateTeacher(getItem(position));
                                teacherlist.remove(position);
                                notifyDataSetChanged();
                                return true;

                            case R.id.edit_popup:
                                final View alertLayout = mActivity.getLayoutInflater().inflate(R.layout.dialog_add_teacher, null);
                                AlertDialogsHelper.getEditTeacherDialog(mActivity, alertLayout, teacherlist, mListView, position);
                                notifyDataSetChanged();
                                return true;
                            default:
                                return onMenuItemClick(item);
                        }
                    }
                });
                popup.show();
            }
        });

        hidePopUpMenu(holder);

        return convertView;
    }

    public ArrayList<Teacher> getTeacherList() {
        return teacherlist;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    private void hidePopUpMenu(ViewHolder holder) {
        SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();
        if (checkedItems.size() > 0) {
            for (int i = 0; i < checkedItems.size(); i++) {
                int key = checkedItems.keyAt(i);
                if (checkedItems.get(key)) {
                    holder.popup.setVisibility(View.INVISIBLE);
                }
            }
        } else {
            holder.popup.setVisibility(View.VISIBLE);
        }
    }
}