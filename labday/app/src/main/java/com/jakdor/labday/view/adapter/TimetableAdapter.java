package com.jakdor.labday.view.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.jakdor.labday.R;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.model.Event;
import com.jakdor.labday.common.model.Timetable;
import com.jakdor.labday.databinding.TimetableItemBinding;
import com.jakdor.labday.view.ui.EventFragment;
import com.jakdor.labday.view.ui.TimetableFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * RecyclerView adapter for {@link com.jakdor.labday.view.ui.TimetableFragment} items
 */
public class TimetableAdapter extends RecyclerView.Adapter<TimetableViewHolder> {

    private List<Timetable> filteredTimetables = new ArrayList<>();
    private List<Event> filteredEvents = new ArrayList<>();

    private TimetableFragment fragment;
    private RequestManager glide;
    private int layoutHeight;

    /**
     * Filter timetables list by activePath
     * @param appData loaded from repository
     * @param activePath id of active Path
     * @param layoutHeight window height used in item height auto scaling
     */
    public TimetableAdapter(TimetableFragment fragment, AppData appData,
                            int activePath, RequestManager glide, int layoutHeight) {
        this.fragment = fragment;
        this.glide = glide;
        this.layoutHeight = layoutHeight;
        for(Timetable timetable : appData.getTimetables()){
            if(timetable.getPathId() == activePath) {
                filteredTimetables.add(timetable);
                for(Event event : appData.getEvents()) {
                    if(event.getId().equals(timetable.getEventId()))
                        filteredEvents.add(event);
                }
            }
        }

        Collections.sort(filteredTimetables, (t1, t2) -> t1.getTimeStart() - t2.getTimeStart());
    }

    /**
     * Binds {@link com.jakdor.labday.view.ui.TimetableFragment} items + height auto scaling
     * @return binded item with scaled height
     */
    @Override
    public TimetableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        TimetableItemBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.timetable_item, parent, false);

        //height auto scaling
        if(layoutHeight != 0) { 
            binding.getRoot().getLayoutParams().height = layoutHeight / 7;
        }

        return new TimetableViewHolder(binding, glide);
    }

    @Override
    public void onBindViewHolder(TimetableViewHolder holder, int position) {
        Timetable timetable = filteredTimetables.get(position);
        Event event = filteredEvents.get(position);
        holder.bind(timetable, event, position);
        holder.getBinding().cardView.setOnClickListener(view -> onCardClick(event.getId()));
    }

    @Override
    public int getItemCount() {
        return filteredTimetables.size();
    }

    public void onCardClick(int eventId){
        if (fragment.getActivity() != null) {
            fragment.getActivity().getSupportFragmentManager().beginTransaction()
                    .addToBackStack(EventFragment.CLASS_TAG)
                    .replace(R.id.fragmentLayout, EventFragment.Companion.newInstance(eventId))
                    .commit();
        }
    }
}
