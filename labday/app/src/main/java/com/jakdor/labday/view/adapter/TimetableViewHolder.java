package com.jakdor.labday.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jakdor.labday.R;
import com.jakdor.labday.common.model.Event;
import com.jakdor.labday.common.model.Timetable;
import com.jakdor.labday.databinding.TimetableItemBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * ViewHolder for timetable_item.xml
 */
public class TimetableViewHolder extends RecyclerView.ViewHolder {

    private final TimetableItemBinding binding;
    private final RequestManager glide;

    public TimetableViewHolder(TimetableItemBinding binding, RequestManager glide) {
        super(binding.getRoot());
        this.binding = binding;
        this.glide = glide;
    }

    public void bind(Timetable timetable, Event event){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.GERMAN);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date start = new Date(timetable.getTimeStart()*1000);
        Date end = new Date(timetable.getTimeEnd()*1000);

        glide.load(event.getImg())
                .apply(new RequestOptions().placeholder(R.mipmap.ic_launcher_foreground).centerCrop())
                .transition(withCrossFade())
                .into(binding.timetableItemImage);

        binding.setTimeStart(simpleDateFormat.format(start));
        binding.setTimeEnd(simpleDateFormat.format(end));

        binding.setTimetable(timetable);
        binding.setEvent(event);
        binding.executePendingBindings();
    }
}
