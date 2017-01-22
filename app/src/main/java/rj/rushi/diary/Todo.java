package rj.rushi.diary;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import rj.rushi.diary.db.TaskDbHelper;

public class Todo extends Fragment{
    private static final String TAG = "To Do";
    private TaskDbHelper mHelper;
    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;
    Context context;
    Button addTodoButton;
    LinearLayout todoLL;

    //@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_todo, container, false);

        todoLL = (LinearLayout) rootView.findViewById(R.id.activity_todo);
        addTodoButton = (Button) todoLL.findViewById(R.id.button_add_todo);

        addTodoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Add a new task");
                final EditText taskEditText = new EditText(context);
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Add a new task")
                        .setMessage("What do you want to do next?")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /*String task = String.valueOf(taskEditText.getText());
                                   Log.d(TAG, "Task to add: " + task);*/
                                String task = String.valueOf(taskEditText.getText());
                                SQLiteDatabase db = mHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(rj.rushi.diary.db.TaskContract.TaskEntry.COL_TASK_TITLE, task);
                                db.insertWithOnConflict(rj.rushi.diary.db.TaskContract.TaskEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                updateUI();//weird
                                db.close();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });

        mTaskListView = (ListView) rootView.findViewById(R.id.list_todo);
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(rj.rushi.diary.db.TaskContract.TaskEntry.TABLE,
                new String[]{rj.rushi.diary.db.TaskContract.TaskEntry._ID, rj.rushi.diary.db.TaskContract.TaskEntry.COL_TASK_TITLE},
                null, null, null, null, null);
        while(cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(rj.rushi.diary.db.TaskContract.TaskEntry.COL_TASK_TITLE);
            Log.d(TAG, "Task: " + cursor.getString(idx));
        }
        updateUI();//wierd
        cursor.close();
        db.close();
        return rootView;
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);


    }*/
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_add_task:
                Log.d(TAG, "Add a new task");
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new task")
                        .setMessage("What do you want to do next?")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /*String task = String.valueOf(taskEditText.getText());
                                   Log.d(TAG, "Task to add: " + task);/
                                String task = String.valueOf(taskEditText.getText());
                                SQLiteDatabase db = mHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(rj.rushi.diary.db.TaskContract.TaskEntry.COL_TASK_TITLE, task);
                                db.insertWithOnConflict(rj.rushi.diary.db.TaskContract.TaskEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                updateUI();//weird
                                db.close();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();



                dialog.show();


                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    private void updateUI() {
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(rj.rushi.diary.db.TaskContract.TaskEntry.TABLE,
                new String[]{rj.rushi.diary.db.TaskContract.TaskEntry._ID, rj.rushi.diary.db.TaskContract.TaskEntry.COL_TASK_TITLE},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(rj.rushi.diary.db.TaskContract.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(idx));
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(context,
                    R.layout.content_todo,
                    R.id.task_title,
                    taskList);
            mTaskListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }
        /*mAdapter = new ArrayAdapter<>(this,
                R.layout.item_todo, // what view to use for the items
                R.id.task_title, // where to put the String of data
                taskList); // where to get all the data*/

        cursor.close();
        db.close();
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(rj.rushi.diary.db.TaskContract.TaskEntry.TABLE,
                rj.rushi.diary.db.TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});
        db.close();
        updateUI();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        mHelper = new TaskDbHelper(this.context);


    }
}
