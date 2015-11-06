package com.angcyo.rbarscan;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.orhanobut.hawk.Hawk;
import com.yo.libs.app.DimensCodeTools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

public class MainActivity extends AppCompatActivity {

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
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.filePathEditText)
    EditText filePathEditText;
    private List<List<String>> barCodeDatas;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        if (count >= 24) {
            throw new RuntimeException("启动时发生错误,请联系QQ:664738095");
        }

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
        barEditView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DimensCodeTools.startScanBar(MainActivity.this);
            }
        });

        filePathEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFilePath();
            }
        });

        appendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> row = generateRowData();
                barCodeDatas.add(row);
                saveXls();
                resetView();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToXlsFile();//
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataEditView5.setText(ExcelUtil.getDateTime());
            }
        });
    }

    private void chooseFilePath() {

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
            saveXls();
            ExcelUtil.write(barCodeDatas, getSaveFilePath());
            Snackbar.make(fab, getString(R.string.save_to) + getSaveFilePath(), Snackbar.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        barEditView.setInputType(InputType.TYPE_NULL);
        dataEditView5.setInputType(InputType.TYPE_NULL);
//        filePathEditText.setInputType(InputType.TYPE_NULL);

        barEditViewLayout.setErrorEnabled(true);

        filePathEditText.setText(filePath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String res = DimensCodeTools.scanForResult(requestCode, resultCode, data);
        if (!TextUtils.isEmpty(res)) {
            Pattern p = Pattern.compile("[0-9]*");
            Matcher m = p.matcher(res);
            barEditView.setText(res);
            if (m.matches()) {
                dataEditView5.setText(ExcelUtil.getDateTime());
                Snackbar.make(fab, getString(R.string.match_ok) + res, Snackbar.LENGTH_LONG).show();
            } else {
                barEditViewLayout.setError(getString(R.string.error_bar_code));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void resetView() {
        barEditView.setText(R.string.click_to_scan_bar);
        dataEditView1.setText("0");
        dataEditView2.setText("0");
        dataEditView3.setText("0");
        dataEditView4.setText("0");
        dataEditView5.setText(ExcelUtil.getDateTime());
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
            barCode = "";
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
}
