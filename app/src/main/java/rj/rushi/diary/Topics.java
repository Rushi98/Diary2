package rj.rushi.diary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by rushi on 21/1/17.
 */

public class Topics extends Fragment {
    DatabaseHandler db;
    LinearLayout topicsLL;
    Context context;
    ListView topicsList;
    LinearLayout addTopicBar;
    EditText addTopicText;
    ImageButton addTopicButton;
    public Topics(){
    }

    public Topics newInstance() {
        Topics topics = new Topics();
        topics.db=new DatabaseHandler(getActivity());
        return topics;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topics, container, false);
        topicsLL = (LinearLayout) view.findViewById(R.id.topics);
        topicsList = (ListView) topicsLL.findViewById(R.id.list_topics);

        topicsList = (ListView) topicsLL.findViewById(R.id.list_topics);

        addTopicBar = (LinearLayout) topicsLL.findViewById(R.id.add_topic_bar);

        addTopicText = (EditText) addTopicBar.findViewById(R.id.edit_topic_name);

        addTopicButton = (ImageButton) addTopicBar.findViewById(R.id.button_add_topic);
        final Toast toast = Toast.makeText(getActivity(), "Topic already exists!", Toast.LENGTH_SHORT);
        View.OnClickListener addTopicListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addTopicText.getText().toString().length() != 0) {
                    int status = db.addTopic(addTopicText.getText().toString());
                    if (status == 1) {
                        toast.show();
                    }
                }
                addTopicText.setText("");
                addTopicText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                Log.d("what", "add Topic");
                onResume();
            }
        };
        addTopicButton.setOnClickListener(addTopicListener);
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        List<String> topics = db.getTOPIC_NAMES();
        TopicListAdapter topicListAdapter = new TopicListAdapter(getContext(), topics);
        topicsList.setAdapter(topicListAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        db = new DatabaseHandler(this.context);
    }
}
