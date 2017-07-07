package com.wizard.hastar.ui.money_manager.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.balysv.materialripple.MaterialRippleLayout;
import com.dd.CircularProgressButton;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Switch;
import com.rey.material.widget.Switch.OnCheckedChangeListener;
import com.wizard.hastar.BuildConfig;
import com.wizard.hastar.MyApplication;
import com.wizard.hastar.R;
import com.wizard.hastar.base.BaseActivity;
import com.wizard.hastar.ui.money_manager.util.RecordManager;
import com.wizard.hastar.ui.money_manager.util.SettingManager;
import com.wizard.hastar.util.CoCoinUtil;
import com.wizard.hastar.util.EmailValidator;
import com.wizard.hastar.util.ToastUtil;
import com.wizard.hastar.widget.RiseNumberTextView;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountBookSettingActivity extends BaseActivity
        implements
        View.OnClickListener,
        ColorChooserDialog.ColorCallback,
        OnCheckedChangeListener {

    private final int UPDATE_LOGO = 0;
    private final int UPDATE_IS_MONTH_LIMIT = 1;
    private final int UPDATE_MONTH_LIMIT = 2;
    private final int UPDATE_IS_COLOR_REMIND = 3;
    private final int UPDATE_MONTH_WARNING = 4;
    private final int UPDATE_REMIND_COLOR = 5;
    private final int UPDATE_IS_FORBIDDEN = 6;
    private final int UPDATE_ACCOUNT_BOOK_NAME = 7;
    private final int UPDATE_ACCOUNT_BOOK_PASSWORD = 8;
    private final int UPDATE_SHOW_PICTURE = 9;
    private final int UPDATE_IS_HOLLOW = 10;
    private final int UPDATE_LOGO_ID = 11;

    private Context mContext;

    private MaterialIconView back;

    private File logoFile;
    private CircleImageView logo;
    private Bitmap logoBitmap;

    private MaterialEditText registerUserName;
    private MaterialEditText registerUserEmail;
    private MaterialEditText registerPassword;
    private MaterialEditText loginUserName;
    private MaterialEditText loginPassword;

    private MaterialRippleLayout profileLayout;
    private MaterialIconView userNameIcon;
    private MaterialIconView userEmailIcon;
    private TextView userName;
    private TextView userEmail;
    private TextView loginButton;
    private RiseNumberTextView expense;
    private TextView expenseTV;
    private RiseNumberTextView records;
    private TextView recordsTV;

    private MaterialRippleLayout monthLayout;
    private MaterialIconView monthIcon;
    private MaterialIconView monthMaxExpenseIcon;
    private MaterialIconView monthColorRemindIcon;
    private MaterialIconView monthWarningIcon;
    private MaterialIconView monthColorRemindTypeIcon;
    private MaterialIconView monthForbiddenIcon;
    private Switch monthSB;
    private Switch monthColorRemindSB;
    private Switch monthForbiddenSB;
    private RiseNumberTextView monthMaxExpense;
    private RiseNumberTextView monthWarning;
    private MaterialIconView monthColorRemindSelect;
    private TextView monthLimitTV;
    private TextView monthMaxExpenseTV;
    private TextView monthColorRemindTV;
    private TextView monthWarningTV;
    private TextView monthColorRemindTypeTV;
    private TextView monthForbiddenTV;

    private MaterialRippleLayout accountBookNameLayout;
    private TextView accountBookNameTV;
    private TextView accountBookName;

    private MaterialRippleLayout changePasswordLayout;
    private TextView changePasswordTV;

    private MaterialRippleLayout sortTagsLayout;
    private TextView sortTagsTV;

    private MaterialRippleLayout showPictureLayout;
    private MaterialIconView showPictureIcon;
    private Switch showPictureSB;
    private TextView showPictureTV;

    private MaterialRippleLayout hollowLayout;
    private MaterialIconView hollowIcon;
    private Switch hollowSB;
    private TextView hollowTV;

    private MaterialRippleLayout updateLayout;
    private TextView currentVersionTV;
    private TextView canBeUpdatedTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        setContentView(R.layout.activity_account_book_setting);

        int currentapiVersion = Build.VERSION.SDK_INT;

        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.statusBarColor));
            }
        } else {
            // do something for phones running an SDK before lollipop
            View statusBarView = findViewById(R.id.status_bar_view);
            statusBarView.getLayoutParams().height = CoCoinUtil.getStatusBarHeight();
        }

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_image:
                changeLogo();
                break;
        }
    }

    // switch change listener///////////////////////////////////////////////////////////////////////////
    @Override
    public void onCheckedChanged(Switch view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.month_limit_enable_button:
                SettingManager.getInstance().setIsMonthLimit(isChecked);
                updateSettingsToServer(UPDATE_IS_MONTH_LIMIT);
                SettingManager.getInstance().setMainViewMonthExpenseShouldChange(true);
                SettingManager.getInstance().setMainViewRemindColorShouldChange(true);
                SettingManager.getInstance().setTodayViewMonthExpenseShouldChange(true);
                setMonthState();
                break;
            case R.id.month_color_remind_button:
                SettingManager.getInstance().setIsColorRemind(isChecked);
                updateSettingsToServer(UPDATE_IS_COLOR_REMIND);
                SettingManager.getInstance().setMainViewRemindColorShouldChange(true);
                setIconEnable(monthColorRemindIcon, isChecked
                        && SettingManager.getInstance().getIsMonthLimit());
                setIconEnable(monthColorRemindTypeIcon, isChecked
                        && SettingManager.getInstance().getIsMonthLimit());
                setIconEnable(monthWarningIcon, isChecked
                        && SettingManager.getInstance().getIsMonthLimit());
                if (isChecked && SettingManager.getInstance().getIsMonthLimit()) {
                    monthColorRemindSelect.setEnabled(true);
                    monthColorRemindSelect
                            .setColor(SettingManager.getInstance().getRemindColor());
                    monthWarning.setEnabled(true);
                    monthWarning.setTextColor(
                            ContextCompat.getColor(mContext, R.color.drawer_text));
                } else {
                    monthColorRemindSelect.setEnabled(false);
                    monthColorRemindSelect
                            .setColor(mContext.getResources().getColor(R.color.my_gray));
                    monthWarning.setEnabled(false);
                    monthWarning.setTextColor(
                            ContextCompat.getColor(mContext, R.color.my_gray));
                }
                setTVEnable(monthColorRemindTypeTV, isChecked
                        && SettingManager.getInstance().getIsMonthLimit());
                setTVEnable(monthWarningTV, isChecked
                        && SettingManager.getInstance().getIsMonthLimit());
                break;
            case R.id.month_forbidden_button:
                SettingManager.getInstance().setIsForbidden(isChecked);
                updateSettingsToServer(UPDATE_IS_FORBIDDEN);
                setIconEnable(monthForbiddenIcon, isChecked
                        && SettingManager.getInstance().getIsMonthLimit());
                break;
            case R.id.whether_show_picture_button:
                SettingManager.getInstance().setShowPicture(isChecked);
                updateSettingsToServer(UPDATE_SHOW_PICTURE);
                setShowPictureState(isChecked);
                break;
            case R.id.whether_show_circle_button:
                SettingManager.getInstance().setIsHollow(isChecked);
                updateSettingsToServer(UPDATE_IS_HOLLOW);
                setHollowState(isChecked);
                SettingManager.getInstance().setTodayViewPieShouldChange(Boolean.TRUE);
                break;
            default:
                break;
        }
    }

    // Load logo from local/////////////////////////////////////////////////////////////////////////////
    private void loadLogo() {
        //TODO 加载头像
    }

    // change the user logo/////////////////////////////////////////////////////////////////////////////
    private void changeLogo() {
        //TODO 修改头像
    }

    // Crop a picture///////////////////////////////////////////////////////////////////////////////////
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX : aspectY
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY the height and width
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    // After select a picture///////////////////////////////////////////////////////////////////////////
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                // after select from gallery
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());
                }
                break;
            case 2:
                // after taking a photo
                if (resultCode == RESULT_OK) {
                    File temp = new File(MyApplication.getAppContext().getFilesDir() + CoCoinUtil.LOGO_NAME);
                    cropPhoto(Uri.fromFile(temp));
                }
                break;
            case 3:
                // after crop the picture
                if (data != null) {
                    Bundle extras = data.getExtras();
                    logoBitmap = extras.getParcelable("data");
                    if (logoBitmap != null) {
                        SettingManager.getInstance().setHasLogo(true);
                        setPicToView(logoBitmap);
                        SettingManager.getInstance().setTodayViewLogoShouldChange(true);
                        logo.setImageBitmap(logoBitmap);
                    }
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Storage a picture////////////////////////////////////////////////////////////////////////////////
    private void setPicToView(Bitmap mBitmap) {
        FileOutputStream b = null;
        File file = new File(MyApplication.getAppContext().getFilesDir() + CoCoinUtil.LOGO_NAME);
        String fileName = file.getAbsolutePath();  // get logo position
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);  // write the data to file
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // close
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadLogoToServer();
        }
    }

    // download logo to local///////////////////////////////////////////////////////////////////////////
    private void downloadLogoFromServer() {
        //TODO
    }

    // update a logo to server//////////////////////////////////////////////////////////////////////////
    private void uploadLogoToServer() {
        //TODO
    }

    // the user's operation when clicking the first card view///////////////////////////////////////////
    private void userOperator() {
        if (!SettingManager.getInstance().getLoggenOn()) {
            // register or log on
            new MaterialDialog.Builder(this)
                    .iconRes(R.drawable.cocoin_logo)
                    .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                    .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                    .title(R.string.welcome)
                    .content(R.string.login_or_register)
                    .positiveText(R.string.login)
                    .negativeText(R.string.register)
                    .neutralText(R.string.cancel)
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {
                            if (which.equals(DialogAction.POSITIVE)) {
                                userLogin();
                            } else if (which.equals(DialogAction.NEGATIVE)) {
                                userRegister();
                            } else {
                                dialog.dismiss();
                            }
                        }
                    })
                    .show();
        } else {
            // log out or user operate
            new MaterialDialog.Builder(this)
                    .iconRes(R.drawable.cocoin_logo)
                    .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                    .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                    .title(mContext.getResources().getString(R.string.hi)
                            + SettingManager.getInstance().getUserName())
                    .content(R.string.whether_logout)
                    .positiveText(R.string.log_out)
                    .neutralText(R.string.cancel)
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {
                            if (which.equals(DialogAction.POSITIVE)) {
                                userLogout();
                            } else {
                                dialog.dismiss();
                            }
                        }
                    })
                    .show();
        }
    }

    // User log out/////////////////////////////////////////////////////////////////////////////////////
    private void userLogout() {
        //TODO
    }

    // User login///////////////////////////////////////////////////////////////////////////////////////
    MaterialDialog loginDialog;
    View loginDialogView;
    CircularProgressButton loginDialogButton;

    private void userLogin() {

        loginDialog.show();
    }

    // User register////////////////////////////////////////////////////////////////////////////////////
    MaterialDialog registerDialog;
    View registerDialogView;
    CircularProgressButton registerDialogButton;

    private void userRegister() {
        registerDialog = new MaterialDialog.Builder(this)
                .title(R.string.go_register)
                .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                .customView(R.layout.dialog_user_register, true)
                .build();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        registerDialogView = registerDialog.getCustomView();
        registerDialogButton = (CircularProgressButton) registerDialogView.findViewById(R.id.button);
        registerDialogButton.setTypeface(CoCoinUtil.GetTypeface());
        registerDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerDialogButton.setProgress(1);
                registerDialog.setCancelable(false);
// User register, a new user////////////////////////////////////////////////////////////////////////
                //TODO 注册
            }
        });

        final MDButton positiveAction = registerDialog.getActionButton(DialogAction.POSITIVE);
        positiveAction.setEnabled(false);
        final EmailValidator emailValidator = new EmailValidator();

        TextView userNameTV
                = (TextView) registerDialog.getCustomView().findViewById(R.id.register_user_name_text);
        TextView userEmailTV
                = (TextView) registerDialog.getCustomView().findViewById(R.id.register_user_email_text);
        TextView userPasswordTV
                = (TextView) registerDialog.getCustomView().findViewById(R.id.register_password_text);
        userNameTV.setTypeface(CoCoinUtil.GetTypeface());
        userEmailTV.setTypeface(CoCoinUtil.GetTypeface());
        userPasswordTV.setTypeface(CoCoinUtil.GetTypeface());

        registerUserName
                = (MaterialEditText) registerDialog.getCustomView().findViewById(R.id.register_user_name);
        registerUserEmail
                = (MaterialEditText) registerDialog.getCustomView().findViewById(R.id.register_user_email);
        registerPassword
                = (MaterialEditText) registerDialog.getCustomView().findViewById(R.id.register_password);

        registerUserName.setTypeface(CoCoinUtil.GetTypeface());
        registerUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean emailOK = emailValidator.validate(registerUserEmail.getText().toString());
                registerDialogButton.setEnabled(
                        0 < registerUserName.getText().toString().length()
                                && registerUserName.getText().toString().length() <= 16
                                && registerPassword.getText().toString().length() > 0
                                && emailOK);
                if (emailValidator.validate(registerUserEmail.getText().toString())) {
                    registerUserEmail.validate();
                } else {
                    registerUserEmail.invalidate();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registerUserEmail.setTypeface(CoCoinUtil.GetTypeface());
        registerUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean emailOK = emailValidator.validate(registerUserEmail.getText().toString());
                registerDialogButton.setEnabled(
                        0 < registerUserName.getText().toString().length()
                                && registerUserName.getText().toString().length() <= 16
                                && registerPassword.getText().toString().length() > 0
                                && emailOK);
                if (emailValidator.validate(registerUserEmail.getText().toString())) {
                    registerUserEmail.validate();
                } else {
                    registerUserEmail.invalidate();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registerPassword.setTypeface(CoCoinUtil.GetTypeface());
        registerPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean emailOK = emailValidator.validate(registerUserEmail.getText().toString());
                registerDialogButton.setEnabled(
                        0 < registerUserName.getText().toString().length()
                                && registerUserName.getText().toString().length() <= 16
                                && registerPassword.getText().toString().length() > 0
                                && emailOK);
                if (emailValidator.validate(registerUserEmail.getText().toString())) {
                    registerUserEmail.validate();
                } else {
                    registerUserEmail.invalidate();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registerDialog.show();
    }

    // Change account book name/////////////////////////////////////////////////////////////////////////
    private void changeAccountBookName() {
        new MaterialDialog.Builder(this)
                .theme(Theme.LIGHT)
                .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                .title(R.string.set_account_book_dialog_title)
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(1, 16)
                .positiveText(R.string.submit)
                .input(SettingManager.getInstance().getAccountBookName()
                        , null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // local change
                                SettingManager.getInstance().setAccountBookName(input.toString());
                                SettingManager.getInstance().setTodayViewTitleShouldChange(true);
                                SettingManager.getInstance().setMainViewTitleShouldChange(true);
                                accountBookName.setText(input.toString());
                                // update change TODO
                                // the new account book name is changed successfully
                                ToastUtil.displayShortToast(mContext, "成功修改账本名字");
                            }
                        }).show();
    }

    // Update some views when login/////////////////////////////////////////////////////////////////////
    private void updateViews() {
        setIconEnable(userNameIcon, SettingManager.getInstance().getLoggenOn());
        setIconEnable(userEmailIcon, SettingManager.getInstance().getLoggenOn());
        if (SettingManager.getInstance().getLoggenOn()) {
            userName.setText(SettingManager.getInstance().getUserName());
            userEmail.setText(SettingManager.getInstance().getUserEmail());
            loginButton.setText(mContext.getResources().getText(R.string.logout_button));
            loginButton.setBackgroundResource(R.drawable.button_logout);
        } else {
            userName.setText("");
            userEmail.setText("");
            loginButton.setText(mContext.getResources().getText(R.string.login_button));
            loginButton.setBackgroundResource(R.drawable.button_login);
        }
    }

    // Start change account book password activity//////////////////////////////////////////////////////
