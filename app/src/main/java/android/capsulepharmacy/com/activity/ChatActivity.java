package android.capsulepharmacy.com.activity;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.adapter.MessagesListAdapter;
import android.capsulepharmacy.com.base.BaseActivity;
import android.capsulepharmacy.com.modal.Message;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends BaseActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private Context mContext;
    private Toolbar toolbar;

    private MessagesListAdapter adapter;
    private List<Message> listMessages = new ArrayList<>();
    private ListView listViewMessages;
    private Button btnSend;

    private EditText inputMsg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mContext = ChatActivity.this;
        findViewId();
        applyInit();
    }

    private void applyInit() {
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        adapter = new MessagesListAdapter(this, listMessages);
        listViewMessages.setAdapter(adapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                message.setMessage(inputMsg.getText().toString());
                listMessages.add(message);
                adapter.notifyDataSetChanged();
            }
        });



    }

    private void findViewId() {
        toolbar = findViewById(R.id.toolbar);
        listViewMessages = findViewById(R.id.list_view_messages);
        inputMsg = findViewById(R.id.inputMsg);

        btnSend = findViewById(R.id.btnSend);
    }
}
