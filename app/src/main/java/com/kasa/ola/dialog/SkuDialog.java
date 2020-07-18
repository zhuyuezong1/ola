package com.kasa.ola.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.kasa.ola.R;
import com.kasa.ola.bean.entity.PriceGroupBean;
import com.kasa.ola.bean.entity.ProductInfoBean;
import com.kasa.ola.bean.entity.SkuBean;
import com.kasa.ola.bean.model.PriceGroupModel;
import com.kasa.ola.bean.model.ProductSkuModel;
import com.kasa.ola.ui.ProductInfoActivity;
import com.kasa.ola.ui.adapter.ProductSkuAdapter;
import com.kasa.ola.ui.adapter.SkuAdapter;
import com.kasa.ola.ui.adapter.SkuTestAdapter;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SkuDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private LoadMoreRecyclerView skuRecycleView;
    private OnConfirmListener onConfirmListener;
    private int num = 1;
    private int maxNum = 1;
    private TextView tvNum;
    private TextView tvProductFormat;
    private ImageView ivProduct;
    private TextView tvProductPrice;
    private SkuTestAdapter skuTestAdapter;
    private ProductInfoBean productInfoBean;
    private final List<ProductInfoBean.SpecificationsBean> specifications;
    private final List<ProductInfoBean.PriceGroupBean> priceGroup;
    private List<String> selectSpecsGroupList;
    private List<String> changeSpecsGroupList;
    private List<List<String>> allSpecsGroupList;
    private ArrayList<List<Integer>> selectList;
    private HashMap<Integer, String> map;
    private SkuAdapter skuAdapter;

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public interface OnConfirmListener {

        void confirm(String groupContent, String skuGroup, String skuFullGroup, String price, int num);
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bg_container:
                dismiss();
                break;
            case R.id.view_container:
                break;
            case R.id.btn_confirm:

                break;
            case R.id.btn_reduce:
                if (num > 1) {
                    num = num-1;
                    tvNum.setText(String.valueOf(num));
                } else {
                    ToastUtils.showShortToast(context, "至少要选择一件哟");
                }
                break;
            case R.id.btn_add:
                if (num < maxNum) {
                    num = num+1;
                    tvNum.setText(String.valueOf(num));
                } else {
                    ToastUtils.showShortToast(context, "最多只能选择" + maxNum + "件哟");
                }
                break;
        }
    }

    public SkuDialog(@NonNull Context context, ProductInfoBean productInfoBean) {
        super(context, R.style.fade_dialog_style);
        this.context = context;
        this.productInfoBean = productInfoBean;
        specifications = productInfoBean.getSpecifications();
        priceGroup = productInfoBean.getPriceGroup();
        map = new HashMap<>();
        initView();
    }

    private void initView() {
        setContentView(R.layout.product_sku_choose_layout);
        Window win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);
        skuRecycleView = findViewById(R.id.sku_recycle_view);

        skuRecycleView.setLayoutManager(new LinearLayoutManager(context));


        //****************************初始化数据**********************************/
        for (int i = 0; i < specifications.size(); i++) {
            for (int j = 0; j < specifications.get(i).getSpeItem().size(); j++) {
                specifications.get(i).getSpeItem().get(j).setCanSelect(true);
            }
        }
