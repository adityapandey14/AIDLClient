package com.example.aidlclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aidlserver.IAIDLColorInterface;

public class MainActivity extends AppCompatActivity {

    private IAIDLColorInterface iAidlColorService;
    private boolean isServiceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handle window insets for system UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Create service connection
        ServiceConnection mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                // Bind to the AIDL service
                iAidlColorService = IAIDLColorInterface.Stub.asInterface(iBinder);
                isServiceBound = true;

                // Now you can call the AIDL method
                try {
                    int color = iAidlColorService.getColor();
                    Log.d("AIDL", "Color from service: " + color);
                } catch (RemoteException e) {
                    Log.e("AIDL", "Error calling getColor()", e);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                // Nullify the reference when the service is disconnected
                iAidlColorService = null;
                isServiceBound = false;
            }
        };

        // Bind to the AIDL service
        Intent intent = new Intent("AIDLColorService");
        intent.setPackage("com.example.aidlserver");
        bindService(intent, mConnection, BIND_AUTO_CREATE);

        Button b = findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               try {
                   int color = iAidlColorService.getColor();
                   Log.d("AIDL", "Color from service: " + color);
               } catch (RemoteException e) {
                   Log.e("AIDL", "Error calling getColor()", e);
               }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}
