package com.example.anzu.ui.goods;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.anzu.R;
import com.example.anzu.TestQuery;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Array;

public class GoodsShelvesActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {
    private Spinner mGoodsCategorySpinner = null;
    private Spinner mGoodsPriceDateSpinner = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_shelves);

        mGoodsCategorySpinner = (Spinner) findViewById(R.id.goods_category_content);
        mGoodsPriceDateSpinner = (Spinner) findViewById(R.id.goods_price_date);
        String[] arr1 = {"电子数码", "服装饰品", "居家生活", "电子娱乐", "其他类别"};
        String[] arr2 = {"天", "周", "月", "季", "年"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr2);

        mGoodsCategorySpinner.setAdapter(adapter1);
        mGoodsPriceDateSpinner.setAdapter(adapter2);

        mGoodsCategorySpinner.setOnItemSelectedListener(this);
        mGoodsPriceDateSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String content = parent.getItemAtPosition(position).toString();
        switch (parent.getId()) {
            case R.id.goods_category_content:
                Toast.makeText(GoodsShelvesActivity.this, "您选择的商品类别是：" + content, Toast.LENGTH_SHORT).show();
                break;
            case R.id.goods_price_date:
                Toast.makeText(GoodsShelvesActivity.this, "您选择按" + content + "租", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}