package com.csit.smusic;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
     private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listview);
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        ArrayList<File> Songs=FethSongs(Environment.getExternalStorageDirectory());
                        String[] items=new String[Songs.size()];
                        for(int i=0;i<Songs.size();i++){
                            items[i]=Songs.get(i).getName().replace(".mp3","").replace(".m4a","");
                        }
                        ArrayAdapter<String> ad=new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,items);
                        listView.setAdapter(ad);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent=new Intent(MainActivity.this,PlayMusic.class);
                            String name=listView.getItemAtPosition(position).toString();
                                intent.putExtra("currentSong",name);
                                intent.putExtra("songList",Songs);
                                intent.putExtra("position",position);
                                startActivity(intent);



                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
    public ArrayList<File> FethSongs(File file){
        ArrayList arrayList=new ArrayList();
        File[] songs=file.listFiles();
        if(songs!=null){
            for(File myfile: songs){
                if(!myfile.isHidden() && myfile.isDirectory()){
                    arrayList.addAll(FethSongs(myfile));
                }
                else{
                    if(  !myfile.getName().startsWith(".")){
                        if(myfile.getName().endsWith(".mp3") ||myfile.getName().endsWith(".m4a"))
                        arrayList.add(myfile);
                    }
                }
            }
        }
        return  arrayList;
    }
}