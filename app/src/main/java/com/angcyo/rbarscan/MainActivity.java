package com.angcyo.rbarscan;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.orhanobut.hawk.Hawk;
import com.yo.libs.app.DimensCodeTools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

public class MainActivity extends AppCompatActivity implements FolderChooseFragment.OnPathSet {

    private static String KEY_RUN_COUNT = "count";
    private static String KEY_FILE_PATH = "path";
    @Bind(R.id.barEditView)
    EditText barEditView;
    @Bind(R.id.dataEditView1)
    EditText dataEditView1;
    @Bind(R.id.dataEditView2)
    EditText dataEditView2;
    @Bind(R.id.dataEditView3)
    EditText dataEditView3;
    @Bind(R.id.dataEditView4)
    EditText dataEditView4;
    @Bind(R.id.dataEditView5)
    EditText dataEditView5;
    @Bind(R.id.barEditViewLayout)
    TextInputLayout barEditViewLayout;
    @Bind(R.id.appendButton)
    Button appendButton;
    @Bind(R.id.saveButton)
    Button saveButton;
    @Bind(R.id.timeButton)
    Button timeButton;
    @Bind(R.id.clearButton)
    Button clearButton;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.filePathEditText)
    EditText filePathEditText;
    @Bind(R.id.rootView)
    View rootView;
    @Bind(R.id.filePathEditTextLayout)
    TextInputLayout filePathEditTextLayout;

    private List<List<String>> barCodeDatas;
    private String filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("TAG", "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.GONE);
        init();
        initViews();
        initEvents();
        initAfter();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("TAG", "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TAG", "onResume");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.e("TAG", "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e("TAG", "onRestoreInstanceState");
    }

    private void initAfter() {
        onPathSet(filePath);
    }

    private void init() {
        ExcelUtil.init(this);
        try {
            barCodeDatas = ExcelUtil.read();
        } catch (Exception e) {
            barCodeDatas = new ArrayList<>();
        }

        long count = Hawk.get(KEY_RUN_COUNT, 0l);
        Hawk.put(KEY_RUN_COUNT, ++count);

        filePath = Hawk.get(KEY_FILE_PATH, Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    private void saveFilePath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        this.filePath = filePath;
        Hawk.put(KEY_FILE_PATH, filePath);
    }

    private void initEvents() {
        barEditView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    DimensCodeTools.starScanWithBarSize(MainActivity.this, getResources().getDisplayMetrics().widthPixels - 100, 250);
                }
                return false;
            }
        });

        filePathEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    chooseFilePath();
                }
                return false;
            }
        });

        appendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                appendData();
                saveXls();
                resetView();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                appendData();
                saveXls();
                saveToXlsFile();//
                resetView();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                dataEditView5.setText(ExcelUtil.getDateTime());
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barCodeDatas.clear();
                ExcelUtil.deleteFile();
                ExcelUtil.deleteFile(getSaveFilePath());
                Snackbar.make(fab, R.string.clear_all_tip, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void appendData() {
        List<String> row = generateRowData();
        barCodeDatas.add(row);
    }

    private void chooseFilePath() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.rootView, FolderChooseFragment.newInstance("/mnt", this), FolderChooseFragment.class.getSimpleName());
        fragmentTransaction.addToBackStack(FolderChooseFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    private String getSaveFilePath() {
        filePath = filePathEditText.getText().toString();
        saveFilePath(filePath);
        return filePath + File.separator + ExcelUtil.EXCEL_FILE_NAME;
    }

    private void saveXls() {
        try {
            ExcelUtil.write(barCodeDatas);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    private void saveToXlsFile() {
        try {
            String filePath = getSaveFilePath();
            ExcelUtil.deleteFile(filePath);
            ExcelUtil.write(barCodeDatas, filePath);
            Snackbar.make(fab, getString(R.string.save_to) + getSaveFilePath(), Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            Snackbar.make(fab, R.string.save_file_fail, Snackbar.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        barEditView.setInputType(InputType.TYPE_NULL);
        dataEditView5.setInputType(InputType.TYPE_NULL);
        filePathEditText.setInputType(InputType.TYPE_NULL);

        barEditViewLayout.setErrorEnabled(true);
        filePathEditTextLayout.setErrorEnabled(true);

        filePathEditText.setText(filePath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String res = DimensCodeTools.scanForResult(requestCode, resultCode, data);
        if (!TextUtils.isEmpty(res)) {
//            Pattern p = Pattern.compile("[0-9]*");
//            Matcher m = p.matcher(res);
            barEditView.setText(res);
//            if (m.matches()) {
                dataEditView5.setText(ExcelUtil.getDateTime());
                barEditViewLayout.setError("");
                Snackbar.make(fab, getString(R.string.match_ok) + res, Snackbar.LENGTH_LONG).show();
//            } else {
//                barEditViewLayout.setError(getString(R.string.error_bar_code));
//            }
        } else {
            barEditView.setText("");
            barEditViewLayout.setError(getString(R.string.error_bar_code));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "onDestroy");
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("TAG", "onKeyDown");
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("TAG", "onBackPressed");
        moveTaskToBack(false);
    }

    private void hideSoftInput() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void resetView() {
        barEditView.setText(R.string.click_to_scan_bar);
        dataEditView1.setText("");
        dataEditView2.setText("");
        dataEditView3.setText("");
        dataEditView4.setText("");
        dataEditView5.setText("");
    }

    private List<String> generateRowData() {
        List<String> rows = new ArrayList<>();
        String barCode, data1, data2, data3, data4, timeString;
        barCode = barEditView.getText().toString();
        data1 = dataEditView1.getText().toString();
        data2 = dataEditView2.getText().toString();
        data3 = dataEditView3.getText().toString();
        data4 = dataEditView4.getText().toString();
        timeString = dataEditView5.getText().toString();

        if (TextUtils.isEmpty(barCode) || barCode.equalsIgnoreCase(getResources().getString(R.string.click_to_scan_bar))) {
            barCode = "-";
        }
        if (TextUtils.isEmpty(data1)) {
            data1 = "0";
        }
        if (TextUtils.isEmpty(data2)) {
            data2 = "0";
        }
        if (TextUtils.isEmpty(data3)) {
            data3 = "0";
        }
        if (TextUtils.isEmpty(data4)) {
            data4 = "0";
        }
        if (TextUtils.isEmpty(timeString)) {
            timeString = ExcelUtil.getDateTime();
        }

        rows.add(String.valueOf(barCodeDatas.size() + 1));
        rows.add(barCode);
        rows.add(data1);
        rows.add(data2);
        rows.add(data3);
        rows.add(data4);
        rows.add(timeString);
        return rows;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPathSet(String path) {
        filePathEditText.setText(path);
        File file = new File(path);
        if (!file.canWrite()) {
            filePathEditTextLayout.setError(getString(R.string.error_path_tip));
        } else {
            filePathEditTextLayout.setError("");
        }
    }
}
