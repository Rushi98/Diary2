package rj.rushi.diary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by rushi on 21/1/17.
 */

public class Scribbles extends Fragment {
    DatabaseHandler db;
    Context context;
    LinearLayout scribblesLL;
    List<String> scribbleDates;
    List<String> scribbleDatesUserFriendly;
    String scribblesDate;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat userFriendlyDate = new SimpleDateFormat("d MMM, yyyy");
    ArrayAdapter<String> dateAdapter;
    Spinner scribblesDateSpinner;
    Boolean userChangedDate = false;
    ListView scribblesList;
    LinearLayout scribblesInputBar;
    int scribblesInputBarHeight;
    EditText scribbleText;
    ImageButton doneScribble;

    public Scribbles(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scribbles, container, false);

        scribblesDate = DatabaseHandler.dateFormat.format(new Date());
        this.scribblesLL = (LinearLayout) view;

        this.scribblesDateSpinner = (Spinner) scribblesLL.findViewById(R.id.scribbles_date_spinner);
        scribblesDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                Log.d("selected", parent.getItemAtPosition(position).toString());
                if(userChangedDate && !Objects.equals(scribblesDate, DatabaseHandler.dateFormat.format(userFriendlyDate.parse(parent.getItemAtPosition(position).toString(), new ParsePosition(0))))) {
                    userChangedDate = false;
                    scribblesDate = DatabaseHandler.dateFormat.format(userFriendlyDate.parse(parent.getItemAtPosition(position).toString(), new ParsePosition(0)));
                    if (!Objects.equals(scribblesDate, DatabaseHandler.dateFormat.format(new Date()))) {
                        scribblesInputBar.getLayoutParams().height = 0;
                        //scribblesInputBar.setVisibility(View.INVISIBLE);
                    } else {
                        scribblesInputBar.getLayoutParams().height = scribblesInputBarHeight;
                        //scribblesInputBar.setVisibility(View.VISIBLE);
                    }
                    Log.d("what", "onItemSelected");
                    onResume();
                }
                else if (!userChangedDate){
                    scribblesDateSpinner.setSelection(scribbleDates.indexOf(scribblesDate));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                scribblesInputBar.setVisibility(View.VISIBLE);
            }
        });
        scribblesDateSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                userChangedDate = true;
                return false;
            }
        });

        this.scribblesList = (ListView) scribblesLL.findViewById(R.id.list_scribbles);

        this.scribblesInputBar = (LinearLayout) scribblesLL.findViewById(R.id.scribbles_input_bar);
        this.scribblesInputBarHeight = scribblesInputBar.getLayoutParams().height;

        this.scribbleText = (EditText) scribblesLL.findViewById(R.id.edit_text_scribbles);
        this.doneScribble = (ImageButton) scribblesLL.findViewById(R.id.button_scribble_done);
        doneScribble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.addNote(scribbleText.getText().toString(), null);
                scribbleText.setText("");
                scribbleText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                Log.d("what", "doneScribbles");
                onResume();
            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        scribbleDates = db.getSCRIBBLE_DATES();
        if(!scribbleDates.contains(DatabaseHandler.dateFormat.format(new Date()))){
            db.addToday();
            scribbleDates = db.getSCRIBBLE_DATES();
        }
        scribbleDatesUserFriendly = new ArrayList<>();
        for (String s:scribbleDates){
            scribbleDatesUserFriendly.add(userFriendlyDate.format(DatabaseHandler.dateFormat.parse(s, new ParsePosition(0))));
        }
        dateAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, scribbleDatesUserFriendly);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scribblesDateSpinner.setAdapter(dateAdapter);

        List<Note> scribblesToday = db.getScribblesDated(DatabaseHandler.dateFormat.parse(scribblesDate, new ParsePosition(0)));
        Log.d("date", DatabaseHandler.dateFormat.parse(scribblesDate, new ParsePosition(0)).toString());
        NotesAdapter scribblesAdapter = new NotesAdapter(getContext(), scribblesToday);
        scribblesList.setAdapter(scribblesAdapter);

    }

    @Override
    public void onStop(){
        scribbleDates.remove(DatabaseHandler.dateFormat.format(new Date()));
        for(String s: scribbleDates){
            List<Note> scribbles = db.getScribblesDated(DatabaseHandler.dateFormat.parse(s, new ParsePosition(0)));
            if(scribbles.size() == 0){
                db.removeDate(s);
            }
        }
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.db = new DatabaseHandler(this.context);
    }
}
