package com.angcyo.rbarscan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by angcyo on 15-11-06-006.
 */
public class FolderChooseFragment extends Fragment implements PathRecycleAdapter.OnItemClick {

    public static final String DEFAULT_PATH = "/mnt";
    public static final String KEY_PATH = "path";
    @Bind(R.id.curPathEditText)
    AppCompatEditText curPathEditText;
    @Bind(R.id.setButton)
    AppCompatButton setButton;
    @Bind(R.id.pathsRecycle)
    RecyclerView pathsRecycle;
    @Bind(R.id.curPathEditTextLayout)
    TextInputLayout curPathEditTextLayout;

    private String rootPath, curPath;
    private View rootView;
    private OnPathSet listener;
    private PathRecycleAdapter pathRecycleAdapter;

    private FolderChooseFragment() {

    }

    public static FolderChooseFragment newInstance(String rootFolder, OnPathSet listener) {
        FolderChooseFragment folderChooseFragment = new FolderChooseFragment();
        folderChooseFragment.setListener(listener);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PATH, TextUtils.isEmpty(rootFolder) ? DEFAULT_PATH : rootFolder);
        folderChooseFragment.setArguments(bundle);
        return folderChooseFragment;
    }

    public static List<String> getPathList(String path) {
        List<String> paths = new ArrayList<>();
        if (TextUtils.isEmpty(path)) {
            return paths;
        }
        File file = new File(path);
        if (!file.exists()) {
            return paths;
        }

        if (!file.canWrite() && !isRootPath(path)) {
            return paths;
        }

        String[] files = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                File file1 = new File(dir, filename);
                if (file1.isDirectory() && file1.canWrite()) {
                    return true;
                }
                return false;
            }
        });

        for (String fileName : files) {
            paths.add(fileName);
        }
        Collections.sort(paths);
        return paths;
    }

    public static boolean isRootPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        if ("/mnt".equalsIgnoreCase(path) || "/mnt/".equalsIgnoreCase(path)) {
            return true;
        }

        return false;
    }

    private void setListener(OnPathSet listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootPath = getArguments().getString(KEY_PATH);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_folder_choose, container, false);
        ButterKnife.bind(this, rootView);

        initViews();
        initEvents();
        initAfter();
        return rootView;
    }

    private void initAfter() {
        curPathEditText.setFocusable(true);
        curPathEditText.requestFocus();
        curPathEditTextLayout.setFocusable(true);
        curPathEditTextLayout.requestFocus();
    }

    private void initEvents() {
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPathSet(getCurPath());
                }
                getActivity().onBackPressed();
            }
        });

        curPathEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    String prePath = getPrePath();
                    setCurPath(prePath);
                    pathRecycleAdapter.setDatas(getPathList(prePath));
                }
                return false;
            }
        });
    }

    private String getPrePath() {
        if (TextUtils.isEmpty(getCurPath())) {
            return DEFAULT_PATH;
        }

        String[] paths = curPath.split("/");
        if (paths.length < 3) {
            return curPath;
        }

        return curPath.substring(0, curPath.lastIndexOf("/"));
    }

    private void initViews() {
        curPathEditText.setInputType(InputType.TYPE_NULL);
        curPathEditText.setText(rootPath);
        pathsRecycle.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        pathRecycleAdapter = new PathRecycleAdapter(getActivity(), getPathList(rootPath), this);
        pathsRecycle.setAdapter(pathRecycleAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private String getCurPath() {
        curPath = curPathEditText.getText().toString();
        return curPath;
    }

    private void setCurPath(String path) {
        curPath = path;
        curPathEditText.setText(curPath);
    }

    @Override
    public void onItemClick(String str, int position) {
        setCurPath(getCurPath() + File.separator + str);
        pathRecycleAdapter.setDatas(getPathList(curPath));
    }

    public interface OnPathSet {
        void onPathSet(String path);
    }
}
