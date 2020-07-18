package com.kasa.ola.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.kasa.ola.R;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab2.photoview2.PhotoView;


public class ImagePreviewFragment extends Fragment implements View.OnClickListener {

    public final String FILE_KEY = "FILE_KEY";
    public final String FILE_SELECT_KEY = "FILE_SELECT_KEY";
    public final String FILE_ALL_KEY = "FILE_ALL_KEY";

    private ArrayList<File> allFileList;
    private ArrayList<View> viewList = new ArrayList<>();
    private ArrayList<File> selectFileList;

    private ImageView selectBtn;

    private int currentIndex;
    private PreviewListener previewListener;

    public interface PreviewListener {
        void onFinish();
    }

    public void setPreviewListener(PreviewListener previewListener) {
        this.previewListener = previewListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        File file = (File) getArguments().getSerializable(FILE_KEY);
        allFileList = (ArrayList<File>) getArguments().getSerializable(FILE_ALL_KEY);
        selectFileList = (ArrayList<File>) getArguments().getSerializable(FILE_SELECT_KEY);
        currentIndex = allFileList.indexOf(file);

        int length = allFileList.size();
        for (int i = 0; i < length; i++) {
            PhotoView photoView = new PhotoView(getActivity());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            photoView.setLayoutParams(layoutParams);
            ImageLoaderUtils.imageLoadFile(getActivity(), allFileList.get(i), photoView);
            viewList.add(photoView);
        }

        View rootView = inflater.inflate(R.layout.image_preview_layout_view, container, false);
        rootView.findViewById(R.id.iv_back).setOnClickListener(this);
        selectBtn = rootView.findViewById(R.id.btn_select);
        selectBtn.setOnClickListener(this);
        rootView.findViewById(R.id.btn_finish).setOnClickListener(this);

        ViewPager previewPager = rootView.findViewById(R.id.preview_page);
        ImagePreviewAdapter imagePreviewAdapter = new ImagePreviewAdapter();
        previewPager.setAdapter(imagePreviewAdapter);
        previewPager.setCurrentItem(currentIndex);
        previewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                updateCheckStatus();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        updateCheckStatus();
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (null != previewListener) {
            previewListener.onFinish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                getFragmentManager().popBackStack();
                break;
            case R.id.btn_finish:
                getFragmentManager().popBackStack();
                //以下是点击完成后返回选中图片的逻辑
//                if (selectFileList.size() == 0) {
//                    ToastUtils.showShortToast(getContext(), "请选择一张图片");
//                    return;
//                }
//                Intent intent = new Intent();
//                intent.putExtra(Const.SELECTED_FILE_KEY, selectFileList.get(0));
//                getActivity().setResult(getActivity().RESULT_OK, intent);
//                getFragmentManager().popBackStack();
//                if (null != getActivity() ) {
//                    getActivity().finish();
//                }
                break;
            case R.id.btn_select:
                changeCheck();
                break;
        }
    }

    private void changeCheck() {
        File file = allFileList.get(currentIndex);
        if (selectFileList.contains(file)) {
            selectFileList.clear();
            selectBtn.setImageResource(R.mipmap.ico_picchoice_dis);
        } else {
            selectFileList.clear();
            selectFileList.add(file);
            selectBtn.setImageResource(R.mipmap.selected_icon);
        }
    }

    private void updateCheckStatus() {
        File file = allFileList.get(currentIndex);
        if (selectFileList.contains(file)) {
            selectBtn.setImageResource(R.mipmap.selected_icon);
        } else {
            selectBtn.setImageResource(R.mipmap.ico_picchoice_dis);
        }
    }

    private class ImagePreviewAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return allFileList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }
    }

    public void finish() {
        FragmentManager fragmentManager = getFragmentManager();
        if (null != fragmentManager) {
            int count = fragmentManager.getBackStackEntryCount();
            if (count > 0) {
                fragmentManager.popBackStack();
            } else {
                getActivity().finish();
            }
        }
    }

}
