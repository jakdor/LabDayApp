package com.jakdor.labday.view.adapter;

import android.support.v7.widget.RecyclerView;

import com.jakdor.labday.common.model.Event;
import com.jakdor.labday.common.model.Timetable;
import com.jakdor.labday.databinding.TimetableItemBinding;

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
        binding.setTimetable(timetable);
        binding.setEvent(event);
        binding.executePendingBindings();
    }
}
