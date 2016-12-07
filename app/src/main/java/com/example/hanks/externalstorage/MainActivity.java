package com.example.hanks.externalstorage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Spinner spinner;
    RadioButton rbPublic,rbPrivate;
    TextView tvShow,tvPath;
    EditText etInput,etFileName;
    Button btnSave,btnDelete,btnLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findviews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupSpinner();
    }

    void findviews(){
        spinner=(Spinner)findViewById(R.id.spinner);
        rbPublic=(RadioButton)findViewById(R.id.radioButton);
        rbPublic.setOnClickListener(rbClick);
        rbPrivate=(RadioButton)findViewById(R.id.radioButton2);
        rbPrivate.setOnClickListener(rbClick);
        tvShow=(TextView)findViewById(R.id.textView);
        tvPath=(TextView)findViewById(R.id.textView2);
        etInput=(EditText)findViewById(R.id.editText);
        btnSave=(Button)findViewById(R.id.button);
        btnDelete=(Button)findViewById(R.id.button2);
        btnLoad=(Button)findViewById(R.id.button3);
    }



    //檢查SDCard是否存在
    boolean isSDcardExists(){
        String state= Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;

    }

    public void onSave(View v){
        View myView=getLayoutInflater().inflate(R.layout.dialog_view,null);
        etFileName=(EditText)myView.findViewById(R.id.editText2);
        new AlertDialog.Builder(this)
                .setTitle(R.string.SaveFile)
                .setView(myView)
                .setPositiveButton(R.string.Ok,dlgBtnClick)
                .setNegativeButton(R.string.Cancel,dlgBtnClick)
                .show();

    }


    DialogInterface.OnClickListener dlgBtnClick=new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch(which){
                case DialogInterface.BUTTON_POSITIVE:
                    if(!isSDcardExists()){
                        //SDCard不存在就直接Return
                        return;
                    }
                    File file=null;
                    if(rbPublic.isChecked()){
                        File filePath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                        file=new File(filePath,etFileName.getText().toString());

                    }else{
                        File filePath= getExternalFilesDir(Environment.DIRECTORY_MUSIC);
                        file=new File(filePath,etFileName.getText().toString());
                    }
                    try {
                        FileOutputStream fos=new FileOutputStream(file);
                        fos.write(etInput.getText().toString().getBytes());
                        fos.close();
                        tvPath.setText(file.toString());
                        Toast.makeText(MainActivity.this,"File is saved!",Toast.LENGTH_SHORT).show();
                        setupSpinner();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    Toast.makeText(MainActivity.this,R.string.CancelFile,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    void setupSpinner(){
        File filePath=null;
        if(rbPublic.isChecked()){
            filePath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        }else{
            filePath=getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        }
        String[] fileNames=filePath.list();
        ArrayAdapter<String> adt=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,fileNames);
        adt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adt);
    }

    View.OnClickListener rbClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setupSpinner();
        }
    };

    public void onDelete(View v){
        File filePath=null;
        if(rbPublic.isChecked()){
            filePath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        }else{
            filePath=getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        }

        File file=new File(filePath,spinner.getSelectedItem().toString());
        file.delete();
        Toast.makeText(MainActivity.this,"File is deleted!",Toast.LENGTH_SHORT).show();
        setupSpinner();
    }

    public void onLoad(View v){
        File filePath=null;
        if(rbPublic.isChecked()){
            filePath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        }else{
            filePath=getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        }

        File file=new File(filePath,spinner.getSelectedItem().toString());
        try {
            //第一種方法
//            FileInputStream fis=new FileInputStream(file);
//            byte[] buffer=new byte[fis.available()];
//            fis.read(buffer);
//            fis.close();
//            tvShow.setText(new String(buffer));

            //第二種方法
            FileInputStream fis =new FileInputStream(file);
            BufferedReader br=new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb=new StringBuilder();
            String str=null;
            while((str=br.readLine())!=null){
                sb.append(str);
            }
            tvShow.setText(sb);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){

        }

    }

}
