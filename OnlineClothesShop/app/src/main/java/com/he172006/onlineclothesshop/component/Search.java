package com.he172006.onlineclothesshop.component;








import static com.he172006.onlineclothesshop.R.id.searchInput;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.he172006.onlineclothesshop.DAO.ProductDAO;
import com.he172006.onlineclothesshop.R;
import com.he172006.onlineclothesshop.adapter.SearchResultAdapter;
import com.he172006.onlineclothesshop.entity.Product;


import java.util.ArrayList;
import java.util.List;




public class Search extends Fragment {
    private EditText searchInput;
    private RecyclerView searchResultsRecyclerView;
    private SearchResultAdapter searchAdapter;
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();
    private ProductDAO productDAO;










    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);








        searchInput = view.findViewById(R.id.searchInput);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));








        productDAO = new ProductDAO(getContext());
        allProducts = productDAO.getAllProducts();
        filteredProducts.addAll(allProducts);








        searchAdapter = new SearchResultAdapter(getContext(), filteredProducts);
        searchResultsRecyclerView.setAdapter(searchAdapter);








        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}








            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }








            @Override
            public void afterTextChanged(Editable s) {}
        });
        Log.d("SearchDebug", "SearchFragment onCreateView called");
        return view;
    }








    private void filterProducts(String query) {
        filteredProducts.clear();
        for (Product product : allProducts) {
            if (product.getProductName().toLowerCase().contains(query.toLowerCase())) {
                filteredProducts.add(product);
            }
        }
        searchAdapter.notifyDataSetChanged();
    }
}










