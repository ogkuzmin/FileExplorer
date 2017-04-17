package com.devnull.fileexplorer.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devnull.fileexplorer.CommonUtils;
import com.devnull.fileexplorer.R;
import com.devnull.fileexplorer.analyzer.FileTypeCollection.CommonType;
import com.devnull.fileexplorer.interfaces.OnBackPressedListener;
import com.devnull.fileexplorer.interfaces.OnItemRowClickListener;

import java.io.File;

/**
 * Created by devnull on 29.03.2016.
 */
public class ItemRow extends RelativeLayout implements
        View.OnClickListener,
        View.OnLongClickListener {

    private static final String TAG = "ItemRow";

    public static final int EXTERNAL_STORAGE_CODE = 4;
    public static final int EXTERNAL_STORAGE_STRING_CODE = R.string.ext_storage;

    public static final int ROOT_DIRECTORY_CODE = 3;
    public static final int ROOT_DIRECTORY_STRING_CODE = R.string.root_directory;

    public static final int DIRECTORY_CODE = 2;
    public static final int DIRECTORY_STRING_CODE = R.string.directory;

    public static final int FILE_CODE = 1;
    public static final int FILE_STRING_CODE = R.string.file;

    private static final int NO_SUCH_ICON = -1;

    private RelativeLayout          container;
    private File                    itemFile;
    private CommonType              fileType;
    private Context                 context;
    private int                     itemCode;
    private boolean                 isParent = false;
    private boolean                 isDir;
    private boolean                 isReadable;
    private ImageView               icon;
    private TextView                title;
    private TextView                subTitle;
    private OnItemRowClickListener  headListener;
    private OnBackPressedListener   onBackPressedListener;

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
        container.setBackgroundResource(R.drawable.list_item_background);

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
            else if (itemFile.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath()))
                itemCode = EXTERNAL_STORAGE_CODE;
            else
                itemCode = DIRECTORY_CODE;
        } else
            itemCode = FILE_CODE;

        Log.i(TAG, "determineItemCode(): itemCode = " + itemCode);
    }

    private void setUpViews() {
        int iconResId = getIconResourceId();

        if (iconResId != NO_SUCH_ICON && iconResId != FILE_CODE) {
            icon.setImageResource(iconResId);
            icon.setImageAlpha(60);
            icon.setColorFilter(new ColorDrawable(getResources().getColor(R.color.toolbar)).getColor());
        } else if (iconResId == FILE_CODE) {
            String extension = CommonUtils.getFileExtension(itemFile);
            icon.setImageBitmap(generateIconBitmap(extension));
        }

        String titleText;

        if (isParentDir()) {
            titleText = "..";
            title.setText(titleText);
        }


        switch (itemCode) {
            case DIRECTORY_CODE: {
                if (!isParentDir()) {
                    titleText = itemFile.getName();
                    title.setText(titleText);
                    if (!isReadable) {
                        icon.setImageAlpha(20);
                        title.setTextColor(getResources().getColor(R.color.no_active_item));
                        subTitle.setTextColor(getResources().getColor(R.color.no_active_item));
                    }
                }
                subTitle.setText(DIRECTORY_STRING_CODE);
            }
            break;

            case FILE_CODE: {
                title.setText(itemFile.getName());
                long lastModified = CommonUtils.getRealLastModified(itemFile);
                String subTitleText = CommonUtils.getStringSizeFileFromLong(itemFile.length());
                if (lastModified > 0)
                    subTitleText += ", " + getResources().getString(R.string.last_modified)
                            + " " + CommonUtils.getStringTimeFromLong(itemFile.lastModified());
                subTitle.setText(subTitleText);
            }
            break;

            case ROOT_DIRECTORY_CODE: {
                if(!isParentDir())
                title.setText("/");
                subTitle.setText(ROOT_DIRECTORY_STRING_CODE);
            }
            break;

            case EXTERNAL_STORAGE_CODE: {
                if (!isParentDir())
                title.setText(EXTERNAL_STORAGE_STRING_CODE);
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
            case FILE_CODE:
                resId = FILE_CODE;
                break;

            case DIRECTORY_CODE:
                resId = R.drawable.ic_folder_black_48dp;
                break;

            case EXTERNAL_STORAGE_CODE:
                resId = R.drawable.ic_sd_storage_black_48dp;
                break;

            case ROOT_DIRECTORY_CODE:
                resId = R.drawable.ic_folder_black_48dp;
                break;
        }
        return resId;
    }

    public void setParentBoolean() {
        isParent = true;
    }

    public boolean isParentDir() {
        return isParent;
    }

    public void registerHeadListener(OnItemRowClickListener listener) {
        headListener = listener;
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener){
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onClick(View v) {

        if (isParentDir()) {
            onBackPressedListener.onBackPressed();
            return;
        }

        switch (itemCode) {
            case ROOT_DIRECTORY_CODE:
                headListener.onItemRowClick(new File("/"));
                break;

            case EXTERNAL_STORAGE_CODE:
                if(CommonUtils.isExtStorageReadable())
                headListener.onItemRowClick(Environment.getExternalStorageDirectory());
                break;

            case DIRECTORY_CODE:
                if (itemFile.canRead())
                headListener.onItemRowClick(itemFile);
                break;

            case FILE_CODE:
                headListener.onItemRowClick(itemFile);
                break;
        }
    }

    public Bitmap generateIconBitmap(String extension) {

        if (extension.length() > 3 || extension.equalsIgnoreCase(""))
            extension = "?";

        int startX = icon.getLeft();
        int startY = icon.getTop();
        float outerWidth = getResources().getDimension(R.dimen.icon_item_size);
        float strokeWidth = outerWidth/8;
        float innerWidth = outerWidth - (2*strokeWidth);
        float textSize = getResources().getDimension(R.dimen.icon_text_size);

        Bitmap icon = Bitmap.createBitmap((int)outerWidth, (int)outerWidth, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(icon);
        Paint outerRectPaint = new Paint(), innerRectPaint = new Paint(), textPaint = new Paint();
        outerRectPaint.setColor(getResources().getColor(R.color.icon_text_color));
        innerRectPaint.setColor(getResources().getColor(R.color.background));
        textPaint.setColor(getResources().getColor(R.color.icon_text_color));
        outerRectPaint.setStyle(Paint.Style.FILL);
        innerRectPaint.setStyle(Paint.Style.FILL);
        outerRectPaint.setStrokeWidth(strokeWidth);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawRect(startX, startY, startX + outerWidth, startY + outerWidth, outerRectPaint);
        canvas.drawRect(startX + strokeWidth, startY + strokeWidth,
                startX + strokeWidth + innerWidth, startY + strokeWidth + innerWidth, innerRectPaint);
        canvas.drawText(extension,startX + outerWidth/2, startY + 5*outerWidth/12 + textSize/2, textPaint);

        return icon;
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }
}


