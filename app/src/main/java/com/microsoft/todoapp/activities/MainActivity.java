package com.microsoft.todoapp.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.microsoft.azure.mobile.MobileCenter;
import com.microsoft.azure.mobile.analytics.Analytics;
import com.microsoft.azure.mobile.crashes.Crashes;
import com.microsoft.todoapp.R;
import com.microsoft.todoapp.database.DatabaseHelper;
import com.microsoft.todoapp.exceptions.InvalidValueException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper mHelper;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileCenter.start(getApplication(), "a73b7067-bc69-4355-94d1-290a53c226c6",
                Analytics.class, Crashes.class);

        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.new_task, null);
                final EditText taskEditText = (EditText) layout.findViewById(R.id.task);
                taskEditText.setSingleLine();

                final Spinner typeSpinner = (Spinner) layout.findViewById(R.id.type);

                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.add_task_dialog_title)
                        .setView(layout)
                        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveTask(String.valueOf(taskEditText.getText()));

                                Map<String,String> properties=new HashMap<String,String>();
                                properties.put("Task Type", typeSpinner.getSelectedItem().toString());
                                Analytics.trackEvent("Task Added", properties);

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Call SDK for event
                                Analytics.trackEvent("Task Cancelled");
                            }
                        })
                        .create();

                Analytics.trackEvent("+ clicked");

                dialog.show();
            }
        });

        mHelper = new DatabaseHelper(this);
        ((ListView) findViewById(R.id.list_todo)).setAdapter(
                mAdapter = new ArrayAdapter<>(this, R.layout.item_todo, R.id.task_title));

        updateUI();
    }

    private void saveTask(String task) {
        if (task.isEmpty()) {
            throw new IllegalArgumentException("Task cannot be null or empty.");
        } else if (task.trim().isEmpty()) {
            throw new InvalidValueException("Task cannot be null or empty.");
        }
        mHelper.saveTask(task);
        updateUI();
    }

    public void deleteTask(View view) {
        TextView taskTextView = (TextView) ((View) view.getParent()).findViewById(R.id.task_title);
        mHelper.deleteTask(String.valueOf(taskTextView.getText()));
        updateUI();
    }

    private void updateUI() {
        ArrayList<String> taskList = mHelper.getTasks();
        mAdapter.clear();
        mAdapter.addAll(taskList);
        mAdapter.notifyDataSetChanged();
    }
}
