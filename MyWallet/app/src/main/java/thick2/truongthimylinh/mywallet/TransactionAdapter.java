package thick2.truongthimylinh.mywallet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter
        extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    List<TransactionModel> list;

    OnTransactionLongClick listener;

    public interface OnTransactionLongClick {

        void onLongClick(TransactionModel transaction);
    }

    public TransactionAdapter(
            List<TransactionModel> list,
            OnTransactionLongClick listener
    ) {

        this.list = list;

        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_transaction,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        TransactionModel model =
                list.get(position);

        holder.txtCategory.setText(
                model.getCategory()
        );

        holder.txtAmount.setText(
                model.getAmount() + "đ"
        );

        holder.txtNote.setText(
                model.getNote()
        );

        if ("INCOME".equals(model.getType())) {

            holder.txtAmount.setTextColor(
                    0xFF1E9E62
            );

        } else {

            holder.txtAmount.setTextColor(
                    0xFFFF5D7A
            );
        }

        holder.cardTransaction
                .setOnLongClickListener(v -> {

                    listener.onLongClick(model);

                    return true;
                });
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtCategory,
                txtAmount,
                txtNote;

        CardView cardTransaction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCategory =
                    itemView.findViewById(
                            R.id.txtCategory
                    );

            txtAmount =
                    itemView.findViewById(
                            R.id.txtAmount
                    );

            txtNote =
                    itemView.findViewById(
                            R.id.txtNote
                    );

            cardTransaction =
                    itemView.findViewById(
                            R.id.cardTransaction
                    );
        }
    }
}
