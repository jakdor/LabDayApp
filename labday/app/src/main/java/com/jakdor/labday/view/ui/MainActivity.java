package com.jakdor.labday.view.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jakdor.labday.R;
import com.jakdor.labday.common.repository.ProjectRepository;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class MainActivity extends AppCompatActivity {

    @Inject
    ProjectRepository projectRepository;

    @BindView(R.id.testTextView)
    TextView testTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        testTextView.setText(projectRepository.daggerHelloWorld());
    }
}
