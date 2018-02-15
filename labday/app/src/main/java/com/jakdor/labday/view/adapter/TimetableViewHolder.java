package com.jakdor.labday.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.jakdor.labday.common.model.Event;
import com.jakdor.labday.common.model.Timetable;
import com.jakdor.labday.databinding.TimetableItemBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * ViewHolder for timetable_item.xml
 */
public class TimetableViewHolder extends RecyclerView.ViewHolder {

    private final TimetableItemBinding binding;

    public TimetableViewHolder(TimetableItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Timetable timetable, Event event){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.GERMAN);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date start = new Date(timetable.getTimeStart()*1000);
        Date end = new Date(timetable.getTimeEnd()*1000);

        binding.setTimeStart(simpleDateFormat.format(start));
        binding.setTimeEnd(simpleDateFormat.format(end));

        binding.setTimetable(timetable);
        binding.setEvent(event);
        binding.executePendingBindings();
    }
}