// I put the update to server part in the change password activity but not here/////////////////////
    private void changePassword() {
        Intent intent = new Intent(mContext, EditPasswordActivity.class);
        startActivity(intent);
    }

    // Start sort tags activity/////////////////////////////////////////////////////////////////////////
// I put the update to server part in the sort tag activity but not here////////////////////////////
    private void sortTags() {
        Intent intent = new Intent(mContext, TagSettingActivity.class);
        startActivity(intent);
    }

    // Init the setting activity////////////////////////////////////////////////////////////////////////
    private void init() {
        back = (MaterialIconView) findViewById(R.id.icon_left);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logo = (CircleImageView) findViewById(R.id.profile_image);
        logo.setOnClickListener(this);
        profileLayout = (MaterialRippleLayout) findViewById(R.id.profile_layout);
        userNameIcon = (MaterialIconView) findViewById(R.id.user_name_icon);
        userEmailIcon = (MaterialIconView) findViewById(R.id.user_email_icon);
        userName = (TextView) findViewById(R.id.user_name);
        userName.setTypeface(CoCoinUtil.typefaceLatoLight);
        userEmail = (TextView) findViewById(R.id.user_email);
        userEmail.setTypeface(CoCoinUtil.typefaceLatoLight);
        loginButton = (TextView) findViewById(R.id.login_button);
        loginButton.setTypeface(CoCoinUtil.typefaceLatoLight);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userOperator();
            }
        });
        expense = (RiseNumberTextView) findViewById(R.id.expense);
        expense.setTypeface(CoCoinUtil.typefaceLatoLight);
        records = (RiseNumberTextView) findViewById(R.id.records);
        records.setTypeface(CoCoinUtil.typefaceLatoLight);
        expenseTV = (TextView) findViewById(R.id.expense_text);
        expenseTV.setTypeface(CoCoinUtil.GetTypeface());
        recordsTV = (TextView) findViewById(R.id.records_text);
        recordsTV.setTypeface(CoCoinUtil.GetTypeface());

        expense.withNumber(RecordManager.SUM).setDuration(1500).start();
        records.withNumber(RecordManager.RECORDS.size()).setDuration(1500).start();

        monthLayout = (MaterialRippleLayout) findViewById(R.id.month_layout);
        monthIcon = (MaterialIconView) findViewById(R.id.month_limit_icon);
        monthMaxExpenseIcon = (MaterialIconView) findViewById(R.id.month_expense_icon);
        monthColorRemindIcon = (MaterialIconView) findViewById(R.id.month_color_icon);
        monthWarningIcon = (MaterialIconView) findViewById(R.id.warning_expense_icon);
        monthColorRemindTypeIcon = (MaterialIconView) findViewById(R.id.month_color_type_icon);
        monthColorRemindSelect = (MaterialIconView) findViewById(R.id.month_color_type);
        monthColorRemindSelect.setColor(SettingManager.getInstance().getRemindColor());
        monthForbiddenIcon = (MaterialIconView) findViewById(R.id.month_forbidden_icon);
        monthSB = (Switch) findViewById(R.id.month_limit_enable_button);
        monthSB.setOnCheckedChangeListener(this);
        monthColorRemindSB = (Switch) findViewById(R.id.month_color_remind_button);
        monthColorRemindSB.setOnCheckedChangeListener(this);
        monthForbiddenSB = (Switch) findViewById(R.id.month_forbidden_button);
        monthForbiddenSB.setOnCheckedChangeListener(this);
        monthMaxExpense = (RiseNumberTextView) findViewById(R.id.month_expense);
        if (SettingManager.getInstance().getIsMonthLimit())
            monthMaxExpense.withNumber(SettingManager.getInstance()
                    .getMonthLimit()).setDuration(1000).start();
