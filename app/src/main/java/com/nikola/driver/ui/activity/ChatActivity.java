package com.nikola.driver.ui.activity;

import android.content.Intent;
import android.net.wifi.hotspot2.ConfigParser;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nikola.driver.R;
import com.nikola.driver.network.model.ChatObject;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIConstants.Constants;
import com.nikola.driver.network.newnetwork.APIConstants.Params;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.network.newnetwork.ParserUtils;
import com.nikola.driver.ui.adapter.ChatAdapter;
import com.nikola.driver.utils.AndyUtils;
import com.nikola.driver.utils.Const;
import com.nikola.driver.utils.PreferenceHelper;
import com.nikola.driver.utils.newutils.AppUtils;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nikola.driver.network.newnetwork.APIConstants.Params.REQUEST_ID;

public class ChatActivity extends AppCompatActivity implements ChatAdapter.ChatScreenInterface {

    public static final String PERSON_NAME = "personName";
    public static final String BOOKING_ID = "bookingId";
    public static final String PROVIDER_ID = "providerId";
    public static final String HOST_ID = "hostId";
    public static final String USER_ID = "userId";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.chatRecycler)
    RecyclerView chatRecycler;
    @BindView(R.id.msgToSend)
    EditText msgToSend;
    @BindView(R.id.sendMsgBtn)
    Button sendMsgBtn;
    @BindView(R.id.chatSendingLayout)
    LinearLayout chatSendingLayout;

    private Socket socket;
    private ChatAdapter chatAdapter;
    private int providerId;
    private int requestId;
    private String userId;
    private String providerPicture;
    private String personName;
    PrefUtils prefUtils;

    private APIInterface apiInterface;
    private ArrayList<ChatObject> chatMessages = new ArrayList<>();
    private RecyclerView.OnScrollListener chatScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (manager.findLastCompletelyVisibleItemPosition() == (chatAdapter.getItemCount() - 1)) {
                chatAdapter.showLoading();
            }
        }
    };
    //Socket stuff
    // Socket Initiating
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                sendMsgBtn.setEnabled(true);
                try {
                    JSONObject object = new JSONObject();
                    object.put(Params.COMMONID, MessageFormat.format("user_id_{0}_provider_id_{1}_request_id_{2}",
                                    String.valueOf(userId), String.valueOf(providerId), String.valueOf(requestId)));
                    object.put(Params.MYID, String.valueOf(providerId));
                    socket.emit("update sender", object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = args -> runOnUiThread(() -> {
        sendMsgBtn.setEnabled(false);
    });

    private Emitter.Listener onConnectError = args -> {
        this.runOnUiThread(() -> {
            try {
                UiUtils.showShortToast(this,getString(R.string.error_in_conneting_socket));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    };

    private Emitter.Listener onNewMessage = args -> runOnUiThread(() -> {
        try{
            JSONObject msgObj = (JSONObject) args[0];
            ChatObject msg = new ChatObject();
            msg.setMessageTime(msgObj.optString(Params.UPDATED_AT));
            msg.setMessageText(msgObj.optString(Params.MESSAGE));
            msg.setMyText(msgObj.optString(Params.CHAT_TYPE).equals(Constants.ChatMessageType.PROVIDER_TO_USER));
            msg.setPersonImage(msgObj.optString(Params.USER_PICTURE));
            msg.setProviderId(msgObj.optInt(Params.PROVIDER_ID));
            addNewMessageToChat(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }

    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(this);
        initSocket();
        Intent chatIntent = getIntent();
        personName = chatIntent.getStringExtra(PERSON_NAME);
        requestId = prefUtils.getIntValue(PrefKeys.REQUEST_ID, 0);
        userId = prefUtils.getStringValue(PrefKeys.USER_ID, "");
        providerId = prefUtils.getIntValue(PrefKeys.ID, 0);
        Log.e("provider", String.valueOf(providerId) + userId);
        providerPicture = prefUtils.getStringValue(PrefKeys.PICTURE, "");
        setUpToolBar();
        setUpChatStuff();
    }

    private void setUpChatStuff() {
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this, chatMessages);
        chatRecycler.setHasFixedSize(true);
        chatRecycler.setAdapter(chatAdapter);
        //Get chats
        getChatFromBackendWith(0);
    }

    private void setUpToolBar() {
        toolbar.setTitle(getString(R.string.message));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(ChatActivity.this, R.drawable.back));
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void fullScrollChatList() {
        chatRecycler.postDelayed(() -> chatRecycler.scrollToPosition(chatMessages.size() - 1), 100);
    }

    private void getChatFromBackendWith(int skip) {
        if (skip == 0) {
            chatRecycler.addOnScrollListener(chatScrollListener);
            UiUtils.showLoadingDialog(this);
        }
        PrefUtils preferences = PrefUtils.getInstance(this);
        Call<String> call = apiInterface.getChatDetails(preferences.getIntValue(PrefKeys.ID, 0),
                preferences.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                requestId,
                userId,
                skip);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject chatResponse = null;
                try {
                    chatResponse = new JSONObject(response.body());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (chatResponse != null)
                    if (chatResponse.optString(APIConstants.Params.SUCCESS).equals(Constants.TRUE)) {
                        JSONArray data = chatResponse.optJSONArray(APIConstants.Params.DATA);
                        ArrayList<ChatObject> temp = ParserUtils.parseChatObjects(data);
                        if (temp != null) {
                            if (!temp.isEmpty())
                                chatMessages.addAll(temp);
                            else
                                chatRecycler.removeOnScrollListener(chatScrollListener);
                        }
                        onChatScreenDataSetChanged();

                        if (chatMessages.isEmpty()) {
                            chatRecycler.setVisibility(View.GONE);
                        }
                    } else {
                        onChatScreenDataSetChanged();
                        UiUtils.showShortToast(ChatActivity.this, chatResponse.optString(Params.ERROR_MESSAGE));
                    }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                onChatScreenDataSetChanged();
                if(NetworkUtils.isNetworkConnected(getApplicationContext())) {
                    UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    private void onChatScreenDataSetChanged() {
        if (chatMessages.isEmpty()) {
            chatRecycler.setVisibility(View.GONE);
        } else {
            chatRecycler.setVisibility(View.VISIBLE);
        }
        chatAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.sendMsgBtn)
    protected void onNewMessageSend() {
        String newMessage = msgToSend.getText().toString();
        if (newMessage.trim().length() == 0)
            UiUtils.showShortToast(this, getString(R.string.cant_sent_empty_msg));
        else {
            if (socket.connected()) {
                ChatObject newChatObject = attemptToSendMessage(newMessage);
                if (newChatObject != null) {
                    addNewMessageToChat(newChatObject);
                    msgToSend.setText("");
                }
            }
        }
    }

    /**
     * Attempt to send message
     *
     * @param message message to be sent
     * @return the new message object. Add this to data set and update UI
     */
    private ChatObject attemptToSendMessage(String message) {
        if (!socket.connected()) return null;
        JSONObject messageObj = new JSONObject();
        try {
            messageObj.put(Params.USER_ID, userId);
            messageObj.put(Params.PROVIDER_ID, providerId);
            messageObj.put(Params.MESSAGE, message);
            messageObj.put(REQUEST_ID, requestId);
            messageObj.put(Params.CHAT_TYPE, Constants.ChatMessageType.PROVIDER_TO_USER);
            messageObj.put(Params.PROVIDER_PICTURE, prefUtils.getStringValue(PrefKeys.PICTURE, ""));

            socket.emit(Socket.EVENT_MESSAGE, messageObj);
        } catch (Exception e){
            e.printStackTrace();
        }
        ChatObject chatMessage = new ChatObject();
        chatMessage.setMessageTime(AppUtils.getTimeAgo(new Date().getTime()));
        chatMessage.setMyText(true);
        chatMessage.setProviderId(Integer.valueOf(userId));
        chatMessage.setPersonImage(providerPicture);
        chatMessage.setBookingId(requestId);
        chatMessage.setMessageText(message);
        return chatMessage;
    }

    private void addNewMessageToChat(ChatObject newMessage) {
        chatMessages.add(newMessage);
        onChatScreenDataSetChanged();
        fullScrollChatList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tearDownSocket();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UiUtils.hideKeyboard(ChatActivity.this);
    }

    @Override
    public void onLoadMoreChatsAfter(int skip) {
        getChatFromBackendWith(skip);
    }

    private void initSocket() {
        try {

            socket = IO.socket(APIConstants.URLs.SOCKET_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.on(Socket.EVENT_MESSAGE, onNewMessage);
        socket.connect();
    }

    private void tearDownSocket() {
        socket.disconnect();
        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.off(Socket.EVENT_MESSAGE, onNewMessage);
    }


}
