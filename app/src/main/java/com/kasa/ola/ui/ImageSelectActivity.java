package com.kasa.ola.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.manager.Const;
import com.kasa.ola.ui.adapter.FolderItemAdapter;
import com.kasa.ola.ui.adapter.ImageSelectAdapter;
import com.kasa.ola.ui.fragment.ImagePreviewFragment;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ToastUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class ImageSelectActivity extends BaseActivity implements View.OnClickListener {

    private final int CAMERA_REQUEST_CODE = 0x1001;

    private ArrayList<File> allImageList = new ArrayList<>();//所有照片集合
    private ArrayList<ArrayList<File>> folderImages = new ArrayList<>();//每个照片文件夹集合
    private ArrayList<File> folderList = new ArrayList<>();//照片文件夹名集合
    private ImageSelectAdapter imageSelectAdapter;
    private PopupWindow imageFolderPop;
    private int selectPosition = 0;
    private View bottomView;
    private TextView imgFolderText, tvFinish;
    private File cameraFile;

    @Override
    protected void onCreate(Bundle arg0) {
        translucentStatus = false;
        super.onCreate(arg0);

        setContentView(R.layout.image_select_layout_view);

        setActionBar("选择照片", "");

        imgFolderText = findViewById(R.id.img_folder_text);    //选择相册按钮
        imgFolderText.setOnClickListener(this);
        bottomView = findViewById(R.id.select_bottom_view);
        bottomView.setOnClickListener(this);
        tvFinish = findViewById(R.id.tv_finish);
        tvFinish.setOnClickListener(this);

        RecyclerView imageShowView = findViewById(R.id.img_show_view);
        imageShowView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        imageShowView.setLayoutManager(gridLayoutManager);

        imageSelectAdapter = new ImageSelectAdapter(this);
        imageSelectAdapter.setSelectAdapterListener(new ImageSelectAdapter.SelectAdapterListener() {
           /* @Override
            public void onCameraClick() {
                requestPermissions(ImageSelectActivity.this, new String[]{Manifest.permission.CAMERA}, new BaseActivity.RequestPermissionCallBack() {
                    @Override
                    public void granted() {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraFile = getImageSelectTempFile();
                        //android 7.0
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(ImageSelectActivity.this, getPackageName() + ".fileprovider", cameraFile)); //Uri.fromFile(tempFile)
                        } else {
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                        }
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                    }

                    @Override
                    public void denied() {
                        ToastUtils.showShortToast(ImageSelectActivity.this, "获取权限失败，正常功能受到影响");
                    }
                });
            }*/

            @Override
            public void onItemClick(File file) {
                ArrayList<File> tempList = new ArrayList<>();
                tempList.add(file);
                goImagePreview(file, tempList, imageSelectAdapter.getSelectImageList());
            }

        });
        imageShowView.setAdapter(imageSelectAdapter);
        requestPermissions(ImageSelectActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new RequestPermissionCallBack() {
            @Override
            public void granted() {
                getImageFile();
            }

            @Override
            public void denied() {
                ToastUtils.showShortToast(ImageSelectActivity.this, "获取权限失败，正常功能受到影响");
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_folder_text://选择相册
                if (imageFolderPop == null) {
                    imageFolderPop = new PopupWindow(this);
                    imageFolderPop.setBackgroundDrawable(new BitmapDrawable());
                    imageFolderPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                    imageFolderPop.setHeight(DisplayUtils.dip2px(this, 400));
                    imageFolderPop.setTouchable(true);
                    imageFolderPop.setFocusable(true);
                    imageFolderPop.setOutsideTouchable(true);
                    RecyclerView recyclerView = new RecyclerView(this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    FolderItemAdapter folderItemAdapter = new FolderItemAdapter(this, allImageList, folderImages, folderList, selectPosition);
                    folderItemAdapter.setFolderItemClickListener(new FolderItemAdapter.FolderItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            if (selectPosition != position) {
                                selectPosition = position;
                                if (position == 0) {
                                    imageSelectAdapter.setImageList(allImageList);
                                    imgFolderText.setText("所有图片");
                                } else {
                                    imageSelectAdapter.setImageList(folderImages.get(position - 1));
                                    imgFolderText.setText(folderList.get(position - 1).getName());
                                }
                            }
                            imageFolderPop.dismiss();
                        }
                    });
                    recyclerView.setAdapter(folderItemAdapter);

                    imageFolderPop.setContentView(recyclerView);
                }
                imageFolderPop.showAtLocation(bottomView, Gravity.BOTTOM, 0, bottomView.getHeight());
                break;
            case R.id.select_bottom_view:
                break;
            case R.id.tv_finish:
                if (imageSelectAdapter.getSelectImageList().size() == 0) {
                    ToastUtils.showShortToast(this, "请选择一张图片");
                    return;
                }
                toNext();
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void getImageFile() {
        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cs = null;
                try {
                    cs = ImageSelectActivity.this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null,
                            MediaStore.Images.Media.DATE_MODIFIED);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                if (cs != null) {
                    FileFilter filter = new FileFilter() {

                        @Override
                        public boolean accept(File pathname) {
                            String path = pathname.getAbsolutePath();
                            if (path.endsWith("jpg") || path.endsWith("jpeg") || path.endsWith("png")) {
                                return true;
                            }
                            return false;
                        }
                    };

                    try {
                        while (cs.moveToNext()) {
                            File image = new File(cs.getString(0));
                            if (!filter.accept(image)) {
                                continue;
                            }
                            allImageList.add(0, image);

                            File folder = image.getParentFile();
                            int folderIndex = folderList.indexOf(folder);
                            if (folderIndex < 0) {
                                folderList.add(folder);
                                ArrayList<File> tempImgList = new ArrayList<>();
                                tempImgList.add(0, image);
                                folderImages.add(tempImgList);
                            } else {
                                ArrayList<File> tempImgList = folderImages.get(folderIndex);
                                tempImgList.add(image);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    } finally {
                        cs.close();
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                imageSelectAdapter.setImageList(allImageList);
                imgFolderText.setText("所有图片");
            }
        }.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                imageSelectAdapter.getSelectImageList().clear();
                imageSelectAdapter.getSelectImageList().add(cameraFile);
                toNext();
            }
        }
    }

    private void goImagePreview(File selectedFile, ArrayList<File> allImageList, ArrayList<File> selectImageList) {
        ImagePreviewFragment imagePreviewFragment = new ImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(imagePreviewFragment.FILE_KEY, selectedFile);
        bundle.putSerializable(imagePreviewFragment.FILE_ALL_KEY, allImageList);
        bundle.putSerializable(imagePreviewFragment.FILE_SELECT_KEY, selectImageList);
        imagePreviewFragment.setArguments(bundle);
        imagePreviewFragment.setPreviewListener(new ImagePreviewFragment.PreviewListener() {
            @Override
            public void onFinish() {
                imageSelectAdapter.notifyDataSetChanged();
            }
        });
        CommonUtils.addFragmentWithHide(getSupportFragmentManager(), imagePreviewFragment, android.R.id.content, imagePreviewFragment.getTag());
    }

    public void toNext() {
        Intent intent = new Intent();
        intent.putExtra(Const.SELECTED_FILE_KEY, imageSelectAdapter.getSelectImageList().get(0));
        setResult(RESULT_OK, intent);
        finish();
    }

    private File getImageSelectTempFile() {
        File dir = new File(getExternalFilesDir("pictures").getAbsolutePath());
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        return new File(dir, System.currentTimeMillis() + ".png");
    }
}