// change the month limit///////////////////////////////////////////////////////////////////////////
        monthMaxExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SettingManager.getInstance().getIsMonthLimit()) {
                    new MaterialDialog.Builder(mContext)
                            .theme(Theme.LIGHT)
                            .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                            .title(R.string.set_month_expense_dialog_title)
                            .inputType(InputType.TYPE_CLASS_NUMBER)
                            .positiveText(R.string.submit)
                            .inputRange(3, 5)
                            .input(SettingManager.getInstance().getMonthLimit().toString()
                                    , null, new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog dialog, CharSequence input) {
                                            int newExpense = SettingManager.getInstance().getMonthLimit();
                                            if (input.length() != 0) {
                                                newExpense = Integer.parseInt(input.toString());
                                            }
                                            // the month limit must be smaller than the month warning
                                            if (newExpense < SettingManager.getInstance().getMonthWarning()) {
                                                SettingManager.getInstance().setMonthWarning(
                                                        ((int) (newExpense * 0.8) / 100 * 100));
                                                if (SettingManager.getInstance().getMonthWarning() < 100) {
                                                    SettingManager.getInstance().setMonthWarning(100);
                                                }
                                                updateSettingsToServer(UPDATE_MONTH_WARNING);
                                                SettingManager.getInstance()
                                                        .setMainViewRemindColorShouldChange(true);
                                                monthWarning.setText(SettingManager
                                                        .getInstance().getMonthWarning().toString());
                                            }
                                            SettingManager.getInstance().setMonthLimit(newExpense);
                                            updateSettingsToServer(UPDATE_MONTH_LIMIT);
                                            SettingManager.getInstance()
                                                    .setTodayViewMonthExpenseShouldChange(true);
                                            SettingManager.getInstance()
                                                    .setMainViewMonthExpenseShouldChange(true);
                                            monthMaxExpense.withNumber(SettingManager.getInstance()
                                                    .getMonthLimit()).setDuration(1000).start();
                                        }
                                    }).show();
                }
            }
        });
        monthWarning = (RiseNumberTextView) findViewById(R.id.warning_expense);
        monthWarning.setText(SettingManager.getInstance().getMonthWarning().toString());
        if (SettingManager.getInstance().getIsMonthLimit()
                && SettingManager.getInstance().getIsColorRemind())
            monthWarning.withNumber(SettingManager.getInstance()
                    .getMonthWarning()).setDuration(1000).start();
