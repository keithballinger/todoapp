package com.microsoft.todoapp.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.microsoft.todoapp.R;
import com.microsoft.todoapp.database.DatabaseHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper mHelper;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText taskEditText = new EditText(MainActivity.this);
                taskEditText.setSingleLine();
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.add_task_dialog_title)
                        .setView(taskEditText)
                        .setNegativeButton(R.string.add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveTask(String.valueOf(taskEditText.getText()));
                            }
                        })
                        .setPositiveButton(R.string.cancel, null)
                        .create();
                dialog.show();
            }
        });

        mHelper = new DatabaseHelper(this);
        ((ListView) findViewById(R.id.list_todo)).setAdapter(
                mAdapter = new ArrayAdapter<>(this, R.layout.item_todo, R.id.task_title));

        updateUI();
    }

    private void saveTask(String task) {
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
