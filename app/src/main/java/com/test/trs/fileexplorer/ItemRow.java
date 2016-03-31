package com.test.trs.fileexplorer;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.Date;

/**
 * Created by kuzmin_og on 29.03.2016.
 */
public class ItemRow extends RelativeLayout implements View.OnClickListener {

    private static final String TAG = "ItemRow";

    public static final String MAP_EXT = "map";

    public static final int INTERNAL_STORAGE_CODE = 5;
    public static final String INTERNAL_STORAGE_STRING = "Внутреннее хранилище";

    public static final int EXTERNAL_STORAGE_CODE = 4;
    public static final String EXTERNAL_STORAGE_STRING = "Внешнее хранилище";

    public static final int ROOT_DIRECTORY_CODE = 3;
    public static final String ROOT_DIRECTORY_STRING = "Корневой каталог";

    public static final int DIRECTORY_CODE = 2;
    public static final String DIRECTORY_STRING = "Директория";

    public static final int REGISTERED_FILE_CODE = 1;
    public static final String REGISTERED_FILE_STRING = "Файл";

    private static final int NO_SUCH_ICON = -1;

    private RelativeLayout container;
    private File itemFile;
    private Context context;
    private int itemCode;
    private boolean isParent = false;
    private boolean isDir;
    private boolean isReadable;
    private ImageView icon;
    private TextView title;
    private TextView subTitle;
    private OnItemRowClickListener headListener;

    /*private final View.OnClickListener itemListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {

            Log.i(TAG, "invoke onClick()");
        }
    };*/

    public interface OnItemRowClickListener {

        public void onItemRowClick(@Nullable File file);
    }

    public ItemRow(Context context) {

        super(context);
        this.context = context;
    }

    public void setItemFile(File file){
        itemFile = file;
    }

    public void initRow(){
        determineItemCode();
        initComponents();
    }

    private void initComponents() {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_row_layout, this);

        container = (RelativeLayout) findViewById(R.id.container_item_row);
        container.setOnClickListener(this);
        icon = (ImageView) findViewById(R.id.item_icon);
        title = (TextView) findViewById(R.id.title_item);
        subTitle = (TextView) findViewById(R.id.subtitle_item);


        setUpViews();

    }

    private void determineItemCode() {

        isDir = itemFile.isDirectory();

        if(isDir)
            isReadable = itemFile.canRead();

        if (isDir) {
            if (itemFile.getAbsolutePath().equalsIgnoreCase("/"))
                itemCode = ROOT_DIRECTORY_CODE;
            else if (itemFile.getAbsolutePath().equalsIgnoreCase(context.getFilesDir().getAbsolutePath()))
                itemCode = INTERNAL_STORAGE_CODE;
            else if (itemFile.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath()))
                itemCode = EXTERNAL_STORAGE_CODE;
            else
                itemCode = DIRECTORY_CODE;
        } else
            itemCode = REGISTERED_FILE_CODE;

        Log.i(TAG, "determineItemCode(): itemCode = " + itemCode);
    }

    private void setUpViews() {

        int iconResId = getIconResourceId();

        if (iconResId != NO_SUCH_ICON) {
            icon.setImageResource(iconResId);
            icon.setImageAlpha(40);
        }

        String titleText;

        if (isParentDir()) {
            titleText = "..";
            title.setText(titleText);
        }


        switch (itemCode) {

            case DIRECTORY_CODE: {


                if (!isParentDir()){

                    titleText = itemFile.getName();
                    title.setText(titleText);

                    if (!isReadable) {

                        icon.setImageAlpha(20);
                        title.setTextColor(getResources().getColor(R.color.hilited_task_color));
                        subTitle.setTextColor(getResources().getColor(R.color.hilited_task_color));
                    }
                }


                subTitle.setText(DIRECTORY_STRING);
            }
            break;

            case REGISTERED_FILE_CODE: {
                title.setText(itemFile.getName());
                String ext = ExplorerFragment.getFileExtension(itemFile);
                String subTitleText = (REGISTERED_FILE_STRING + " " + ext +
                        ", " + (itemFile.getTotalSpace() / 1024.0) + " kB, last modified " + new Date(itemFile.lastModified()));
                subTitle.setText(subTitleText);
            }
            break;

            case ROOT_DIRECTORY_CODE: {
                if(!isParentDir())
                title.setText("/");

                subTitle.setText(ROOT_DIRECTORY_STRING);
            }
            break;

            case INTERNAL_STORAGE_CODE: {
                if(!isParentDir())
                title.setText(INTERNAL_STORAGE_STRING);

                subTitle.setVisibility(INVISIBLE);
            }
            break;

            case EXTERNAL_STORAGE_CODE: {
                if (!isParentDir())
                title.setText(EXTERNAL_STORAGE_STRING);

                subTitle.setVisibility(INVISIBLE);
            }
            break;

        }
    }

    private int getIconResourceId() {

        int resId = NO_SUCH_ICON;
        String extension = "";

        if(isParentDir())
            return R.drawable.ic_folder_black_48dp;

        switch (itemCode) {
            case REGISTERED_FILE_CODE:
                extension = ExplorerFragment.getFileExtension(itemFile);
                if (extension.equalsIgnoreCase(MAP_EXT))
                    resId = R.drawable.ic_public_black_48dp;
                break;

            case DIRECTORY_CODE:
                resId = R.drawable.ic_folder_black_48dp;
                break;

            case EXTERNAL_STORAGE_CODE:
                resId = R.drawable.ic_sd_storage_black_48dp;
                break;

            case INTERNAL_STORAGE_CODE:
                resId = R.drawable.ic_storage_black_48dp;
                break;

            case ROOT_DIRECTORY_CODE:
                resId = R.drawable.ic_folder_black_48dp;
                break;
        }

        return resId;
    }

    public void setParentBoolean() {

        if (isDir)
            isParent = true;
    }

    public boolean isParentDir() {
        return isParent;
    }

    public void registerHeadListener(OnItemRowClickListener listener) {

        headListener = listener;
    }


    @Override
    public void onClick(View v) {

        Log.i(TAG, "onClick():  with itemFile.getName() = " + itemFile.getName());

        switch (itemCode) {

            case ROOT_DIRECTORY_CODE:
                headListener.onItemRowClick(new File("/"));
                break;

            case INTERNAL_STORAGE_CODE:
                headListener.onItemRowClick(context.getFilesDir());
                break;

            case EXTERNAL_STORAGE_CODE:
                headListener.onItemRowClick(Environment.getExternalStorageDirectory());
                //TODO check if external storage is ready to read;
                break;

            case DIRECTORY_CODE:

                if (itemFile.canRead())
                headListener.onItemRowClick(itemFile);

                break;

            case REGISTERED_FILE_CODE:
                headListener.onItemRowClick(itemFile);
                break;

        }

    }
}