// change month warning/////////////////////////////////////////////////////////////////////////////
        monthWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SettingManager.getInstance().getIsMonthLimit()
                        && SettingManager.getInstance().getIsColorRemind()) {
                    new MaterialDialog.Builder(mContext)
                            .theme(Theme.LIGHT)
                            .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                            .title(R.string.set_month_expense_dialog_title)
                            .inputType(InputType.TYPE_CLASS_NUMBER)
                            .positiveText(R.string.submit)
                            .alwaysCallInputCallback()
                            .input(null, null, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(final MaterialDialog dialog, final CharSequence input) {
                                    if (input.length() == 0) {
                                        dialog.setContent(mContext.getResources().getString(
                                                R.string.set_warning_expense_dialog_title));
                                        dialog.getActionButton(DialogAction.POSITIVE)
                                                .setEnabled(false);
                                    } else if (Integer.parseInt(input.toString()) < 100) {
                                        dialog.setContent("≥ 100");
                                        dialog.getActionButton(DialogAction.POSITIVE)
                                                .setEnabled(false);
                                    } else if (Integer.parseInt(input.toString())
                                            > SettingManager.getInstance().getMonthLimit()) {
                                        dialog.setContent("≤ " + SettingManager.getInstance()
                                                .getMonthLimit().toString());
                                        dialog.getActionButton(DialogAction.POSITIVE)
                                                .setEnabled(false);
                                    } else {
                                        dialog.setContent(mContext.getResources().getString(
                                                R.string.set_warning_expense_dialog_title));
                                        dialog.getActionButton(DialogAction.POSITIVE)
                                                .setEnabled(true);
                                    }
                                    dialog.getActionButton(DialogAction.POSITIVE)
                                            .setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    SettingManager.getInstance()
                                                            .setMonthWarning(Integer.parseInt(input.toString()));
                                                    updateSettingsToServer(UPDATE_MONTH_WARNING);
                                                    SettingManager.getInstance()
                                                            .setMainViewRemindColorShouldChange(true);
                                                    monthWarning.withNumber(SettingManager.getInstance()
                                                            .getMonthWarning()).setDuration(1000).start();
                                                    dialog.dismiss();
                                                }
                                            });
                                }
                            }).show();
                }
            }
        });
