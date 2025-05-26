package com.example.ipping;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    ArrayList<IpListItem> ipList;
    ArrayList<IpListItem> deleteIpItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ipList = new ArrayList<>();
        deleteIpItems = new ArrayList<>();
    }
    public void OpenFile(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        openFileLauncher.launch(Intent.createChooser(intent, "Select a file"));
    }

    ActivityResultLauncher<Intent> openFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Gson gson = new Gson();
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        String jsonString = readTextFromUri(uri);
                        Type listType = new TypeToken<ArrayList<IpListItem>>() {}.getType();
                        ipList = gson.fromJson(jsonString, listType);
                        UpdateListView();
                    }
                }
            }
    );

    private String readTextFromUri(Uri uri) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            Log.d("ERROR", Objects.requireNonNull(e.getMessage()));
        }
        return stringBuilder.toString();
    }

    public void OnClick(View view)
    {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        view = LayoutInflater.from(MainActivity.this).inflate(R.layout.bottom_sheet, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        EditText ipAddress = view.findViewById(R.id.ipAddress);
        EditText serverName = view.findViewById(R.id.serverName);
        Button addButton = view.findViewById(R.id.button);

        addButton.setOnClickListener(view1 -> {
            if(Objects.requireNonNull(ipAddress.getText()).toString().isEmpty() || Objects.requireNonNull(serverName.getText()).toString().isEmpty())
            {
                Toast.makeText(getApplicationContext(), "IP Address and Server name required", Toast.LENGTH_LONG).show();
            } else {
                AddItemToIpList(ipAddress.getText().toString(), serverName.getText().toString());
                bottomSheetDialog.dismiss();
            }
        });

    }
    public void CheckAllIp(View view)
    {
        ImageButton imageButton = findViewById(R.id.playAll);
        imageButton.setActivated(false);
        ListView listView = findViewById(R.id.listView);
        for(int i = 0; i<ipList.size(); i++)
        {
            IpListItem ipListItem = ipList.get(i);
            view = listView.getChildAt(i);
            ImageView imageView = view.findViewById(R.id.pingStatus);
            SetPingStatusImageView(imageView, ipListItem);
        }
        Toast.makeText(this, "All IPs checked", Toast.LENGTH_LONG).show();
        imageButton.setActivated(true);
    }
    public void SetPingStatusImageView(ImageView imageView, IpListItem ipListItem)
    {
        imageView.setImageResource(R.drawable.baseline_loading_24);
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate);
        imageView.startAnimation(animation);
        ipListItem.Success = PingIpAddress(ipListItem.IpAddress);
        imageView.clearAnimation();
        if(ipListItem.Success)
        {
            imageView.setImageResource(R.drawable.baseline_ok_24);
        }
        else
        {
            imageView.setImageResource(R.drawable.baseline_bad_24);
        }
    }
    public void AddItemToIpList(String ipAddress, String serverName)
    {
        IpListItem ipListItem = new IpListItem();
        ipListItem.IpAddress = ipAddress;
        ipListItem.ServerName = serverName;
        ipListItem.Success = false;
        ipList.add(ipListItem);
        UpdateListView();
    }
    public void UpdateListView()
    {
        ListView listView = findViewById(R.id.listView);
        ListItemAdapter adapter = new ListItemAdapter(getApplicationContext(), 0, ipList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            IpListItem ipItem = (IpListItem) (listView.getItemAtPosition(position));
            ImageView imageView = view.findViewById(R.id.pingStatus);
            SetPingStatusImageView(imageView, ipItem);
        });
        listView.setOnItemLongClickListener((adapterView, view, position, l) -> {
            IpListItem ipItem = (IpListItem) (listView.getItemAtPosition(position));
            int bgColor = ((ColorDrawable)view.getBackground()).getColor();
            if(bgColor == Color.parseColor("#ffffff"))
            {
                view.setBackgroundColor(Color.parseColor("#c40c0c"));
                deleteIpItems.add(ipItem);
            } else {
                view.setBackgroundColor(Color.parseColor("#ffffff"));
                deleteIpItems.removeIf(item -> item.equals(ipItem));
            }
            return true;
        });
    }
    public void DeleteIpListItems(View view)
    {
        if(deleteIpItems.isEmpty())
            return;
        deleteIpItems.forEach(item -> ipList.removeIf(listItem -> listItem.equals(item)));
        deleteIpItems = new ArrayList<>();
        UpdateListView();
    }
    public boolean PingIpAddress(String ip)
    {
        final boolean[] success = new boolean[1];
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() ->{
            try {
                InetAddress inetAddress = InetAddress.getByName(ip);
                success[0] = inetAddress.isReachable(10000);
            } catch (Exception e){
                Log.d("info", Objects.requireNonNull(e.getMessage()));
            }
        });
        return success[0];
    }
}