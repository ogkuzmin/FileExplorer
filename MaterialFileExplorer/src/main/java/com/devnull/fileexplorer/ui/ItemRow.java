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

import java.io.File;

/**
 * Created by devnull on 29.03.2016.
 */
public class ItemRow extends RelativeLayout implements
        View.OnClickListener,
        View.OnLongClickListener {

    private static final String TAG = ItemRow.class.getSimpleName();

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

    private boolean isInitialized = false;

    public interface OnBackPressedListener{

        public void onBackPressed();
    }

    /**
     * The interface that describes callback on item click event.
     */
    public interface OnItemRowClickListener {

        public void onItemRowClick(@Nullable File file);
    }

    public ItemRow(Context context) {

        super(context);
        this.context = context;
    }
    public ItemRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setRowDataAndInitUi(RowData rowData) {
        itemData = rowData;
        if (!isInitialized) {
            initUiComponents();
        } else {
            clearAllEffects();
        }
        setUpViews();
    }
    public RowData getItemData() {
        return itemData;
    }
    private void initUiComponents() {
        container = (RelativeLayout) findViewById(R.id.container_item_row);
        container.setOnClickListener(this);
        container.setBackgroundResource(R.drawable.list_item_background);

        icon = (ImageView) findViewById(R.id.item_icon);
        title = (TextView) findViewById(R.id.title_item);
        subTitle = (TextView) findViewById(R.id.subtitle_item);

        isInitialized = true;
    }
    private void clearAllEffects() {
        icon.setImageAlpha(255);
        icon.clearColorFilter();
        title.setTextColor(getResources().getColor(R.color.textColor));
        subTitle.setTextColor(getResources().getColor(R.color.text_color_secondary));
    }
    private void setUpViews() {
        container.setOnClickListener(itemData);

        String titleText;

        if (itemData.isParentDir()) {
            titleText = "..";
            title.setText(titleText);
        }

        switch (itemData.getItemCode()) {

            case RowData.DIRECTORY_CODE: {
                if (!itemData.isParentDir()) {
                    titleText = itemData.getItemFile().getName();
                    title.setText(titleText);
                    if (!itemData.isReadable()) {
                        icon.setImageAlpha(getResources().getInteger(R.integer.unreadable_directory_alpha));
                        title.setTextColor(getResources().getColor(R.color.no_active_item));
                        subTitle.setTextColor(getResources().getColor(R.color.no_active_item));
                    }
                }
                subTitle.setText(RowData.DIRECTORY_STRING_CODE);
            }
            break;

            case RowData.FILE_CODE: {

                Log.d(TAG, "setUpViews(). case RowData.FILE_CODE");

                title.setText(itemData.getItemFile().getName());
                long lastModified = CommonUtils.getRealLastModified(itemData.getItemFile());
                String subTitleText = CommonUtils.getStringSizeFileFromLong(itemData.getItemFile().length());
                if (lastModified > 0)
                    subTitleText += ", " + getResources().getString(R.string.last_modified)
                            + " " + CommonUtils.getStringTimeFromLong(itemData.getItemFile().lastModified());
                subTitle.setText(subTitleText);

                Log.d(TAG, "setUpViews(). case RowData.FILE_CODE\n" + "file is " + itemData.getItemFile().getName() + "\n" +
                        "last modified " + CommonUtils.getStringTimeFromLong(itemData.getItemFile().lastModified()) + "\n" +
                        "size is " + CommonUtils.getStringSizeFileFromLong(itemData.getItemFile().length()));
            }
            break;

            case RowData.ROOT_DIRECTORY_CODE: {
                if(!itemData.isParentDir())
                title.setText("/");
                subTitle.setText(RowData.ROOT_DIRECTORY_STRING_CODE);
            }
            break;

            case RowData.EXTERNAL_STORAGE_CODE: {
                if (!itemData.isParentDir())
                title.setText(RowData.EXTERNAL_STORAGE_STRING_CODE);
                subTitle.setVisibility(INVISIBLE);
            }
            break;

        }

        int iconResId = getIconResourceId();

        if (iconResId != RowData.NO_SUCH_ICON && iconResId != RowData.FILE_CODE) {
            icon.setImageResource(iconResId);
            icon.setImageAlpha(getResources().getInteger(R.integer.standard_icon_alpha));
            icon.setColorFilter(new ColorDrawable(getResources().getColor(R.color.toolbar)).getColor());
        } else if (iconResId == RowData.FILE_CODE) {
            String extension = CommonUtils.getFileExtension(itemData.getItemFile());
            icon.setImageBitmap(generateIconBitmap(extension));
        }
    }
    private int getIconResourceId() {

        int resId = RowData.NO_SUCH_ICON;

        if(itemData.isParentDir())
            return R.drawable.ic_folder_black_48dp;

        switch (itemData.getItemCode()) {
            case RowData.FILE_CODE:
                resId = RowData.FILE_CODE;
                break;

            case RowData.DIRECTORY_CODE:
                resId = R.drawable.ic_folder_black_48dp;
                break;

            case RowData.EXTERNAL_STORAGE_CODE:
                resId = R.drawable.ic_sd_storage_black_48dp;
                break;

            case RowData.ROOT_DIRECTORY_CODE:
                resId = R.drawable.ic_folder_black_48dp;
                break;
        }

        return resId;
    }
    private Bitmap generateIconBitmap(String extension) {

        Log.d(TAG, "generateIconBitmap(String extension). Ext is " + extension);

        if (extension.length() > 3 || extension.equalsIgnoreCase(""))
            extension = "?";

        float outerWidth = getResources().getDimension(R.dimen.icon_item_size);
        float strokeWidth = outerWidth/8;
        float innerWidth = outerWidth - (2*strokeWidth);
        float textSize = getResources().getDimension(R.dimen.icon_text_size);

        Bitmap bitmapIcon = Bitmap.createBitmap((int)outerWidth, (int)outerWidth, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapIcon);
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

        int startX = container.getLeft() + 16;
        int startY = container.getTop() + 16;

        canvas.drawRect(startX, startY, startX + outerWidth, startY + outerWidth, outerRectPaint);
        canvas.drawRect(startX + strokeWidth, startY + strokeWidth,
                startX + strokeWidth + innerWidth, startY + strokeWidth + innerWidth, innerRectPaint);
        canvas.drawText(extension,startX + outerWidth/2, startY + 5*outerWidth/12 + textSize/2, textPaint);

        return bitmapIcon;
    }
}


