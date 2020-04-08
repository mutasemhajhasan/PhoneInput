package me.mutasem.phoneinput;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;



import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

/**
 * PhoneField is a custom view for phone numbers with the corresponding country flag, and it uses
 * libphonenumber to validate the phone number.
 * <p>
 * Created by Ismail on 5/6/16.
 */
public abstract class PhoneField extends LinearLayout {
    private TextView.OnEditorActionListener onEditorActionListener;
    private Spinner mSpinner;

    private EditText mEditText;

    private Country mCountry;

    private PhoneNumberUtil mPhoneUtil = PhoneNumberUtil.createInstance(getContext());

    private int mDefaultCountryPosition = 0;

    /**
     * Instantiates a new Phone field.
     *
     * @param context the context
     */
    public PhoneField(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Phone field.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public PhoneField(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new Phone field.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public PhoneField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), getLayoutResId(), this);
        updateLayoutAttributes();
        prepareView();
        this.mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (onEditorActionListener != null)
                    return onEditorActionListener.onEditorAction(v, actionId, event);
                return false;
            }
        });
    }

    /**
     * Prepare view.
     */
    protected void prepareView() {
        mSpinner = findViewWithTag(getResources().getString(R.string.com_lamudi_phonefield_flag_spinner));

        mEditText = findViewWithTag(getResources().getString(R.string.com_lamudi_phonefield_edittext));

        if (mSpinner == null || mEditText == null) {
            throw new IllegalStateException("Please provide a valid xml layout");
        }

        final CountriesAdapter adapter = new CountriesAdapter(getContext(), Countries.COUNTRIES);


        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String rawNumber = s.toString();
                if (rawNumber.isEmpty()) {
                    mSpinner.setSelection(mDefaultCountryPosition);
                } else {
                    if (rawNumber.startsWith("00")) {
                        rawNumber = rawNumber.replaceFirst("00", "+");
                        mEditText.removeTextChangedListener(this);
                        mEditText.setText(rawNumber);
                        mEditText.addTextChangedListener(this);
                        mEditText.setSelection(rawNumber.length());
                    }
                    try {
                        Phonenumber.PhoneNumber number = parsePhoneNumber(rawNumber);
                        if (mCountry == null || mCountry.getDialCode() != number.getCountryCode()) {
                            selectCountry(number.getCountryCode());
                        }
                    } catch (NumberParseException ignored) {
                    }
                }
            }
        };

        mEditText.addTextChangedListener(textWatcher);

        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCountry = adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCountry = null;
            }
        });

    }

    /**
     * Gets spinner.
     *
     * @return the spinner
     */
    public Spinner getSpinner() {
        return mSpinner;
    }

    /**
     * Gets edit text.
     *
     * @return the edit text
     */
    public EditText getEditText() {
        return mEditText;
    }

    /**
     * Checks whether the entered phone number is valid or not.
     *
     * @return a boolean that indicates whether the number is of a valid pattern
     */
    public boolean isValid() {
        try {
            return mPhoneUtil.isValidNumber(parsePhoneNumber(getRawInput()));
        } catch (NumberParseException e) {
            return false;
        }
    }

    private Phonenumber.PhoneNumber parsePhoneNumber(String number) throws NumberParseException {
        String defaultRegion = mCountry != null ? mCountry.getCode().toUpperCase() : "";
        return mPhoneUtil.parseAndKeepRawInput(number, defaultRegion);
    }

    /**
     * Gets phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        try {
            Phonenumber.PhoneNumber number = parsePhoneNumber(getRawInput());
            return mPhoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException ignored) {
        }
        return getRawInput();
    }

    /**
     * Sets phone number.
     *
     * @param rawNumber the raw number
     */
    public void setPhoneNumber(String rawNumber) {
        try {
            Phonenumber.PhoneNumber number = parsePhoneNumber(rawNumber);
            if (mCountry == null || mCountry.getDialCode() != number.getCountryCode()) {
                selectCountry(number.getCountryCode());
            }
            mEditText.setText(mPhoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.NATIONAL));
        } catch (NumberParseException ignored) {
        }
    }

    /**
     * Sets default country.
     *
     * @param countryCode the country code
     */
    public void setDefaultCountry(String countryCode) {
        for (int i = 0; i < Countries.COUNTRIES.size(); i++) {
            Country country = Countries.COUNTRIES.get(i);
            if (country.getCode().equalsIgnoreCase(countryCode)) {
                mCountry = country;
                mDefaultCountryPosition = i;
                mSpinner.setSelection(i);
            }
        }
    }

    private void selectCountry(int dialCode) {
        for (int i = 0; i < Countries.COUNTRIES.size(); i++) {
            Country country = Countries.COUNTRIES.get(i);
            if (country.getDialCode() == dialCode) {
                mCountry = country;
                mSpinner.setSelection(i);
            }
        }
    }

    private void hideKeyboard() {
        ((InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * Update layout attributes.
     */
    protected abstract void updateLayoutAttributes();

    /**
     * Gets layout res id.
     *
     * @return the layout res id
     */
    public abstract int getLayoutResId();

    /**
     * Sets hint.
     *
     * @param resId the res id
     */
    public void setHint(int resId) {
        mEditText.setHint(resId);
    }

    /**
     * Gets raw input.
     *
     * @return the raw input
     */
    public String getRawInput() {
        return mEditText.getText().toString();
    }

    /**
     * Sets error.
     *
     * @param error the error
     */
    public void setError(String error) {
        mEditText.setError(error);
    }

    /**
     * Sets text color.
     *
     * @param resId the res id
     */
    public void setTextColor(int resId) {
        mEditText.setTextColor(resId);
    }

    public TextView.OnEditorActionListener getOnEditorActionListener() {
        return onEditorActionListener;
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener onEditorActionListener) {
        this.onEditorActionListener = onEditorActionListener;
    }

    @Override
    public void setOnFocusChangeListener(final OnFocusChangeListener l) {
        super.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    getEditText().setError(null);
                }
                l.onFocusChange(v, hasFocus);
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        getEditText().setEnabled(enabled);
        getSpinner().setEnabled(enabled);

    }

    public void addTextChangedListener(TextWatcher watcher) {
        getEditText().addTextChangedListener(watcher);
    }

    public void setHint(CharSequence hint) {
        getEditText().setHint(hint);
    }
}