//******************开始匹配前再把所有没货的id挑出来(开始)******************/
        List<String> allIDList = new ArrayList<>();
        List<String> allIDList1 = new ArrayList<>();
        List<String> allList = new ArrayList<>();
        for (int n = 0; n < priceGroup.size(); n++) {
            for (int i = 0; i < specifications.size(); i++) {
                for (int j = 0; j < specifications.get(i).getSpeItem().size(); j++) {
                    allList.add(specifications.get(i).getSpeItem().get(j).getSpeID());//所有规格id列表
                    if (priceGroup.get(n).getGroupContent().contains(specifications.get(i).getSpeItem().get(j).getSpeID())) {
                        if (priceGroup.get(n).getInventory()<=0){
                            allIDList.add(priceGroup.get(n).getGroupContent());
                        }
                    }
                }
            }
        }
        for (int i = 0; i < allIDList.size(); i++) {
            List<String> list = Arrays.asList(allIDList.get(i).split("_"));
            for (String s : list) {
                allIDList1.add(s);
            }
        }
        allList.removeAll(allIDList1);
        for (int n = 0; n < allList.size(); n++) {
            for (int i = 0; i < specifications.size(); i++) {
                for (int j = 0; j < specifications.get(i).getSpeItem().size(); j++) {
                    if (allList.get(n).contains(specifications.get(i).getSpeItem().get(j).getSpeID())) {
                        specifications.get(i).getSpeItem().get(j).setCanSelect(false);
                    }
                }
            }
        }
        skuAdapter = new SkuAdapter(context,productInfoBean);
        skuRecycleView.setAdapter(skuAdapter);
        skuAdapter.setTagItemOnClickListener(new SkuAdapter.TagItemOnClick() {
            @Override
            public void onItemClick(View view, int tagPosition, int position) {
                if (specifications.get(position).getSpeItem().get(tagPosition).isCanSelect()) {
                    if (specifications.get(position).getSpeItem().get(tagPosition).isSelect()) {
                        map.remove(position);
                        specifications.get(position).getSpeItem().get(tagPosition).setSelect(false);
                    } else {
                        map.put(position, specifications.get(position).getSpeItem().get(tagPosition).getSpeID());
                        for (int i = 0; i < specifications.get(position).getSpeItem().size(); i++) {
                            specifications.get(position).getSpeItem().get(i).setSelect(false);
                        }
                        specifications.get(position).getSpeItem().get(tagPosition).setSelect(true);
                    }
                    //*********************布局切换相关(结束)*********************/
                    if (map.size() == skuAdapter.getItemCount()) {
                        Log.v("规格", "开始匹配");
                        String ids = "";
                        String ids1 = "";
                        for (int i = 0; i < map.size(); i++) {
                            ids += map.get(i) + ",";
                        }
                        if (map.size() >= 1) {
                            for (int i = map.size() - 1; i >= 0; i--) {
                                ids1 += map.get(i) + ",";
                            }
                        }
                        boolean isHave = false;
                        for (int i = 0; i < priceGroup.size(); i++) {
                            String idString = priceGroup.get(i).getGroupContent();
                            if (ids.contains(idString) || ids1.contains(idString)) {
                                isHave = true;
                                Log.v("规格", "匹配到了");
//                                tvMoney.setText("¥ " + BigDecimalUtils.toDecimal(thingsDetails.getSpecList().get(i).getPlatform_price(), 2));
//                                tvLeaveNumber.setText("库存" + thingsDetails.getSpecList().get(i).getNumber() + "件");
//                                ((TextView) dialogView.findViewById(R.id.tv_have_choose)).setText("已选择 " + thingsDetails.getSpecList().get(i).getSpec_value_texts());
//                                sum = thingsDetails.getSpecList().get(i).getNumber();
                            }
                        }
                        if (!isHave) {
//                            Utils.setImageViewSigle(thingsDetails.getPics(), ivImage, mContext);
//                            tvMoney.setText("¥ " + BigDecimalUtils.toDecimal(thingsDetails.getPlatform_price(), 2));
//                            tvLeaveNumber.setText("库存" + 0 + "件");
//                            ((TextView) dialogView.findViewById(R.id.tv_have_choose)).setText("已选择");
//                            sum = 0;
//                            Toast.makeText(MainActivity.this, "该规格没库存了", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //****************开始匹配********************/
                    for (int i = 0; i < specifications.size(); i++) {
                        for (int j = 0; j < specifications.get(i).getSpeItem().size(); j++) {
                            specifications.get(i).getSpeItem().get(j).setCanSelect(true);
                        }
                    }
                    List<String> idValue = new ArrayList<>();//所选的id在所有可选规格里的可能id
                    List<String> idList = new ArrayList<>();//把这些id拆开，装里面
                    List<String> allIdList = new ArrayList<>();//所有的可能id
                    boolean isMath = true;//是否开始进行匹配
                    //******************如果所有配对都有货就不进行以下所有的匹配了(开始)**********/
                    int totalType = 1;
                    for (int i = 0; i < specifications.size(); i++) {
                        totalType = totalType * specifications.get(i).getSpeItem().size();
                    }
                    if (totalType == priceGroup.size()) {
                        isMath = false;
                    }
                    //******************如果所有配对都有货就不进行以下所有的匹配了(结束)**********/
                    for (int i = 0; i < specifications.size(); i++) {
                        for (Map.Entry<Integer, String> m : map.entrySet()) {
                            if (priceGroup.get(i).getGroupContent().contains(m.getValue())) {
                                idValue.add(priceGroup.get(i).getGroupContent());
                            }
                        }
                    }
                    if (map.size() == 0) {//如果现在取消刚在选中的就当前没有一个被选中就不匹配了
                        isMath = false;
                    }
                    if (isMath) {
                        for (int i = 0; i < specifications.size(); i++) {
                            for (int j = 0; j < specifications.get(i).getSpeItem().size(); j++) {
                                allIdList.add(specifications.get(i).getSpeItem().get(j).getSpeID());
                            }
                        }
                        for (int i = 0; i < idValue.size(); i++) {
                            List<String> list = Arrays.asList(idValue.get(i).split(","));
                            for (String s : list) {
                                idList.add(s);
                            }
                        }
                        allIdList.removeAll(idList);
                        for (int n = 0; n < allIdList.size(); n++) {
                            for (int i = 0; i < specifications.size(); i++) {
                                for (int j = 0; j < specifications.get(i).getSpeItem().size(); j++) {
                                    if (allIdList.get(n).contains(specifications.get(i).getSpeItem().get(j).getSpeID())) {
                                        specifications.get(i).getSpeItem().get(j).setCanSelect(false);
                                    }
                                }
                            }
                        }
                        for (int i = 0; i < specifications.size(); i++) {
                            for (int j = 0; j < specifications.get(i).getSpeItem().size(); j++) {
                                Log.v("规格打印", specifications.get(i).getSpeItem().get(j).getSpeID() + ":" + specifications.get(i).getSpeItem().get(j).isCanSelect() + "");
                            }
                        }
                    }
                }
                //***********************匹配结束******************************/
                skuAdapter.notifyDataSetChanged();
                Log.v("规格", "" + specifications.get(position).getSpeItem().get(tagPosition).isSelect());
                Log.v("规格", position + "," + map.get(position));
            }
        });
//        skuTestAdapter = new SkuTestAdapter(context, skuBeans,priceGroups);
//        skuTestAdapter.setOnLabelClickListener(new SkuTestAdapter.OnLabelClickListener() {
//            @Override
//            public void onSelect(int position, int labelPosition) {
////                getNewData(position,labelPosition, skuTestAdapter.getSelectSpeIdList());
//
//            }
//
//            @Override
//            public void onCancel(int position, int labelPosition) {
//            }
//        });
//        skuRecycleView.setAdapter(skuTestAdapter);

        findViewById(R.id.bg_container).setOnClickListener(this);
        findViewById(R.id.view_container).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);

        View headView = LayoutInflater.from(context).inflate(R.layout.view_mall_sku_head, skuRecycleView, false);

        ivProduct = headView.findViewById(R.id.iv_product);
        tvProductPrice = headView.findViewById(R.id.tv_product_price);
        tvProductFormat = headView.findViewById(R.id.tv_product_format);

        for (int i = 0; i < productInfoBean.getImgArr().size(); i++) {
            if (i == 0) {
                ImageLoaderUtils.imageLoad(context, productInfoBean.getImgArr().get(i).getImageUrl(), ivProduct);
            }
        }

        tvProductPrice.setText("￥"+productInfoBean.getSpecialPrice());

        TextView btnReduce = headView.findViewById(R.id.btn_reduce);
        tvNum = headView.findViewById(R.id.tv_num);
        TextView btnAdd = headView.findViewById(R.id.btn_add);
        btnReduce.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        tvNum.setText(num + "");
        tvNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviseNumDialog.Builder builder = new ReviseNumDialog.Builder(context);
                builder
                        .setLeftButton(context.getString(R.string.cancel))
                        .setRightButton(context.getString(R.string.ok))
                        .setDialogInterface(new ReviseNumDialog.DialogInterface() {

                            @Override
                            public void leftButtonClick(ReviseNumDialog dialog) {
                                dialog.dismiss();
                            }

                            @Override
                            public void rightButtonClick(ReviseNumDialog dialog, final int number) {
                                dialog.dismiss();
                                if (number <= 0) {
                                    ToastUtils.showLongToast(context, context.getString(R.string.no_product_prompt));
                                }else {
                                    num = number;
                                    tvNum.setText(num + "");
                                }
                            }
                        })
                        .create(num)
                        .show();
            }
        });
        skuRecycleView.addHeaderView(headView);
    }

