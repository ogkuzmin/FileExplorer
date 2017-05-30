package com.devnull.fileexplorer.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devnull.fileexplorer.CommonUtils;
import com.devnull.fileexplorer.R;

/**
 * Created by devnull on 29.03.2016.
 */
public class ItemRow extends RelativeLayout {

    private static final String TAG = ItemRow.class.getSimpleName();

    private RelativeLayout  container;
    private ImageView       icon;
    private TextView        title;
    private TextView        subTitle;

    private FileRowModel itemData;

    private boolean isInitialized = false;

    public ItemRow(Context context) {
        super(context);
    }
    public ItemRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setRowDataAndInitUi(FileRowModel fileRowModel) {
        itemData = fileRowModel;
        if (!isInitialized) {
            initUiComponents();
        } else {
            clearAllEffects();
        }
        setUpViews();
    }
    public FileRowModel getItemData() {
        return itemData;
    }
    private void initUiComponents() {
        container = (RelativeLayout) findViewById(R.id.container_item_row);
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

        String titleText;

        if (itemData.isParentDir()) {
            titleText = "..";
            title.setText(titleText);
        }

        switch (itemData.getItemCode()) {

            case FileRowModel.DIRECTORY_CODE: {
                if (!itemData.isParentDir()) {
                    titleText = itemData.getItemFile().getName();
                    title.setText(titleText);
                    if (!itemData.isReadable()) {
                        icon.setImageAlpha(getResources().getInteger(R.integer.unreadable_directory_alpha));
                        title.setTextColor(getResources().getColor(R.color.no_active_item));
                        subTitle.setTextColor(getResources().getColor(R.color.no_active_item));
                    }
                }
                subTitle.setText(FileRowModel.DIRECTORY_STRING_CODE);
            }
            break;

            case FileRowModel.FILE_CODE: {

                Log.d(TAG, "setUpViews(). case FileRowModel.FILE_CODE");

                title.setText(itemData.getItemFile().getName());
                long lastModified = CommonUtils.getRealLastModified(itemData.getItemFile());
                String subTitleText = CommonUtils.getStringSizeFileFromLong(itemData.getItemFile().length());
                if (lastModified > 0)
                    subTitleText += ", " + getResources().getString(R.string.last_modified)
                            + " " + CommonUtils.getStringTimeFromLong(itemData.getItemFile().lastModified());
                subTitle.setText(subTitleText);

                Log.d(TAG, "setUpViews(). case FileRowModel.FILE_CODE\n" + "file is " + itemData.getItemFile().getName() + "\n" +
                        "last modified " + CommonUtils.getStringTimeFromLong(itemData.getItemFile().lastModified()) + "\n" +
                        "size is " + CommonUtils.getStringSizeFileFromLong(itemData.getItemFile().length()));
            }
            break;

            case FileRowModel.ROOT_DIRECTORY_CODE: {
                if(!itemData.isParentDir())
                title.setText("/");
                subTitle.setText(FileRowModel.ROOT_DIRECTORY_STRING_CODE);
            }
            break;

            case FileRowModel.EXTERNAL_STORAGE_CODE: {
                if (!itemData.isParentDir())
                title.setText(FileRowModel.EXTERNAL_STORAGE_STRING_CODE);
                subTitle.setVisibility(INVISIBLE);
            }
            break;

        }

        int iconResId = getIconResourceId();

        if (iconResId != FileRowModel.NO_SUCH_ICON && iconResId != FileRowModel.FILE_CODE) {
            icon.setImageResource(iconResId);
            icon.setImageAlpha(getResources().getInteger(R.integer.standard_icon_alpha));
            icon.setColorFilter(new ColorDrawable(getResources().getColor(R.color.toolbar)).getColor());
        } else if (iconResId == FileRowModel.FILE_CODE) {
            String extension = CommonUtils.getFileExtension(itemData.getItemFile());
            icon.setImageBitmap(generateIconBitmap(extension));
        }
    }
    private int getIconResourceId() {

        int resId = FileRowModel.NO_SUCH_ICON;

        if(itemData.isParentDir())
            return R.drawable.ic_folder_black_48dp;

        switch (itemData.getItemCode()) {
            case FileRowModel.FILE_CODE:
                resId = FileRowModel.FILE_CODE;
                break;

            case FileRowModel.DIRECTORY_CODE:
                resId = R.drawable.ic_folder_black_48dp;
                break;

            case FileRowModel.EXTERNAL_STORAGE_CODE:
                resId = R.drawable.ic_sd_storage_black_48dp;
                break;

            case FileRowModel.ROOT_DIRECTORY_CODE:
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


