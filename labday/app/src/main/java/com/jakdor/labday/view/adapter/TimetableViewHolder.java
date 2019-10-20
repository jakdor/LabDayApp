package com.jakdor.labday.view.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jakdor.labday.R;
import com.jakdor.labday.common.model.Event;
import com.jakdor.labday.common.model.Timetable;
import com.jakdor.labday.databinding.TimetableItemBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public void bind(Timetable timetable, Event event, int pos){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.GERMAN);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Date start = new Date((long)timetable.getTimeStart()*1000);
        Date end = new Date((long)timetable.getTimeEnd()*1000);

        glide.load(event.getImg())
                .apply(new RequestOptions().placeholder(R.mipmap.ic_launcher_foreground).centerCrop())
                .transition(withCrossFade())
                .into(binding.timetableItemImage);

        binding.setIsNow(isNow(start, end));

        binding.setIsDarkColor(pos % 2 == 0);

        binding.setTimeStart(simpleDateFormat.format(start));
        binding.setTimeEnd(simpleDateFormat.format(end));

        binding.setTimetable(timetable);
        binding.setEvent(event);
        binding.executePendingBindings();
    }

    /**
     * @param start Date from
     * @param end Date to
     * @return true if within range
     */
    public boolean isNow(Date start, Date end){
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        return now.before(end) && now.after(start);
    }

    public TimetableItemBinding getBinding() {
        return binding;
    }
}