//    private void getNewData(int position, int labelPosition, List<String> selectSpeIdList) {
//        List<PriceGroupBean> priceGroups = getPriceGroups(selectSpeIdList, this.priceGroups);//当前选中规格遍历结果
//        for (int i=0;i<selectSpeIdList.size();i++){
//            SkuBean skuBean = skuBeans.get(i);
//            if (position!=i & TextUtils.isEmpty(selectSpeIdList.get(i))){
//                long total=0;
//                for (int x=0;x<skuBean.getSpeItem().size();x++){
//                    for (int j=0;j<priceGroups.size();j++){
//                        if (priceGroups.get(j).getGroupContent().contains(skuBean.getSpeItem().get(x).getSpeID()+"")){
//                            total+=priceGroups.get(j).getInventory();
//                        }
//                    }
//                    if (total>0){
//                        skuBean.getSpeItem().get(x).setClickable(true);
//                    }else {
//                        skuBean.getSpeItem().get(x).setClickable(false);
//                    }
//                }
//            }
//        }
//        skuTestAdapter.notifyDataSetChanged();
//    }
//    private List<PriceGroupBean> getPriceGroups(List<String> selectSpeIdList,List<PriceGroupBean> priceGroups){
//        List<PriceGroupBean> priceGroupBeans = new ArrayList<>();
//        List<PriceGroupBean> objects = new ArrayList<>();
//        objects.addAll(priceGroups);
//        for (int j=0;j<selectSpeIdList.size();j++){
//            objects = getSingle(selectSpeIdList.get(j), objects);
//        }
//        priceGroupBeans.addAll(objects);
//        return priceGroupBeans;
//    }
//    private List<PriceGroupBean> getSingle(String speID,List<PriceGroupBean> priceGroups){
//        ArrayList<PriceGroupBean> priceGroupBeans = new ArrayList<>();
//        for (int i=0;i<priceGroups.size();i++){
//            if (priceGroups.get(i).getGroupContent().contains(speID)){
//                priceGroupBeans.add(priceGroups.get(i));
//            }
//        }
//        return priceGroupBeans;
//    }
//private void addView() {
//    adapterList = new ArrayList<>();
//    //规格组合的第一个 为选择
//    if (priceGroup.size() > 0 || priceGroup != null) {
//        selectSpecsGroupList = priceGroup.get(0).getGroupContent();
//
//        changeSpecsGroupList = new ArrayList<>();
//        changeSpecsGroupList.addAll(selectSpecsGroupList);
//    }
//
//    //后台所给的所有规格组合的集合
//    allSpecsGroupList = new ArrayList<>();
//    for (Data.SpecsGroupBean specsGroupBean : specsGroupList) {
//        List<String> list = specsGroupBean.getGoods_spec();
//        allSpecsGroupList.add(list);
//    }
//
//    selectList = new ArrayList<>();
//    for (int i = 0; i < specKeyList.size(); i++) {
//        List<Integer> list = new ArrayList<>();
//        //获取规格
//        List<String> specKeyString = specKeyList.get(i).getSpec_key();
//        for (int j = 0; j < specKeyString.size(); j++) {
//            if (specKeyString.get(j).equals(selectSpecsGroupList.get(i))) {
//                list.add(j, 1);
//            } else {
//                list.add(j, 0);
//            }
//        }
//        selectList.add(i, list);
//    }
////添加View
//    for (int i = 0; i < specKeyList.size(); i++) {
//        //AddView
//        View view = LayoutInflater.from(context).inflate(R.layout.item_specs_key, null);
//        TextView tvSpecKey = (TextView) view.findViewById(R.id.tv_spec_key);
//        CustomListView customListView = (CustomListView) view.findViewById(R.id.custom_list_view);
//        //
//        Data.SpecKeyBean specKeyBean = specKeyList.get(i);
//        //各规格属性联动
//        getSetting(i, specKeyBean);
//        //设置数据
//        tvSpecKey.setText(specKeyBean.getSpec_name());
//        SpecsGroupAdapter specGroupAdapter = new SpecsGroupAdapter(context, specKeyBean.getSpec_key(), selectList.get(i));
//        adapterList.add(specGroupAdapter);
//        customListView.setAdapter(specGroupAdapter);
//        final int finalI = i;
//        specGroupAdapter.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                int clickFormat = finalI;
//                Data.SpecKeyBean clickSpecKeyBean = specKeyList.get(clickFormat);
//                //步骤1：先处理点击事件
//                if (selectList.get(clickFormat).get(position) != 2) {
//                    //遍历一下
//                    for (int i = 0; i < selectList.get(clickFormat).size(); i++) {
//                        switch (selectList.get(clickFormat).get(i)) {
//                            case 0:
//                                if (i == position) {
//                                    selectList.get(clickFormat).set(i, 1);
//                                }
//                                break;
//                            case 1:
//                                selectList.get(clickFormat).set(i, 0);
//                                break;
//                            case 2:
//                                break;
//                        }
//                    }
//                }
//
//                //点击之后的  各规格的选择状态
//                List<String> stringsList = new ArrayList<>();
//                stringsList = clickSpecKeyBean.getSpec_key();
//                if (selectList.get(clickFormat).contains(1)) {
//                    for (int g = 0; g < selectList.get(clickFormat).size(); g++) {
//                        if (selectList.get(clickFormat).get(g) == 1) {
//                            changeSpecsGroupList.set(clickFormat, stringsList.get(g));
//                        }
//                    }
//                } else {
//                    changeSpecsGroupList.set(clickFormat, "未选");
//                }
//                //根据选择的组合 设置价格，库存，图片
//                if (!changeSpecsGroupList.contains("未选")) {
//                    StringBuilder sb = new StringBuilder();
//                    for (String s : changeSpecsGroupList) {//
//                        sb.append(s).append(",");
//                    }
//                    String newString = sb.toString();
//                    for (Data.SpecsGroupBean specBean : specsGroupList) {
//                        StringBuilder sbSpec = new StringBuilder();
//                        for (String s : specBean.getGoods_spec()) {
//                            sbSpec.append(s).append(",");
//                        }
//                        //这样就可以把集合转化为字符串了
//                        String specString = sbSpec.toString();
//                        if (newString.contains(specString)) {
//                            pic_url = specBean.getImg();
//                            Glide.with(context).load(pic_url).into(iv_goods_pic);
//                            tv_goods_price.setText(specBean.getPrice());
//                            repertory_counts = Integer.parseInt(specBean.getRepertory());
//                            counts = 1;
//                            tv_counts.setText(counts + "");
//                            iv_minus_counts.setImageResource(R.mipmap.icon_minus_light);
//                            if (repertory_counts == 1) {
//                                iv_plus_counts.setImageResource(R.mipmap.icon_plus_light);
//                            } else {
//                                iv_plus_counts.setImageResource(R.mipmap.icon_plus_deep);
//                            }
//                        }
//                    }
//                }
//                for (int i = 0; i < specKeyList.size(); i++) {
//                    Data.SpecKeyBean specKeyBean = specKeyList.get(i);
//                    //各规格属性联动
//                    getSetting(i, specKeyBean);
//                    adapterList.get(i).notifyDataSetChanged();
//                }
//            }
//        });
//
//        ln_add_view.addView(view);
//    }
//}
//
//    //各规格属性联动
//    private void getSetting(int position, ProductInfoBean.SpecificationsBean specificationsBean) {
//        ArrayList<List<String>> list = new ArrayList<>();
//        list.addAll(allSpecsGroupList);
//        //不含有已选规格属性的属性组合的数组
//        ArrayList<List<String>> remove_list = new ArrayList<>();
//        //遍历数据中 所有规则属性的组合
//        for (int goods_i = 0; goods_i < list.size(); goods_i++) {
//            List<String> goodsList = list.get(goods_i);
//            //遍历选中组合
//            for (int select_i = 0; select_i < changeSpecsGroupList.size(); select_i++) {
//                //去掉一个规格
//                if (select_i == position) {
//
//                } else {
//                    if (!changeSpecsGroupList.get(select_i).equals(goodsList.get(select_i))) {
//                        if (!changeSpecsGroupList.get(select_i).equals("未选")) {
//                            remove_list.add(list.get(goods_i));
//                        }
//                    }
//                }
//            }
//        }
//        //除掉不含有已选规格属性的属性组合的数组  得到在包含此规格外 其他规格已选属性的组合的数组
//        list.removeAll(remove_list);
//        //aloneString组合为 该规格下 所能有的全部属性
//        ArrayList<String> aloneString = new ArrayList<>();
//        for (int j = 0; j < list.size(); j++) {
//            List<String> specialList = list.get(j);
//            aloneString.add(specialList.get(position));
//        }
//        //通过这些字段组合 去对比此规格的所有样式  然后设置 是否有此规格组合
//        for (int m = 0; m < specificationsBean.getSpeItem().size(); m++) {
//            if (!aloneString.contains(specificationsBean.getSpeItem().get(m))) {
//                selectList.get(position).set(m, 2);
//            } else {
//                switch (selectList.get(position).get(m)) {
//                    case 0:
//                        selectList.get(position).set(m, 0);
//                        break;
//                    case 1:
//                        selectList.get(position).set(m, 1);
//                        break;
//                    case 2:
//                        selectList.get(position).set(m, 0);
//                        break;
//                }
//            }
//        }
//
//    }
}
