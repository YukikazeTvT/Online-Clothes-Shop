package com.he172006.onlineclothesshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.he172006.onlineclothesshop.DAO.CartDAO;
import com.he172006.onlineclothesshop.DAO.ProductDAO;
import com.he172006.onlineclothesshop.R;
import com.he172006.onlineclothesshop.entity.Cart;
import com.he172006.onlineclothesshop.entity.Product;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Cart> cartList;
    private List<Boolean> selectedItems;
    private CartDAO cartDAO;
    private ProductDAO productDAO;
    private OnCartChangeListener onCartChangeListener;

    public interface OnCartChangeListener {
        void onCartChanged(double totalPrice);
    }

    public CartAdapter(Context context, List<Cart> cartList, OnCartChangeListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.selectedItems = new ArrayList<>();
        for (int i = 0; i < cartList.size(); i++) {
            selectedItems.add(false);
        }
        this.cartDAO = new CartDAO(context);
        this.productDAO = new ProductDAO(context);
        this.onCartChangeListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cart = cartList.get(position);
        Product product = productDAO.getProductById(cart.getProductId());

        if (product != null) {
            holder.txtProductName.setText(product.getProductName());
            holder.txtProductPrice.setText("USD$" + product.getPrice());
            holder.txtQuantity.setText(String.valueOf(cart.getQuantity()));
            Glide.with(context).load(product.getImage()).into(holder.imgProduct);
        }

        holder.chkSelect.setChecked(selectedItems.get(position));
        holder.chkSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            selectedItems.set(position, isChecked);
            notifyCartChange(); // Cập nhật tổng giá sau khi thay đổi trạng thái chọn
        });

        holder.btnIncrease.setOnClickListener(v -> {
            int newQuantity = cart.getQuantity() + 1;
            if (product != null) {
                if (newQuantity <= product.getStock()) {
                    cart.setQuantity(newQuantity);
                    cartDAO.updateCart(cart);
                    holder.txtQuantity.setText(String.valueOf(newQuantity));
                    notifyCartChange();
                } else {
                    Toast.makeText(context, "Số lượng vượt quá tồn kho!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int newQuantity = cart.getQuantity() - 1;
            if (newQuantity > 0) {
                cart.setQuantity(newQuantity);
                cartDAO.updateCart(cart);
                holder.txtQuantity.setText(String.valueOf(newQuantity));
                notifyCartChange();
            } else {
                Toast.makeText(context, "Số lượng không thể nhỏ hơn 1!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            cartDAO.deleteCart(cart.getCartId());
            cartList.remove(position);
            selectedItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartList.size());
            notifyCartChange();
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void selectAll(boolean isSelected) {
        for (int i = 0; i < selectedItems.size(); i++) {
            selectedItems.set(i, isSelected);
        }
        notifyDataSetChanged();
        notifyCartChange();
    }

    public List<Cart> getSelectedItems() {
        List<Cart> selectedCarts = new ArrayList<>();
        for (int i = 0; i < cartList.size(); i++) {
            if (selectedItems.get(i)) {
                selectedCarts.add(cartList.get(i));
            }
        }
        return selectedCarts;
    }

    public void removeSelectedItems() {
        List<Cart> selectedCarts = getSelectedItems();
        for (Cart cart : selectedCarts) {
            cartDAO.deleteCart(cart.getCartId());
            int position = cartList.indexOf(cart);
            cartList.remove(cart);
            selectedItems.remove(position);
            notifyItemRemoved(position);
        }
        notifyItemRangeChanged(0, cartList.size());
        notifyCartChange();
    }

    public double calculateTotalPrice() {
        double total = 0;
        for (int i = 0; i < cartList.size(); i++) {
            if (selectedItems.get(i)) { // Chỉ tính tổng giá của các sản phẩm được chọn
                Cart cart = cartList.get(i);
                Product product = productDAO.getProductById(cart.getProductId());
                if (product != null) {
                    total += product.getPrice() * cart.getQuantity();
                }
            }
        }
        return total;
    }

    private void notifyCartChange() {
        double totalPrice = calculateTotalPrice();
        onCartChangeListener.onCartChanged(totalPrice);
    }

    // Đóng DAO khi adapter bị hủy
    public void close() {
        if (cartDAO != null) {
            cartDAO.close();
        }
        if (productDAO != null) {
            productDAO.close();
        }
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox chkSelect;
        ImageView imgProduct;
        TextView txtProductName, txtProductPrice, txtQuantity;
        Button btnIncrease, btnDecrease, btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            chkSelect = itemView.findViewById(R.id.chkSelect);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}