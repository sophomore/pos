package org.jaram.ds.views.widgets;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import org.jaram.ds.util.NumberUtil;
import org.jaram.ds.util.StringUtils;

/**
 * Created by jdekim43 on 2016. 5. 17..
 */
public class EditPriceView extends EditText {

    private OnFocusChangeListener onFocusChangeListener;

    public EditPriceView(Context context) {
        super(context);
        setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
        super.setOnFocusChangeListener(new PriceFormatInvalidator());
    }

    public EditPriceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
        super.setOnFocusChangeListener(new PriceFormatInvalidator());
    }

    public EditPriceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
        super.setOnFocusChangeListener(new PriceFormatInvalidator());
    }

    public EditPriceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
        super.setOnFocusChangeListener(new PriceFormatInvalidator());
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (TextUtils.isEmpty(text)) {
            super.setText("0", type);
            return;
        }

        text = text.toString().replaceAll(",", "");

        if (NumberUtil.isNotNumber(text)) {
            throw new IllegalArgumentException("입력 값은 반드시 숫자여야합니다.");
        }

        try {
            super.setText(NumberUtil.convertPriceFormat(Integer.parseInt(text.toString())), type);
        } catch (NumberFormatException e) {
            //do nothing
        }
    }

    @Override
    public void append(CharSequence text, int start, int end) {
        setText(new StringBuilder(getText()).append(text, start, end));
    }

    public void setNumber(int number) {
        setText(StringUtils.format("%d", number));
    }

    public int getNumber() {
        return Integer.parseInt(getText().toString().replaceAll(",", ""));
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        this.onFocusChangeListener = l;
    }

    private class PriceFormatInvalidator implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditPriceView.super.setText(getText().toString().replaceAll(",", ""), BufferType.EDITABLE);
            if (!hasFocus) {
                setText(getText());
            }

            if (onFocusChangeListener != null) {
                onFocusChangeListener.onFocusChange(v, hasFocus);
            }
        }
    }
}