// change month remind color////////////////////////////////////////////////////////////////////////
        monthColorRemindSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingManager.getInstance()
                        .setMainViewRemindColorShouldChange(true);
                remindColorSelectDialog.show((AppCompatActivity) mContext);
            }
        });
        monthMaxExpense.setTypeface(CoCoinUtil.typefaceLatoLight);
        monthWarning.setTypeface(CoCoinUtil.typefaceLatoLight);
        monthLimitTV = (TextView) findViewById(R.id.month_limit_text);
        monthLimitTV.setTypeface(CoCoinUtil.GetTypeface());
        monthWarningTV = (TextView) findViewById(R.id.warning_expense_text);
        monthWarningTV.setTypeface(CoCoinUtil.GetTypeface());
        monthMaxExpenseTV = (TextView) findViewById(R.id.month_expense_text);
        monthMaxExpenseTV.setTypeface(CoCoinUtil.GetTypeface());
        monthColorRemindTV = (TextView) findViewById(R.id.month_color_remind_text);
        monthColorRemindTV.setTypeface(CoCoinUtil.GetTypeface());
        monthColorRemindTypeTV = (TextView) findViewById(R.id.month_color_type_text);
        monthColorRemindTypeTV.setTypeface(CoCoinUtil.GetTypeface());
        monthForbiddenTV = (TextView) findViewById(R.id.month_forbidden_text);
        monthForbiddenTV.setTypeface(CoCoinUtil.GetTypeface());

        accountBookNameLayout = (MaterialRippleLayout) findViewById(R.id.account_book_name_layout);
        accountBookNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAccountBookName();
            }
        });
        accountBookName = (TextView) findViewById(R.id.account_book_name);
        accountBookName.setTypeface(CoCoinUtil.GetTypeface());
        accountBookName.setText(SettingManager.getInstance().getAccountBookName());
        accountBookNameTV = (TextView) findViewById(R.id.account_book_name_text);
        accountBookNameTV.setTypeface(CoCoinUtil.GetTypeface());

        changePasswordLayout = (MaterialRippleLayout) findViewById(R.id.change_password_layout);
        changePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
        changePasswordTV = (TextView) findViewById(R.id.change_password_text);
        changePasswordTV.setTypeface(CoCoinUtil.GetTypeface());

        sortTagsLayout = (MaterialRippleLayout) findViewById(R.id.sort_tags_layout);
        sortTagsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortTags();
            }
        });
        sortTagsTV = (TextView) findViewById(R.id.sort_tags_text);
        sortTagsTV.setTypeface(CoCoinUtil.GetTypeface());

        showPictureLayout = (MaterialRippleLayout) findViewById(R.id.whether_show_picture_layout);
        showPictureIcon = (MaterialIconView) findViewById(R.id.whether_show_picture_icon);
        showPictureSB = (Switch) findViewById(R.id.whether_show_picture_button);
        showPictureSB.setOnCheckedChangeListener(this);
        showPictureTV = (TextView) findViewById(R.id.whether_show_picture_text);
        showPictureTV.setTypeface(CoCoinUtil.GetTypeface());

        hollowLayout = (MaterialRippleLayout) findViewById(R.id.whether_show_circle_layout);
        hollowIcon = (MaterialIconView) findViewById(R.id.whether_show_circle_icon);
        hollowSB = (Switch) findViewById(R.id.whether_show_circle_button);
        hollowSB.setOnCheckedChangeListener(this);
        hollowTV = (TextView) findViewById(R.id.whether_show_circle_text);
        hollowTV.setTypeface(CoCoinUtil.GetTypeface());

        updateLayout = (MaterialRippleLayout) findViewById(R.id.update_layout);
        updateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });
        currentVersionTV = (TextView) findViewById(R.id.update_text);
        currentVersionTV.setTypeface(CoCoinUtil.GetTypeface());
        currentVersionTV.setText(mContext.getResources().getString(R.string.current_version) + BuildConfig.VERSION_NAME);
        canBeUpdatedTV = (TextView) findViewById(R.id.update_tag);
        canBeUpdatedTV.setTypeface(CoCoinUtil.GetTypeface());
        if (SettingManager.getInstance().getCanBeUpdated()) {
            canBeUpdatedTV.setVisibility(View.VISIBLE);
        } else {
            canBeUpdatedTV.setVisibility(View.GONE);
        }

        boolean loggenOn = SettingManager.getInstance().getLoggenOn();
        if (loggenOn) {
            // is logged on, set the user name and email
            userName.setText(SettingManager.getInstance().getUserName());
            userEmail.setText(SettingManager.getInstance().getUserEmail());
            loginButton.setText(mContext.getResources().getText(R.string.logout_button));
            loginButton.setBackgroundResource(R.drawable.button_logout);
        } else {
            userName.setText("");
            userEmail.setText("");
            loginButton.setText(getResourceString(R.string.login_button));
        }
        setIconEnable(userNameIcon, loggenOn);
        setIconEnable(userEmailIcon, loggenOn);

        loadLogo();

        monthSB.setCheckedImmediately(SettingManager.getInstance().getIsMonthLimit());
        setMonthState();

        showPictureSB.setCheckedImmediately(SettingManager.getInstance().getShowPicture());
        setShowPictureState(SettingManager.getInstance().getShowPicture());

        hollowSB.setCheckedImmediately(SettingManager.getInstance().getIsHollow());
        setHollowState(SettingManager.getInstance().getIsHollow());
    }

    // Set all states about month limit/////////////////////////////////////////////////////////////////
    private void setMonthState() {
        boolean isMonthLimit = SettingManager.getInstance().getIsMonthLimit();
        boolean isMonthColorRemind = SettingManager.getInstance().getIsColorRemind();
        boolean isForbidden = SettingManager.getInstance().getIsForbidden();

        setIconEnable(monthIcon, isMonthLimit);
        setIconEnable(monthMaxExpenseIcon, isMonthLimit);
        setTVEnable(monthMaxExpenseTV, isMonthLimit);
        setTVEnable(monthMaxExpense, isMonthLimit);
        setTVEnable(monthColorRemindTV, isMonthLimit);
        setTVEnable(monthColorRemindTypeTV, isMonthLimit && isMonthColorRemind);
        setTVEnable(monthWarningTV, isMonthLimit && isMonthColorRemind);
        setTVEnable(monthForbiddenTV, isMonthLimit);
        monthMaxExpense.setText(SettingManager.getInstance().getMonthLimit() + "");

        setIconEnable(monthColorRemindIcon, isMonthLimit && isMonthColorRemind);
        setIconEnable(monthWarningIcon, isMonthLimit && isMonthColorRemind);
        setIconEnable(monthColorRemindTypeIcon, isMonthLimit && isMonthColorRemind);
        setIconEnable(monthColorRemindSelect, isMonthLimit && isMonthColorRemind);
        if (isMonthLimit && isMonthColorRemind) {
            monthColorRemindSelect.setEnabled(true);
            monthColorRemindSelect
                    .setColor(SettingManager.getInstance().getRemindColor());
            monthWarning.setEnabled(true);
            monthWarning.setTextColor(
                    ContextCompat.getColor(mContext, R.color.drawer_text));
        } else {
            monthColorRemindSelect.setEnabled(false);
            monthColorRemindSelect
                    .setColor(mContext.getResources().getColor(R.color.my_gray));
            monthWarning.setEnabled(false);
            monthWarning.setTextColor(
                    ContextCompat.getColor(mContext, R.color.my_gray));
        }
        setIconEnable(monthForbiddenIcon, isMonthLimit && isForbidden);

        monthColorRemindSB.setEnabled(isMonthLimit);
        monthColorRemindSB.setCheckedImmediately(
                SettingManager.getInstance().getIsColorRemind());
        monthForbiddenSB.setEnabled(isMonthLimit);
        monthForbiddenSB.setCheckedImmediately(
                SettingManager.getInstance().getIsForbidden());
    }

    private void setShowPictureState(boolean isChecked) {
        setIconEnable(showPictureIcon, isChecked);
    }

    private void setHollowState(boolean isChecked) {
        setIconEnable(hollowIcon, isChecked);
    }

    private void setIconEnable(MaterialIconView icon, boolean enable) {
        if (enable) icon.setColor(mContext.getResources().getColor(R.color.my_blue));
        else icon.setColor(mContext.getResources().getColor(R.color.my_gray));
    }

    private void setTVEnable(TextView tv, boolean enable) {
        if (enable) tv.setTextColor(mContext.getResources().getColor(R.color.drawer_text));
        else tv.setTextColor(mContext.getResources().getColor(R.color.my_gray));
    }

    // choose a color///////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onColorSelection(ColorChooserDialog dialog, int selectedColor) {
        monthColorRemindSelect.setColor(selectedColor);
        SettingManager.getInstance().setRemindColor(selectedColor);
        updateSettingsToServer(UPDATE_REMIND_COLOR);
        SettingManager.getInstance().setMainViewRemindColorShouldChange(true);
    }

    ColorChooserDialog remindColorSelectDialog =
            new ColorChooserDialog.Builder(this, R.string.set_remind_color_dialog_title)
                    .titleSub(R.string.set_remind_color_dialog_sub_title)
                    .preselect(SettingManager.getInstance().getRemindColor())
                    .doneButton(R.string.submit)
                    .cancelButton(R.string.cancel)
                    .backButton(R.string.back)
                    .customButton(R.string.custom)
                    .dynamicButtonColor(true)
                    .build();

    // whether sync the settings from server////////////////////////////////////////////////////////////
    private void whetherSyncSettingsFromServer() {
        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.cocoin_logo)
                .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .title(R.string.sync_dialog_title)
                .forceStacking(true)
                .content(R.string.sync_dialog_content)
                .positiveText(R.string.sync_dialog_sync_to_local)
                .negativeText(R.string.sync_dialog_sync_to_server)
                .cancelable(false)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        if (which.equals(DialogAction.POSITIVE)) {
                            // sync to local
                            //TODO
                        } else if (which.equals(DialogAction.NEGATIVE)) {
                            // sync to server
                            //TODO
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void updateAllSettings() {
        updateSettingsToServer(0);
        updateSettingsToServer(1);
        updateSettingsToServer(2);
    }

    // update part of settings//////////////////////////////////////////////////////////////////////////
    private void updateSettingsToServer(final int setting) {
        //TODO
    }


    // Get string///////////////////////////////////////////////////////////////////////////////////////
    private String getResourceString(int resourceId) {
        return MyApplication.getAppContext().getResources().getString(resourceId);
    }

    // activity finish//////////////////////////////////////////////////////////////////////////////////
    @Override
    public void finish() {

        super.finish();
    }


}
