package com.nikola.driver.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nikola.driver.R;
import com.nikola.driver.network.model.CreditCard;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.ui.activity.PaymentsActivity;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreditCardAdapter extends RecyclerView.Adapter<CreditCardAdapter.CreditCardViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<CreditCard> cards;
    private APIInterface apiInterface;
    private CreditCard lastDefaultCard;
    private CreditCardInterface cardInterface;


    public CreditCardAdapter(Context context, ArrayList<CreditCard> cards) {
        this.cards = cards;
        this.context = context;
        this.cardInterface = (PaymentsActivity) context;
        this.apiInterface = APIClient.getClient().create(APIInterface.class);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public void onBindViewHolder(@NonNull CreditCardViewHolder viewHolder, int position) {
        CreditCard cardItem = cards.get(position);
        viewHolder.cardLastFour.setText(String.format("%s%s", context.getString(R.string.cardHint), cardItem.getCardLastFour()));
        viewHolder.cardType.setText(cardItem.getCardType());

        viewHolder.cardSelected.setBackgroundResource(
                cardItem.isDefault() ?
                        R.drawable.circle
                        : R.drawable.grey_dot);
        viewHolder.cardSelected.setOnClickListener(v -> {
            if (cards.size() > 1) {
                lastDefaultCard = getDefaultCard();
                setDefaultCardInLocal(cardItem);
                setDefaultCardInBacked(cardItem.getId());
            }
        });
        viewHolder.deleteCard.setOnClickListener(v -> deleteCardConfirm(cardItem));
    }

    private CreditCard getDefaultCard() {
        for (int i = 0; i < cards.size(); i++)
            if (cards.get(i).isDefault())
                return cards.get(i);
        return null;
    }

    private void setDefaultCardInLocal(CreditCard cardItem) {
        if (cardItem == null)
            return;
        CreditCard temp;
        for (int i = 0; i < cards.size(); i++) {
            temp = cards.get(i);
            temp.setIsDefault(temp.getId().equals(cardItem.getId()));
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return cards.size();
    }

    @NonNull
    @Override
    public CreditCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_credit_card, viewGroup, false);
        return new CreditCardViewHolder(view);
    }

    private void setDefaultCardInBacked(String cardId) {
        UiUtils.showLoadingDialog(context);
        Call<String> call = apiInterface.makeCardDefault(
                PrefUtils.getInstance(context).getIntValue(PrefKeys.ID, 0)
                , PrefUtils.getInstance(context).getStringValue(PrefKeys.SESSION_TOKEN, "")
                , cardId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject makeDefaultCardResponse = null;
                try {
                    makeDefaultCardResponse = new JSONObject(response.body());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (makeDefaultCardResponse != null) {
                    if (makeDefaultCardResponse.optString(APIConstants.Constants.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        UiUtils.showShortToast(context, makeDefaultCardResponse.optString(APIConstants.Params.MESSAGE));
                    } else {
                        UiUtils.showShortToast(context, makeDefaultCardResponse.optString(APIConstants.Params.ERROR_MESSAGE));
                        rollBackCardMadeDefault();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(NetworkUtils.isNetworkConnected(context)) {
                    UiUtils.showShortToast(context, context.getString(R.string.may_be_your_is_lost));
                }
                rollBackCardMadeDefault();
            }
        });
    }

    private void rollBackCardMadeDefault() {
        setDefaultCardInLocal(lastDefaultCard);
    }

    private void deleteCardConfirm(CreditCard cardToDelete) {
        new AlertDialog.Builder(context)
                .setMessage(String.format("%s delete %s card ending with %s?"
                        , context.getString(R.string.sure_to_text)
                        , cardToDelete.getCardType()
                        , cardToDelete.getCardLastFour()))
                .setTitle(context.getString(R.string.delete_confirmation))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(context.getString(R.string.yes), (dialog, which) -> deleteCardInBackend(cardToDelete))
                .setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> {
                }).create().show();
    }

    private void deleteCardInBackend(CreditCard cardToDelete) {
        UiUtils.showLoadingDialog(context);
        Call<String> call = apiInterface.deleteCard(
                PrefUtils.getInstance(context).getIntValue(PrefKeys.ID, 0)
                , PrefUtils.getInstance(context).getStringValue(PrefKeys.SESSION_TOKEN, "")
                , cardToDelete.getId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject deleteCardResponse = null;
                try {
                    deleteCardResponse = new JSONObject(response.body());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (deleteCardResponse != null) {
                    if (deleteCardResponse.optString(APIConstants.Constants.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        UiUtils.showShortToast(context, deleteCardResponse.optString(APIConstants.Params.MESSAGE));
                        cards.remove(cardToDelete);
                        notifyDataSetChanged();
                        cardInterface.onGetDefaultCard();
                        if (cards.size() == 0 && cardInterface != null) {
                            cardInterface.onCardUpdate(true);
                        }
                    } else {
                        UiUtils.showShortToast(context, deleteCardResponse.optString(APIConstants.Params.ERROR_MESSAGE));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(NetworkUtils.isNetworkConnected(context)) {
                    UiUtils.showShortToast(context, context.getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    public interface CreditCardInterface {
        void onCardUpdate(boolean isEmpty);
        void onGetDefaultCard();
    }

    class CreditCardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardLastFour)
        TextView cardLastFour;
        @BindView(R.id.cardType)
        TextView cardType;
        @BindView(R.id.cardSelected)
        ImageView cardSelected;
        @BindView(R.id.deleteCard)
        ImageView deleteCard;

        CreditCardViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
