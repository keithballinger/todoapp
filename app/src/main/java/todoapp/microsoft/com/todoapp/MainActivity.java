package todoapp.microsoft.com.todoapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import todoapp.microsoft.com.todoapp.db.TaskContract;
import todoapp.microsoft.com.todoapp.db.TaskDbHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private todoapp.microsoft.com.todoapp.db.TaskDbHelper mHelper;
    private ListView mTaskListView;
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
                        .setTitle("Add a new task")
                        .setView(taskEditText)
                        .setNegativeButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveTask(String.valueOf(taskEditText.getText()));
                            }
                        })
                        .setPositiveButton("Cancel", null)
                        .create();
                dialog.show();
                // Log.d(TAG, "Add a new task");
            }
        });

        mHelper = new TaskDbHelper(this);
        mTaskListView = (ListView) findViewById(R.id.list_todo);
        updateUI();
    }

    private void saveTask(String task) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
        db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
        updateUI();
        db.close();

    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});
        db.close();
        updateUI();
    }

    private void updateUI() {
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(idx));
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,
                    R.id.task_title,
                    taskList);
            mTaskListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

}
