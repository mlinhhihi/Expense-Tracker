package thick2.truongthimylinh.mywallet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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

        try {

            double amount =
                    Double.parseDouble(
                            model.getAmount()
                    );

            java.text.DecimalFormat format =
                    new java.text.DecimalFormat("#,###");

            String money =
                    format.format(amount)
                            .replace(",", ".");

            if ("INCOME".equals(model.getType())) {

                holder.txtAmount.setText(
                        "+ " + money + "đ"
                );

            } else {

                holder.txtAmount.setText(
                        "- " + money + "đ"
                );
            }

        } catch (Exception e) {

            if ("INCOME".equals(model.getType())) {

                holder.txtAmount.setText(
                        "+ " + model.getAmount() + "đ"
                );

            } else {

                holder.txtAmount.setText(
                        "- " + model.getAmount() + "đ"
                );
            }
        }

        holder.txtNote.setText(
                model.getNote()
        );

        // hiển thị thời gian
        if (model.getTime() != null) {

            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm",
                            Locale.getDefault()
                    );

            holder.txtTime.setText(
                    sdf.format(
                            model.getTime().toDate()
                    )
            );
        }

        // màu tiền
        if ("INCOME".equals(model.getType())) {

            holder.txtAmount.setTextColor(
                    0xFF1E9E62
            );

        } else {

            holder.txtAmount.setTextColor(
                    0xFFFF5D7A
            );
        }

        // long click
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

    public void updateList(List<TransactionModel> newList) {

        list = newList;

        notifyDataSetChanged();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtCategory,
                txtAmount,
                txtNote,
                txtTime;

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

            txtTime =
                    itemView.findViewById(
                            R.id.txtTime
                    );

            cardTransaction =
                    itemView.findViewById(
                            R.id.cardTransaction
                    );
        }
    }
}